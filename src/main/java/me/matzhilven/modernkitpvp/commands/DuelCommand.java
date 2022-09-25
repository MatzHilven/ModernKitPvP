package me.matzhilven.modernkitpvp.commands;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DuelCommand implements CommandExecutor, TabCompleter {

    private final ModernKitPvP main;
    private final PartiesAPI api;

    public DuelCommand(ModernKitPvP main) {
        this.main = main;
        this.api = Parties.getApi();

        main.getCommand("duel").setExecutor(this);
        main.getCommand("duel").setTabCompleter(this);

    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        if (args.length != 1) {
            StringUtils.sendMessage(sender, main.getMessagesConfig().getString("usage-duel"));
            return true;
        }

        Player player = (Player) sender;
        PartyPlayer partyPlayer = api.getPartyPlayer(player.getUniqueId());

        if (partyPlayer == null || !partyPlayer.isInParty()) {
            StringUtils.sendMessage(sender, main.getMessagesConfig().getString("not-in-party"));
            return true;
        }


        Party targetParty = api.getParty(args[0]);

        if (targetParty == null) {
            StringUtils.sendMessage(sender, main.getMessagesConfig().getString("invalid-party"));
            return true;
        }


        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], api.getOnlineParties().stream().map(Party::getName).collect(Collectors.toList()),
                    new ArrayList<>());
        }

        return null;
    }
}
