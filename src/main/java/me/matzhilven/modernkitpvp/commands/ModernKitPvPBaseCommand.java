package me.matzhilven.modernkitpvp.commands;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.commands.subcommands.kit.AddKitCommand;
import me.matzhilven.modernkitpvp.commands.subcommands.kit.ReloadKitsCommand;
import me.matzhilven.modernkitpvp.commands.subcommands.map.CreateMapCommand;
import me.matzhilven.modernkitpvp.commands.subcommands.map.SetMapNameCommand;
import me.matzhilven.modernkitpvp.commands.subcommands.map.SetMapRegionCommand;
import me.matzhilven.modernkitpvp.commands.subcommands.map.SetMapSpawnCommand;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModernKitPvPBaseCommand implements CommandExecutor, TabExecutor {

    private final ModernKitPvP main;

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public ModernKitPvPBaseCommand(ModernKitPvP main) {
        this.main = main;

        main.getCommand("kitpvp").setExecutor(this);
        main.getCommand("kitpvp").setTabCompleter(this);

        // Map
        registerSubCommand(new CreateMapCommand(main));
        registerSubCommand(new SetMapNameCommand(main));
        registerSubCommand(new SetMapSpawnCommand(main));
        registerSubCommand(new SetMapRegionCommand(main));

        // Kit
        registerSubCommand(new AddKitCommand(main));
        registerSubCommand(new ReloadKitsCommand(main));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            StringUtils.sendMessage(sender, main.getMessagesConfig().getString("usage"));

            subCommands.values().forEach(subCommand -> {
                StringUtils.sendMessage(sender, " -/ModernKitPvP " + subCommand.getName() + " " + subCommand.getUsage());
            });

            return true;
        }

        String subCommandString = args[0];

        if (!subCommands.containsKey(subCommandString)) {
            StringUtils.sendMessage(sender, main.getMessagesConfig().getString("usage"));

            subCommands.values().forEach(subCommand -> {
                StringUtils.sendMessage(sender, " -/ModernKitPvP " + subCommand.getName() + " " + subCommand.getUsage());
            });

            return true;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            SubCommand subCommand = subCommands.get(args[0]);

            if (subCommand.getPermission() != null && !player.hasPermission(subCommand.getPermission())) {
                StringUtils.sendMessage(sender, main.getMessagesConfig().getString("invalid-permission"));
                return true;
            }

            if (subCommand.getArgumentSize() != 0 && args.length - 1 != subCommand.getArgumentSize()) {
                StringUtils.sendMessage(sender, "&cUsage: /ModernKitPvP " + subCommand.getName() + " " + subCommand.getUsage());
                return true;
            }

            subCommand.onCommand(sender, args);
        }

        return true;
    }

    public void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName(), subCommand);

        for (String alias : subCommand.getAliases()) {
            subCommands.put(alias, subCommand);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], subCommands.keySet(), new ArrayList<>());
        } else {
            if (subCommands.get(args[0]) == null) return null;
            return subCommands.get(args[0]).onTabComplete(sender, args);
        }
    }
}
