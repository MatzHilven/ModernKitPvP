package me.matzhilven.modernkitpvp.commands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public interface SubCommand {

    void onCommand(CommandSender sender, String[] args);

    default ArrayList<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    String getName();

    String getUsage();

    int getArgumentSize();

    default String getPermission() {
        return null;
    }

    default String[] getAliases() {
        return new String[]{};
    }

}
