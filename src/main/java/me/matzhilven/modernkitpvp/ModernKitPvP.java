package me.matzhilven.modernkitpvp;

import me.matzhilven.modernkitpvp.commands.subcommands.duel.DuelCommand;
import me.matzhilven.modernkitpvp.commands.ModernKitPvPBaseCommand;
import me.matzhilven.modernkitpvp.kit.KitManager;
import me.matzhilven.modernkitpvp.listeners.InventoryListener;
import me.matzhilven.modernkitpvp.listeners.PlayerListener;
import me.matzhilven.modernkitpvp.map.MapManager;
import me.matzhilven.modernkitpvp.utils.Config;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class ModernKitPvP extends JavaPlugin {

    private Config menusConfig;
    private Config messagesConfig;

    private MapManager mapManager;
    private KitManager kitManager;

    @Override
    public void onEnable() {
        saveFiles();

        mapManager = new MapManager(this);
        kitManager = new KitManager(this);

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
}
