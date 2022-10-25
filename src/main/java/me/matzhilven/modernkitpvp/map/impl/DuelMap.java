package me.matzhilven.modernkitpvp.map.impl;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.Party;
import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.inventories.impl.KitSelectorMenu;
import me.matzhilven.modernkitpvp.map.Map;
import me.matzhilven.modernkitpvp.map.MapType;
import me.matzhilven.modernkitpvp.matchmaking.MatchMakingType;
import me.matzhilven.modernkitpvp.tasks.CountdownTask;
import me.matzhilven.modernkitpvp.utils.Region;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class DuelMap extends Map {

    private final ModernKitPvP main;
    private final HashMap<Integer, UUID> parties;
    private final HashMap<UUID, Integer> team1;
    private final HashMap<UUID, Integer> team2;
    private final List<MatchMakingType> supportedTypes;
    private final String author;
    private final List<UUID> players;
    private final int killStreakAmount;
    private int ready;
    private Location spawnPoint1;
    private Location spawnPoint2;
    private boolean active;
    private boolean started;

    public DuelMap(ModernKitPvP main, String id) {
        super(id);

        this.main = main;

        this.author = null;

        this.team1 = new HashMap<>();
        this.team2 = new HashMap<>();
        this.parties = new HashMap<>();
        this.players = new ArrayList<>();
        this.supportedTypes = Arrays.asList(MatchMakingType.values());

        this.killStreakAmount = main.getConfig().getInt("kill-streak");

        this.active = false;
        this.started = false;
    }

    public DuelMap(ModernKitPvP main, String id, String name, Location spawnPoint1, Location spawnPoint2,
                   Region region, List<MatchMakingType> supportedTypes, String author) {
        super(id, name, region);

        this.main = main;
        this.spawnPoint1 = spawnPoint1;
        this.spawnPoint2 = spawnPoint2;
        this.supportedTypes = supportedTypes;
        this.author = author;

        this.team1 = new HashMap<>();
        this.team2 = new HashMap<>();
        this.parties = new HashMap<>();
        this.players = new ArrayList<>();

        this.killStreakAmount = main.getConfig().getInt("kill-streak");

        this.active = false;
        this.started = false;
    }

    public String getAuthor() {
        return author;
    }

    public void onKill(Player player, Player killer) {
        if (killer != null) {
            int currentKillStreak = calculateKillStreak(killer);
            if (currentKillStreak % killStreakAmount == 0) {
                StringUtils.broadCast(main.getMessagesConfig().getString("kill-streak")
                        .replace("%player%", killer.getName())
                        .replace("%kills%", StringUtils.format(getKillStreak(killer.getUniqueId())))
                );
            }
        }

        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(getRegion().getCenter());

        team1.remove(player.getUniqueId());
        team2.remove(player.getUniqueId());

        checkTeams();
    }

    private void checkTeams() {
        if (parties.size() != 0) {

            Party winningParty = null;

            if (team1.size() == 0) {
                winningParty = Parties.getApi().getParty(parties.get(2));
            } else if (team2.size() == 0) {
                winningParty = Parties.getApi().getParty(parties.get(1));
            }

            if (winningParty == null) return;

            Party finalWinningParty = winningParty;
            getOnlinePlayers().forEach(player -> {
                StringUtils.sendMessage(player, main.getMessagesConfig().getString("win-message-party")
                        .replace("%party%", finalWinningParty.getName()));
                leave(player, true);
            });
        } else {

            if (team1.size() != 0 && team2.size() != 0) return;

            String winner = team1.size() == 0 ? "2" : "1";

            getOnlinePlayers().forEach(player -> {
                StringUtils.sendMessage(player, main.getMessagesConfig().getString("win-message")
                        .replace("%team%", winner));
                leave(player, true);
            });
        }


        reset();
    }

    private int calculateKillStreak(Player killer) {
        addKill(killer.getUniqueId());
        return getKillStreak(killer.getUniqueId());
    }

    public void addKill(UUID uuid) {
        if (team1.containsKey(uuid)) {
            team1.computeIfPresent(uuid, (uuid1, integer) -> integer + 1);
            return;
        }

        team2.computeIfPresent(uuid, (uuid1, integer) -> integer + 1);
    }

    public int getKillStreak(UUID uuid) {
        if (team1.containsKey(uuid)) return team1.get(uuid);
        return team2.getOrDefault(uuid, 0);
    }

    public List<Player> getOnlinePlayers() {
        return players.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public void start(Party party1, Party party2) {
        setActive(true);
        ready = 0;
        setUpPlayers(party1, 1);
        setUpPlayers(party2, 2);
    }

    public void start(List<UUID> players) {
        setActive(true);
        ready = 0;
        setUpPlayers(players);
    }

    private void setUpPlayers(List<UUID> players) {
        players.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;

            setUpPlayer(player);

            if (team1.size() > team2.size()) {
                team2.put(uuid, 0);
            } else {
                team1.put(uuid, 0);
            }

            this.players.add(player.getUniqueId());

            player.teleport(getSpawnPoint(player));
            new KitSelectorMenu(player, MapType.MATCHMAKING).open();
        });

    }

    private void setUpPlayers(Party party, int team) {
        if (team == 1) {
            parties.put(1, party.getId());
        } else {
            parties.put(2, party.getId());
        }

        party.getOnlineMembers().forEach(partyPlayer -> {
            Player player = Bukkit.getPlayer(partyPlayer.getPlayerUUID());
            if (player == null) return;

            setUpPlayer(player);

            if (team == 1) {
                team1.put(player.getUniqueId(), 0);
            } else {
                team2.put(player.getUniqueId(), 0);
            }

            players.add(player.getUniqueId());

            player.teleport(getSpawnPoint(player));
            new KitSelectorMenu(player, MapType.MATCHMAKING).open();
        });
    }

    private void setUpPlayer(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(player.getHealthScale());
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));


        if (main.getMapManager().getBattleFieldMap() != null)
            main.getMapManager().getBattleFieldMap().getActivePlayers().remove(player.getUniqueId());
    }

    private Location getSpawnPoint(Player player) {
        return team1.containsKey(player.getUniqueId()) ? spawnPoint1 : spawnPoint2;
    }

    public void reset() {
        team1.clear();
        team2.clear();
        parties.clear();
        players.clear();

        setActive(false);
        setStarted(false);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isIn(Player player) {
        return team1.containsKey(player.getUniqueId()) || team2.containsKey(player.getUniqueId());
    }

    public Location getSpawnPoint1() {
        return spawnPoint1;
    }

    public void setSpawnPoint1(Location spawnPoint1) {
        this.spawnPoint1 = spawnPoint1;
    }

    public Location getSpawnPoint2() {
        return spawnPoint2;
    }

    public void setSpawnPoint2(Location spawnPoint2) {
        this.spawnPoint2 = spawnPoint2;
    }

    public boolean hasStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        if (started) {
            getOnlinePlayers().forEach(player -> {
                player.sendTitle(StringUtils.colorize(main.getMessagesConfig().getString("title").replace("%map%", getName())),
                        StringUtils.colorize(main.getMessagesConfig().getString("subtitle").replace("%author%", author)),
                        10, 70, 20);
            });
        }
        this.started = started;
    }

    public void chooseKit() {
        if (++this.ready == players.size()) {
            new CountdownTask(main, this).runTaskTimer(main, 0L, 20L);
        }
    }

    public void leave(Player player, boolean ended) {
        if (!ended) {
            team1.remove(player.getUniqueId());
            team2.remove(player.getUniqueId());
            checkTeams();
        }

        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(player.getHealthScale());
        player.setFoodLevel(20);

        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));

        if (main.getMapManager().getBattleFieldMap() != null)
            player.teleport(main.getMapManager().getBattleFieldMap().getSpawnPoint());
    }

    public List<MatchMakingType> getSupportedTypes() {
        return supportedTypes;
    }
}
