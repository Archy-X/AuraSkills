package dev.aurelium.auraskills.common.source;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.SourceType;
import dev.aurelium.auraskills.api.source.SourceValues;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.message.MessageKey;
import dev.aurelium.auraskills.api.source.SourceIncome;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class Source implements XpSource {

    protected final AuraSkillsPlugin plugin;
    private final SourceValues values;

    public Source(AuraSkillsPlugin plugin, SourceValues values) {
        this.plugin = plugin;
        this.values = values;
    }

    @Override
    public NamespacedId getId() {
        return values.getId();
    }

    @Override
    public SourceType getType() {
        return values.getType();
    }

    @Override
    public String getDisplayName(Locale locale) {
        SourceType sourceType = getType();
        if (sourceType == null) {
            return getId().getKey();
        }
        String messagePath = "sources." + sourceType.toString().toLowerCase(Locale.ROOT) + "." + getId().getKey().toLowerCase(Locale.ROOT);
        String msg = plugin.getMsg(MessageKey.of(messagePath), locale);
        if (msg.equals(messagePath)) {
            // Try to use defined display name
            if (values.getDisplayName() != null) {
                return values.getDisplayName();
            }
            if (sourceType == SourceTypes.BLOCK) {
                return "!!REMOVE!!";
            }
            if (sourceType == SourceTypes.ENTITY) {
                return "<lang:entity.minecraft." + this.getId().getKey() + ">";
            }
        }
        return msg; // Return if exists in messages
    }

    @Override
    public @Nullable String getUnitName(Locale locale) {
        return plugin.getApi().getSourceManager().getUnitName(this, locale);
    }

    @Override
    public String name() {
        return getId().getKey().toUpperCase(Locale.ROOT);
    }

    @Override
    public double getXp() {
        return values.getXp();
    }

    public SourceIncome getIncome() {
        return values.getIncome();
    }

    @Override
    public SourceValues getValues() {
        return values;
    }

    @Override
    public String toString() {
        return getId().toString();
    }

}
