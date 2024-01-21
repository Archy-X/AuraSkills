package dev.aurelium.auraskills.api.source;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class CustomSource implements XpSource {

    private final AuraSkillsApi auraSkills;
    private final SourceValues values;

    public CustomSource(AuraSkillsApi auraSkills, SourceValues values) {
        this.auraSkills = auraSkills;
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
        String msg = auraSkills.getMessageManager().getMessage(messagePath, locale);
        if (msg.equals(messagePath)) {
            // Try to use defined display name
            if (values.getDisplayName() != null) {
                return values.getDisplayName();
            }
        }
        return msg; // Return if exists in messages
    }

    @Override
    public @Nullable String getUnitName(Locale locale) {
        return null;
    }

    @Override
    public String name() {
        return getId().getKey().toUpperCase(Locale.ROOT);
    }

    @Override
    public double getXp() {
        return values.getXp();
    }
}
