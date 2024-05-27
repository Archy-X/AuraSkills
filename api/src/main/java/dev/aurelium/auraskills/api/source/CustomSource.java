package dev.aurelium.auraskills.api.source;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class CustomSource implements XpSource {

    private final SourceValues values;

    public CustomSource(SourceValues values) {
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
        String msg = values.getApi().getMessageManager().getMessage(messagePath, locale);
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
        return values.getApi().getSourceManager().getUnitName(this, locale);
    }

    @Override
    public String name() {
        return getId().getKey().toUpperCase(Locale.ROOT);
    }

    @Override
    public double getXp() {
        return values.getXp();
    }

    @Override
    public SourceIncome getIncome() {
        return values.getIncome();
    }

    public SourceValues getValues() {
        return values;
    }
}
