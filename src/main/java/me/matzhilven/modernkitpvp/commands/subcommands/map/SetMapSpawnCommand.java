package me.matzhilven.modernkitpvp.commands.subcommands.map;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.commands.SubCommand;
import me.matzhilven.modernkitpvp.map.Map;
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

        if (!optionalMap.isPresent()) {
            StringUtils.sendMessage(sender, main.getMessagesConfig().getString("invalid-map"));
            return;
        }

        Map map = optionalMap.get();
        map.setSpawnPoint(player.getLocation());
        main.getMapManager().saveMap(map);

        StringUtils.sendMessage(sender, main.getMessagesConfig().getString("set-spawn")
                .replace("%id%", id)
        );
    }

    @Override
    public String getName() {
        return "setmapspawn";
    }

    @Override
    public String getUsage() {
        return "<id>";
    }

    @Override
    public int getArgumentSize() {
        return 1;
    }
}
