package net.purevanilla.uhc.utils;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class WorldEffectsUtils {

    private WorldEffectsUtils() {
        //
    }

    public static void strikeLightning(final Location location) {
        final World world = location.getWorld();
        if (world == null) return;
        world.strikeLightningEffect(location);
    }

    public static void spawnFireworks(final Location location) {
        final World world = location.getWorld();
        if (world == null) return;

        Firework firework = (Firework) world.spawnEntity(location.add(0, 3, 0), EntityType.FIREWORK);

        FireworkMeta fwMeta = firework.getFireworkMeta();

        fwMeta.setPower(4);
        fwMeta.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());
        firework.setFireworkMeta(fwMeta);
        firework.detonate();
        firework.detonate();
        firework.detonate();
    }

}
