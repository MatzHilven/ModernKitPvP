package me.matzhilven.modernkitpvp.map;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.utils.ConfigUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

public class MapManager {

    private final ModernKitPvP main;
    private final HashMap<String, Map> maps;

    public MapManager(ModernKitPvP main) {
        this.main = main;
        this.maps = new HashMap<>();

        loadMaps();
    }

    private void loadMaps() {
        File dataFolder = new File(main.getDataFolder(), "maps");

        for (File file : dataFolder.listFiles((dir, name) -> name.endsWith(".yml"))) {
            YamlConfiguration yamlConfiguration = new YamlConfiguration();

            try {
                yamlConfiguration.load(file);

                Map map = new Map(
                        yamlConfiguration.getString("id"),
                        yamlConfiguration.getString("name"),
                        ConfigUtils.getLocation(yamlConfiguration.getConfigurationSection("spawn")),
                        ConfigUtils.getRegion(yamlConfiguration.getConfigurationSection("region"))
                );

                maps.put(map.getId(), map);

            } catch (IOException | InvalidConfigurationException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void addMap(Map map) {
        maps.put(map.getId(), map);
        saveMap(map);
    }

    public Optional<Map> getById(String id) {
        return Optional.ofNullable(maps.get(id));
    }

    public void saveMap(Map map) {
        String path = "maps" + File.separator + map.getId() + ".yml";

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

            yamlConfiguration.set("id", map.getId());
            if (map.getName() != null) yamlConfiguration.set("name", map.getName());

            if (map.getSpawnPoint() != null) {
                yamlConfiguration.createSection("spawn");
                ConfigUtils.saveLocation(yamlConfiguration.getConfigurationSection("spawn"), map.getSpawnPoint());
            }

            if (map.getRegion() != null) {
                yamlConfiguration.createSection("region");
                ConfigUtils.saveRegion(yamlConfiguration.getConfigurationSection("region"), map.getRegion());
            }

            yamlConfiguration.save(file);

        } catch (IOException | InvalidConfigurationException ioException) {
            ioException.printStackTrace();
        }
    }
}
