package org.guardian.commands;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.guardian.entries.Entry;
import org.guardian.params.QueryParams;
import org.guardian.params.QueryParamsFactory;
import org.guardian.util.BukkitUtils;

public class SearchCommand extends BaseCommand {

    public SearchCommand() {
        name = "search";
        usage = "<params> <- search the database";
        minArgs = 1;
    }

    @Override
    public boolean execute() {
        QueryParams params = new QueryParamsFactory().create(sender, args);
        session.setLastQuery(params);
        try {
            new CommandSearch(sender, params, true);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    public void moreHelp() {
        // TODO display help
    }

    @Override
    public boolean permission() {
        return sender.hasPermission("guardian.lookup");
    }

    public class CommandSearch extends AbstractCommand {

        public CommandSearch(CommandSender sender, QueryParams params, boolean async) throws Exception {
            super(sender, params, async);
        }

        public void run() {
            try {
                if (!params.silent) {
                    BukkitUtils.sendMessage(sender, ChatColor.GREEN + "Searching for entries");
                }
                List<Entry> results = plugin.getLog(params);
                if (results.size() > 0) {
                    plugin.getSessionManager().getSession(this.sender).setEntryCache(results);
                    showPage(sender, 1);
                } else {
                    this.sender.sendMessage(ChatColor.DARK_AQUA + "No results found.");
                    plugin.getSessionManager().getSession(this.sender).setEntryCache(null);
                }
            } catch (final Exception ex) {
                sender.sendMessage(ChatColor.RED + "Exception, check error log");
            }
        }
    }

    @Override
    public boolean permission(CommandSender csender) {
        return csender.hasPermission("guardian.lookup");
    }

    @Override
    public BaseCommand newInstance() {
        // TODO Auto-generated method stub
        return new SearchCommand();
    }
}
