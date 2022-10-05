package me.matzhilven.modernkitpvp.commands.subcommands.map;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.commands.SubCommand;
import me.matzhilven.modernkitpvp.map.Map;
import me.matzhilven.modernkitpvp.map.impl.BattleFieldMap;
import me.matzhilven.modernkitpvp.map.impl.MatchMakingMap;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class SetBattlefieldSpawnCommand implements SubCommand {

    private final ModernKitPvP main;

    public SetBattlefieldSpawnCommand(ModernKitPvP main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        Player player = (Player) sender;


        BattleFieldMap battleFieldMap = main.getMapManager().getBattleFieldMap();
        battleFieldMap.setSpawnPoint(player.getLocation());
        main.getMapManager().saveMap(battleFieldMap);

        StringUtils.sendMessage(sender, main.getMessagesConfig().getString("set-spawn")
                .replace("%id%", battleFieldMap.getId())
        );
    }

    @Override
    public String getName() {
        return "setbattlefieldspawn";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public int getArgumentSize() {
        return 0;
    }
}
