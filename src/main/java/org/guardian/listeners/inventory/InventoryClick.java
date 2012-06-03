package org.guardian.listeners.inventory;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.BlockState;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.guardian.ActionType;
import org.guardian.entries.ItemEntry;
import org.guardian.listeners.LoggingListener;

public class InventoryClick extends LoggingListener {

    private final Map<HumanEntity, InventoryClickState> containers = new HashMap<HumanEntity, InventoryClickState>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(final InventoryClickEvent event) {
        InventoryClickState lastState = containers.get(event.getWhoClicked());
        if (!(event.getInventory().getHolder() instanceof BlockState))
            return;
        BlockState eventBlock = (BlockState) event.getInventory().getHolder();
        if (event.getSlot() == event.getRawSlot()) {
            // Clicked Top Inventory
            if (lastState != null) {
                if (lastState.rawSlot != lastState.slot) {
                    // Last Click Was Bottom Inventory
                    if (event.isLeftClick()) {
                        consumer.queueEntry(new ItemEntry(ActionType.INVENTORY_ADD, event.getWhoClicked().getName(), eventBlock.getLocation(), System.currentTimeMillis(), event.getCursor(), PLUGIN));
                    }
                }
            }
        } else {
            // Clicked Bottom Inventory
            if (lastState != null) {
                if (lastState.rawSlot == lastState.slot) {
                    if (event.isLeftClick() && (event.getCursor() != null)) {
                        consumer.queueEntry(new ItemEntry(ActionType.INVENTORY_TAKE, event.getWhoClicked().getName(), eventBlock.getLocation(), System.currentTimeMillis(), event.getCursor(), PLUGIN));
                    }
                }
            }
        }
        containers.put(event.getWhoClicked(), new InventoryClickState(event.getCurrentItem(), event.getCursor(), event.isLeftClick(), event.isRightClick(), event.isShiftClick(), event.getRawSlot(), event.getSlot()));
    }

    public static class InventoryClickState {
        public final ItemStack currentItem;
        public final ItemStack cursorItem;
        public final boolean leftClick;
        public final boolean rightClick;
        public final boolean shiftClick;
        public final int rawSlot;
        public final int slot;

        private InventoryClickState(ItemStack paramCurrentItem, ItemStack paramCursorItem, boolean paramLeftClick, boolean paramRightClick, boolean paramShiftClick, int paramRawSlot, int paramSlot) {
            currentItem = paramCurrentItem;
            cursorItem = paramCursorItem;
            leftClick = paramLeftClick;
            rightClick = paramRightClick;
            shiftClick = paramShiftClick;
            rawSlot = paramRawSlot;
            slot = paramSlot;
        }
    }

}
