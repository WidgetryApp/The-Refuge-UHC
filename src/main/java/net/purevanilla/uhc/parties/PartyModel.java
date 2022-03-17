package net.purevanilla.uhc.parties;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PartyModel {

    private final Pair<String, String> partyUsers;

    public PartyModel(final String partyOwner, final String partyUser) {
        this.partyUsers = new Pair<>(partyOwner, partyUser);
    }

    public PartyModel(final String partyOwner) {
        this.partyUsers = new Pair<>(partyOwner, null);
    }


    public void setPartyUser(final String partyUser) {
        partyUsers.setB(partyUser);
    }

    public boolean hasPartyUser() {
        return partyUsers.getB() != null;
    }

    public String getOwner() {
        return partyUsers.getA();
    }

    public String getPartyUser() {
        return partyUsers.getB();
    }

    public void sendMessage(final Object message) {
        final List<Player> players = new ArrayList<>();
        try {
            players.add(Bukkit.getPlayer(getOwner()));
        } catch (Exception ignored) {
        } try {
            players.add(Bukkit.getPlayer(getPartyUser()));
        } catch (Exception ignored) {
        }
        players.forEach(player -> player.sendMessage(Objects.toString(message)));
    }
}
