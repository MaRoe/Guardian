package org.guardian.config;

import java.io.File;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginDescriptionFile;
import org.guardian.ActionType;
import org.guardian.Guardian;
import org.guardian.params.QueryParams;
import org.guardian.permissions.RollbackSize;
import org.guardian.permissions.RollbackSizes;
import org.guardian.tools.Tool;
import org.guardian.tools.ToolBehavior;
import org.guardian.tools.ToolMode;
import org.guardian.util.BukkitUtils;
import org.guardian.util.Utils;

public final class Config {

    // Main config
    public boolean debug, checkVersion, sendStatistics, logPlayerInfo, ninjaMode, motd;
    // Bridge config
    public String bridgeName, tablePrefix, host;
    public int port;
    public String database, username, password;
    public int maxConnections;
    public String url;
    // World config
    public HashMap<String, WorldConfig> worlds = new HashMap<String, WorldConfig>();
    public WorldConfig superWorldConfig;
    // Consumer config
    public int maxTimerPerRun, forceToProcessAtLeast, delayBetweenRuns;
    // Lookup config
    public int linesLimit, defaultTime, defaultDistance, linesPerPage;
    // Clearlog config
    public boolean enableAutoClearLog;
    public ArrayList<QueryParams> autoParameters = new ArrayList<QueryParams>();
    public boolean dumpClearedLog;
    // Rolback config
    public List<Integer> ignoredBlocks;
    public List<Integer> forcedBlocks;
    public EnumMap<RollbackSize, RollbackSizes> rollbackSizes = new EnumMap<RollbackSize, RollbackSizes>(RollbackSize.class);
    // Tools
    public ArrayList<Tool> tools;
    public HashMap<String, Tool> toolsByName;
    public HashMap<Integer, Tool> toolsByType;
    // Materials
    public MaterialData materialDataManager;
    // Other
    public PluginDescriptionFile bridgeDescription;
    private Guardian plugin = Guardian.getInstance();
    @SuppressWarnings("unused")
    private String lastVersion;
	public boolean useMetrics;
	public boolean askBeforeRollback;

    public Config() {
        load();
    }

    public void load() {
        // Load the config
        FileConfiguration config = plugin.getConfig();
        config.options().copyDefaults(true);
        plugin.saveConfig();
        // Populate main config
        debug = config.getBoolean("debug");
        checkVersion = config.getBoolean("checkVersion");
        sendStatistics = config.getBoolean("sendStatistics");
        logPlayerInfo = config.getBoolean("logPlayerInfo");
        ninjaMode = config.getBoolean("ninjaMode");
        motd = config.getBoolean("motd");
        useMetrics = config.getBoolean("Enable-Metrics", true);
        askBeforeRollback = config.getBoolean("askBeforeRollback", true);
        // Populate bridge config
        bridgeName = config.getString("bridge.name");
        tablePrefix = config.getString("bridge.tablePrefix");
        host = config.getString("bridge.host");
        port = config.getInt("bridge.port");
        database = config.getString("bridge.database");
        username = config.getString("bridge.username");
        password = config.getString("bridge.password");
        maxConnections = config.getInt("bridge.maxConnections");
        url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        // Populate the world config
        ConfigurationSection globalSection = config.getConfigurationSection("worlds.global");
        for (World world : Bukkit.getServer().getWorlds()) {
            String name = world.getName();
            ConfigurationSection cs = config.getConfigurationSection("worlds." + world.getName());
            if (cs == null) {
                cs = globalSection;
            }
            for (String key : cs.getKeys(false)) {
                if (key == null) {
                    cs.set(key, globalSection.get(key));
                }
            }
            worlds.put(name, new WorldConfig(cs));
        }
        superWorldConfig = new WorldConfig(worlds.values());
        // Populate the consumer config
        maxTimerPerRun = config.getInt("consumer.maxTimePerRun");
        forceToProcessAtLeast = config.getInt("consumer.forceToProcessAtLeast");
        delayBetweenRuns = config.getInt("consumer.delayBetweenRuns");
        // Populate the lookup config
        linesLimit = config.getInt("lookup.linesLimit");
        defaultTime = Utils.parseTimeSpec(config.getString("lookup.defaultTime"));
        defaultDistance = config.getInt("lookup.defaultDistance");
        linesPerPage = config.getInt("lookup.linesPerPage");
        // Populate the clearlog config
        enableAutoClearLog = config.getBoolean("clearlog.enableAutoClearLog");
        // TODO autoParamaters
        dumpClearedLog = config.getBoolean("clearlog.dumpClearedLog");
        // Rollback config
        ignoredBlocks = config.getIntegerList("rollback.ignoredBlocks") != null
                ? config.getIntegerList("rollback.ignoredBlocks") : new ArrayList<Integer>();
        forcedBlocks = config.getIntegerList("rollback.forcedBlocks") != null
                ? config.getIntegerList("rollback.forcedBlocks") : new ArrayList<Integer>();
        // Permissions
        ConfigurationSection permSection = config.getConfigurationSection("rollback.sizes");
        for (String key : permSection.getKeys(false)) {
            key = key.toUpperCase();
            rollbackSizes.put(RollbackSize.valueOf(key), new RollbackSizes(permSection.getInt(key + ".maxArea"), permSection.getInt(key + ".maxTime")));
        }
        // TODO Populate the tool config
        Set<String> toolNames = config.getConfigurationSection("tools").getKeys(false);
        tools = new ArrayList<Tool>();
        for (String toolName : toolNames) {
            try {
                String path = "tools." + toolName;
                List<String> aliases = config.getStringList(path + ".aliases");
                ToolBehavior leftClickBehavior = ToolBehavior.valueOf(config.getString(path + ".leftClickBehavior").toUpperCase());
                ToolBehavior rightClickBehavior = ToolBehavior.valueOf(config.getString(path + ".rightClickBehavior").toUpperCase());
                boolean defaultEnabled = config.getBoolean(path + ".defaultEnabled", false);
                int item = config.getInt(path + ".item", 0);
                ToolMode mode = ToolMode.valueOf(config.getString(path + ".mode").toUpperCase());
                boolean giveTool = config.getBoolean(path + ".giveTool", true);
                List<String> params = Arrays.asList(config.getString(path + ".params").split(" "));
                tools.add(new Tool(toolName, aliases, leftClickBehavior, rightClickBehavior, defaultEnabled, item, params, mode, giveTool));
                Bukkit.getServer().getPluginManager().addPermission(new Permission("guardian.tools." + toolName, PermissionDefault.OP));
            } catch (Exception ex) {
                BukkitUtils.warning("Error at parsing tool '" + toolName + "':)", ex);
            }
        }
        toolsByName = new HashMap<String, Tool>();
        toolsByType = new HashMap<Integer, Tool>();
        for (Tool tool : tools) {
            toolsByType.put(tool.item, tool);
            toolsByName.put(tool.name, tool);
            for (String alias : tool.aliases) {
                toolsByName.put(alias, tool);
            }
        }
        // Materials
        materialDataManager = new MaterialData(new File(plugin.getDataFolder().getPath() + File.separator + "materials.yml"));
        String version = plugin.getDescription().getVersion();
        lastVersion = config.getString("version", version);
        config.set("version", version);
        plugin.saveConfig();
    }

    public boolean isLogged(String world) {
        final WorldConfig wcfg = worlds.get(world);
        return wcfg != null && !wcfg.isIgnored();
    }

    public boolean isLogged(String world, ActionType action) {
        final WorldConfig wcfg = worlds.get(world);
        return wcfg != null && !wcfg.isIgnored() && wcfg.isLogging(action);
    }

    public boolean isLogging(Player p) {
        final WorldConfig wcfg = worlds.get(p.getWorld().getName());
        return wcfg != null && !wcfg.isIgnored(p.getName());
    }

    public boolean isLogged(String world, ActionType action, String playerName) {
        final WorldConfig wcfg = worlds.get(world);
        return wcfg != null && !wcfg.isIgnored() && wcfg.isLogging(action) && !wcfg.isIgnored(playerName);
    }
}
