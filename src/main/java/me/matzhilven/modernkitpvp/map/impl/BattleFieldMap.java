package me.matzhilven.modernkitpvp.map.impl;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.kit.Kit;
import me.matzhilven.modernkitpvp.map.Map;
import me.matzhilven.modernkitpvp.utils.Region;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

public class BattleFieldMap extends Map {

    private final ModernKitPvP main;
    private final HashMap<UUID, Kit> kits;
    private final HashMap<UUID, Integer> killStreak;
    private final HashSet<UUID> activePlayers;
    private final int killStreakAmount;

    private Location spawnPoint;
    private Region spawnRegion;

    public BattleFieldMap(ModernKitPvP main, String id) {
        super(id);
        this.main = main;

        this.kits = new HashMap<>();
        this.killStreak = new HashMap<>();
        this.activePlayers = new HashSet<>();
        this.killStreakAmount = main.getConfig().getInt("kill-streak");
    }

    public BattleFieldMap(ModernKitPvP main, String id, String name, Location spawnPoint, Region region, Region spawnRegion) {
        super(id, name, region);
        this.main = main;
        this.spawnRegion = spawnRegion;
        this.spawnPoint = spawnPoint;

        this.kits = new HashMap<>();
        this.killStreak = new HashMap<>();
        this.activePlayers = new HashSet<>();
        this.killStreakAmount = main.getConfig().getInt("kill-streak");
    }

    public int getKillStreak(UUID uuid) {
        return killStreak.getOrDefault(uuid, 0);
    }

    public void onKill(Player player, Player killer) {
        removeKillStreak(player);

        int currentKillStreak = calculateKillStreak(killer);
        if (currentKillStreak % killStreakAmount == 0) {
            StringUtils.broadCast(main.getMessagesConfig().getString("kill-streak")
                    .replace("%player%", killer.getName())
                    .replace("%kills%", StringUtils.format(getKillStreak(killer.getUniqueId())))
            );
        }
    }

    private void removeKillStreak(Player player) {
        killStreak.put(player.getUniqueId(), 0);
    }

    private int calculateKillStreak(Player killer) {

        if (!killStreak.containsKey(killer.getUniqueId())) {
            killStreak.put(killer.getUniqueId(), 1);
        } else {
            killStreak.computeIfPresent(killer.getUniqueId(), (uuid, kills) -> kills + 1);
        }

        return killStreak.get(killer.getUniqueId());
    }

    public void setKit(Player player, Kit kit) {
        kits.put(player.getUniqueId(), kit);
    }

    public Optional<Kit> getKit(Player player) {
        activePlayers.add(player.getUniqueId());
        return Optional.ofNullable(kits.get(player.getUniqueId()));
    }

    public HashSet<UUID> getActivePlayers() {
        return activePlayers;
    }

    public Region getSpawnRegion() {
        return spawnRegion;
    }

    public void setSpawnRegion(Region spawnRegion) {
        this.spawnRegion = spawnRegion;
    }

    public void removeKit(Player player) {
        kits.remove(player.getUniqueId());
    }

    public Location getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(Location spawnPoint) {
        this.spawnPoint = spawnPoint;
    }
}
