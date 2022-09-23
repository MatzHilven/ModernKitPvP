package me.matzhilven.modernkitpvp.listeners;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.inventories.Menu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

public class InventoryListener implements Listener {

    public InventoryListener(ModernKitPvP main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (event.getCurrentItem() == null) return;
        if (!(holder instanceof Menu)) return;
        event.setCancelled(true);
        ((Menu) holder).handleClick(event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (!(holder instanceof Menu)) return;
        ((Menu) holder).handleClose(event);
    }
}
