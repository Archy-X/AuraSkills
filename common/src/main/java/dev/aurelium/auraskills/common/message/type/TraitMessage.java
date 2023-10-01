package dev.aurelium.auraskills.common.message.type;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.common.message.MessageKey;

import java.util.Locale;

public enum TraitMessage implements MessageKey {

    ATTACK_DAMAGE_NAME,
    HP_NAME,
    SATURATION_REGEN_NAME,
    HUNGER_REGEN_NAME,
    MANA_REGEN_NAME,
    LUCK_NAME,
    DOUBLE_DROP_NAME,
    EXPERIENCE_BONUS_NAME,
    ANVIL_DISCOUNT_NAME,
    MAX_MANA_NAME,
    DAMAGE_REDUCTION_NAME,
    CRIT_CHANCE_NAME,
    CRIT_DAMAGE_NAME;

    private final String path;

    TraitMessage() {
        Trait trait;
        try {
            trait = Traits.valueOf(this.name().substring(0, this.name().lastIndexOf("_")));
        }
        catch (IllegalArgumentException e) {
            trait = Traits.valueOf(this.name().substring(0, this.name().indexOf("_")));
        }
        path = "traits." + trait.name().toLowerCase(Locale.ROOT) + "." + this.name().substring(trait.name().length() + 1).toLowerCase(Locale.ROOT);
    }

    @Override
    public String getPath() {
        return path;
    }
}
