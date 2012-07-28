package org.guardian;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class Ask {
	
	private static Map<String, Boolean> askedPlayers = new HashMap<String, Boolean>();

	public static void askPlayer(CommandSender player) {
		if (!askedPlayers.containsKey(player.getName()))
			askedPlayers.put(player.getName(), false);
		player.sendMessage(ChatColor.RED + "Type '/guard confirm' to confirm the rollback or rebuild!");
	}

	public static Map<String, Boolean> getAskedPlayers() {
	    return askedPlayers;
    }
	
}
