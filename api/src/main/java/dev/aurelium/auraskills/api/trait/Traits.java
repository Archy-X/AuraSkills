package dev.aurelium.auraskills.api.trait;

import dev.aurelium.auraskills.api.annotation.Inject;
import dev.aurelium.auraskills.api.registry.NamespacedId;

import java.util.Locale;

public enum Traits implements Trait {

    ATTACK_DAMAGE,
    HP,
    SATURATED_REGEN,
    FULL_HUNGER_REGEN,
    ALMOST_FULL_HUNGER_REGEN,
    MANA_REGEN,
    LUCK,
    DOUBLE_DROP_CHANCE,
    EXPERIENCE_GAIN,
    ANVIL_COST_REDUCTION,
    MAX_MANA,
    INCOMING_DAMAGE_REDUCTION;

    @Inject
    private TraitProvider provider;

    private final NamespacedId id;

    Traits() {
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

    private void validate() {
        if (provider == null) {
            throw new IllegalStateException("Attempting to access stat provider before it has been injected!");
        }
    }

}
