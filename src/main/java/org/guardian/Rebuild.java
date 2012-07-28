package org.guardian;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.guardian.entries.Entry;
import org.guardian.util.BukkitUtils;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Rebuild implements Runnable {

    private final Queue<Entry> entries;
    private final int taskId;
    private final CommandSender sender;
    @SuppressWarnings("unused")
    private final int size;

    public Rebuild(List<Entry> entries, CommandSender sender) {
        this.entries = new LinkedBlockingQueue<Entry>(entries);
        this.sender = sender;
        this.size = entries.size();
        int waits = 0;
        // Use the same setting, why have two? Same general concept
        if (Guardian.getInstance().getConf().askBeforeRollback) {
            boolean finished = false;
            while (!finished) {
                finished = Ask.getAskedPlayers().get(sender.getName());
                if (waits > 1000) {
                    taskId = 0;
                    return;
                }
                waits++;
            }
        }
        taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Guardian.getInstance(), this, 20, 20);
    }

    @Override
    public void run() {
        int counter = 0;
        while (!entries.isEmpty() && counter < 1000) {
            Entry entry = entries.poll();
            if (entry.isRollbacked()) {
                for (BlockState state : entry.getRebuildBlockStates()) {
                    state.update(true);
                }
            }
            counter++;
        }
        if (entries.isEmpty()) {
            BukkitUtils.sendMessage(sender, ChatColor.DARK_AQUA + "Rebuild complete!");
            Bukkit.getServer().getScheduler().cancelTask(taskId);
        }
    }
}
