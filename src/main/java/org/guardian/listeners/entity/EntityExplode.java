package org.guardian.listeners.entity;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.guardian.ActionType;
import org.guardian.entries.BlockEntry;
import org.guardian.listeners.LoggingListener;

import java.util.List;

public class EntityExplode extends LoggingListener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplode(final EntityExplodeEvent event) {

        final Entity entity = event.getEntity();
        final Location loc = event.getLocation();
        List<Block> blockList = event.blockList();

        switch (entity.getType()) {
            case FIREBALL:
                Fireball fireball = (Fireball) entity;

                if (fireball.getShooter() instanceof Ghast && guardian.getConf().isLogged(loc.getWorld().getName(), ActionType.ENTITY_GHAST_FIREBALL, ENVIRONMENT)) {
                    for (final Block block : blockList) {
                        consumer.queueEntry(new BlockEntry(ActionType.ENTITY_GHAST_FIREBALL, ENVIRONMENT, block.getLocation(), System.currentTimeMillis(), block.getTypeId(), block.getData(), 0, (byte) 0, PLUGIN));
                    }
                } else if (guardian.getConf().isLogged(loc.getWorld().getName(), ActionType.ENTITY_OTHER_EXPLODE, ENVIRONMENT)) {
                    for (final Block block : blockList) {
                        consumer.queueEntry(new BlockEntry(ActionType.ENTITY_OTHER_EXPLODE, ENVIRONMENT, block.getLocation(), System.currentTimeMillis(), block.getTypeId(), block.getData(), 0, (byte) 0, PLUGIN));
                    }
                }
                break;
            case PRIMED_TNT:

                // TODO log by cause
                if (guardian.getConf().isLogged(loc.getWorld().getName(), ActionType.ENTITY_TNT_EXPLODE, ENVIRONMENT)) {
                    for (final Block block : blockList) {
                        consumer.queueEntry(new BlockEntry(ActionType.ENTITY_TNT_EXPLODE, ENVIRONMENT, block.getLocation(), System.currentTimeMillis(), block.getTypeId(), block.getData(), 0, (byte) 0, PLUGIN));
                    }
                }
                break;
            case CREEPER:
                Creeper creeper = (Creeper) entity;
                LivingEntity target = creeper.getTarget();

                if (target != null && target instanceof Player
                        && guardian.getConf().isLogged(loc.getWorld().getName(), ActionType.ENTITY_CREEPER_EXPLODE_AS_PLAYER, ((Player) target).getName())) {

                    for (final Block block : blockList) {
                        consumer.queueEntry(new BlockEntry(ActionType.ENTITY_CREEPER_EXPLODE_AS_PLAYER, ((Player) target).getName(), block.getLocation(), System.currentTimeMillis(), block.getTypeId(), block.getData(), 0, (byte) 0, PLUGIN));
                    }

                } else if (guardian.getConf().isLogged(loc.getWorld().getName(), ActionType.ENTITY_CREEPER_EXPLODE, ENVIRONMENT)) {

                    for (final Block block : blockList) {
                        consumer.queueEntry(new BlockEntry(ActionType.ENTITY_CREEPER_EXPLODE, ENVIRONMENT, block.getLocation(), System.currentTimeMillis(), block.getTypeId(), block.getData(), 0, (byte) 0, PLUGIN));
                    }
                }
                break;
            case ENDER_DRAGON:
                if (guardian.getConf().isLogged(loc.getWorld().getName(), ActionType.ENTITY_ENDERDRAGON_DESTROY, ENVIRONMENT)) {
                    for (final Block block : blockList) {
                        consumer.queueEntry(new BlockEntry(ActionType.ENTITY_ENDERDRAGON_DESTROY, ENVIRONMENT, block.getLocation(), System.currentTimeMillis(), block.getTypeId(), block.getData(), 0, (byte) 0, PLUGIN));
                    }
                }
                break;
            // TODO Give Ender Crystals their own log type
            case ENDER_CRYSTAL:
            case UNKNOWN:
                if (guardian.getConf().isLogged(loc.getWorld().getName(), ActionType.ENTITY_OTHER_EXPLODE, ENVIRONMENT)) {
                    for (final Block block : blockList) {
                        consumer.queueEntry(new BlockEntry(ActionType.ENTITY_OTHER_EXPLODE, ENVIRONMENT, block.getLocation(), System.currentTimeMillis(), block.getTypeId(), block.getData(), 0, (byte) 0, PLUGIN));
                    }
                }
                break;
        }
    }
}
