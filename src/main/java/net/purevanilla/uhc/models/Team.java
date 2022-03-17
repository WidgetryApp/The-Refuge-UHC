package net.purevanilla.uhc.models;

import net.purevanilla.uhc.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.haoshoku.nick.api.NickAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Team {

    private final List<String> teamMatesNames;
    private final List<String> aliveTeamMates;
    private ChatColor teamColor;


    public Team(List<String> teamMates, ChatColor teamColor) {
        this.teamColor = teamColor;
        this.aliveTeamMates = teamMates;
        this.teamMatesNames = new ArrayList<>();

        for (String uuid : teamMates) {
            final Player player = Bukkit.getPlayer(UUID.fromString(uuid));
            if (player == null) {
                ChatUtils.broadcastChatMessage("&cERROR CODE: 562");
                return;
            }
            NickAPI.nick(player,
                    teamColor + player.getName());
            NickAPI.refreshPlayer(player);
            player.setDisplayName(teamColor + player.getName());
            teamMatesNames.add(player.getName());
        }

        //nametag i glupsoti
    }

    public List<String> getAliveTeamMates() {
        return aliveTeamMates;
    }

    public ChatColor getTeamColor() {
        return teamColor;
    }

    public void removePlayerAlive(String uuid) {
        aliveTeamMates.remove(uuid);
    }

    public boolean hasPlayersAlive() {
        return !aliveTeamMates.isEmpty();
    }

    public List<String> getTeamMatesNames() {
        return teamMatesNames;
    }

    public boolean hasPlayer(String uuid) {
        return aliveTeamMates.contains(uuid);
    }

}
