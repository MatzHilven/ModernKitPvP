package me.matzhilven.modernkitpvp.commands.subcommands.kit;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.commands.SubCommand;
import me.matzhilven.modernkitpvp.inventories.impl.KitSelectorMenu;
import me.matzhilven.modernkitpvp.inventories.impl.MatchMakingSelectorMenu;
import me.matzhilven.modernkitpvp.map.MapType;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MatchMakingCommand implements SubCommand {

    private final ModernKitPvP main;

    public MatchMakingCommand(ModernKitPvP main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player) && args.length != 2) return;

        if (args.length == 1) {
            new MatchMakingSelectorMenu((Player) sender).open();
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            StringUtils.sendMessage(sender, main.getMessagesConfig().getString("invalid-target"));
            return;
        }

        new MatchMakingSelectorMenu(target).open();
    }

    @Override
    public String getName() {
        return "matchmaking";
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
