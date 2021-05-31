package com.archyx.aureliumskills.loot;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.CustomMessageKey;
import com.archyx.aureliumskills.lang.Lang;
import org.bukkit.entity.Player;

import java.util.Locale;

public abstract class Loot {

    protected final AureliumSkills plugin;
    protected final int weight;
    protected final String message;

    public Loot(AureliumSkills plugin, int weight, String message) {
        this.plugin = plugin;
        this.weight = weight;
        this.message = message;
    }

    public int getWeight() {
        return weight;
    }

    public String getMessage() {
        return message;
    }

    protected void attemptSendMessage(Player player) {
        if (message != null && !message.equals("")) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData == null) return;

            Locale locale = playerData.getLocale();
            // Try to get message as message key
            CustomMessageKey key = new CustomMessageKey(message);
            String finalMessage = Lang.getMessage(key, locale);
            // Use input as message if fail
            if (finalMessage == null) {
                finalMessage = message;
            }
            player.sendMessage(finalMessage);
        }
    }

}
