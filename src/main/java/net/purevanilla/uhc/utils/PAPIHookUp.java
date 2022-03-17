package net.purevanilla.uhc.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PAPIHookUp extends PlaceholderExpansion {

    /*
    The identifier, shouldn't contain any _ or %
     */
    public String getIdentifier() {
        return "purevanillauhc";
    }

    public String getPlugin() {
        return null;
    }


    /*
     The author of the Placeholder
     This cannot be null
     */
    public String getAuthor() {
        return "JustTenor";
    }

    /*
     Same with #getAuthor() but for versioon
     This cannot be null
     */

    public String getVersion() {
        return "1.0.0";
    }

    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }
        if (identifier.equalsIgnoreCase("JustTenor")) {
            return QueueUtils.getQueuePlayers() + "";
        }
        return "" + 0;
    }
}