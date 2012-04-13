package org.guardian.commands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GuardianCommandExecutor implements CommandExecutor {

    private List<BaseCommand> commands = new ArrayList<BaseCommand>();

    public GuardianCommandExecutor() {
        // Register commands
        commands.add(new HelpCommand());
        commands.add(new SearchCommand());
        commands.add(new PageCommand());
        commands.add(new RollbackCommand());
        commands.add(new ToolCommand());
    }

    /**
     * Command manager for Guardian
     *
     * @param sender - {@link CommandSender}
     * @param command - {@link Command}
     * @param label command name
     * @param args arguments
     */
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // If no arg provided for guardian command, set to help by default
        if (args.length == 0) {
            args = new String[]{"help"};
        }

        // Loop through commands to find match. Supports sub-commands
        outer:
        for (BaseCommand guardCmd : getCommands().toArray(new BaseCommand[0])) {
            String[] cmds = guardCmd.name.split(" ");
            for (int i = 0; i < cmds.length; i++) {
                if (i >= args.length || !cmds[i].equalsIgnoreCase(args[i])) {
                    continue outer;
                }
            }
            // TODO We need to implement something here that is creating new instances, we don't need instances in memory to have variables set
            if(guardCmd instanceof HelpCommand)
                return new HelpCommand().run(sender, args, label);
            if(guardCmd instanceof SearchCommand)
                return new SearchCommand().run(sender, args, label);
            if(guardCmd instanceof PageCommand)
                return new PageCommand().run(sender, args, label);
            if(guardCmd instanceof RollbackCommand)
                return new RollbackCommand().run(sender, args, label);
            if(guardCmd instanceof ToolCommand)
                return new RollbackCommand().run(sender, args, label);
        }

        // If no matches, just send help
        new HelpCommand().run(sender, args, label);
        return true;

    }

    /**
     * @return the commands
     */
    public List<BaseCommand> getCommands() {
        return commands;
    }
}
