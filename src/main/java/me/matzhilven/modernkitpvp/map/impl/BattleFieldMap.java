package me.matzhilven.modernkitpvp.map.impl;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.kit.Kit;
import me.matzhilven.modernkitpvp.map.Map;
import me.matzhilven.modernkitpvp.utils.Region;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class BattleFieldMap {

    private final ModernKitPvP main;
    private final Map map;
    private final HashMap<UUID, Kit> kits;
    private final HashMap<UUID, Integer> killStreak;

    public BattleFieldMap(ModernKitPvP main, Map map) {
        this.main = main;
        this.map = map;

        this.kits = new HashMap<>();
        this.killStreak = new HashMap<>();
    }

    public String getId() {
        return map.getId();
    }

    public String getName() {
        return map.getName();
    }

    public Region getRegion() {
        return map.getRegion();
    }

    public int getKillStreak(UUID uuid) {
        return killStreak.getOrDefault(uuid, 0);
    }

    public void onKill(Player player, Player killer) {
        removeKillStreak(player);

        int currentKillStreak = calculateKillStreak(killer);
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
        return Optional.ofNullable(kits.get(player.getUniqueId()));
    }
}
