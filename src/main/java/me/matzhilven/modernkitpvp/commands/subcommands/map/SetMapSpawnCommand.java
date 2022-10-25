package me.matzhilven.modernkitpvp.commands.subcommands.map;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.commands.SubCommand;
import me.matzhilven.modernkitpvp.map.Map;
import me.matzhilven.modernkitpvp.map.impl.DuelMap;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class SetMapSpawnCommand implements SubCommand {

    private final ModernKitPvP main;

    public SetMapSpawnCommand(ModernKitPvP main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        String id = args[1].toLowerCase();
        Player player = (Player) sender;

        Optional<Map> optionalMap = main.getMapManager().getById(id);

        if (!optionalMap.isPresent() || !(optionalMap.get() instanceof DuelMap)) {
            StringUtils.sendMessage(sender, main.getMessagesConfig().getString("invalid-map"));
            return;
        }

        int num;

        try {
            num = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            StringUtils.sendMessage(sender, main.getMessagesConfig().getString("invalid-number"));
            return;
        }

        if (num != 1 && num != 2) {
            StringUtils.sendMessage(sender, main.getMessagesConfig().getString("invalid-number"));
            return;
        }

        DuelMap map = (DuelMap) optionalMap.get();

        if (num == 1) {
            map.setSpawnPoint1(player.getLocation());
        } else {
            map.setSpawnPoint2(player.getLocation());
        }

        main.getMapManager().saveMap(map);

        StringUtils.sendMessage(sender, main.getMessagesConfig().getString("set-spawn-match")
                .replace("%id%", id)
                .replace("%num%", String.valueOf(num))
        );
    }

    @Override
    public String getName() {
        return "setmapspawn";
    }

    @Override
    public String getUsage() {
        return "<id> <num>";
    }

    @Override
    public int getArgumentSize() {
        return 2;
    }
}
