package me.matzhilven.modernkitpvp.commands.subcommands.map;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.commands.SubCommand;
import me.matzhilven.modernkitpvp.map.Map;
import me.matzhilven.modernkitpvp.map.impl.BattleFieldMap;
import me.matzhilven.modernkitpvp.utils.Region;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class SetSpawnRegionCommand implements SubCommand {

    private final ModernKitPvP main;

    public SetSpawnRegionCommand(ModernKitPvP main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        Player player = (Player) sender;

        BattleFieldMap map = main.getMapManager().getBattleFieldMap();

        if (map == null) {
            StringUtils.sendMessage(sender, main.getMessagesConfig().getString("invalid-map"));
            return;
        }

        Region spawnRegion = map.getSpawnRegion();

        int num;

        try {
            num = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            StringUtils.sendMessage(sender, main.getMessagesConfig().getString("invalid-number"));
            return;
        }

        if (num != 1 && num != 2) {
            StringUtils.sendMessage(sender, main.getMessagesConfig().getString("invalid-number"));
            return;
        }

        if (spawnRegion == null) {
            map.setSpawnRegion(Region.of(player.getLocation()));
        } else {
            spawnRegion.setCorner(num, player.getLocation());
            map.setSpawnRegion(spawnRegion);
        }

        main.getMapManager().saveMap(map);

        StringUtils.sendMessage(sender, main.getMessagesConfig().getString("set-spawn-region"));
    }

    @Override
    public String getName() {
        return "setspawnregion";
    }

    @Override
    public String getUsage() {
        return "<num>";
    }

    @Override
    public int getArgumentSize() {
        return 1;
    }
}
