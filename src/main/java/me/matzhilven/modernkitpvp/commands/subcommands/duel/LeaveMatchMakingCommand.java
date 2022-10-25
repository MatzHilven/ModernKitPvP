package me.matzhilven.modernkitpvp.commands.subcommands.duel;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.commands.SubCommand;
import me.matzhilven.modernkitpvp.map.impl.DuelMap;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class LeaveMatchMakingCommand implements SubCommand {

    private final ModernKitPvP main;

    public LeaveMatchMakingCommand(ModernKitPvP main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        Player player = (Player) sender;
        main.getMatchMakingManager().removePlayer(player.getUniqueId());
    }

    @Override
    public String getName() {
        return "leavematchmaking";
    }

    @Override
    public String getUsage() {
        return "leavematchmaking";
    }

    @Override
    public int getArgumentSize() {
        return 0;
    }
}
