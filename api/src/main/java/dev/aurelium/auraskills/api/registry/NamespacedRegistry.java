package dev.aurelium.auraskills.api.registry;

import dev.aurelium.auraskills.api.ability.CustomAbility;
import dev.aurelium.auraskills.api.mana.CustomManaAbility;
import dev.aurelium.auraskills.api.skill.CustomSkill;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.source.XpSourceSerializer;
import dev.aurelium.auraskills.api.stat.CustomStat;
import dev.aurelium.auraskills.api.trait.CustomTrait;

import java.io.File;

public interface NamespacedRegistry {

    /**
     * Gets the namespace (plugin name) associated with the registry.
     *
     * @return the namespace
     */
    String getNamespace();

    /**
     * Registers a custom skill in the registry.
     *
     * @param skill the {@link CustomSkill}
     */
    void registerSkill(CustomSkill skill);

    /**
     * Registers a custom ability in the registry.
     *
     * @param ability the {@link CustomAbility}
     */
    void registerAbility(CustomAbility ability);

    /**
     * Registers a custom mana ability in the registry.
     *
     * @param manaAbility the {@link CustomManaAbility}
     */
    void registerManaAbility(CustomManaAbility manaAbility);

    /**
     * Registers a custom stat in the registry.
     *
     * @param stat the {@link CustomStat}
     */
    void registerStat(CustomStat stat);

    /**
     * Registers a custom trait in the registry.
     *
     * @param trait the {@link CustomTrait}
     */
    void registerTrait(CustomTrait trait);

    void registerSourceType(String name, Class<? extends XpSource> sourceClass, Class<? extends XpSourceSerializer<?>> serializerClass);

    /**
     * Gets the directory from which configuration files are loaded for this namespace.
     * This is usually the plugin data folder for the plugin associated with the namespace.
     *
     * @return the content directory
     */
    File getContentDirectory();

    /**
     * Sets the content directory from which configuration files are loaded for this namespace.
     *
     * @param file the content directory
     */
    void setContentDirectory(File file);

}
