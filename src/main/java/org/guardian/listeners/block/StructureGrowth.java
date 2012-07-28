package org.guardian.listeners.block;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.StructureGrowEvent;
import org.guardian.ActionType;
import org.guardian.entries.BlockEntry;
import org.guardian.listeners.LoggingListener;

import java.util.List;

public class StructureGrowth extends LoggingListener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStructureGrow(final StructureGrowEvent event) {

        final Player player = event.getPlayer();
        final Location loc = event.getLocation();
        List<BlockState> blockList = event.getBlocks();

        if (player != null && guardian.getConf().isLogged(loc.getWorld().getName(), ActionType.WORLD_STRUCTURE_GROW_AS_PLAYER, player.getName())) {
            for (final BlockState block : blockList) {
                consumer.queueEntry(new BlockEntry(ActionType.WORLD_STRUCTURE_GROW_AS_PLAYER, player.getName(), loc, System.currentTimeMillis(), 0, (byte) 0, block.getTypeId(), block.getData().getData(), PLUGIN));
            }
        } else if (guardian.getConf().isLogged(loc.getWorld().getName(), ActionType.WORLD_STRUCTURE_GROW, ENVIRONMENT)) {
            for (final BlockState block : blockList) {
                consumer.queueEntry(new BlockEntry(ActionType.WORLD_STRUCTURE_GROW, ENVIRONMENT, loc, System.currentTimeMillis(), 0, (byte) 0, block.getTypeId(), block.getData().getData(), PLUGIN));
            }
        }
    }
}