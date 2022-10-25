package me.matzhilven.modernkitpvp.listeners;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.map.Map;
import me.matzhilven.modernkitpvp.map.impl.BattleFieldMap;
import me.matzhilven.modernkitpvp.map.impl.DuelMap;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class PlayerListener implements Listener {

    private final ModernKitPvP main;
    private final HashMap<UUID, UUID> lastHit;

    public PlayerListener(ModernKitPvP main) {
        this.main = main;

        this.lastHit = new HashMap<>();

        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onToggleGlide(EntityToggleGlideEvent e) {

        Player player = (Player) e.getEntity();

        if (e.isGliding()) return;

        Optional<? extends Map> optionalMap = main.getMapManager().getMap(player.getLocation());

        if (!optionalMap.isPresent()) return;
        if (!(optionalMap.get() instanceof BattleFieldMap)) return;

        BattleFieldMap map = (BattleFieldMap) optionalMap.get();

        if (map.getActivePlayers().contains(player.getUniqueId())) return;

        map.getKit(player).ifPresent(kit -> {
            map.getActivePlayers().add(player.getUniqueId());
            kit.apply(player);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (main.getMapManager().getBattleFieldMap() == null) return;

        Player player = event.getPlayer();
        setUpPlayer(player);

        main.getKitManager().getById("default").ifPresent(kit -> main.getMapManager().getBattleFieldMap().setKit(player, kit));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (lastHit.containsKey(player.getUniqueId())) {
            UUID uuid = lastHit.remove(player.getUniqueId());
            Player damager = Bukkit.getPlayer(uuid);
            if (damager != null) {
                main.getMapManager().getCurrentGame(player).ifPresentOrElse(duelMap -> {
                    duelMap.onKill(player, damager);
                }, () -> {
                    if (main.getMapManager().getBattleFieldMap() == null) return;

                    main.getMapManager().getBattleFieldMap().onKill(player, damager);
                });
            }
        }

        if (main.getMapManager().getBattleFieldMap() == null) return;
        main.getMapManager().getBattleFieldMap().getActivePlayers().remove(player.getUniqueId());
        main.getMatchMakingManager().removePlayer(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (main.getMapManager().getBattleFieldMap() == null) return;

        Player player = event.getPlayer();

        event.setRespawnLocation(main.getMapManager().getBattleFieldMap().getSpawnPoint());

        setUpPlayer(player);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (main.getMapManager().getBattleFieldMap() == null) return;
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) return;
        Player player = event.getPlayer();

        if (!"battlefield_spawn".equals(main.getMapManager().getRegionName(player))) return;
        main.getMapManager().getBattleFieldMap().getActivePlayers().remove(player.getUniqueId());

        player.getInventory().clear();
        player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) return;

        lastHit.put(event.getEntity().getUniqueId(), event.getDamager().getUniqueId());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (main.getMapManager().getBattleFieldMap() == null) return;
        event.getDrops().clear();

        Player player = event.getEntity();
        Player killer = player.getKiller();

        lastHit.remove(player.getUniqueId());

        String region = main.getMapManager().getRegionName(player);
        if ("battlefield".equals(region)) {
            main.getMapManager().getBattleFieldMap().getActivePlayers().remove(player.getUniqueId());
            main.getMapManager().getBattleFieldMap().onKill(player, killer);
        }

        if (killer == null) return;
        if ("battlefield_spawn".equals(main.getMapManager().getRegionName(player))
                || "battlefield_spawn".equals(main.getMapManager().getRegionName(killer))) return;

        main.setInCombat(player.getUniqueId());
        main.setInCombat(killer.getUniqueId());

        Optional<DuelMap> optionalMap = main.getMapManager().getCurrentGame(player);

        if (optionalMap.isPresent()) {
            DuelMap map = optionalMap.get();
            if (!map.hasStarted()) return;

            map.onKill(player, player.getKiller());
        }

    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        String region = main.getMapManager().getRegionName(event.getPlayer());
        if (region == null || event.getPlayer().getGameMode() != GameMode.SURVIVAL) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeaponDamageEntity(WeaponDamageEntityEvent event) {
        if (!(event.getVictim() instanceof Player)) return;

        Player shooter = event.getPlayer();
        Player victim = (Player) event.getVictim();

        lastHit.remove(victim.getUniqueId());

        if (victim.getHealth() - event.getDamage() > 0) {
            lastHit.put(victim.getUniqueId(), shooter.getUniqueId());
            return;
        }

        event.setDamage(0);

        String region = main.getMapManager().getRegionName(victim);
        if ("battlefield".equals(region)) {
            main.getMapManager().getBattleFieldMap().getActivePlayers().remove(victim.getUniqueId());
            main.getMapManager().getBattleFieldMap().onKill(victim, shooter);
            setUpPlayer(victim);
        }

        main.setInCombat(victim.getUniqueId());
        main.setInCombat(shooter.getUniqueId());

        Optional<DuelMap> optionalMap = main.getMapManager().getCurrentGame(victim);

        if (optionalMap.isPresent()) {
            DuelMap map = optionalMap.get();
            if (!map.hasStarted()) return;

            map.onKill(shooter, victim.getKiller());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Optional<DuelMap> optionalMap = main.getMapManager().getCurrentGame(player);

        if (optionalMap.isPresent()) {
            DuelMap map = optionalMap.get();

            if (map.hasStarted()) return;
            if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                    && event.getFrom().getBlockY() == event.getTo().getBlockY()
                    && event.getFrom().getBlockZ() == event.getTo().getBlockZ()
            ) return;

            event.setCancelled(true);
        }

        if (!player.isGliding()) return;
        if (!main.getMapManager().getBattleFieldMap().getActivePlayers().contains(player.getUniqueId())) return;

        String currentRegion = main.getMapManager().getRegionName(player);
        if (currentRegion == null || !currentRegion.equals("battlefield_spawn")) return;

        event.setTo(event.getFrom().clone().add(0, -5, 0));
    }

    private void setUpPlayer(Player player) {
        player.teleport(main.getMapManager().getBattleFieldMap().getSpawnPoint());

        player.getInventory().clear();
        player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));

        player.setHealth(player.getHealthScale());
        player.setFoodLevel(20);
        player.setGameMode(GameMode.SURVIVAL);
    }

}
