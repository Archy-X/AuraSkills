package dev.aurelium.auraskills.common.source;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.message.MessageKey;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class Source implements XpSource {

    private final AuraSkillsPlugin plugin;
    private final NamespacedId id;
    private final double xp;
    @Nullable
    private final String displayName;

    public Source(AuraSkillsPlugin plugin, NamespacedId id, double xp, @Nullable String displayName) {
        this.plugin = plugin;
        this.id = id;
        this.xp = xp;
        this.displayName = displayName;
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public String getDisplayName(Locale locale) {
        SourceType sourceType = SourceType.getFromSource(this);
        if (sourceType == null) {
            return id.getKey();
        }
        String messagePath = "sources." + sourceType.toString().toLowerCase(Locale.ROOT) + "." + getId().getKey().toLowerCase(Locale.ROOT);
        String msg = plugin.getMsg(MessageKey.of(messagePath), locale);
        if (msg.equals(messagePath)) {
            // Try to use defined display name
            if (displayName != null) {
                return displayName;
            }
        }
        return msg; // Return if exists in messages
    }

    @Override
    public @Nullable String getUnitName(Locale locale) {
        String unitName = plugin.getItemRegistry().getSourceMenuItems().getSourceUnit(this);
        if (unitName == null) {
            return null;
        }
        // Try to replace placeholders
        for (String keyStr : TextUtil.getPlaceholders(unitName)) {
            MessageKey key = MessageKey.of(keyStr);
            String message = plugin.getMsg(key, locale);
            unitName = TextUtil.replace(unitName, "{" + keyStr + "}", message);
        }
        return unitName;
    }

    @Override
    public String name() {
        return id.getKey().toUpperCase(Locale.ROOT);
    }

    @Override
    public double getXp() {
        return xp;
    }

    @Override
    public String toString() {
        return id.toString();
    }

}
