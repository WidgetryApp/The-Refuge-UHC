package net.purevanilla.uhc.listeners;

import net.purevanilla.uhc.Main;
import net.purevanilla.uhc.configuration.Configuration;
import net.purevanilla.uhc.enums.GameStatus;
import net.purevanilla.uhc.enums.PartiesStatus;
import net.purevanilla.uhc.parties.PartyManager;
import net.purevanilla.uhc.parties.PartyModel;
import net.purevanilla.uhc.utils.ChatUtils;
import net.purevanilla.uhc.utils.QueueUtils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCListener implements Listener {


    @EventHandler(ignoreCancelled = true)
    public void onClick(NPCRightClickEvent event) {

        final int npcID = event.getNPC().getId();
        final Player clicker = event.getClicker();
        final String uuid = clicker.getUniqueId().toString();

        if (npcID == 3) {
            if (Main.gameStatus == GameStatus.IN_GAME) return;
            if (QueueUtils.containsPlayer(uuid)) return;
            if(QueueUtils.getQueuePlayers() == 36) {
                clicker.sendMessage(ChatUtils.colorize("&c&lThe limit is 36 players!"));
            }
            if(QueueUtils.getQueuePlayers() > 8 && Main.gameStatus == GameStatus.INACTIVE) {
                Main.gameStatus = GameStatus.COUNTDOWN_QUEUE;
                QueueUtils.startCountDown();
            }

            final String queueAddedMessage = " &7was added to the queue!";
            if (Main.partiesStatus == PartiesStatus.ON && PartyManager.INSTANCE.isPlayerInParty(clicker)) {

                if (PartyManager.INSTANCE.isPlayerPartyOwner(clicker)) {

                    final PartyModel partyByPlayer = PartyManager.INSTANCE.getPartyByPlayer(clicker.getName());
                    QueueUtils.addPlayer(uuid);
                    ChatUtils.broadcastChatMessage("&e" + clicker.getName() + queueAddedMessage);

                    if (partyByPlayer.hasPartyUser()) {
                        final Player partyUser = Bukkit.getPlayer(partyByPlayer.getPartyUser());

                        if (partyUser != null && !QueueUtils.containsPlayer(partyUser.getUniqueId().toString())) {
                            QueueUtils.addPlayer(partyUser.getUniqueId().toString());
                            ChatUtils.broadcastChatMessage("&e" + partyUser.getName() + queueAddedMessage);
                        }
                    }
                } else {
                    clicker.sendMessage(ChatUtils.colorize("&cThe owner of party should join the queue first!"));
                }
            } else {
                QueueUtils.addPlayer(uuid);
                ChatUtils.broadcastChatMessage("&e" + clicker.getName() + queueAddedMessage);
            }
        }


        if (npcID == 1) {
            if (Main.gameStatus != GameStatus.INACTIVE) return;
            if(!clicker.isOp()) {
                clicker.sendMessage(ChatUtils.colorize("&cYou're not OP to start the game!"));
            }
            if (QueueUtils.checkIfEligibleToStart()) {
                Main.gameStatus = GameStatus.COUNTDOWN_QUEUE;
                QueueUtils.startCountDown();
                // Стартва се Countdown
            } else {
                QueueUtils.broadcastBadStatus();
            }
        }
        if (npcID == 2) {
            if (Main.partiesStatus == PartiesStatus.ON) {
                Main.partiesStatus = PartiesStatus.OFF;
                Configuration.INSTANCE.changePartyStatus("&cOFF","&7The teams will be randomly creasted!");
            } else {
                Main.partiesStatus = PartiesStatus.ON;
                Configuration.INSTANCE.changePartyStatus("&aON", "&7The teams will be formed according to who you are at a party with - /party help");
            }
        }
    }


}
