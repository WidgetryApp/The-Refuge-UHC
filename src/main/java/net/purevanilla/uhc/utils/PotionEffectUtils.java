package net.purevanilla.uhc.utils;

import net.purevanilla.uhc.configuration.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectUtils {

    private PotionEffectUtils() {
        //
    }

    public static void addEffects(Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExp(0);
            player.addPotionEffect(getPotionEffect(PotionEffectType.FAST_DIGGING));
            player.addPotionEffect(getPotionEffect(PotionEffectType.SPEED));
            player.addPotionEffect(getPotionEffect(PotionEffectType.NIGHT_VISION));

    }

    public static void addBlindness(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 6, 2));
    }


    public static void removeEffects(Player player) {
        player.removePotionEffect(PotionEffectType.FAST_DIGGING);
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
    }

    private static PotionEffect getPotionEffect(PotionEffectType effectType) {
        return new PotionEffect(effectType, 500000, 1);
    }
}
