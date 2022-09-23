package me.matzhilven.modernkitpvp.listeners;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import org.bukkit.event.Listener;

public class PlayerListener implements Listener {

    private final ModernKitPvP main;

    public PlayerListener(ModernKitPvP main) {
        this.main = main;

        main.getServer().getPluginManager().registerEvents(this, main);
    }


}
