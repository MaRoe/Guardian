package org.guardian.commands;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.guardian.Guardian;
import org.guardian.entries.Entry;
import org.guardian.params.QueryParams;
import org.guardian.params.QueryParamsFactory;
import org.guardian.util.BukkitUtils;

import java.util.List;

public class RebuildCommand extends BaseCommand {

    public RebuildCommand() {
        name = "rebuild";
        usage = "<parameters> <- rebuild changes";
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
                        plugin.rebuild(results, sender);
                    } else {
                        BukkitUtils.sendMessage(sender, ChatColor.DARK_AQUA + "No results found.");
                    }
                } catch (final Exception ex) {
                    BukkitUtils.severe("Error occurred during rebuild", ex);
                    sender.sendMessage(ChatColor.RED + "Exception, check error log");
                }
            }

        };
    }

    @Override
    public boolean permission(CommandSender csender) {
        return csender.hasPermission("guardian.rebuild");
    }

    @Override
    public BaseCommand newInstance() {
        return new RebuildCommand();
    }
}