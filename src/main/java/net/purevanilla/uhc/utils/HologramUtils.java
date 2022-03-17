package net.purevanilla.uhc.utils;

import net.purevanilla.uhc.configuration.Configuration;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Location;

public class HologramUtils {

    private HologramUtils() {
        //
    }

    public static Hologram createHologram(final Location location) {
        return HologramsAPI.createHologram(Configuration.INSTANCE.getPlugin(), location);
    }
}
