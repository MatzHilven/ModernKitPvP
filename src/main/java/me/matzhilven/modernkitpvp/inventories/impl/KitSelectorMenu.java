package me.matzhilven.modernkitpvp.inventories.impl;

import me.matzhilven.modernkitpvp.inventories.Menu;
import me.matzhilven.modernkitpvp.kit.Kit;
import me.matzhilven.modernkitpvp.map.MapType;
import me.matzhilven.modernkitpvp.map.impl.MatchMakingMap;
import me.matzhilven.modernkitpvp.utils.ItemBuilder;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.nio.Buffer;
import java.util.List;
import java.util.Optional;

public class KitSelectorMenu extends Menu {

    private final MapType mapType;
    private boolean chosen;

    public KitSelectorMenu(Player player, MapType mapType) {
        super(player);
        this.mapType = mapType;
        this.chosen = false;
    }

    @Override
    public String getMenuName() {
        return main.getMenusConfig().getConfig().getString("kit-selector.name");
    }

    @Override
    public int getSlots() {
        return main.getMenusConfig().getConfig().getInt("kit-selector.size");
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        if (!slots.containsKey(event.getSlot())) return;

        if (slots.get(event.getSlot()).equals("close")) {
            player.closeInventory();
            return;
        }

        Optional<Kit> optionalKit = main.getKitManager().getById(slots.get(event.getSlot()));

        if (!optionalKit.isPresent()) return;

        Kit kit = optionalKit.get();

        if (!main.getKitManager().hasUnlocked(player, kit)) {
            StringUtils.sendMessage(player, main.getMessagesConfig().getConfig().getString("invalid-kit-permissions"));
            return;
        }

        if (mapType == MapType.BATTLEFIELD) {
            if (main.getMapManager().getBattleFieldMap() != null) main.getMapManager().getBattleFieldMap().setKit(player, kit);
        } else {
            Optional<MatchMakingMap> optionalGame = main.getMapManager().getCurrentGame(player);
            if (!optionalGame.isPresent()) return;
            optionalGame.get().chooseKit();
            kit.apply(player);
            chosen = true;
        }

        player.closeInventory();

        StringUtils.sendMessage(player, main.getMessagesConfig().getConfig().getString("selected-kit")
                .replace("%kit%", kit.getName())
        );

    }

    @Override
    public void handleClose(InventoryCloseEvent event) {
        if (mapType == MapType.MATCHMAKING && !chosen) Bukkit.getScheduler().runTaskLater(main, this::open, 1L);
    }

    @Override
    public void setMenuItems() {
        setFiller(ItemBuilder.fromConfigSection(
                        main.getMenusConfig().getConfig().getConfigurationSection("kit-selector.filler")).toItemStack(),
                main.getMenusConfig().getIntegerList("kit-selector.filler.slots"));

        for (String key : main.getMenusConfig().getConfig().getConfigurationSection("kit-selector.items").getKeys(false)) {
            int slot = main.getMenusConfig().getConfig().getInt("kit-selector.items." + key + ".slot");

            inventory.setItem(
                    slot,
                    ItemBuilder.fromConfigSection(main.getMenusConfig().getConfig().getConfigurationSection("kit-selector.items." + key))
                            .toItemStack()
            );

            slots.put(slot, key);
        }

        final List<String> unlockedLore = main.getMenusConfig().getConfig().getStringList("kit-selector.kit-format.lore-unlocked");
        final List<String> lockedLore = main.getMenusConfig().getConfig().getStringList("kit-selector.kit-format.lore-locked");

        for (Kit kit : main.getKitManager().getKits().values()) {
            int slot = inventory.firstEmpty();

            inventory.addItem(new ItemBuilder(kit.getDisplayItem())
                    .setName(kit.getName())
                    .setLore(main.getKitManager().hasUnlocked(player, kit) ? unlockedLore : lockedLore)
                    .toItemStack()
            );

            slots.put(slot, kit.getId());
        }

    }
}
