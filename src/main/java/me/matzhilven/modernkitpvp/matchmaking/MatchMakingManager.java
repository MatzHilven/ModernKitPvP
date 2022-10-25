package me.matzhilven.modernkitpvp.matchmaking;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.map.impl.DuelMap;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import me.matzhilven.modernkitpvp.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class MatchMakingManager {

    private final ModernKitPvP main;
    private final HashMap<MatchMakingType, Queue<UUID>> queuedPlayers;
    private final HashMap<MatchMakingType, BukkitTask> queueTasks;
    private final long queueTime;

    public MatchMakingManager(ModernKitPvP main) {
        this.main = main;
        this.queuedPlayers = new HashMap<>();
        this.queueTasks = new HashMap<>();
        this.queueTime = TimeUtils.calculate(main.getConfig().getString("queue-time")) * 20;

        Bukkit.getScheduler().runTaskTimer(main, () -> {
            queuedPlayers.values().stream()
                    .flatMap(Collection::stream)
                    .map(Bukkit::getPlayer)
                    .filter(Objects::nonNull)
                    .forEach(player -> getType(player).ifPresent(type ->
                            StringUtils.sendMessage(player, main.getMessagesConfig().getString("reminder")
                                    .replace("%type%", type.getName())))
                    );
        }, 20L * 60L, 20 * 60L);

        setup();
    }

    private void setup() {
        for (MatchMakingType value : MatchMakingType.values()) {
            queuedPlayers.put(value, new PriorityQueue<>());
        }
    }

    public void removePlayer(UUID uuid) {
        queuedPlayers.values().forEach(queuedPlayers -> queuedPlayers.remove(uuid));
    }

    public void addPlayer(MatchMakingType matchMakingType, UUID uuid) {
        removePlayer(uuid);

        queuedPlayers.computeIfPresent(matchMakingType, (type, queue) -> new PriorityQueue<>(queue) {{
            add(uuid);
        }});

        Queue<UUID> queue = queuedPlayers.get(matchMakingType);
        Optional<DuelMap> optionalMatchMakingMap = main.getMapManager().getAvailableMap(matchMakingType);

        if (queue.size() >= matchMakingType.getMinPlayers() && optionalMatchMakingMap.isPresent()) {
            if (queue.size() == matchMakingType.getMaxPlayers())
                start(queue, optionalMatchMakingMap.get(), matchMakingType);
            else
                startRunnable(queue, matchMakingType);
        }
    }

    private void startRunnable(Queue<UUID> queue, MatchMakingType matchMakingType) {
        if (queueTasks.containsKey(matchMakingType)) queueTasks.get(matchMakingType).cancel();

        queueTasks.put(matchMakingType, Bukkit.getScheduler().runTaskLater(main, () -> {
            Optional<DuelMap> optionalMatchMakingMap = main.getMapManager().getAvailableMap(matchMakingType);

            if (!optionalMatchMakingMap.isPresent()) {
                startRunnable(queue, matchMakingType);
                return;
            }

            if (queue.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).count() < matchMakingType.getMinPlayers()) return;

            start(queue, optionalMatchMakingMap.get(), matchMakingType);

        }, queueTime));
    }

    private void start(Queue<UUID> queue, DuelMap duelMap, MatchMakingType matchMakingType) {
        List<UUID> players = new ArrayList<>();

        for (int i = 0; i < matchMakingType.getMaxPlayers(); i++) {
            if (queue.size() < i) break;
            players.add(queue.poll());
        }

        queuedPlayers.replace(matchMakingType, queue);
        main.getMapManager().startDuel(duelMap, players);
    }

    public Optional<MatchMakingType> getType(Player player) {
        return queuedPlayers.entrySet().stream()
                .filter(entry -> entry.getValue().contains(player.getUniqueId()))
                .map(Map.Entry::getKey)
                .findFirst();
    }
}
