package dev.aurelium.auraskills.api.registry;

import dev.aurelium.auraskills.api.ability.CustomAbility;
import dev.aurelium.auraskills.api.mana.CustomManaAbility;
import dev.aurelium.auraskills.api.skill.CustomSkill;
import dev.aurelium.auraskills.api.source.SourceType;
import dev.aurelium.auraskills.api.source.XpSourceParser;
import dev.aurelium.auraskills.api.stat.CustomStat;
import dev.aurelium.auraskills.api.trait.CustomTrait;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Optional;

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

    /**
     * Registers a source type in the registry.
     *
     * @param name The name of the source type in lowercase. Used as the key part of the NamespacedId of the source.
     * @param parser the parser for the source
     * @return the created {@link SourceType}
     */
    SourceType registerSourceType(String name, XpSourceParser<?> parser);

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
     * @param contentDirectory the content directory
     */
    void setContentDirectory(File contentDirectory);

    /**
     * Gets the directory where menus are loaded for this namespace. Menu files with the
     * same name as the main plugin are merged together.
     *
     * @return the menu directory as an optional
     */
    @NotNull
    Optional<File> getMenuDirectory();

    /**
     * Sets the directory where menus are loaded for this namespace. Menu files with the
     * same name as the main plugin are merged together.
     *
     * @param menuDirectory the directory
     */
    void setMenuDirectory(File menuDirectory);

}
