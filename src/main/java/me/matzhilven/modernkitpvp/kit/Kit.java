package me.matzhilven.modernkitpvp.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Kit {

    private final String id;
    private final String name;
    private final ItemStack displayItem;
    private final HashMap<Integer, ItemStack> inventoryContents;
    private final ItemStack[] armorContents;

    public Kit(String id, String name, ItemStack displayItem, HashMap<Integer, ItemStack> inventoryContents, ItemStack[] armorContents) {
        this.id = id;
        this.name = name;
        this.displayItem = displayItem;
        this.inventoryContents = inventoryContents;
        this.armorContents = armorContents;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public HashMap<Integer, ItemStack> getInventoryContents() {
        return inventoryContents;
    }

    public ItemStack[] getArmorContents() {
        return armorContents;
    }

    public void apply(Player player) {

        player.setHealth(player.getHealthScale());
        player.setFoodLevel(20);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

        PlayerInventory inventory = player.getInventory();

        inventory.clear();

        inventory.setArmorContents(armorContents);

        for (Map.Entry<Integer, ItemStack> entry : inventoryContents.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue());
        }
    }
}
