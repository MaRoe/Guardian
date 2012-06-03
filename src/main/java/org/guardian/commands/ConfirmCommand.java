package org.guardian.commands;

import org.bukkit.command.CommandSender;
import org.guardian.Ask;

public class ConfirmCommand extends BaseCommand {
	
	public ConfirmCommand() {
		name = "confirm";
		usage = "";
		minArgs = 1;
	}

	@Override
    public boolean execute() {
    	if (Ask.getAskedPlayers().containsKey(sender.getName()) && !Ask.getAskedPlayers().get(sender.getName())) {
    		Ask.getAskedPlayers().remove(sender.getName());
    		Ask.getAskedPlayers().put(sender.getName(), true);
    		return true;
    	}
    	return false;
    }

	@Override
    public boolean permission(CommandSender csender) {
	    return Ask.getAskedPlayers().containsKey(csender.getName());
    }

	@Override
    public BaseCommand newInstance() {
	    return new ConfirmCommand();
    }
	
}
