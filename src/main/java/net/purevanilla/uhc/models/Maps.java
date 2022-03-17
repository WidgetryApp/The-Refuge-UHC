package net.purevanilla.uhc.models;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import org.bukkit.Location;

import java.util.List;

public record Maps(List<Location> spawnLocations, List<Location> meetUpLocations, Clipboard schematic) {

    public Clipboard getSchematic() {
        return schematic;
    }

    public List<Location> getSpawnLocations() {
        return spawnLocations;
    }

    public List<Location> getMeetUpLocations() {
        return meetUpLocations;
    }
}
