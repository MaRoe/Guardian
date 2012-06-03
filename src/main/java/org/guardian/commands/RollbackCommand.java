package org.guardian.commands;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.guardian.Guardian;
import org.guardian.entries.Entry;
import org.guardian.params.QueryParams;
import org.guardian.params.QueryParamsFactory;
import org.guardian.util.BukkitUtils;

public class RollbackCommand extends BaseCommand {

    public RollbackCommand() {
        name = "rollback";
        usage = "<parameters> <- rollback changes";
        minArgs = 1;
    }

    @Override
    public boolean execute() {
        QueryParams params = new QueryParamsFactory().create(sender, args);
        session.setLastQuery(params);
        session.setEntryCache(null);
        plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, asTask(sender, params, plugin));
        return true;
    }

    @Override
    public void moreHelp() {
        // TODO display help
    }
    
    public static Runnable asTask(final CommandSender sender, final QueryParams params, final Guardian plugin) {
        return new Runnable() {

            @Override
            public void run() {
                try {
                    if (!params.silent) {
                        BukkitUtils.sendMessage(sender, ChatColor.BLUE + "Searching for entries");
                    }
                    List<Entry> results = plugin.getLog(params);
                    if (results.size() > 0) {
                        BukkitUtils.sendMessage(sender, ChatColor.GREEN + "Found " + results.size() + " entries!");
                        plugin.rollback(results, sender);
                    } else {
                        BukkitUtils.sendMessage(sender, ChatColor.DARK_AQUA + "No results found.");
                    }
                } catch (final Exception ex) {
                    BukkitUtils.severe("Error occurred during rollback", ex);
                    sender.sendMessage(ChatColor.RED + "Exception, check error log");
                }
            }
            
        };
    }

    @Override
    public boolean permission(CommandSender csender) {
        return csender.hasPermission("guardian.rollback");
    }

    @Override
    public BaseCommand newInstance() {
        return new RollbackCommand();
    }
}