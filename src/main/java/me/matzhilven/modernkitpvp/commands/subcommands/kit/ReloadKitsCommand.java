package me.matzhilven.modernkitpvp.commands.subcommands.kit;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.commands.SubCommand;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.command.CommandSender;

public class ReloadKitsCommand implements SubCommand {

    private final ModernKitPvP main;

    public ReloadKitsCommand(ModernKitPvP main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        main.getKitManager().reloadKits();
        StringUtils.sendMessage(sender, main.getMessagesConfig().getString("reloaded-kits"));
    }

    @Override
    public String getPermission() {
        return "modernkitpvp.admin";
    }

    @Override
    public String getName() {
        return "reloadkits";
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
