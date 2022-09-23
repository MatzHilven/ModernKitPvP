package me.matzhilven.modernkitpvp.commands.subcommands.map;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.commands.SubCommand;
import me.matzhilven.modernkitpvp.map.Map;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class SetMapNameCommand implements SubCommand {

    private final ModernKitPvP main;

    public SetMapNameCommand(ModernKitPvP main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        String id = args[1].toLowerCase();
        String name = Arrays.stream(args).skip(2).collect(Collectors.joining(" "));

        Optional<Map> optionalMap = main.getMapManager().getById(id);

        if (!optionalMap.isPresent()) {
            StringUtils.sendMessage(sender, main.getMessagesConfig().getString("invalid-map"));
            return;
        }

        Map map = optionalMap.get();
        map.setName(name);
        main.getMapManager().saveMap(map);

        StringUtils.sendMessage(sender, main.getMessagesConfig().getString("set-name")
                .replace("%id%", id)
                .replace("%name%", name)
        );
    }

    @Override
    public String getName() {
        return "setmapname";
    }

    @Override
    public String getUsage() {
        return "<id> <name>";
    }

    @Override
    public int getArgumentSize() {
        return 0;
    }
}
