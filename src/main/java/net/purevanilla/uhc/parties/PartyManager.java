package net.purevanilla.uhc.parties;

import net.purevanilla.uhc.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public enum PartyManager {
    INSTANCE;

    final Map<String, PartyModel> parties = new HashMap<>();

    public boolean isPlayerInParty(final String player) {
        return parties.containsKey(player);
    }

    public PartyModel getPartyByPlayer(final Player player) {
        return parties.get(player.getName());
    }

    public PartyModel getPartyByPlayer(final String player) {
        return parties.get(player);
    }

    public boolean isPlayerInParty(final Player player) {
        return isPlayerInParty(player.getName());
    }

    private boolean isPlayerPartyOwner(final String player) {
        return parties.containsKey(player) && parties.get(player).getOwner().equalsIgnoreCase(player);
    }

    public boolean isPlayerPartyOwner(final Player player) {
        return isPlayerPartyOwner(player.getName());
    }

    public Status createParty(final String player) {
        if (parties.containsKey(player)) {
            return Status.ALREADY_IN_PARTY;
        }

        parties.put(player, new PartyModel(player));

        return Status.PARTY_CREATED;
    }

    public Status createParty(final Player player) {
        return createParty(player.getName());
    }

    public Status joinParty(final String owner, final String player) {
        if (isPlayerInParty(player)) {
            return Status.ALREADY_IN_PARTY;
        }
        if (!isPlayerInParty(owner)) {
            return Status.INVALID_OWNER;
        }
        final PartyModel party = parties.get(owner);
        if (party.hasPartyUser()) {
            return Status.PARTY_IS_FULL;
        }

        party.setPartyUser(player);
        parties.put(player, party);

        return Status.JOINED_PARTY;
    }

    public Status joinParty(final Player owner, final Player player) {
        return joinParty(owner.getName(), player.getName());
    }

    public Status leaveParty(final Player player) {
        if (!isPlayerInParty(player)) {
            return Status.NOT_IN_PARTY;
        }
        final PartyModel party = parties.get(player.getName());

        final String owner = party.getOwner();
        final String partyUser = party.getPartyUser();
        if (isPlayerPartyOwner(player)) {
            parties.remove(owner);
            final String disband = ChatUtils.colorize("&cThe Party was deleted!");
            if (partyUser != null) {
                parties.remove(partyUser);
                final Player user = Bukkit.getPlayer(partyUser);
                if (user != null) {
                    user.sendMessage(disband);
                }
            }

            player.sendMessage(disband);

            return Status.PARTY_DISBANDED;
        } else {
            party.sendMessage("Тест курчо");
            parties.remove(partyUser);
            party.setPartyUser(null);
            return Status.LEFT_PARTY;
        }

    }

    public enum Status {
        INVALID_OWNER,
        NOT_IN_PARTY,
        ALREADY_IN_PARTY,
        PARTY_CREATED,
        PARTY_DISBANDED,
        JOINED_PARTY,
        LEFT_PARTY,
        PARTY_IS_FULL
    }
}
