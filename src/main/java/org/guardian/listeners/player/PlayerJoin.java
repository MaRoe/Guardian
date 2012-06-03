package org.guardian.listeners.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.guardian.entries.PlayerEntry;
import org.guardian.listeners.LoggingListener;

public class PlayerJoin extends LoggingListener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        PlayerEntry tempEntry = new PlayerEntry();
        tempEntry.setPlayerLogin(true);
        tempEntry.setName(event.getPlayer().getName());
        tempEntry.setLastLogin(System.currentTimeMillis() / 1000);
        tempEntry.setIp(event.getPlayer().getAddress().toString().replace("'", "\\'"));
        consumer.queueEntry(tempEntry);
    }
}
