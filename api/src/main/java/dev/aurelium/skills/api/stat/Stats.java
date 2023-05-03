package dev.aurelium.skills.api.stat;

import dev.aurelium.skills.api.annotation.Inject;
import dev.aurelium.skills.api.util.NamespacedId;

import java.util.Locale;

public enum Stats implements Stat {

    STRENGTH,
    HEALTH,
    REGENERATION,
    LUCK,
    WISDOM,
    TOUGHNESS;

    @Inject
    private StatProvider provider;

    private final NamespacedId id;

    Stats() {
        this.id = NamespacedId.from(NamespacedId.AURELIUMSKILLS, this.name().toLowerCase());
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public String getDisplayName(Locale locale) {
        validate();
        return provider.getDisplayName(this, locale);
    }

    @Override
    public String getDescription(Locale locale) {
        validate();
        return provider.getDescription(this, locale);
    }

    @Override
    public String getColor(Locale locale) {
        validate();
        return provider.getColor(this, locale);
    }

    @Override
    public String getSymbol(Locale locale) {
        validate();
        return provider.getSymbol(this, locale);
    }

    private void validate() {
        if (provider == null) {
            throw new IllegalStateException("Attempting to access stat provider before it has been injected!");
        }
    }

}
