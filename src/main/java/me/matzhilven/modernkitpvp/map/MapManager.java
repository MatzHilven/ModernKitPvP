package me.matzhilven.modernkitpvp.map;

import com.alessiodp.parties.api.interfaces.Party;
import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.map.impl.BattleFieldMap;
import me.matzhilven.modernkitpvp.map.impl.DuelMap;
import me.matzhilven.modernkitpvp.matchmaking.MatchMakingType;
import me.matzhilven.modernkitpvp.utils.ConfigUtils;
import me.matzhilven.modernkitpvp.utils.Region;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class MapManager {

    private final ModernKitPvP main;
    private final HashMap<String, DuelMap> maps;
    private final HashMap<UUID, UUID> invites;
    private final HashMap<UUID, String> chosenMaps;
    private BattleFieldMap battleFieldMap;

    public MapManager(ModernKitPvP main) {
        this.main = main;
        this.maps = new HashMap<>();
        this.invites = new HashMap<>();
        this.chosenMaps = new HashMap<>();

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
                    DuelMap map = new DuelMap(main,
                            yamlConfiguration.getString("id"),
                            yamlConfiguration.getString("name"),
                            ConfigUtils.getLocation(yamlConfiguration.getConfigurationSection("spawn-1")),
                            ConfigUtils.getLocation(yamlConfiguration.getConfigurationSection("spawn-2")),
                            ConfigUtils.getRegion(yamlConfiguration.getConfigurationSection("region")),
                            yamlConfiguration.getStringList("supported-types")
                                    .stream()
                                    .map(MatchMakingType::valueOf)
                                    .collect(Collectors.toList()),
                            yamlConfiguration.getString("author"));
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
            maps.put(map.getId(), (DuelMap) map);
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

        Optional<DuelMap> optionalMap = maps.values().stream().filter(map -> map.getRegion() != null)
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

            if (map.getRegion() != null) {
                yamlConfiguration.createSection("region");
                ConfigUtils.saveRegion(yamlConfiguration.getConfigurationSection("region"), map.getRegion());
            }

            if (map instanceof BattleFieldMap) {
                BattleFieldMap battleFieldMap = (BattleFieldMap) map;
                Region spawnRegion = battleFieldMap.getSpawnRegion();

                if (battleFieldMap.getSpawnPoint() != null) {
                    yamlConfiguration.createSection("spawn");
                    ConfigUtils.saveLocation(yamlConfiguration.getConfigurationSection("spawn"), battleFieldMap.getSpawnPoint());
                }

                if (spawnRegion != null) {
                    yamlConfiguration.createSection("spawn-region");
                    ConfigUtils.saveRegion(yamlConfiguration.getConfigurationSection("spawn-region"), spawnRegion);
                }
            } else {
                DuelMap duelMap = (DuelMap) map;

                if (duelMap.getSpawnPoint1() != null) {
                    yamlConfiguration.createSection("spawn-1");
                    ConfigUtils.saveLocation(yamlConfiguration.getConfigurationSection("spawn-1"), duelMap.getSpawnPoint1());
                }
                if (duelMap.getSpawnPoint2() != null) {
                    yamlConfiguration.createSection("spawn-2");
                    ConfigUtils.saveLocation(yamlConfiguration.getConfigurationSection("spawn-2"), duelMap.getSpawnPoint2());
                }

                yamlConfiguration.set("author", duelMap.getAuthor());

                yamlConfiguration.set("supported-types", duelMap.getSupportedTypes()
                        .stream()
                        .map(MatchMakingType::toString)
                        .collect(Collectors.toList()));
            }

            yamlConfiguration.save(file);

        } catch (IOException | InvalidConfigurationException ioException) {
            ioException.printStackTrace();
        }
    }

    public List<DuelMap> getAvailableMaps() {
        return maps.values().stream().filter(matchMakingMap -> !matchMakingMap.isActive()).collect(Collectors.toList());
    }

    public Optional<DuelMap> getAvailableMap(MatchMakingType type) {
        return maps.values().stream()
                .filter(matchMakingMap -> !matchMakingMap.isActive())
                .filter(matchMakingMap -> matchMakingMap.getSupportedTypes().contains(type))
                .findFirst();
    }

    public Optional<DuelMap> getCurrentGame(Player player) {
        return maps.values().stream().filter(DuelMap::isActive).filter(map -> map.isIn(player)).findFirst();
    }

    public BattleFieldMap getBattleFieldMap() {
        return battleFieldMap;
    }

    public HashMap<UUID, UUID> getInvites() {
        return invites;
    }


    public HashMap<UUID, String> getChosenMaps() {
        return chosenMaps;
    }

    public void startMatchMaking(Party hostParty, Party party) {
        Optional<Map> optionalMap = getById(chosenMaps.get(hostParty.getId()));
        if (!optionalMap.isPresent()) return;

        DuelMap duelMap = (DuelMap) optionalMap.get();
        duelMap.start(hostParty, party);
    }

    public void startDuel(DuelMap map, List<UUID> players) {
        map.start(players);
    }
}
