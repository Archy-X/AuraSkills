package dev.aurelium.skills.common.rewards.type;

import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.data.PlayerData;
import dev.aurelium.skills.common.hooks.PlaceholderHook;
import dev.aurelium.skills.common.message.MessageKey;
import dev.aurelium.skills.common.rewards.Reward;
import dev.aurelium.skills.common.util.text.TextUtil;

import java.util.Locale;

public abstract class MessagedReward extends Reward {

    protected final String menuMessage;
    protected final String chatMessage;

    public MessagedReward(AureliumSkillsPlugin plugin, String menuMessage, String chatMessage) {
        super(plugin);
        this.menuMessage = menuMessage;
        this.chatMessage = chatMessage;
    }

    @Override
    public String getMenuMessage(PlayerData playerData, Locale locale, Skill skill, int level) {
        return attemptAsMessageKey(menuMessage, playerData, locale, skill, level);
    }

    @Override
    public String getChatMessage(PlayerData playerData, Locale locale, Skill skill, int level) {
        return attemptAsMessageKey(chatMessage, playerData, locale, skill, level);
    }

    /**
     * Attempts to use the input as a message key. If a matching translation for the key is found, it will return the translation.
     * Otherwise it will return the key.
     */
    private String attemptAsMessageKey(String potentialKey, PlayerData playerData, Locale locale, Skill skill, int level) {
        String message = plugin.getMessageProvider().get(MessageKey.of(potentialKey), locale);
        if (message == null) {
            message = potentialKey;
        }
        return replacePlaceholders(message, playerData, skill, level);
    }

    private String replacePlaceholders(String message, PlayerData playerData, Skill skill, int level) {
        message = TextUtil.replace(message, "{player}", playerData.getUsername(),
                "{skill}", skill.toString().toLowerCase(Locale.ROOT),
                "{level}", String.valueOf(level));
        if (hooks.isRegistered(PlaceholderHook.class)) {
            message = hooks.getHook(PlaceholderHook.class).setPlaceholders(playerData, message);
        }
        message = TextUtil.replaceNonEscaped(message, "&", "§");
        return message;
    }

}
