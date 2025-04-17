package dev.aurelium.auraskills.bukkit.item;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum TraitModifiers implements BuiltInModifier {

    BOUNTIFUL_HARVEST("farming_luck_ability"),
    LUMBERJACK("foraging_luck_ability"),
    LUCKY_MINER("mining_luck_ability"),
    LUCKY_CATCH("fishing_luck_ability"),
    BIGGER_SCOOP("excavation_luck_ability"),
    FLEETING("auraskills/fleeting");

    private final String id;

    TraitModifiers(String id) {
        this.id = id;
    }

    @Nullable
    public static TraitModifiers fromId(String id) {
        for (TraitModifiers instance : TraitModifiers.values()) {
            if (instance.getModifierId().equals(id)) {
                return instance;
            }
        }
        return null;
    }

    public String getModifierId() {
        return id;
    }

    public String getMessageName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

}
