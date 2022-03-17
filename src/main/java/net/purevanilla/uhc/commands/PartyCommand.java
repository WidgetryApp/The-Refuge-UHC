package net.purevanilla.uhc.commands;

import net.purevanilla.uhc.parties.Pair;
import net.purevanilla.uhc.parties.PartyManager;
import net.purevanilla.uhc.parties.PartyModel;
import net.purevanilla.uhc.parties.PartyManager.Status;
import net.purevanilla.uhc.utils.ChatUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

enum Action {
    JOIN,
    INVITE,
    MEMBERS,
    LEAVE
}

public class PartyCommand implements CommandExecutor {

    private static final PartyManager partyManager = PartyManager.INSTANCE;
    private static final List<Pair<String, String>> invites = new ArrayList<>();


    private void sendHelpMessage(final Player player) {
        player.sendMessage(ChatUtils.colorize("&c/party members &8- &7See member in your party."));
        player.sendMessage(ChatUtils.colorize("&c/party join &e<playername> &8- &7Accept request from another player, to join the party."));
        player.sendMessage(ChatUtils.colorize("&c/party invite &e<playername> &8- &7Invite a player to join your party, so you can create one if you don't have one."));
        player.sendMessage(ChatUtils.colorize("&c/party leave &8- &7Leave the party, but if you're the owner the party will be deleated!"));

    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof final Player player)) return true;

        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }
        final Action action;
        try {
            action = Action.valueOf(args[0].toUpperCase());
        } catch (final Exception ex) {
            sendHelpMessage(player);
            return true;
        }

        if (action == Action.LEAVE) {
            if(partyManager.isPlayerPartyOwner(player)){
                invites.removeIf(pair -> pair.getB().equalsIgnoreCase(player.getName()));
            }

            final Status status = partyManager.leaveParty(player);
            if (status == Status.NOT_IN_PARTY) {
                player.sendMessage(ChatUtils.colorize("&cYou are not in party, to leave it!"));
            } else if (status == Status.LEFT_PARTY) {
                player.sendMessage(ChatUtils.colorize("Sub JustTenor"));
            }
            return true;
        }
        if (action == Action.MEMBERS) {
            if (!partyManager.isPlayerInParty(player)) {
                player.sendMessage(ChatUtils.colorize("&cYou are not in party!"));
                player.sendMessage(ChatUtils.colorize("&7Use &c/party help - to how to join or create a party!"));
            } else {
                final PartyModel partyByPlayer = partyManager.getPartyByPlayer(player);
                player.sendMessage(ChatUtils.colorize("&7Members of the party are : &c" +
                        partyByPlayer.getOwner() + "&7, &a" +
                        partyByPlayer.getPartyUser() + " &7(&amember&7)"));

            }
            return true;
        }

        if (args.length != 2) {
            sendHelpMessage(player);
            return true;
        }

        if (action == Action.INVITE) {
            if (args[1].equalsIgnoreCase(player.getName())) {
                player.sendMessage(ChatUtils.colorize("&cYou cannot invite yourself in this party!"));
                return true;
            }
            if (!partyManager.isPlayerPartyOwner(player)) {
                player.sendMessage("You are not party owner");
            } else {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("Player not found");
                } else {
                    player.sendMessage("Invited target");
                    target.sendMessage(player.getName() + " invited you to his party. Type /party join " + player.getName() + " to join his party");
                    invites.add(new Pair<>(target.getName(), player.getName()));
                }
            }
            return true;
        }
        if (action == Action.JOIN) {
            if (partyManager.isPlayerInParty(player)) {
                player.sendMessage(ChatUtils.colorize("&cYou are already in party!&7 To accept the request, first you need to leave your current party &c/party leave&7!"));
            } else {
                final String targetParty = args[1];
                final Pair<String,String> partyToJoin = invites
                        .parallelStream()
                        .filter(pair -> pair.getA().equalsIgnoreCase(player.getName()) && pair.getB().equalsIgnoreCase(targetParty))
                        .findFirst()
                        .orElse(null);

                if(partyToJoin == null){
                    player.sendMessage("You haven't been invited to this party");
                }else{
                    partyManager.joinParty(partyToJoin.getB(), partyToJoin.getA());
                    invites.remove(partyToJoin);
                }
            }
            return true;
        }

        return false;
    }
}