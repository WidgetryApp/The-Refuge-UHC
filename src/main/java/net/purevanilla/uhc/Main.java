package net.purevanilla.uhc;

import net.purevanilla.uhc.commands.PartyCommand;
import net.purevanilla.uhc.configuration.Configuration;
import net.purevanilla.uhc.enums.GameStatus;
import net.purevanilla.uhc.enums.PartiesStatus;
import net.purevanilla.uhc.enums.Recipes;
import net.purevanilla.uhc.listeners.BlockListener;
import net.purevanilla.uhc.listeners.ChatListener;
import net.purevanilla.uhc.listeners.NPCListener;
import net.purevanilla.uhc.listeners.PlayerListener;
import net.purevanilla.uhc.utils.PAPIHookUp;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Main extends JavaPlugin {

    public static GameStatus gameStatus = GameStatus.INACTIVE;
    public static PartiesStatus partiesStatus = PartiesStatus.OFF;

    @Override
    public void onEnable() {
        try {
            Configuration.INSTANCE.init(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Recipes.INSTANCE.init(this); // Register custom recipes

        //register listeners
        Bukkit.getPluginManager().registerEvents(new NPCListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);

        //register commands
        Objects.requireNonNull(getCommand("party")).setExecutor(new PartyCommand());

        new PAPIHookUp().register();
    }

}
