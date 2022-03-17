package net.purevanilla.uhc.utils;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.NumberConversions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SchematicUtils {

    private SchematicUtils() {
        // do not remove bih
    }

    public static void loadMap(final Clipboard clipboard) {
        final com.sk89q.worldedit.world.World worldWE = FaweAPI.getWorld("game");
        executor.execute(() -> {
            try (final EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(worldWE, -1)) {
                final Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(3, 3, 3))
                        .build();
                Operations.completeBlindly(operation);
            } catch (Exception ex) {
                ex.printStackTrace();

            }
        });
    }


    public boolean isInside(Location loc, Location l1, Location l2) {
        int x1 = Math.min(l1.getBlockX(), l2.getBlockX());
        int y1 = Math.min(l1.getBlockY(), l2.getBlockY());
        int z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
        int x2 = Math.max(l1.getBlockX(), l2.getBlockX());
        int y2 = Math.max(l1.getBlockY(), l2.getBlockY());
        int z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());

        return loc.getX() >= x1 && loc.getX() <= x2 && loc.getY() >= y1 && loc.getY() <= y2 && loc.getZ() >= z1 && loc.getZ() <= z2;
    }
}
