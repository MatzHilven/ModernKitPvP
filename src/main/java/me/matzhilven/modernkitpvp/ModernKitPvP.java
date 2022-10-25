package me.matzhilven.modernkitpvp;

import me.matzhilven.modernkitpvp.commands.ModernKitPvPBaseCommand;
import me.matzhilven.modernkitpvp.commands.subcommands.duel.DuelCommand;
import me.matzhilven.modernkitpvp.kit.KitManager;
import me.matzhilven.modernkitpvp.listeners.InventoryListener;
import me.matzhilven.modernkitpvp.listeners.PlayerListener;
import me.matzhilven.modernkitpvp.map.MapManager;
import me.matzhilven.modernkitpvp.matchmaking.MatchMakingManager;
import me.matzhilven.modernkitpvp.utils.Config;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public final class ModernKitPvP extends JavaPlugin {

    private Config menusConfig;
    private Config messagesConfig;

    private MapManager mapManager;
    private KitManager kitManager;
    private MatchMakingManager matchMakingManager;

    private HashMap<UUID, Long> combat;

    @Override
    public void onEnable() {
        saveFiles();

        mapManager = new MapManager(this);
        kitManager = new KitManager(this);
        matchMakingManager = new MatchMakingManager(this);

        combat = new HashMap<>();

        new PlayerListener(this);
        new InventoryListener(this);

        new ModernKitPvPBaseCommand(this);
        new DuelCommand(this);
    }

    @Override
    public void onDisable() {


    }

    private void saveFiles() {

        saveDefaultConfig();

        menusConfig = new Config(this, "menus.yml");
        messagesConfig = new Config(this, "messages.yml");

        File kitsFolder = new File(getDataFolder(), "kits");
        if (!kitsFolder.exists()) kitsFolder.mkdirs();

        File mapsFolder = new File(getDataFolder(), "maps");
        if (!mapsFolder.exists()) mapsFolder.mkdirs();
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public Config getMenusConfig() {
        return menusConfig;
    }

    public Config getMessagesConfig() {
        return messagesConfig;
    }

    public void setInCombat(UUID uuid) {
        combat.put(uuid, System.currentTimeMillis() + (getConfig().getInt("combat-time") * 1000L));
    }

    public boolean isInCombat(UUID uniqueId) {
        return combat.containsKey(uniqueId) && combat.get(uniqueId) > System.currentTimeMillis();
    }

    public MatchMakingManager getMatchMakingManager() {
        return matchMakingManager;
    }
}
