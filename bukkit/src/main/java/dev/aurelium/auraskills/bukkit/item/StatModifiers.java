package dev.aurelium.auraskills.bukkit.item;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum StatModifiers implements BuiltInModifier {

    VALOR("foraging-valor"),
    STAMINA("mining-stamina"),
    ENCHANTED_STRENGTH("AbilityModifier-EnchantedStrength"),
    WISE_EFFECT("AbilityModifier-WiseEffect"),
    REVIVAL(new String[]{"AureliumSkills.Ability.Revival.Health", "AureliumSkills.Ability.Revival.Regeneration"});

    private final String[] ids;

    StatModifiers(String[] ids) {
        this.ids = ids;
    }

    StatModifiers(String id) {
        this.ids = new String[]{id};
    }

    @Nullable
    public static StatModifiers fromId(String id) {
        for (StatModifiers instance : StatModifiers.values()) {
            for (String modId : instance.getModifierIds()) {
                if (modId.equals(id)) {
                    return instance;
                }
            }
        }
        return null;
    }

    public String[] getModifierIds() {
        return ids;
    }

    public String getMessageName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

}
