package me.matzhilven.modernkitpvp.inventories;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class Menu implements InventoryHolder {

    protected final ModernKitPvP main = ModernKitPvP.getPlugin(ModernKitPvP.class);
    protected final Player player;
    protected final HashMap<Integer, String> slots;
    protected Inventory inventory;

    public Menu(Player player) {
        this.player = player;
        this.slots = new HashMap<>();
    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public void handleClick(InventoryClickEvent event) {
    }

    public void handleClose(InventoryCloseEvent event) {
    }

    public abstract void setMenuItems();

    public void open() {
        inventory = Bukkit.createInventory(this, getSlots(), StringUtils.colorize(getMenuName()));
        setMenuItems();

        player.openInventory(inventory);
    }

    public void setFiller(ItemStack glass, List<Integer> slots) {
        for (Integer i : slots) inventory.setItem(i, glass);
    }

    public long getFreeSlots() {
        return Arrays.stream(inventory.getContents()).filter(itemStack -> itemStack == null || itemStack.getType() == Material.AIR).count();
    }

    public Inventory getInventory() {
        return inventory;
    }
}
