package dev.auramc.auraskills.common.rewards.type;

import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.data.PlayerData;
import dev.auramc.auraskills.common.hooks.PlaceholderHook;
import dev.auramc.auraskills.common.message.MessageKey;
import dev.auramc.auraskills.common.rewards.Reward;
import dev.auramc.auraskills.common.util.text.TextUtil;

import java.util.Locale;

public abstract class MessagedReward extends Reward {

    protected final String menuMessage;
    protected final String chatMessage;

    public MessagedReward(AuraSkillsPlugin plugin, String menuMessage, String chatMessage) {
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
