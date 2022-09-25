package me.matzhilven.modernkitpvp.listeners;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.map.Map;
import me.matzhilven.modernkitpvp.map.impl.BattleFieldMap;
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

import java.util.*;

public class PlayerListener implements Listener {

    private final ModernKitPvP main;
    private final Set<UUID> gliding;
    private final HashMap<UUID, String> latestRegions;

    public PlayerListener(ModernKitPvP main) {
        this.main = main;

        gliding = new HashSet<>();
        latestRegions = new HashMap<>();

        main.getServer().getPluginManager().registerEvents(this, main);

    }

    @EventHandler
    public void onGliding(EntityToggleGlideEvent e) {

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

    }



    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (main.getMapManager().getBattleFieldMap() == null) return;

        Player player = event.getPlayer();
        latestRegions.put(player.getUniqueId(), main.getMapManager().getRegionName(player));

        event.setRespawnLocation(main.getMapManager().getBattleFieldMap().getSpawnPoint());
        player.getInventory().clear();
        player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));

    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (main.getMapManager().getBattleFieldMap() == null) return;
        // todo check if spawn -> clear inv
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().clear();

        Player player = event.getEntity();

        if (player.getKiller() != null) {
            Player killer = player.getKiller();

            if ("battlefield".equals(main.getMapManager().getRegionName(player))) {
                main.getMapManager().getBattleFieldMap().onKill(player, killer);
                return;
            }
        }



    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!gliding.contains(player.getUniqueId())) return;

        if (!latestRegions.containsKey(player.getUniqueId())) {
            latestRegions.put(player.getUniqueId(), main.getMapManager().getRegionName(player));
            return;
        }

        String currentRegion = main.getMapManager().getRegionName(player);

        if (currentRegion == null) {
            latestRegions.remove(player.getUniqueId());
            return;
        }

        String latestRegion = latestRegions.get(player.getUniqueId());

        if (!currentRegion.equals(latestRegion) && currentRegion.equals("battlefield_spawn")) {
            event.setTo(event.getFrom().clone().add(0,-5,0));
        }

    }
}
