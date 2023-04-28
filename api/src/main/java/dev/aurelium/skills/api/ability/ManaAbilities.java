package dev.aurelium.skills.api.ability;

import dev.aurelium.skills.api.util.NamespacedId;

import java.util.Locale;

public enum ManaAbilities implements ManaAbility {

    REPLENISH,
    TREECAPITATOR,
    SPEED_MINE,
    SHARP_HOOK,
    TERRAFORM,
    CHARGED_SHOT,
    ABSORPTION,
    LIGHTNING_BLADE;

    private final NamespacedId id;

    ManaAbilities() {
        this.id = NamespacedId.from("aureliumskills", this.name().toLowerCase(Locale.ROOT));
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

}