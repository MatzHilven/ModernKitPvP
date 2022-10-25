package me.matzhilven.modernkitpvp.tasks;

import me.matzhilven.modernkitpvp.ModernKitPvP;
import me.matzhilven.modernkitpvp.map.impl.DuelMap;
import me.matzhilven.modernkitpvp.utils.StringUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class CountdownTask extends BukkitRunnable {

    private final DuelMap map;
    private final String message;
    private final String startMessage;
    private int countDown;

    public CountdownTask(ModernKitPvP main, DuelMap map) {
        this.map = map;
        this.countDown = main.getConfig().getInt("countdown");
        this.message = main.getMessagesConfig().getString("countdown");
        this.startMessage = main.getMessagesConfig().getString("started");
    }

    @Override
    public void run() {
        map.getOnlinePlayers().forEach(player -> StringUtils.sendMessage(player, message.replace("%seconds%", String.valueOf(countDown))));

        countDown--;

        if (countDown <= 0) {
            map.getOnlinePlayers().forEach(player -> StringUtils.sendMessage(player, startMessage));
            map.setStarted(true);
            cancel();
        }
    }
}
