package dev.auramc.auraskills.api.stat;

import dev.auramc.auraskills.api.annotation.Inject;
import dev.auramc.auraskills.api.registry.NamespacedId;

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
        this.id = NamespacedId.from(NamespacedId.AURASKILLS, this.name().toLowerCase());
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
