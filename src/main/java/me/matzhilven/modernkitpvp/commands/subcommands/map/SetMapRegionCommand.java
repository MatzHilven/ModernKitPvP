package me.matzhilven.modernkitpvp.commands.subcommands.map;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.commands.SubCommand;
import me.matzhilven.modernkitpvp.map.Map;
import me.matzhilven.modernkitpvp.utils.Region;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class SetMapRegionCommand implements SubCommand {

    private final ModernKitPvP main;

    public SetMapRegionCommand(ModernKitPvP main) {
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

        Region region = map.getRegion();

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

        if (region == null) {
            map.setRegion(Region.of(player.getLocation()));
        } else {
            region.setCorner(num, player.getLocation());
            map.setRegion(region);
        }

        main.getMapManager().saveMap(map);

        StringUtils.sendMessage(sender, main.getMessagesConfig().getString("set-region")
                .replace("%id%", id)
        );
    }

    @Override
    public String getName() {
        return "setmapregion";
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
