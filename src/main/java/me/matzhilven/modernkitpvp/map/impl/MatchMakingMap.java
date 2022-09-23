package me.matzhilven.modernkitpvp.map.impl;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.kit.Kit;
import me.matzhilven.modernkitpvp.map.Map;
import me.matzhilven.modernkitpvp.utils.Region;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class MatchMakingMap {

    private final ModernKitPvP main;
    private final Map map;
    private final HashMap<UUID, Kit> kits;
    private final HashMap<UUID, Integer> team1;
    private final HashMap<UUID, Integer> team2;

    public MatchMakingMap(ModernKitPvP main, Map map) {
        this.main = main;
        this.map = map;

        this.team1 = new HashMap<>();
        this.team2 = new HashMap<>();
        this.kits = new HashMap<>();
    }

    public String getId() {
        return map.getId();
    }

    public String getName() {
        return map.getName();
    }

    public Location getSpawnPoint() {
        return map.getSpawnPoint();
    }

    public Region getRegion() {
        return map.getRegion();
    }

    public int getKillStreak(UUID uuid) {
        if (team1.containsKey(uuid)) return team1.get(uuid);

        return team2.getOrDefault(uuid, 0);
    }

    public void addKill(UUID uuid) {
        if (team1.containsKey(uuid)) team1.computeIfPresent(uuid, (uuid1, integer) -> integer + 1);
        team2.computeIfPresent(uuid, (uuid1, integer) -> integer + 1);
    }

    public void onKill(Player player, Player killer) {
        calculateKillStreak(killer);
    }

    private void calculateKillStreak(Player killer) {

        addKill(killer.getUniqueId());
        int currentKillStreak = getKillStreak(killer.getUniqueId());

        StringUtils.sendMessage(killer, main.getMessagesConfig().getString("kill-streak")
                .replace("%amount%", StringUtils.format(currentKillStreak))
        );

    }

    public void addPlayer(Player player) {
        player.setHealth(player.getHealthScale());
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

        player.teleport(getSpawnPoint());
    }

    public void setKit(Player player, Kit kit) {
        kits.put(player.getUniqueId(), kit);
    }

    public Optional<Kit> getKit(Player player) {
        return Optional.ofNullable(kits.get(player.getUniqueId()));
    }

    public void reset() {
        kits.clear();
        team1.clear();
        team2.clear();
    }
}
