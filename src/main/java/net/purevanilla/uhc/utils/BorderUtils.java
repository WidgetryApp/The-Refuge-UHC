package net.purevanilla.uhc.utils;

import net.purevanilla.uhc.configuration.Configuration;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;
import java.util.List;

public class BorderUtils {

    private static List<BukkitTask> borderTasks = new LinkedList<>();

    private BorderUtils() {
        //
    }

    public static void setBorderSize(final World world, final int size) {
        world.getWorldBorder().setSize(size); // the value is one side of the square
    }

    private static void setBorderSizeWithTime(final World world, final int newSize, final int timeInSeconds) {
        world.getWorldBorder().setSize(newSize, timeInSeconds); // the value is one side of the square
    }

    private static void createTaskForBorderChange(final JavaPlugin plugin, final World world, final int newSize, final int timeInSeconds, final int cycleTime) {
        BukkitTask newTask = Bukkit.getScheduler().runTaskLater(plugin, () ->
                setBorderSizeWithTime(world, newSize, timeInSeconds), cycleTime);
        borderTasks.add(newTask);
    }

    public static void startBorderAction() {
        final JavaPlugin plugin = Configuration.INSTANCE.getPlugin();
        final World world = Bukkit.getWorld("game");
        if (world == null) {
            ChatUtils.broadcastChatMessage("ERROR: 48120");
            return;
        }
        setBorderSize(world, 35);


        int cycleTime = 20 * 5; // 5 секунди

        createTaskForBorderChange(plugin, world, 420, 30, cycleTime);

        createCountDownWithText(35);
        cycleTime = cycleTime + (180 * 20) /* пауза */ + (30 * 20); // 3 минутес

        createTaskForBorderChange(plugin, world, 250, 120, cycleTime);

        cycleTime = cycleTime + (120 * 20) /* последен scheduler */;
        createCountDownWithText(cycleTime / 20);
        cycleTime = cycleTime + (60 * 20) /* пауза */;

        createTaskForBorderChange(plugin, world, 140, 180, cycleTime);

        cycleTime = cycleTime + (180 * 20) /* последен scheduler */;
        createCountDownWithText(cycleTime / 20);
        cycleTime = cycleTime + (60 * 20) /* пауза */;

        createTaskForBorderChange(plugin, world, 50, 100, cycleTime);
    }

    public static void cancelAllTasks() {
        borderTasks.forEach(BukkitTask::cancel);
    }

    private static void createCountDownWithText(final int executeCountAfter) {
        final JavaPlugin plugin = Configuration.INSTANCE.getPlugin();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (int z = 180, q = 0; z >= 0; z--, q++) {
                int finalQ = q;
                BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    final String time = DurationFormatUtils.formatDuration(finalQ * 1000L, "**mm:ss**", true);
                    Configuration.INSTANCE.sendActionBarToInGamePlayers("&cThe border will shrink after &4&l %time% !".replace("%time%", time + ""));

                }, z * 20L);
                borderTasks.add(task);
            }
        }, executeCountAfter * 20L);

    }
    // border = 705
    //meetup start - 35border
    //after 5s starts expanding to 350 for 30S
    // 3min pause;
    // 350-> 250 ; 2min
    // 1 min. pause;
    // 250-> 150 - 3min;
    // 1min pause
    // 150 -> 60; 90sec
    // last border shrink to 16

}
