package net.purevanilla.uhc.enums;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

public enum Recipes {

    INSTANCE;

    public void init(final Plugin plugin) {
       final ItemStack goldenApple = new ItemStack(Material.GOLDEN_APPLE, 3);
        final NamespacedKey key = new NamespacedKey(plugin, "golden_apple");
        final ShapedRecipe craftGoldenApple = new ShapedRecipe(key, goldenApple);
        craftGoldenApple.shape("GGG", "GHG", "GGG");
        craftGoldenApple.setIngredient('G', Material.GOLD_INGOT);
        craftGoldenApple.setIngredient('H', Material.PLAYER_HEAD);
        plugin.getServer().addRecipe(craftGoldenApple);
    }
}
