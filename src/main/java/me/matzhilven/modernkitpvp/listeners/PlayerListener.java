package me.matzhilven.modernkitpvp.listeners;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.map.Map;
import me.matzhilven.modernkitpvp.map.impl.BattleFieldMap;
import me.matzhilven.modernkitpvp.map.impl.MatchMakingMap;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class PlayerListener implements Listener {

    private final ModernKitPvP main;
    private final Set<UUID> gliding;

    public PlayerListener(ModernKitPvP main) {
        this.main = main;

        gliding = new HashSet<>();

        main.getServer().getPluginManager().registerEvents(this, main);

    }

    @EventHandler
    public void onToggleGlide(EntityToggleGlideEvent e) {

        Player player = (Player) e.getEntity();

        if (e.isGliding()) {
            gliding.add(player.getUniqueId());
            return;
        }

        gliding.remove(player.getUniqueId());

        Optional<? extends Map> optionalMap = main.getMapManager().getMap(player.getLocation());

        if (!optionalMap.isPresent()) return;
        if (!(optionalMap.get() instanceof BattleFieldMap)) return;

        BattleFieldMap map = (BattleFieldMap) optionalMap.get();
        map.getKit(player).ifPresent(kit -> {
            kit.apply(player);
            map.removeKit(player);
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (main.getMapManager().getBattleFieldMap() == null) return;

        Player player = event.getPlayer();

        player.teleport(main.getMapManager().getBattleFieldMap().getSpawnPoint());
        player.getInventory().clear();
        player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));

        main.getKitManager().getById("default").ifPresent(kit -> main.getMapManager().getBattleFieldMap().setKit(player, kit));

    }


    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (main.getMapManager().getBattleFieldMap() == null) return;

        Player player = event.getPlayer();

        event.setRespawnLocation(main.getMapManager().getBattleFieldMap().getSpawnPoint());
        player.getInventory().clear();
        player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));

    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (main.getMapManager().getBattleFieldMap() == null) return;
        Player player = event.getPlayer();

        if (!"battlefield_spawn".equals(main.getMapManager().getRegionName(player))) return;
        main.getMapManager().getBattleFieldMap().getActivePlayers().remove(player.getUniqueId());

        player.getInventory().clear();
        player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().clear();

        Player player = event.getEntity();

        if ("battlefield".equals(main.getMapManager().getRegionName(player)))
            main.getMapManager().getBattleFieldMap().getActivePlayers().remove(player.getUniqueId());

        if (player.getKiller() != null) {
            Player killer = player.getKiller();

            if ("battlefield".equals(main.getMapManager().getRegionName(player))) {
                main.getMapManager().getBattleFieldMap().onKill(player, killer);
                return;
            }
        }

        Optional<MatchMakingMap> optionalMap = main.getMapManager().getCurrentGame(player);

        if (optionalMap.isPresent()) {
            MatchMakingMap map = optionalMap.get();
            if (!map.hasStarted()) return;

            map.onKill(player, player.getKiller());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Optional<MatchMakingMap> optionalMap = main.getMapManager().getCurrentGame(player);
        if (optionalMap.isPresent()) {
            MatchMakingMap map = optionalMap.get();
            if (map.hasStarted()) return;
            if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                    && event.getFrom().getBlockY() == event.getTo().getBlockY()
                    && event.getFrom().getBlockZ() == event.getTo().getBlockZ()
            ) return;

            event.setCancelled(true);
        }

        if (!gliding.contains(player.getUniqueId())) return;

        if (!main.getMapManager().getBattleFieldMap().getActivePlayers().contains(player.getUniqueId())) return;

        String currentRegion = main.getMapManager().getRegionName(player);

        if (currentRegion == null || !currentRegion.equals("battlefield_spawn")) return;

        event.setTo(event.getFrom().clone().add(0, -5, 0));

    }
}
