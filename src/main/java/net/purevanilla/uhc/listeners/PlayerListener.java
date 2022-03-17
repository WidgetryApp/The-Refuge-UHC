package net.purevanilla.uhc.listeners;

import net.purevanilla.uhc.Main;
import net.purevanilla.uhc.configuration.Configuration;
import net.purevanilla.uhc.enums.GameStatus;
import net.purevanilla.uhc.models.Team;
import net.purevanilla.uhc.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

public class PlayerListener implements Listener {

    private static boolean disableMove = false;

    public static void disableMovement(int ticks, final Player player, boolean blindness) {
        if (blindness) {
            PotionEffectUtils.addBlindness(player);
        }
        Bukkit.getScheduler().runTaskLater(Configuration.INSTANCE.getPlugin(), () -> disableMove = true, 5);
        Bukkit.getScheduler().runTaskLater(Configuration.INSTANCE.getPlugin(), () -> disableMove = false, ticks);

    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        if (Main.gameStatus != GameStatus.IN_GAME) return;
        dropHead(event);
        final Player entity = event.getEntity();
        onInGamePlayerDie(entity, false);
        WorldEffectsUtils.strikeLightning(entity.getLocation());
    }

    private void dropHead(PlayerDeathEvent event) {
        final Player entity = event.getEntity();
        final Player killer = entity.getKiller();

        if (killer == null) {
            ChatColor killedColor = ChatColor.GREEN;
            for (Team team : Configuration.INSTANCE.getTeams()) {
                if (team.hasPlayer(entity.getUniqueId().toString())) {
                    killedColor = team.getTeamColor();
                }
            }
            event.setDeathMessage(ChatUtils.colorize(killedColor + entity.getName() + " &cdied, cuz he is noob."));
        } else {
            HeadUtils.dropHead(entity);
            ChatColor killerColor = ChatColor.RED;
            ChatColor killedColor = ChatColor.GREEN;
            for (Team team : Configuration.INSTANCE.getTeams()) {
                if (team.hasPlayer(entity.getUniqueId().toString())) {
                    killedColor = team.getTeamColor();
                }
                if (team.hasPlayer(killer.getUniqueId().toString())) {
                    killerColor = team.getTeamColor();
                }
            }
            event.setDeathMessage(ChatUtils.colorize(killedColor + entity.getName() + " &7was killed from " + killerColor + killer.getName() + "&7."));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        onInGamePlayerDie(player, true);
        onQueueLeave(player);
    }

    private void onInGamePlayerDie(Player player, boolean quit) {

        final String uuid = player.getUniqueId().toString();
        if (Configuration.INSTANCE.getPlayersInGame().contains(uuid)) {
            for (Team team : new ArrayList<>(Configuration.INSTANCE.getTeams())) {
                if (team.hasPlayer(uuid)) {
                    Configuration.INSTANCE.removePlayerFromTeam(team, player, quit);
                }
            }
        }
    }

    private void onQueueLeave(Player player) {
        if (QueueUtils.removePlayer(player.getUniqueId().toString())) {
            ChatUtils.broadcastChatMessage("&e" + player.getName() + " &7quit the queue!");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityHitEvent(EntityDamageByEntityEvent event) {
        if (Main.gameStatus != GameStatus.IN_GAME) return;
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) return;
        for (Team team : Configuration.INSTANCE.getTeams()) {
            if (team.hasPlayer(event.getEntity().getUniqueId().toString()) && team.hasPlayer(event.getDamager().getUniqueId().toString())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (disableMove) {
            if (event.getTo() == null) return;
            if (Configuration.INSTANCE.getSpectators().contains(event.getPlayer().getUniqueId().toString())) return;
            if (event.getFrom().getX() != event.getTo().getX() ||
                    event.getFrom().getZ() != event.getTo().getZ() || event.getFrom().getY() != event.getTo().getY()) {
                event.setCancelled(true);
            }
        }
    }
}
