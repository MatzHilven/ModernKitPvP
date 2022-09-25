package me.matzhilven.modernkitpvp.commands.subcommands.kit;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.commands.SubCommand;
import me.matzhilven.modernkitpvp.inventories.impl.KitsMenu;
import me.matzhilven.modernkitpvp.kit.Kit;
import me.matzhilven.modernkitpvp.map.MapType;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class KitsMenuCommand implements SubCommand {

    private final ModernKitPvP main;

    public KitsMenuCommand(ModernKitPvP main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            StringUtils.sendMessage(sender, main.getMessagesConfig().getString("invalid-target"));
            return;
        }

        MapType type;

        try {
            type = MapType.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            StringUtils.sendMessage(sender, main.getMessagesConfig().getString("invalid-map-type"));
            return;
        }

        new KitsMenu(target, type).open();

    }

    @Override
    public String getName() {
        return "kitsmenu";
    }

    @Override
    public String getUsage() {
        return "<player> <type>";
    }

    @Override
    public int getArgumentSize() {
        return 2;
    }

}
