package dev.aurelium.auraskills.common.message.type;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.common.message.MessageKey;

import java.util.Locale;

public enum TraitMessage implements MessageKey {

    ATTACK_DAMAGE_NAME,
    HP_NAME,
    SATURATION_REGENERATION_NAME,
    HUNGER_REGENERATION_NAME,
    MANA_REGENERATION_NAME,
    LUCK_NAME,
    DOUBLE_DROP_CHANCE_NAME,
    EXPERIENCE_GAIN_NAME,
    ANVIL_COST_REDUCTION_NAME,
    MAX_MANA_NAME,
    INCOMING_DAMAGE_REDUCTION_NAME;

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
