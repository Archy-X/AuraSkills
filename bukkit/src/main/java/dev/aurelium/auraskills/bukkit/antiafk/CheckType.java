package dev.aurelium.auraskills.bukkit.antiafk;

import dev.aurelium.auraskills.bukkit.antiafk.checks.*;

public enum CheckType {

    BLOCK_A(BlockA.class),
    DAMAGE_A(DamageA.class),
    DAMAGE_B(DamageB.class),
    DAMAGE_C(DamageC.class),
    ENTITY_A(EntityA.class),
    ENTITY_B(EntityB.class),
    ENTITY_C(EntityC.class),
    FISHING_A(FishingA.class);

    private final Class<? extends Check> checkClass;

    CheckType(Class<? extends Check> checkClass) {
        this.checkClass = checkClass;
    }

    public Class<? extends Check> getCheckClass() {
        return checkClass;
    }
}
