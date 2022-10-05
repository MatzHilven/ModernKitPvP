package me.matzhilven.modernkitpvp.inventories.impl;

import com.alessiodp.parties.api.interfaces.Party;
import me.matzhilven.modernkitpvp.inventories.Menu;
import me.matzhilven.modernkitpvp.map.Map;
import me.matzhilven.modernkitpvp.map.impl.MatchMakingMap;
import me.matzhilven.modernkitpvp.utils.ItemBuilder;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.Optional;

public class MapSelectorMenu extends Menu {


    private final Player target;
    private final Party hostParty;
    private final Party targetParty;

    public MapSelectorMenu(Player player, Player target, Party hostParty, Party targetParty) {
        super(player);
        this.target = target;
        this.hostParty = hostParty;
        this.targetParty = targetParty;
    }

    @Override
    public String getMenuName() {
        return main.getMenusConfig().getConfig().getString("map-selector.name");
    }

    @Override
    public int getSlots() {
        return main.getMenusConfig().getConfig().getInt("map-selector.size");
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        if (!slots.containsKey(event.getSlot())) return;

        if (slots.get(event.getSlot()).equals("close")) {
            player.closeInventory();
            return;
        }

        Optional<Map> optionalMap = main.getMapManager().getById(slots.get(event.getSlot()));

        if (!optionalMap.isPresent()) return;

        MatchMakingMap map = (MatchMakingMap) optionalMap.get();

        if (map.isActive()) {
            StringUtils.sendMessage(player, main.getMessagesConfig().getString("map-active"));
            open();
            return;
        }

        main.getMapManager().getInvites().put(targetParty.getId(), hostParty.getId());
        main.getMapManager().getChosenMaps().put(hostParty.getId(), optionalMap.get().getId());

        player.closeInventory();

        StringUtils.sendMessage(player, main.getMessagesConfig().getString("invited")
                .replace("%party%", targetParty.getName())
        );

        StringUtils.sendClickableMessage(target, main.getMessagesConfig().getString("invited-target")
                        .replace("%party%", hostParty.getName())
                , "/duel accept " + hostParty.getName());

    }

    @Override
    public void setMenuItems() {
        setFiller(ItemBuilder.fromConfigSection(
                        main.getMenusConfig().getConfig().getConfigurationSection("map-selector.filler")).toItemStack(),
                main.getMenusConfig().getIntegerList("map-selector.filler.slots"));

        for (String key : main.getMenusConfig().getConfig().getConfigurationSection("map-selector.items").getKeys(false)) {
            int slot = main.getMenusConfig().getConfig().getInt("map-selector.items." + key + ".slot");

            inventory.setItem(
                    slot,
                    ItemBuilder.fromConfigSection(main.getMenusConfig().getConfig().getConfigurationSection("map-selector.items." + key))
                            .toItemStack()
            );

            slots.put(slot, key);
        }

        Material material = Material.getMaterial(main.getMenusConfig().getString("map-selector.map-format.material"));
        String name = main.getMenusConfig().getString("map-selector.map-format.name");
        List<String> lore = main.getMenusConfig().getStringList("map-selector.map-format.lore");


        for (MatchMakingMap map : main.getMapManager().getAvailableMaps()) {
            int slot = inventory.firstEmpty();

            inventory.addItem(new ItemBuilder(material)
                    .setName(name)
                    .setLore(lore)
                    .replaceAll("%name%", map.getName())
                    .toItemStack()
            );

            slots.put(slot, map.getId());
        }

    }
}
