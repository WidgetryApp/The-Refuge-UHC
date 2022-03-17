package net.purevanilla.uhc.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class LocationUtils {

    private LocationUtils() {
        // do no remove
    }

    public static List<Location> getLocationsByStringList(List<String> locationsString) {
        List<Location> locationsList = new ArrayList<>();
        for (String stringLocation : locationsString) {
            locationsList.add(getLocationByString(stringLocation));
        }
        return locationsList;
    }


    public static String getStringByLocation(final Location loc) {
        if (loc == null) {
            return "";
        }
        return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }


    public static Location getLocationByString(final String location) {
        if (location == null || location.trim().isEmpty()) {
            return null;
        }
        final String[] parts = location.split(":");
        if (parts.length == 4) {
            final World w = Bukkit.getServer().getWorld(parts[0]);
            final double x = Double.parseDouble(parts[1]);
            final double y = Double.parseDouble(parts[2]);
            final double z = Double.parseDouble(parts[3]);
            return new Location(w, x, y, z);
        }
        return null;
    }

}
