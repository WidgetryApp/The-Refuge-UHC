package net.purevanilla.uhc.utils;

import net.purevanilla.uhc.configuration.Configuration;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class HeadUtils {

    private HeadUtils() {

    }

    public static void dropHead(final Player player) {
        final ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        final SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta == null) return;
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
        meta.setOwningPlayer(offlinePlayer);
        skull.setItemMeta(meta);
        final Location location = player.getLocation();
        final World world = location.getWorld();
        if (world == null) return;
        world.dropItemNaturally(location, skull);
    }

    public static void startExplosion(final Location headLocation) {
        final World world = headLocation.getWorld();
        if (world == null) return;
        final Location aboveHead = headLocation.clone().add(0D, 0.75D, 0D);
        Hologram hologramTimer = HologramUtils.createHologram(aboveHead);
        hologramTimer.insertItemLine(0, new ItemStack(Material.TNT));
        hologramTimer.insertTextLine(1, ChatUtils.colorize("&c0:03"));

        for (int i = 3, j = 0; i >= 0; i--, j++) { //Countdown from 3 to 0
            int finalJ = j;
            Bukkit.getScheduler().runTaskLater(Configuration.INSTANCE.getPlugin(), () -> {
                hologramTimer.removeLine(1);
                hologramTimer.insertTextLine(1, ChatUtils.colorize("&c0:0" + (finalJ)));

                if (!(headLocation.getBlock().getType() == Material.PLAYER_HEAD ||
                        headLocation.getBlock().getType() == Material.PLAYER_WALL_HEAD)) {
                    hologramTimer.delete();
                    return;
                }
                if (finalJ == 0) {
                    hologramTimer.delete();
                    headLocation.getBlock().setType(Material.AIR);
                    world.createExplosion(headLocation, 5.5F, false);

                }
            }, i * 17L);
            // 3, 2, 1, 0
            //
        }

        // head explodes on place
    }
}
