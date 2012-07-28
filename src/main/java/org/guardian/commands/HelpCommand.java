package org.guardian.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.guardian.util.BukkitUtils;

import java.util.List;

public class HelpCommand extends BaseCommand {

    public HelpCommand() {
        name = "help";
        maxArgs = 1;
        minArgs = 0;
        usage = "<- lists all Guardian commands";
    }

    @Override
    public boolean execute() {
        // General help
        if (args.isEmpty()) {
            BukkitUtils.sendMessage(sender, ChatColor.AQUA + cleanTitle("Guardian v" + plugin.getDescription().getVersion(), "="));
            BukkitUtils.sendMessage(sender, "Type /guardian help <command> for more info on that command");
            List<BaseCommand> var = plugin.getCommandExecutor().getCommands();
            for (BaseCommand cmd : var.toArray(new BaseCommand[var.size()])) {
                if (cmd.permission(sender)) {
                    BukkitUtils.sendMessage(sender, "- " + ChatColor.GREEN + "/" + usedCommand + " " + cmd.name + ChatColor.GREEN + " " + cmd.usage);
                }
            }
        } // Command-specific help
        else {
            List<BaseCommand> var = plugin.getCommandExecutor().getCommands();
            for (BaseCommand cmd : var.toArray(new BaseCommand[var.size()])) {
                if (cmd.permission(sender) && cmd.name.equalsIgnoreCase(args.get(0))) {
                    BukkitUtils.sendMessage(sender, "---------------------- Guardian - " + cmd.name);
                    BukkitUtils.sendMessage(sender, "- " + ChatColor.GREEN + "/" + usedCommand + " " + cmd.name + ChatColor.GREEN + " " + cmd.usage);
                    cmd.sender = sender;
                    cmd.moreHelp();
                    return true;
                }
            }
            BukkitUtils.sendMessage(sender, "No command found by that name");
        }
        return true;
    }

    @Override
    public void moreHelp() {
        BukkitUtils.sendMessage(sender, ChatColor.AQUA + "Shows all Guardian commands");
        BukkitUtils.sendMessage(sender, ChatColor.RED + "Type " + ChatColor.GRAY + "/guardian help <command>" + ChatColor.RED + " for help on that command");
    }

    @Override
    public boolean permission(CommandSender csender) {
        return true;
    }

    @Override
    public BaseCommand newInstance() {
        return new HelpCommand();
    }
    
    public String cleanTitle(String title, String fill) { // Formats a string with a provided title and padding and centers title
        int chatWidthMax = 53; // Vanilla client line character max
        int titleWidth = title.length() + 2; // Title's character width with 2 spaces padding
        int fillWidth = (int) ((chatWidthMax - titleWidth) / 2D); // Fill string calculation for padding either side
        String cleanTitle = "";
        
        for(int i = 0; i < fillWidth; i++)
            cleanTitle += fill;
        cleanTitle += " " + title + " ";
        for(int i = 0; i < fillWidth; i++)
            cleanTitle += fill;
        
        return cleanTitle;
    }
}
