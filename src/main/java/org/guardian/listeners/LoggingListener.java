package org.guardian.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.guardian.Consumer;
import org.guardian.Guardian;

public abstract class LoggingListener implements Listener {

    protected final String ENVIRONMENT = "Environment";
    protected final String PLUGIN = "Guardian";
    protected final Guardian guardian = Guardian.getInstance();
    protected final Consumer consumer = Guardian.getInstance().getConsumer();

    protected LoggingListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, guardian);
    }
}
