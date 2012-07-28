package org.guardian.listeners.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.guardian.ActionType;
import org.guardian.entries.BlockEntry;
import org.guardian.listeners.LoggingListener;

public class EntityChangeBlock extends LoggingListener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityChangeBlock(final EntityChangeBlockEvent event) {
        final Entity entity = event.getEntity();
        final Block block = event.getBlock();
        final Location loc = block.getLocation();
        final Material to = event.getTo();


        if (entity instanceof Enderman) {
            // TODO Get data of new block

            ActionType actionType = ActionType.ENTITY_ENDERMEN_PLACE;
            if (block.getTypeId() != 0) actionType = ActionType.ENTITY_ENDERMEN_PICKUP;

            if (guardian.getConf().isLogged(loc.getWorld().getName(), actionType, ENVIRONMENT)) {
                consumer.queueEntry(new BlockEntry(actionType, ENVIRONMENT, loc, System.currentTimeMillis(), block.getTypeId(), block.getData(), to.getId(), (byte) 0, PLUGIN));
            }
        }
    }
}
