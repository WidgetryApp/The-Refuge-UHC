package net.purevanilla.uhc.listeners;

import net.purevanilla.uhc.Main;
import net.purevanilla.uhc.enums.GameStatus;
import net.purevanilla.uhc.utils.ChatUtils;
import net.purevanilla.uhc.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (Main.gameStatus == GameStatus.IN_GAME) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatUtils.colorize("&cYou cannot write in chat durring game!"));
        }
    }
}
