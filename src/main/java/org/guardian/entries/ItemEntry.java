package org.guardian.entries;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.guardian.ActionType;
import org.guardian.util.BukkitUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemEntry extends DataEntry {

    protected int typeId, data, amount;
    protected Map<Enchantment, Integer> enchantments;

    protected ItemEntry() { }
    
    public ItemEntry(ActionType action, String playerName, Location loc, long date, ItemStack item, String pluginName) {
        super(action, playerName, loc, loc.getWorld().getName(), date, pluginName);
        typeId = item.getTypeId();
        data = item.getDurability();
        amount = item.getAmount();
        enchantments = item.getEnchantments();
    }

    public int getTypeId() {
        return typeId;
    }

    public int getData() {
        return data;
    }

    public int getAmount() {
        return amount;

    }

    /*public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }*/
    
    public Map<String, Integer> getEnchantments() {
        Map<String, Integer> tempMap = new HashMap<String, Integer>();
        for(Enchantment tempEnchant : enchantments.keySet()) {
            tempMap.put(tempEnchant.getName(), enchantments.get(tempEnchant));
        }
        return tempMap;
    }
    
    public void setEnchantments(Map<String, Integer> paramMap) {
        Map<Enchantment, Integer> tempMap = new HashMap<Enchantment, Integer>();
        for(String tempString : paramMap.keySet()) {
            tempMap.put(Enchantment.getByName(tempString), paramMap.get(tempString));
        }
        enchantments = tempMap;
    }

    public ItemStack getItemStack() {
        ItemStack stack = new ItemStack(typeId, amount, (short) data);
        stack.addEnchantments(enchantments);
        return stack;
    }

    public String getMessage() {
        final StringBuilder msg = new StringBuilder();
        if (date > 0) {
            msg.append(dateFormat.format(date));
            msg.append(" ");
        }
        if (playerName != null) {
            msg.append(playerName);
            msg.append(" ");
        }
        if (action == ActionType.INVENTORY_TAKE) {
            msg.append("took ").append(amount).append("x ").append(BukkitUtils.materialName(typeId, (byte) data));
            msg.append(" ");
        } else {
            msg.append("put in ").append(amount).append("x ").append(BukkitUtils.materialName(typeId, (byte) data));
            msg.append(" ");
        }
        if (loc != null) {
            msg.append(" at ");
            msg.append(loc.getBlockX());
            msg.append(":");
            msg.append(loc.getBlockY());
            msg.append(":");
            msg.append(loc.getBlockZ());
        }
        return msg.toString();
    }

    public List<BlockState> getRollbackBlockStates() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<BlockState> getRebuildBlockStates() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
