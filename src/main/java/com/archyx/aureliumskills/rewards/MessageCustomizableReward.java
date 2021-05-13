package com.archyx.aureliumskills.rewards;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.CustomMessageKey;
import com.archyx.aureliumskills.lang.Lang;
import org.bukkit.ChatColor;

import java.util.Locale;

public abstract class MessageCustomizableReward extends Reward {

    protected final String menuMessage;
    protected final String chatMessage;

    public MessageCustomizableReward(AureliumSkills plugin, String menuMessage, String chatMessage) {
        super(plugin);
        this.menuMessage = menuMessage;
        this.chatMessage = chatMessage;
    }

    @Override
    public String getMenuMessage(Locale locale) {
        return attemptAsMessageKey(menuMessage, locale);
    }

    @Override
    public String getChatMessage(Locale locale) {
        return attemptAsMessageKey(chatMessage, locale);
    }

    /**
     * Attempts to use the input as a message key. If a matching translation for the key is found, it will return the translation.
     * Otherwise it will return the key.
     */
    private String attemptAsMessageKey(String potentialKey, Locale locale) {
        CustomMessageKey key = new CustomMessageKey(potentialKey);
        String message = Lang.getMessage(key, locale);
        if (message == null) {
            message = potentialKey;
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
