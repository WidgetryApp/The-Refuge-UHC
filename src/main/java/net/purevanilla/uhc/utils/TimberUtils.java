package net.purevanilla.uhc.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class TimberUtils {

    private TimberUtils() {

    }

    public static void dropTree(final Location location) {
        List<Block> blocks = new LinkedList<>();

        final World world = location.getWorld();
        if (world == null) return;

        final int blockY = location.getBlockY();

        for (int i = blockY; i < (blockY + 10); i++) {
            Location locationNew = location.add(0, 1, 0);
            if (isLog(locationNew)) {
                blocks.add(locationNew.getBlock());
            } else {
                break;
            }
        }
        for (Block block : blocks) {
            block.breakNaturally(new ItemStack(Material.DIAMOND_AXE));
        }
    }

    public static boolean isLog(final Location blockLocation) {
        return blockLocation.getBlock().getType().toString().contains("LOG");
    }

    public static boolean isAxe(Material material) {
        return switch (material) {
            case WOODEN_AXE, STONE_AXE, IRON_AXE, GOLDEN_AXE, DIAMOND_AXE -> true;
            default -> false;
        };
    }

}
