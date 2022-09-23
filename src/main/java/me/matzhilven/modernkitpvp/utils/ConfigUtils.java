package me.matzhilven.modernkitpvp.utils;

import me.matzhilven.ModernKitPvP.kit.KitEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        if (section == null) return null;

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
            section.set(getType(itemStack), itemStack);
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

    public static void savePotionEffects(ConfigurationSection section, Set<KitEffect> potionEffects) {

        int i = 0;
        for (KitEffect effect : potionEffects) {
            section.set(i + ".effect", effect.getPotionEffectType().getName());
            section.set(i + ".amplifier", effect.getDuration());
            section.set(i + ".duration", effect.getDuration() == Integer.MAX_VALUE ? "permanent" : effect.getDuration());
            i++;
        }
    }

    public static Set<KitEffect> getPotionEffects(ConfigurationSection section) {
        if (section == null) return null;

        HashSet<KitEffect> kitEffects = new HashSet<>();

        for (String key : section.getKeys(false)) {
            kitEffects.add(new KitEffect(
                    PotionEffectType.getByName(section.getString(key + ".effect")),
                    section.getInt(key + ".amplifier"),
                    section.getString(key + ".duration").equalsIgnoreCase("permanent")
                            ? Integer.MAX_VALUE : section.getInt(key + ".duration")
            ));
        }

        return kitEffects;
    }

    private static String getType(ItemStack itemStack) {
        return itemStack.getType().name().split("_")[1].toLowerCase();
    }
}
