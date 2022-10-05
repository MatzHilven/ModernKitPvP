package me.matzhilven.modernkitpvp.commands.subcommands.kit;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.commands.SubCommand;
import me.matzhilven.modernkitpvp.inventories.impl.KitSelectorMenu;
import me.matzhilven.modernkitpvp.map.MapType;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand implements SubCommand {

    private final ModernKitPvP main;

    public KitCommand(ModernKitPvP main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player) && args.length != 2) return;
        if (args.length == 1) {
            new KitSelectorMenu((Player) sender, MapType.BATTLEFIELD).open();
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            StringUtils.sendMessage(sender, main.getMessagesConfig().getString("invalid-target"));
            return;
        }

        new KitSelectorMenu(target, MapType.BATTLEFIELD).open();
    }

    @Override
    public String getName() {
        return "kit";
    }

    @Override
    public String getUsage() {
        return "[player]";
    }

    @Override
    public int getArgumentSize() {
        return 0;
    }

}
