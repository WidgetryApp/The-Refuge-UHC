package net.purevanilla.uhc.utils;

import net.purevanilla.uhc.Main;
import net.purevanilla.uhc.configuration.Configuration;
import net.purevanilla.uhc.enums.GameStatus;
import net.purevanilla.uhc.enums.PartiesStatus;
import net.purevanilla.uhc.listeners.PlayerListener;
import net.purevanilla.uhc.models.Maps;
import net.purevanilla.uhc.models.Team;
import net.purevanilla.uhc.parties.PartyManager;
import net.purevanilla.uhc.parties.PartyModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class QueueUtils {

    private static final List<String> queuePlayersList = new ArrayList<>();

    private QueueUtils() {
        //
    }

    public static int getQueuePlayers() {
        return queuePlayersList.size();
    }

    public static void addPlayer(final String uuid) {
        queuePlayersList.add(uuid);
    }

    public static boolean removePlayer(final String uuid) {
        return queuePlayersList.remove(uuid);
    }

    public static boolean checkIfEligibleToStart() {
        final int size = queuePlayersList.size();
        return size >= 4 && (size % 2 == 0);
    }
    // size < 10 ? false : size % 2 == 0 ? true : false ; - not simplified

    public static void startCountDown() {
        //choosing the map
        //TODO: add random map picker
        final Maps map = Configuration.INSTANCE.getMaps().get(0);
        Configuration.INSTANCE.setCurrentGameMap(map);
        Configuration.INSTANCE.resetTimers();
        Configuration.INSTANCE.getSpectators().clear();
        BorderUtils.setBorderSize(map.getSpawnLocations().get(0).getWorld(), 800);


        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg flag __global__ -w game pvp deny");
        for (int i = 45, j = 0; i >= 0; i--, j++) { //Countdown from 3 to 0
            int finalJ = j;

            if (i == 44) {
                //loading the chosen map
                SchematicUtils.loadMap(map.getSchematic());
            }
            Bukkit.getScheduler().runTaskLater(Configuration.INSTANCE.getPlugin(), () -> {
                ChatUtils.broadcastChatMessage("&7The Game starts after: &e" + finalJ);
                if (finalJ == 0) {

                    if (checkIfEligibleToStart()) {
                        /*Bukkit.broadcastMessage(queuePlayersList + " dasvldl");*/
                        //selecting teams
                        Configuration.INSTANCE.shuffleColors();
                        if (Main.partiesStatus == PartiesStatus.ON) {
                            //ако има четен брой хора които не са в party ги вкарва автоматично в team
                            List<String> playersNotInParty = new ArrayList<>();
                            int colorSelect = 0;
                            List<String> playerHasTeam = new ArrayList<>();
                            for (final String uuid : queuePlayersList) {
                                final Player player = Bukkit.getPlayer(UUID.fromString((uuid)));

                                if (player == null) {
                                    ChatUtils.broadcastChatMessage("&cCountDown aborted!");
                                    broadcastBadStatus();
                                    return;
                                }
                                if (playerHasTeam.contains(uuid)) continue;
                                if (PartyManager.INSTANCE.isPlayerInParty(player)) {
                                    final PartyModel partyByPlayer = PartyManager.INSTANCE.getPartyByPlayer(player.getName());
                                    if (!PartyManager.INSTANCE.isPlayerPartyOwner(player)) {
                                        if (Bukkit.getPlayer(partyByPlayer.getOwner()) == null) {
                                            playersNotInParty.add(uuid);
                                        }
                                        continue;
                                    }
                     /*               Bukkit.broadcastMessage(partyByPlayer + "partisimos ");
                                    Bukkit.broadcastMessage(player.getName() + "lider");*/
                                    final Player partyUser = Bukkit.getPlayer(partyByPlayer.getPartyUser());

                                    if (partyUser == null) {
                                        ChatUtils.broadcastChatMessage("&cCountDown aborted!!");
                                        broadcastBadStatus();
                                        return;
                                    }
                                    List<String> partyTeam = new ArrayList<>();
                                    partyTeam.add(uuid);
                                    final String uuidSecondUser = partyUser.getUniqueId().toString();
                                    partyTeam.add(uuidSecondUser);
                                    playerHasTeam.add(uuid);
                                    playerHasTeam.add(uuidSecondUser);
                                    colorSelect++;
                                    Configuration.INSTANCE.addTeam(partyTeam, colorSelect);
                                } else {
                                    playersNotInParty.add(uuid);
                                }
                            }

                            if (playersNotInParty.size() % 2 != 0) {
                                ChatUtils.broadcastChatMessage("&cCountDown aborted!!");
                                broadcastBadStatus();
                                return;
                            }

                            createRandomTeams(playersNotInParty, colorSelect);


                        } else {
                            // селектира ги на рандъм
                            int colorSelect = 0;
                            createRandomTeams(queuePlayersList, colorSelect);
                        }
                        //Clearing all the queuePlayers
                        Configuration.INSTANCE.getPlayersInGame().addAll(queuePlayersList);
                        queuePlayersList.clear();
                        //Changing the status of the game
                        Main.gameStatus = GameStatus.IN_GAME;

                        // teleporting teams to spawn locations
                        final List<Location> spawnLocsCopy = new ArrayList<>(map.getSpawnLocations());
                        Collections.shuffle(spawnLocsCopy);
                        int locationSelector = 0;
                        for (Team team : Configuration.INSTANCE.getTeams()) {
                            if (team.hasPlayersAlive()) {
                                final Location loc = spawnLocsCopy.get(locationSelector);


                                for (String uuid : team.getAliveTeamMates()) {
                                    final Player player = Bukkit.getPlayer(UUID.fromString(uuid));
                                    if (player == null) {
                                        ChatUtils.broadcastChatMessage("&cERROR CODE: 713");
                                        return;
                                    }
                                    player.teleport(loc);
                                    //DisableMovement
                                    PlayerListener.disableMovement(75, player, true);
                                }

                                locationSelector++;
                            }

                        }
                        //Countdown from 3 to 0 + blind effect + special perks
                        for (int z = 3, q = 0; z >= 0; z--, q++) {
                            int finalQ = q;
                            Bukkit.getScheduler().runTaskLater(Configuration.INSTANCE.getPlugin(), () -> {
                                TitleUtils.sendTitle("&e&lThe Game starts After:", "&9&l" + finalQ, 20, 0, 0);
                                if (finalQ == 0) {
                                    //Adding Special Effects to all players
                                    for (final String uuid : Configuration.INSTANCE.getPlayersInGame()) {
                                        final Player player = Bukkit.getPlayer(UUID.fromString((uuid)));

                                        if (player == null) {
                                            ChatUtils.broadcastChatMessage("&cERROR CODE: WTF69");
                                            ChatUtils.broadcastChatMessage("&cRestart server - only way to fix it ;)");
                                            return;
                                        }
                                        PotionEffectUtils.addEffects(player);
                                        //adding kit to player if he is special . . . Or adding extra things to him
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ci * **");

                                    }
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg flag __global__ -w game fall-damage allow");
                                }
                            }, z * 20L);
                        }

                        //Starting countdown for PVP in action bar
                        Configuration.INSTANCE.startPvPTimer();


                    } else {
                        broadcastBadStatus();
                        Main.gameStatus = GameStatus.INACTIVE;
                        queuePlayersList.clear();
                    }
                }
            }, i * 17L);

        }
    }

    private static void createRandomTeams(final List<String> playersNotInParty, final int colorSelect) {
     /*   Bukkit.broadcastMessage("Not meshed" + playersNotInParty);*/
        Collections.shuffle(playersNotInParty);
       /* Bukkit.broadcastMessage("Meshed" + playersNotInParty);*/
        int colorSelectCopy = colorSelect;
        for (int b = 0; b < playersNotInParty.size(); b += 2) {
            List<String> randomTeam = new ArrayList<>();
            randomTeam.add(playersNotInParty.get(b));
            randomTeam.add(playersNotInParty.get(b + 1));
            colorSelectCopy++;
            Configuration.INSTANCE.addTeam(randomTeam, colorSelectCopy);
        }
    }
    //

    public static boolean containsPlayer(final String uuid) {
        return queuePlayersList.contains(uuid);
    }


    public static void broadcastBadStatus() {
        ChatUtils.broadcastChatMessage("&cThe game cannot start because there are not enough players in the queue or they are not even.");
        ChatUtils.broadcastChatMessage("&7Num of players in the queue: &c" + queuePlayersList.size());
        ChatUtils.broadcastChatMessage("&7Minimum players to start the game: 10");
    }
}
