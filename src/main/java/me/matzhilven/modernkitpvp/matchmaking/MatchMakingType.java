package me.matzhilven.modernkitpvp.matchmaking;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.Optional;

public enum MatchMakingType {
    ONE_VS_ONE("1v1", 2, 2),
    TWO_VS_TWO("2v2", 2, 4),
    TREE_VS_TREE("3v3", 4, 6),
    FOUR_VS_FOUR("4v4", 6, 8),
    EIGHT_VS_EIGHT("8v8", 8, 16);

    private final String name;
    private final int maxPlayers;
    private int minPlayers;

    MatchMakingType(String name, int minPlayers, int maxPlayers) {
        this.name = name;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    static {
        FileConfiguration config = ModernKitPvP.getPlugin(ModernKitPvP.class).getConfig();

        for (String key : config.getConfigurationSection("minimum-players").getKeys(false)) {
            MatchMakingType matchMakingType = valueOf(key);
            matchMakingType.setMinPlayers(config.getInt("minimum-players." + key));
        }
    }

    public static Optional<MatchMakingType> byName(String name) {
        return Arrays.stream(values()).filter(type -> type.getName().equals(name)).findFirst();
    }

    public String getName() {
        return name;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}
