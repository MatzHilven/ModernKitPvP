package me.matzhilven.modernkitpvp.utils;

import me.matzhilven.modernkitpvp.matchmaking.MatchMakingType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigUtils {

    public static void saveLocation(ConfigurationSection section, Location location) {
        if (section == null || location == null) return;

        section.set("world", location.getWorld().getName());
        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());
        section.set("yaw", location.getYaw());
        section.set("pitch", location.getPitch());
    }

    public static Location getLocation(ConfigurationSection section) {
        if (section == null || section.getString("world") == null) return null;

        return new Location(
                Bukkit.getWorld(section.getString("world")),
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                (float) section.getDouble("yaw"),
                (float) section.getDouble("pitch")
        );
    }

    public static void saveRegion(ConfigurationSection section, Region region) {

        if (!section.isConfigurationSection("corner-1")) section.createSection("corner-1");
        if (!section.isConfigurationSection("corner-2")) section.createSection("corner-2");

        saveLocation(section.getConfigurationSection("corner-1"), region.getCorner1());
        saveLocation(section.getConfigurationSection("corner-2"), region.getCorner2());
    }

    public static Region getRegion(ConfigurationSection section) {
        if (section == null) return null;

        return new Region(
                getLocation(section.getConfigurationSection("corner-1")),
                getLocation(section.getConfigurationSection("corner-2"))
        );
    }

    public static void saveInventory(ConfigurationSection section, HashMap<Integer, ItemStack> inventoryContents) {
        for (Map.Entry<Integer, ItemStack> entry : inventoryContents.entrySet()) {
            section.set(String.valueOf(entry.getKey()), entry.getValue());
        }
    }

    public static HashMap<Integer, ItemStack> getInventory(ConfigurationSection section) {
        if (section == null) return null;

        HashMap<Integer, ItemStack> inventoryContents = new HashMap<>();

        for (String slot : section.getKeys(false)) {
            inventoryContents.put(Integer.parseInt(slot), section.getItemStack(slot));
        }

        return inventoryContents;
    }

    public static void saveArmor(ConfigurationSection section, ItemStack[] armorContents) {
        for (ItemStack itemStack : armorContents) {
            if (itemStack == null) continue;
            section.set(itemStack.getType().toString(), itemStack);
        }
    }

    public static ItemStack[] getArmor(ConfigurationSection section) {
        if (section == null) return null;

        ItemStack[] inventoryContents = new ItemStack[4];

        int i = 0;
        for (String type : section.getKeys(false)) {
            inventoryContents[i++] = section.getItemStack(type);
        }

        return inventoryContents;
    }
}
