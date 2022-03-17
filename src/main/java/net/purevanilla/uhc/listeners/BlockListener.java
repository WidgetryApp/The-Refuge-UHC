package net.purevanilla.uhc.listeners;

import net.purevanilla.uhc.utils.ChatUtils;
import net.purevanilla.uhc.utils.HeadUtils;
import net.purevanilla.uhc.utils.TimberUtils;

import java.security.SecureRandom;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {

    private static final SecureRandom random = new SecureRandom();

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        final Block block = event.getBlock();
        final Material blockMaterial = block.getType();
        if (blockMaterial == Material.PLAYER_HEAD || blockMaterial == Material.PLAYER_WALL_HEAD) {
            final Location location = block.getLocation();
            HeadUtils.startExplosion(location);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLeavesDecay(LeavesDecayEvent event) {
        final Location location = event.getBlock().getLocation();
        randomDropApple(location, 0.01);

    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakLeaves(final BlockBreakEvent event) {
        final Block block = event.getBlock();
        final String type = block.getType().toString();
        if (type.contains("LEAVES")) {
            randomDropApple(block.getLocation(), 0.02);
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void autoChopTrees(final BlockBreakEvent event) {
        if (event.getPlayer().getItemInHand() == null) return;
        if (TimberUtils.isAxe(event.getPlayer().getItemInHand().getType())) {
            final Location blockLocation = event.getBlock().getLocation();
            if (TimberUtils.isLog(blockLocation))
                TimberUtils.dropTree(blockLocation);
        }
    }

    private boolean nextToBlockIsABarrier(final Block block) {
        final Location loc = block.getLocation();
        final Block blockXM = loc.clone().add(-1, 0, 0).getBlock();
        final Block blockXP = loc.clone().add(1, 0, 0).getBlock();
        final Block blockZM = loc.clone().add(0, 0, -1).getBlock();
        final Block blockZP = loc.clone().add(0, 0, 1).getBlock();
        final Block blockYM = loc.clone().add(0, -1, 0).getBlock();
        final Block blockYP = loc.clone().add(0, 1, 0).getBlock();
        return blockXM.getType().equals(Material.BARRIER) || blockXP.getType().equals(Material.BARRIER) || blockZM.getType().equals(Material.BARRIER) ||
                blockZP.getType().equals(Material.BARRIER) || blockYM.getType().equals(Material.BARRIER) || blockYP.getType().equals(Material.BARRIER);
    }

    @EventHandler(ignoreCancelled = true)
    public void noBorderGrief(final BlockBreakEvent event) {
        final Block block = event.getBlock();
        final Material type = block.getType();
        if (type.equals(Material.RED_CONCRETE) && nextToBlockIsABarrier(block)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void autoSmeltOres(final BlockBreakEvent event) {
        final Block block = event.getBlock();
        Material drop;
        switch (event.getBlock().getType()) {
            case GOLD_ORE, DEEPSLATE_GOLD_ORE:
                drop = Material.GOLD_INGOT;
                break;
            case IRON_ORE, DEEPSLATE_IRON_ORE:
                drop = Material.IRON_INGOT;
                break;
            default:
                return;
        }

        final World world = block.getWorld();
        final Location location = block.getLocation();
        final Player player = event.getPlayer();

        if (player.getInventory().firstEmpty() == -1) {
            world.dropItemNaturally(location.add(0.2D, 0.2D, 0.2D), new ItemStack(drop, 1));
        } else {
            player.getInventory().addItem(new ItemStack(drop, 1));
        }
        // world.spawn(location, ExperienceOrb.class).setExperience(event.getExpToDrop());
        player.giveExp(2);
        player.playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 10);
        world.spawnParticle(Particle.DRIP_LAVA, location, 7);
        block.setType(Material.AIR);
        event.setCancelled(true);
    }

    private void randomDropApple(final Location location, double chance) {
        double rNumber = random.nextDouble();
        if (rNumber < chance) {
            final World world = location.getWorld();
            if (world == null) return;
            world.dropItemNaturally(location.add(0.2D, 0.2D, 0.2D),
                    new ItemStack(Material.APPLE, 1));
        }
    }

}

