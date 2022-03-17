package net.purevanilla.uhc.configuration;

import net.purevanilla.uhc.Main;
import net.purevanilla.uhc.enums.GameStatus;
import net.purevanilla.uhc.listeners.PlayerListener;
import net.purevanilla.uhc.models.Maps;
import net.purevanilla.uhc.models.Team;
import net.purevanilla.uhc.utils.*;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.haoshoku.nick.api.NickAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public enum Configuration {

    INSTANCE;

    private List<Maps> maps;
    private List<Team> teams;
    private List<ChatColor> teamColors;
    private List<String> playersInGame;
    private List<String> spectators;
    private JavaPlugin plugin;
    private Hologram partiesHologram;
    private Maps currentGameMap;
    //tasks timers
    private int pvpTimer;
    private int meetUpTimer;
    //timers length
    private int pvpTimerLength;
    private int meetUpTimerLength;
    private Location lobby;

    public void resetTimers() {
        this.pvpTimerLength = 590;
        this.meetUpTimerLength = 600;
    }

    public void init(final JavaPlugin plugin) throws IOException {
        final FileConfiguration config = plugin.getConfig();
        plugin.saveDefaultConfig();
        this.plugin = plugin;
        this.currentGameMap = null;

        this.pvpTimerLength = 590;
        this.meetUpTimerLength = 600;

        this.playersInGame = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.maps = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.teamColors = new ArrayList<>();
        teamColors.add(ChatColor.AQUA);
        teamColors.add(ChatColor.BLUE);
        teamColors.add(ChatColor.DARK_AQUA);
        teamColors.add(ChatColor.DARK_BLUE);
        teamColors.add(ChatColor.DARK_GREEN);
        teamColors.add(ChatColor.DARK_PURPLE);
        teamColors.add(ChatColor.DARK_RED);
        teamColors.add(ChatColor.GOLD);
        teamColors.add(ChatColor.GRAY);
        teamColors.add(ChatColor.GREEN);
        teamColors.add(ChatColor.LIGHT_PURPLE);
        teamColors.add(ChatColor.RED);
        teamColors.add(ChatColor.YELLOW);


        final World world = Bukkit.getWorld("world");
        partiesHologram = HologramUtils.createHologram(new Location(world,
                -773.5, 79, 569.4));
        changePartyStatus("&cOFF", "&7The teams will be randomly generated!");

        final ConfigurationSection maps = config.getConfigurationSection("maps");
        if (maps != null) {
            final Set<String> keys = maps.getKeys(false);
            for (String key : keys) {
                final ConfigurationSection map = config.getConfigurationSection("maps." + key);
                if (map != null) {

                    final File parent = new File(plugin.getDataFolder().getAbsolutePath() + "/maps/");
                    parent.mkdirs();
                    final File schematicFile = new File(parent, map.getString("schematic") + ".schem");

                    final ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
                    if (format == null) return;

                    final ClipboardReader reader = format.getReader(new FileInputStream(schematicFile));
                    final Maps newMap = new Maps(LocationUtils.getLocationsByStringList(
                            map.getStringList("locations")), LocationUtils.getLocationsByStringList(
                            map.getStringList("meetuplocations")), reader.read()

                    );

                    this.maps.add(newMap);
                }
            }
        }

        final World w = Bukkit.getServer().getWorld("world");
        this.lobby = new Location(w, -195, 62, -130);
        System.out.println(lobby);

    }

    public void shuffleColors() {
        Collections.shuffle(teamColors);
    }

    private void setSpectator(Player player) {
        final Location loc = player.getLocation().clone();
        loc.add(0, 2, 0);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.spigot().respawn();
        }, 1L);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.teleport(loc);
        }, 3L);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            player.sendMessage(ChatUtils.colorize("&c&lYou are spectator!"));
            player.setGameMode(GameMode.SPECTATOR);
            spectators.add(player.getUniqueId().toString());
        }, 5L);
    }

    public void removePlayerFromGame(final Player player) {
        final String uuid = player.getUniqueId().toString();
        spectators.remove(uuid);
        playersInGame.remove(uuid);
        NickAPI.resetNick(player);
        NickAPI.refreshPlayer(player);
    }

    public void removePlayerFromTeam(final Team team, final Player player, boolean quit) {
        final String uuid = player.getUniqueId().toString();
        team.removePlayerAlive(uuid);
        ChatUtils.broadcastChatMessage(team.getTeamColor() + player.getName() + "&c was eliminated!");
        if (!team.hasPlayersAlive()) {
            ChatUtils.broadcastChatMessage("&cTeam " + team.getTeamColor() + team.getTeamMatesNames().toString().replace("[", "")
                    .replace("]", "") + "&c were eliminated!");
            terminateTeam(team);
        }
        if (!quit) {
            if ((Main.gameStatus == GameStatus.IN_GAME)) {
                setSpectator(player);
            } else {
                final Location loc = player.getLocation().clone();
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.spigot().respawn();
                }, 1L);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.teleport(loc);
                }, 3L);
            }
        }

    }

    public void terminateTeam(Team team) {
        teams.remove(team);
        //check for winner
        if (teams.size() == 1) {

            Main.gameStatus = GameStatus.INACTIVE;
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg flag __global__ -w game pvp deny");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg flag __global__ -w game fall-damage deny");

            //Winner is there :)
            final Team winners = teams.get(0);
            final String winnerS = winners.getTeamColor() + winners.getTeamMatesNames().toString().replace("[", "")
                    .replace("]", "");
            ChatUtils.broadcastChatMessage("&b&lTeam " + winnerS + " &b&l are the winners!");
            TitleUtils.sendTitle("&e&lTeam winners are:", "&l" + winnerS + "&b&l!!!", 20 * 10, 15, 10);
            for (int i = 0; i < winners.getAliveTeamMates().size() - 1; i++) {
                WorldEffectsUtils.spawnFireworks(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(winners.getAliveTeamMates().get(i)))).getLocation());
            }


            for (final String uuid : new ArrayList<>(Configuration.INSTANCE.getPlayersInGame())) {
                final Player player = Bukkit.getPlayer(UUID.fromString((uuid)));

                if (player == null) {
                    ChatUtils.broadcastChatMessage("&cERROR CODE: WTF 19");
                    return;
                }

                removePlayerFromGame(player);
                PotionEffectUtils.removeEffects(player);
                player.setGameMode(GameMode.SURVIVAL);
                Bukkit.getScheduler().runTaskLater(plugin, () ->
                        player.teleport(lobby), 220L);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ci * **");
            }

            Bukkit.getScheduler().runTaskLater(plugin, () ->
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "killall drops game"), 240L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "killall mobs game"), 240L);

            teams.clear();
            BorderUtils.cancelAllTasks();
            Bukkit.getScheduler().cancelTask(pvpTimer);
            Bukkit.getScheduler().cancelTask(meetUpTimer);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "clear **");


        }
    }

    public List<Maps> getMaps() {
        return maps;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public List<String> getSpectators() {
        return spectators;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setCurrentGameMap(Maps map) {
        currentGameMap = map;
    }

    public void removeTeam(final Team team) {
        teams.remove(team);
    }

    public void addTeam(final List<String> teamMates, int colorNumber) {
        teams.add(new Team(teamMates, teamColors.get(colorNumber)));
    }

    public void changePartyStatus(final String status, final String result) {
        partiesHologram.clearLines();
        partiesHologram.insertTextLine(0, ChatUtils.colorize("&7The teams are: " + status));
        partiesHologram.insertTextLine(1, ChatUtils.colorize(result));
    }

    public List<String> getPlayersInGame() {
        return playersInGame;
    }

    public void startPvPTimer() {
        pvpTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

            final String time = DurationFormatUtils.formatDuration(pvpTimerLength * 1000L, "**mm:ss**", true);

            sendActionBarToInGamePlayers("&ePVP will be active after: &c" + time);
            pvpTimerLength = pvpTimerLength - 1;

            //Timestamps където ще аноунсва в чата за активация на пвпто в чата:
            // 5min, 3min, 1min; 30sec
            if (meetUpTimerLength == 300) {
                ChatUtils.broadcastChatMessage("&c&l5 minutes till the pvp is ONN!!!");
            }

            if (meetUpTimerLength == 180) {
                ChatUtils.broadcastChatMessage("&c&l3 minutes till the pvp is ONN!!!");
            }

            if (meetUpTimerLength == 60) {
                ChatUtils.broadcastChatMessage("&c&l1 minute till the pvp is ONN!!!");
            }
            if (meetUpTimerLength == 30) {
                ChatUtils.broadcastChatMessage("&c&l30 seconds till the pvp is ONN!!!");
            }

            if (pvpTimerLength == 0) {
                //PVP-о се активира
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg flag __global__ -w game pvp allow");

                //започваме CountDown за MeetUp-a
                startCountDownTimer();

                //Спираме този таск
                pvpTimerLength = 590;
                Bukkit.getScheduler().cancelTask(pvpTimer);
            }
        }, 40, 20);

    }

    public void sendActionBarToInGamePlayers(final String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (playersInGame.contains(player.getUniqueId().toString())) {
                ChatUtils.sendActionBarMessage(player, message);
            }
        }
    }

    public void startCountDownTimer() {
        meetUpTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

            final String time = DurationFormatUtils.formatDuration(meetUpTimerLength * 1000L, "**mm:ss**", true);
            sendActionBarToInGamePlayers("&eMeetUp starts after: &c" + time);
            meetUpTimerLength = meetUpTimerLength - 1;

            //Timestamps където ще аноунсва в чата за мийтъпа в чата:
            // 5min, 3min, 1min; 30sec
            if (meetUpTimerLength == 300) {
                ChatUtils.broadcastChatMessage("&c&l5 minutes until the meetup!");
            }

            if (meetUpTimerLength == 180) {
                ChatUtils.broadcastChatMessage("&c&l3 minutes until the meetup!");
            }

            if (meetUpTimerLength == 60) {
                ChatUtils.broadcastChatMessage("&c&lОстава 1 minute until the meetup!");
            }
            if (meetUpTimerLength == 30) {
                ChatUtils.broadcastChatMessage("&c&l30 seconds until the meetup!");
            }

            if (meetUpTimerLength == 10) {
                //countdown ot 10 do 0 za meetup start
                for (int z = 10, q = 0; z >= 0; z--, q++) {
                    int finalQ = q;
                    Bukkit.getScheduler().runTaskLater(Configuration.INSTANCE.getPlugin(), () -> {
                        TitleUtils.sendTitle("&e&lMeetUp starts after:", "&9&l" + finalQ, 20, 0, 0);

                    }, z * 20L);
                }
            }
            if (meetUpTimerLength == 0) {
                //телепортираме всички живи до мийтъпа:

                //TODO: stop pvp here
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg flag __global__ -w game pvp deny");


                final List<Location> meetUpLocations = new ArrayList<>(currentGameMap.getMeetUpLocations());
                Collections.shuffle(meetUpLocations);
                int meetUpNumber = 0;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (playersInGame.contains(player.getUniqueId().toString())) {
                        player.teleport(meetUpLocations.get(meetUpNumber));
                        PlayerListener.disableMovement(200, player, false);
                        meetUpNumber++;
                    }
                }
                //countdown from 10 to 0 fro meetup start
                for (int z = 10, q = 0; z >= 0; z--, q++) {
                    int finalQ = q;
                    Bukkit.getScheduler().runTaskLater(Configuration.INSTANCE.getPlugin(), () -> {
                        TitleUtils.sendTitle("&e&lMeetUp starts after:", "&9&l" + finalQ, 20, 0, 0);
                        if (finalQ == 0) {
                            //TODO: enable pvp here
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg flag __global__ -w game pvp allow");
                        }
                    }, z * 20L);
                }
                BorderUtils.startBorderAction();
                //Спираме този таск
                meetUpTimerLength = 600;
                Bukkit.getScheduler().cancelTask(meetUpTimer);
            }
        }, 40, 20);
    }


}
