package org.guardian.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.guardian.util.BukkitUtils;

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
        BaseCommand guardCmd;
        BaseCommand[] guardCmdArray = commands.toArray(new BaseCommand[0]);
        int index = 0;
        String[] tempArgs = args;
        
        while (index < guardCmdArray.length && tempArgs.length > 0) {
            guardCmd = guardCmdArray[index];
            if(tempArgs[0].equalsIgnoreCase(guardCmd.name)) {
                if(guardCmd.subCommands.size() > 0 && tempArgs.length > 1) {
                    guardCmdArray = guardCmd.subCommands.toArray(new BaseCommand[0]);
                    index = 0;
                    tempArgs = (String[]) ArrayUtils.remove(tempArgs, 0);
                } else {
                    tempArgs = (String[]) ArrayUtils.remove(tempArgs, 0);
                    return guardCmd.newInstance().run(sender, tempArgs, label);
                }
            } else {
                index++;
            }
        }
        
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
