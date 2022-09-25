package me.matzhilven.modernkitpvp.map;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.map.impl.BattleFieldMap;
import me.matzhilven.modernkitpvp.map.impl.MatchMakingMap;
import me.matzhilven.modernkitpvp.utils.ConfigUtils;
import me.matzhilven.modernkitpvp.utils.Region;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

public class MapManager {

    private final ModernKitPvP main;
    private final HashMap<String, MatchMakingMap> maps;
    private BattleFieldMap battleFieldMap;

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

                String id = yamlConfiguration.getString("id");

                if (id == null) continue;

                if (id.equalsIgnoreCase("battlefield")) {
                    battleFieldMap = new BattleFieldMap(main,
                            yamlConfiguration.getString("id"),
                            yamlConfiguration.getString("name"),
                            ConfigUtils.getLocation(yamlConfiguration.getConfigurationSection("spawn")),
                            ConfigUtils.getRegion(yamlConfiguration.getConfigurationSection("region")),
                            ConfigUtils.getRegion(yamlConfiguration.getConfigurationSection("spawn-region"))
                    );
                } else {
                    MatchMakingMap map = new MatchMakingMap(main,
                            yamlConfiguration.getString("id"),
                            yamlConfiguration.getString("name"),
                            ConfigUtils.getLocation(yamlConfiguration.getConfigurationSection("spawn")),
                            ConfigUtils.getRegion(yamlConfiguration.getConfigurationSection("region"))
                    );
                    maps.put(map.getId(), map);
                }

            } catch (IOException | InvalidConfigurationException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void addMap(Map map) {
        if (map instanceof BattleFieldMap) {
            battleFieldMap = (BattleFieldMap) map;
        } else {
            maps.put(map.getId(), (MatchMakingMap) map);
        }
        saveMap(map);
    }

    public Optional<Map> getById(String id) {
        if (id.equalsIgnoreCase("battlefield")) return Optional.ofNullable(battleFieldMap);
        return Optional.ofNullable(maps.get(id));
    }

    public Optional<? extends Map> getMap(Location location) {
        if (battleFieldMap != null && battleFieldMap.getRegion() != null &&
                (battleFieldMap.getRegion().contains(location))) {
            return Optional.of(battleFieldMap);
        }

        return maps.values().stream().filter(map -> map.getRegion() != null)
                .filter(map -> map.getRegion().contains(location))
                .findFirst();
    }

    public String getRegionName(Player player) {
        Location location = player.getLocation();

        Optional<MatchMakingMap> optionalMap = maps.values().stream().filter(map -> map.getRegion() != null)
                .filter(map -> map.getRegion().contains(location))
                .findFirst();

        if (optionalMap.isPresent()) return optionalMap.get().getId();

        if (battleFieldMap != null) {
            if (battleFieldMap.getRegion().contains(player.getLocation())) {
                return battleFieldMap.getId();
            } else if (battleFieldMap.getSpawnRegion().contains(player.getLocation())) {
                return battleFieldMap.getId() + "_spawn";
            }
        }

        return null;
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

            if (map instanceof BattleFieldMap) {
                Region spawnRegion = ((BattleFieldMap) map).getSpawnRegion();

                if (spawnRegion != null) {
                    yamlConfiguration.createSection("spawn-region");
                    ConfigUtils.saveRegion(yamlConfiguration.getConfigurationSection("spawn-region"), spawnRegion);
                }
            }

            yamlConfiguration.save(file);

        } catch (IOException | InvalidConfigurationException ioException) {
            ioException.printStackTrace();
        }
    }

    public HashMap<String, MatchMakingMap> getMaps() {
        return maps;
    }

    public BattleFieldMap getBattleFieldMap() {
        return battleFieldMap;
    }
}
