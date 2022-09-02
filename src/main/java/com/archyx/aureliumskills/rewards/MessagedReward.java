package com.archyx.aureliumskills.rewards;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.CustomMessageKey;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.text.TextUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public abstract class MessagedReward extends Reward {

    protected final String menuMessage;
    protected final String chatMessage;

    public MessagedReward(AureliumSkills plugin, String menuMessage, String chatMessage) {
        super(plugin);
        this.menuMessage = menuMessage;
        this.chatMessage = chatMessage;
    }

    @Override
    public String getMenuMessage(@NotNull Player player, Locale locale, @NotNull Skill skill, int level) {
        return attemptAsMessageKey(menuMessage, player, locale, skill, level);
    }

    @Override
    public String getChatMessage(@NotNull Player player, Locale locale, @NotNull Skill skill, int level) {
        return attemptAsMessageKey(chatMessage, player, locale, skill, level);
    }

    /**
     * Attempts to use the input as a message key. If a matching translation for the key is found, it will return the translation.
     * Otherwise it will return the key.
     */
    private String attemptAsMessageKey(String potentialKey, @NotNull Player player, Locale locale, @NotNull Skill skill, int level) {
        CustomMessageKey key = new CustomMessageKey(potentialKey);
        String message = Lang.getMessage(key, locale);
        if (message == null) {
            message = potentialKey;
        }
        return replacePlaceholders(message, player, skill, level);
    }

    private @NotNull String replacePlaceholders(String message, @NotNull Player player, @NotNull Skill skill, int level) {
        String m = TextUtil.replace(message, "{player}", player.getName(),
                "{skill}", skill.toString().toLowerCase(Locale.ROOT),
                "{level}", String.valueOf(level));
        assert (null != m);
        if (plugin.isPlaceholderAPIEnabled()) {
            m = PlaceholderAPI.setPlaceholders(player, m);
        }
        m = TextUtil.replaceNonEscaped(m, "&", "§");
        assert (null != m);
        return m;
    }

}
