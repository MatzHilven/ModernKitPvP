package me.matzhilven.modernkitpvp.commands.subcommands.duel;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.inventories.impl.MapSelectorMenu;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

        Player player = (Player) sender;
        PartyPlayer partyPlayer = api.getPartyPlayer(player.getUniqueId());

        if (partyPlayer == null || !partyPlayer.isInParty() || partyPlayer.getPartyId() == null) {
            StringUtils.sendMessage(sender, main.getMessagesConfig().getString("not-in-party"));
            return true;
        }

        Party hostParty = api.getParty(partyPlayer.getPartyId());

        if (!hostParty.getLeader().equals((player.getUniqueId()))) {
            StringUtils.sendMessage(sender, main.getMessagesConfig().getString("invalid-party-leader"));
            return true;
        }

        if (args.length == 1) {
            Party targetParty = api.getParty(args[0]);

            if (targetParty == null || targetParty.getLeader() == null) {
                StringUtils.sendMessage(sender, main.getMessagesConfig().getString("invalid-party"));
                return true;
            }

            if (hostParty.getId().equals(targetParty.getId())) {
                StringUtils.sendMessage(sender, main.getMessagesConfig().getString("invalid-party-self"));
                return true;
            }

            UUID targetUUID = targetParty.getLeader();
            Player target = Bukkit.getPlayer(targetUUID);

            if (target == null) {
                StringUtils.sendMessage(sender, main.getMessagesConfig().getString("invalid-target-party-leader"));
                return true;
            }

            new MapSelectorMenu(player, target, hostParty, targetParty).open();
            return true;
        } else if (args.length == 2) {

            if (!args[0].equalsIgnoreCase("accept") && !args[0].equalsIgnoreCase("reject")) {
                StringUtils.sendMessage(sender, main.getMessagesConfig().getString("usage-duel"));
                return true;
            }

            Party party = api.getParty(args[1]);

            if (!main.getMapManager().getInvites().containsKey(hostParty.getId()) || party == null) {
                StringUtils.sendMessage(sender, main.getMessagesConfig().getString("not-invited"));
                return true;
            }

            if (player.isGliding()) {
                StringUtils.sendMessage(sender, main.getMessagesConfig().getString("gliding"));
                return true;
            }

            if (main.isInCombat(player.getUniqueId())) {
                StringUtils.sendMessage(sender, main.getMessagesConfig().getString("in-combat"));
                return true;
            }

            main.getMapManager().getInvites().remove(hostParty.getId());

            UUID targetUUID = party.getLeader();
            Player target = Bukkit.getPlayer(targetUUID);

            if (target == null) {
                StringUtils.sendMessage(sender, main.getMessagesConfig().getString("invalid-target-party-leader"));
                return true;
            }

            if (args[0].equalsIgnoreCase("accept")) {

                main.getMapManager().startMatchMaking(party, hostParty);

                StringUtils.sendMessage(player, main.getMessagesConfig().getString("invite-accepted")
                        .replace("%party%", party.getName())
                );

                StringUtils.sendMessage(target, main.getMessagesConfig().getString("invite-accepted-target")
                                .replace("%party%", hostParty.getName())
                        );
            } else {
                StringUtils.sendMessage(player, main.getMessagesConfig().getString("invite-rejected")
                        .replace("%party%", party.getName())
                );
            }

            return true;
        }


        StringUtils.sendMessage(sender, main.getMessagesConfig().getString("usage-duel"));
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
