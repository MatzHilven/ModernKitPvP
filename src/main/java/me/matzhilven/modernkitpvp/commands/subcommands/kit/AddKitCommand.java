package me.matzhilven.modernkitpvp.commands.subcommands.kit;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.commands.SubCommand;
import me.matzhilven.modernkitpvp.kit.Kit;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class AddKitCommand implements SubCommand {

    private final ModernKitPvP main;

    public AddKitCommand(ModernKitPvP main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        String id = args[1].toLowerCase();
        Player player = (Player) sender;

        if (main.getKitManager().getById(id).isPresent()) {
            StringUtils.sendMessage(sender, main.getMessagesConfig().getString("existing-kit"));
            return;
        }

        HashMap<Integer, ItemStack> inventoryContents = new HashMap<>();

        int slot = -1;
        for (ItemStack content : player.getInventory().getContents()) {
            slot++;
            if (content == null) continue;
            inventoryContents.put(slot, content);
        }

        ItemStack displayItem = player.getInventory().getItemInMainHand().getType() == Material.AIR
                ? new ItemStack(Material.DIRT) : player.getInventory().getItemInMainHand();

        Kit kit = new Kit(id, id, displayItem, inventoryContents, player.getInventory().getArmorContents());
        main.getKitManager().addKit(kit);

        StringUtils.sendMessage(sender, main.getMessagesConfig().getString("created-kit")
                .replace("%id%", id)
        );
    }

    @Override
    public String getName() {
        return "addkit";
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
