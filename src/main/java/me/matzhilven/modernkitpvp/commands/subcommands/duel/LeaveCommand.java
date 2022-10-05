package me.matzhilven.modernkitpvp.commands.subcommands.duel;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.commands.SubCommand;
import me.matzhilven.modernkitpvp.map.impl.MatchMakingMap;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class LeaveCommand implements SubCommand {

    private final ModernKitPvP main;

    public LeaveCommand(ModernKitPvP main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        Player player = (Player) sender;

        Optional<MatchMakingMap> optionalMap = main.getMapManager().getCurrentGame(player);

        if (!optionalMap.isPresent()) {
            StringUtils.sendMessage(player, main.getMessagesConfig().getString("not-in-game"));
            return;
        }

        MatchMakingMap map = optionalMap.get();
        map.leave(player, false);

    }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getUsage() {
        return "leave";
    }

    @Override
    public int getArgumentSize() {
        return 0;
    }
}
