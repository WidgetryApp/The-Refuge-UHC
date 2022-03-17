package net.purevanilla.uhc.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TitleUtils {

    private TitleUtils() {
        //
    }

    public static void sendTitle(final String title, int showtime) {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(ChatUtils.colorize(title), "", 10, showtime, 20);
        }
    }

    public static void sendTitle(final String title, final String subtitle, int showtime, int fadeIn, int fadeOut) {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(ChatUtils.colorize(title),
                    ChatUtils.colorize(subtitle), fadeIn, showtime, fadeOut);
        }
    }
}
