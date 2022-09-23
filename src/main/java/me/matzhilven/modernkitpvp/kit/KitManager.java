package me.matzhilven.modernkitpvp.kit;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.utils.ConfigUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

public class KitManager {

    private final ModernKitPvP main;
    private final HashMap<String, Kit> kits;

    public KitManager(ModernKitPvP main) {
        this.main = main;
        this.kits = new HashMap<>();

        loadKits();
    }

    private void loadKits() {
        File dataFolder = new File(main.getDataFolder(), "kits");

        for (File file : dataFolder.listFiles((dir, name) -> name.endsWith(".yml"))) {
            YamlConfiguration yamlConfiguration = new YamlConfiguration();

            try {
                yamlConfiguration.load(file);

                Kit kit = new Kit(
                        yamlConfiguration.getString("id"),
                        yamlConfiguration.getString("name"),
                        yamlConfiguration.getItemStack("display_item"),
                        ConfigUtils.getInventory(yamlConfiguration.getConfigurationSection("inventory_contents")),
                        ConfigUtils.getArmor(yamlConfiguration.getConfigurationSection("armor_contents"))
                );

                kits.put(kit.getId(), kit);

            } catch (IOException | InvalidConfigurationException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void addKit(Kit kit) {
        kits.put(kit.getId(), kit);
        saveKit(kit);
    }

    private void saveKit(Kit kit) {
        String path = "kits" + File.separator + kit.getId() + ".yml";

        File file = new File(main.getDataFolder(), path);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        YamlConfiguration yamlConfiguration = new YamlConfiguration();

        try {
            yamlConfiguration.load(file);

            yamlConfiguration.set("id", kit.getId());
            yamlConfiguration.set("display_item", kit.getDisplayItem());

            if (kit.getName() != null) yamlConfiguration.set("name", kit.getName());

            if (kit.getInventoryContents() != null) {
                yamlConfiguration.createSection("inventory_contents");
                ConfigUtils.saveInventory(yamlConfiguration.getConfigurationSection("inventory_contents"), kit.getInventoryContents());
            }

            if (kit.getArmorContents() != null) {
                yamlConfiguration.createSection("armor_contents");
                ConfigUtils.saveArmor(yamlConfiguration.getConfigurationSection("armor_contents"), kit.getArmorContents());
            }

            yamlConfiguration.save(file);

        } catch (IOException | InvalidConfigurationException ioException) {
            ioException.printStackTrace();
        }
    }

    public Optional<Kit> getById(String id) {
        return Optional.ofNullable(kits.get(id));
    }

    public void reloadKits() {
        kits.clear();
        loadKits();
    }

    public HashMap<String, Kit> getKits() {
        return kits;
    }

    public boolean hasUnlocked(Player player, Kit kit) {
        // TODO: 22/09/2022
        return true;
    }

    public void unlockKit(Player player, Kit kit) {
        // TODO: 22/09/2022
    }
}
