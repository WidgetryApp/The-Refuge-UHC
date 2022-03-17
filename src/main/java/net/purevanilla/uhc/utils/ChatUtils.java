package net.purevanilla.uhc.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtils {

    private ChatUtils() {
        //
    }

    public static String colorize(final String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static void broadcastChatMessage(final String message) {
        Bukkit.broadcastMessage(colorize(message));
    }

    public static void sendActionBarMessage(final Player player, final String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(colorize(message)));
    }
}
