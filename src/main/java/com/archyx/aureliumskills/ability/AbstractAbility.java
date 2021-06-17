package com.archyx.aureliumskills.ability;

import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.skills.Skill;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public interface AbstractAbility {

    Skill getSkill();

    double getDefaultBaseValue();

    double getDefaultValuePerLevel();

    @Nullable
    static AbstractAbility valueOf(String abilityName) {
        try {
            return Ability.valueOf(abilityName.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            try {
                return MAbility.valueOf(abilityName.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }
    }

}
