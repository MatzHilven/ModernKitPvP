package me.matzhilven.modernkitpvp.inventories.impl;

import me.matzhilven.modernkitpvp.inventories.Menu;
import me.matzhilven.modernkitpvp.matchmaking.MatchMakingManager;
import me.matzhilven.modernkitpvp.matchmaking.MatchMakingType;
import me.matzhilven.modernkitpvp.utils.ItemBuilder;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Optional;

public class MatchMakingSelectorMenu extends Menu {

    public MatchMakingSelectorMenu(Player player) {
        super(player);
    }

    @Override
    public String getMenuName() {
        return main.getMenusConfig().getConfig().getString("matchmaking-selector.name");
    }

    @Override
    public int getSlots() {
        return main.getMenusConfig().getConfig().getInt("matchmaking-selector.size");
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        if (!slots.containsKey(event.getSlot())) return;

        if (slots.get(event.getSlot()).equals("close")) {
            player.closeInventory();
            return;
        }

        Optional<MatchMakingType> optionalMatchMakingType = MatchMakingType.byName(slots.get(event.getSlot()));
        if (!optionalMatchMakingType.isPresent()) return;

        MatchMakingType matchMakingType = optionalMatchMakingType.get();

        main.getMatchMakingManager().addPlayer(matchMakingType, player.getUniqueId());
        StringUtils.sendMessage(player, main.getMessagesConfig().getString("added")
                .replace("%type%", event.getCurrentItem().getItemMeta().getDisplayName()));

        player.closeInventory();
    }

    @Override
    public void setMenuItems() {
        setFiller(ItemBuilder.fromConfigSection(
                        main.getMenusConfig().getConfig().getConfigurationSection("matchmaking-selector.filler")).toItemStack(),
                main.getMenusConfig().getIntegerList("matchmaking-selector.filler.slots"));

        for (String key : main.getMenusConfig().getConfig().getConfigurationSection("matchmaking-selector.items").getKeys(false)) {
            int slot = main.getMenusConfig().getConfig().getInt("matchmaking-selector.items." + key + ".slot");

            inventory.setItem(
                    slot,
                    ItemBuilder.fromConfigSection(main.getMenusConfig().getConfig().getConfigurationSection("matchmaking-selector.items." + key))
                            .toItemStack()
            );

            slots.put(slot, key);
        }
    }
}
