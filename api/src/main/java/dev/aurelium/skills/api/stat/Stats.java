package dev.aurelium.skills.api.stat;

import dev.aurelium.skills.api.util.NamespacedId;

public enum Stats implements Stat {

    STRENGTH,
    HEALTH,
    REGENERATION,
    LUCK,
    WISDOM,
    TOUGHNESS;

    private final NamespacedId id;

    Stats() {
        this.id = NamespacedId.from("aureliumskills", this.name().toLowerCase());
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

}
