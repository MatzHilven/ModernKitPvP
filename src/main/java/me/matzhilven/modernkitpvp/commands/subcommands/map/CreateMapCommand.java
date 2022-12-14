package me.matzhilven.modernkitpvp.commands.subcommands.map;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.commands.SubCommand;
import me.matzhilven.modernkitpvp.map.impl.BattleFieldMap;
import me.matzhilven.modernkitpvp.map.impl.DuelMap;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.command.CommandSender;

public class CreateMapCommand implements SubCommand {

    private final ModernKitPvP main;

    public CreateMapCommand(ModernKitPvP main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        String id = args[1].toLowerCase();

        if (main.getMapManager().getById(id).isPresent()) {
            StringUtils.sendMessage(sender, main.getMessagesConfig().getString("existing-map"));
            return;
        }
        if (id.equalsIgnoreCase("battlefield")) {
            main.getMapManager().addMap(new BattleFieldMap(main, id));
        } else {
            main.getMapManager().addMap(new DuelMap(main, id));
        }

        StringUtils.sendMessage(sender, main.getMessagesConfig().getString("created-map")
                .replace("%id%", id)
        );
    }

    @Override
    public String getName() {
        return "createmap";
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
