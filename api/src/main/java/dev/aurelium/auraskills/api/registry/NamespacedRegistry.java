package dev.aurelium.auraskills.api.registry;

import dev.aurelium.auraskills.api.ability.CustomAbility;
import dev.aurelium.auraskills.api.mana.CustomManaAbility;
import dev.aurelium.auraskills.api.skill.CustomSkill;
import dev.aurelium.auraskills.api.stat.CustomStat;
import dev.aurelium.auraskills.api.trait.CustomTrait;

import java.io.File;

public interface NamespacedRegistry {

    String getNamespace();

    void registerSkill(CustomSkill skill);

    void registerAbility(CustomAbility ability);

    void registerManaAbility(CustomManaAbility manaAbility);

    void registerStat(CustomStat stat);

    void registerTrait(CustomTrait trait);

    File getContentDirectory();

    void setContentDirectory(File file);

}
