package org.guardian;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.guardian.commands.GuardianCommandExecutor;
import org.guardian.config.Config;
import org.guardian.entries.Entry;
import org.guardian.listeners.ToolListener;
import org.guardian.listeners.block.*;
import org.guardian.listeners.inventory.InventoryClick;
import org.guardian.params.QueryParams;
import org.guardian.util.BukkitUtils;
import org.guardian.util.Utils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Guardian extends JavaPlugin {

    // Plugins
    private static Guardian guardian;
    private static WorldEditPlugin worldEdit;
    // Configuration and commands
    private Config conf;
    private GuardianCommandExecutor commandExecutor;
    // Database, consumer, sessions etc
    private SessionManager sessionMan;
    private DatabaseBridge database;
    private int consumerId;
    private final int pluginId = 1;

    @Override
    public void onLoad() {
        guardian = this;
        try {
            URL test = new URL("http://guardian.nekotech.tk:8080/job/Guardian/Guardian-RB/api/json");
            HttpURLConnection connection = (HttpURLConnection) test.openConnection();
            connection.connect();
            JSONObject object = (JSONObject) new JSONParser().parse(new InputStreamReader(connection.getInputStream()));
            String version = guardian.getDescription().getVersion();
            if (!version.equals("Unknown") && Integer.parseInt(version) < Integer.parseInt(object.get("number").toString())) {
                BukkitUtils.info("Guardian is out of date, please download the latest");
            }
        } catch (Exception ex) {
            BukkitUtils.severe("Error occurred while checking if Guardian is up to date");
        }
    }

    @Override
    public void onEnable() {
        // Load configuration
        conf = new Config();
        // Initialize commands
        // Testing Rebase
        commandExecutor = new GuardianCommandExecutor();
        getCommand("guardian").setExecutor(commandExecutor);
        // Initialize the session manager
        sessionMan = new SessionManager();
        // Lets get some bridge action going
        final File file = new File(getDataFolder() + File.separator + getConf().bridgeName);
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            BukkitUtils.severe("Could not find a valid bridge! Please check it is installed and present in config.yml");
            BukkitUtils.warning("Attempting to download the MySQL bridge for you");
            Utils.download("http://guardian.nekotech.tk:8080/job/Guardian-MySQL/Guardian-MySQL-RB/artifact/target/Guardian-MySQL.jar", file);
        }
        if (!file.exists()) {
            fatalError(getConf().bridgeName + " can still not be found. Aborting");
            return;
        }
        database = DatabaseLoader.loadBridge(file);
        // Something went wrong
        if (database == null) {
            fatalError(getConf().bridgeName + " is not a database bridge!");
            return;
        }
        // Check that the database is working
        try {
            if (!database.init()) {
                fatalError("The database bridge failed to verify itself");
                return;
            }
        } catch (final SQLException ex) {
            fatalError(ex.getMessage());
            return;
        }
        // Start the consumer
        consumerId = getServer().getScheduler().scheduleAsyncRepeatingTask(this, database, getConf().delayBetweenRuns * 20, getConf().delayBetweenRuns * 20);
        if (consumerId <= 0) {
            fatalError("Failed to start the consumer");
            return;
        }
        // Activate listeners
        if (conf.superWorldConfig.isLogging(ActionType.BLOCK_BREAK)) {
            new BlockBreak();
        }
        if (conf.superWorldConfig.isLogging(ActionType.BLOCK_BURN)) {
            new BlockBurn();
        }
        if (conf.superWorldConfig.isLogging(ActionType.BLOCK_FADE)) {
            new BlockFade();
            new LeavesDecay();
        }
        if (conf.superWorldConfig.isLogging(ActionType.BLOCK_FORM)) {
            new BlockForm();
        }
        if (conf.superWorldConfig.isLogging(ActionType.BLOCK_FROM_TO)) {
            new BlockFromTo();
            new BlockPistonMovement();
        }
        if (conf.superWorldConfig.isLogging(ActionType.BLOCK_SPREAD)) {
            new BlockSpread();
        }
        if (conf.superWorldConfig.isLogging(ActionType.BLOCK_PLACE)) {
            new BlockPlace();
        }
        if (conf.superWorldConfig.isLogging(ActionType.INVENTORY_CLICK)) {
            new InventoryClick();
        }
        new ToolListener();
        // Check for WorldEdit
        Plugin wePlugin = getServer().getPluginManager().getPlugin("WorldEdit");
        if (wePlugin != null) {
            worldEdit = (WorldEditPlugin) wePlugin;
            BukkitUtils.info("WorldEdit " + getWorldEdit().getDescription().getVersion() + " has been found, selection rollbacks enabled");
        }
        if (conf.useMetrics) {
        BukkitUtils.info("Activating Metrics");
        	try {
            	Metrics metrics = new Metrics(this);
            	metrics.start();
        	} catch (IOException ex) {
        		ex.printStackTrace();
        	}
        }
    }

    @Override
    public void onDisable() {
        // Cancel the consumer task
        getServer().getScheduler().cancelTask(consumerId);
        // Cancel anything else that happens to be working for us
        getServer().getScheduler().cancelTasks(this);
        if (database != null) {
            if (database.getQueueSize() > 0) {
                BukkitUtils.info("Waiting for consumer ...");
                int tries = 10;
                while (database.getQueueSize() > 0) {
                    BukkitUtils.info("Remaining queue size: " + database.getQueueSize());
                    if (tries > 0)
                        BukkitUtils.info("Remaining tries: " + tries);
                    else {
                        BukkitUtils.info("Unable to save queue to database. Trying to write to a local file.");
                        try {
                            database.writeLocalDump();
                            BukkitUtils.info("Successfully dumped queue.");
                        } catch (IOException ex) {
                            BukkitUtils.severe("Failed to write. Given up.", ex);
                            break;
                        }
                    }
                    database.run();
                    tries--;
                }
            }
        }
        BukkitUtils.info("Guardian disabled.");
    }

    public void fatalError(String error) {
        BukkitUtils.severe(error);
        BukkitUtils.severe("Fatal error detected! version " + getDescription().getVersion() + " disabled");
        getServer().getPluginManager().disablePlugin(this);
    }

    /**
     * @return the current plugin instance
     */
    public static Guardian getInstance() {
        return guardian;
    }

    /**
     * @return the config
     */
    public Config getConf() {
        return conf;
    }

    /**
     * @return the current session manager
     */
    public SessionManager getSessionManager() {
        return sessionMan;
    }

    /**
     * @return the main command executor for Guardian
     */
    public GuardianCommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    /**
     * @return the WorldEdit instance
     */
    public WorldEditPlugin getWorldEdit() {
        return worldEdit;
    }

    /**
     * Returns all log matching specified parameters. Also intern methods should
     * use this.
     *
     * @param params the query paramaters to use
     * @return A list of all entries
     * @throws SQLException when there is a database error
     */
    public List<Entry> getLog(QueryParams params) throws SQLException {
        return getDatabaseBridge().getEntries(params);
    }

    /**
     * Performs a rollback on all log matching specified parameters. Also intern
     * methods should use this.
     *
     * @param params the query paramaters to use
     */
    public void rollback(QueryParams params) throws SQLException {
        rollback(getLog(params), null);
    }

    public void rollback(List<Entry> entries, CommandSender sender) {
        new Rollback(entries, sender); // TODO add active rollbacks
    }

    /**
     * Redoes all changes matching parameters, basically a undo of a rollback.
     * Internal methods should use this.
     *
     * @param params the query paramaters to use
     */
    public void rebuild(QueryParams params) {
        // TODO
    }

    /**
     * Deletes all log matching specified parameters. Also intern methods should
     * use this.
     *
     * @param params the query paramaters to use
     * @throws SQLException when there is a database error
     */
    public void clearLog(QueryParams params) throws SQLException {
        // database.removeEntries(params);
    }

    /**
     * @return the currently used DatabaseBridge
     */
    public DatabaseBridge getDatabaseBridge() {
        return database;
    }

    /**
     *
     * @return Guardian's plugin Id
     */
    public int getPluginId() {
        return pluginId;
    }
}
