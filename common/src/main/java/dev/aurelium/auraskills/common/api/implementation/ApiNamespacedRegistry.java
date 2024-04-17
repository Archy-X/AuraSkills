package dev.aurelium.auraskills.common.api.implementation;

import dev.aurelium.auraskills.api.ability.CustomAbility;
import dev.aurelium.auraskills.api.mana.CustomManaAbility;
import dev.aurelium.auraskills.api.registry.NamespaceIdentified;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.registry.NamespacedRegistry;
import dev.aurelium.auraskills.api.skill.CustomSkill;
import dev.aurelium.auraskills.api.source.SourceType;
import dev.aurelium.auraskills.api.source.XpSourceParser;
import dev.aurelium.auraskills.api.stat.CustomStat;
import dev.aurelium.auraskills.api.trait.CustomTrait;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Locale;
import java.util.Optional;

public class ApiNamespacedRegistry implements NamespacedRegistry {

    private final String namespace;
    private final AuraSkillsPlugin plugin;
    private File contentDirectory;
    @Nullable
    private File menuDirectory;
    @Nullable
    private File lootDirectory;

    public ApiNamespacedRegistry(AuraSkillsPlugin plugin, String namespace, File contentDirectory) {
        this.plugin = plugin;
        this.namespace = namespace;
        this.contentDirectory = contentDirectory;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void registerSkill(CustomSkill skill) {
        validateNamespace(skill);
        plugin.getSkillRegistry().register(skill.getId(), skill, plugin.getSkillManager().getSupplier());
    }

    @Override
    public void registerAbility(CustomAbility ability) {
        validateNamespace(ability);
        plugin.getAbilityRegistry().register(ability.getId(), ability, plugin.getAbilityManager().getSupplier());
    }

    @Override
    public void registerManaAbility(CustomManaAbility manaAbility) {
        validateNamespace(manaAbility);
        plugin.getManaAbilityRegistry().register(manaAbility.getId(), manaAbility, plugin.getManaAbilityManager().getSupplier());
    }

    @Override
    public void registerStat(CustomStat stat) {
        validateNamespace(stat);
        plugin.getStatRegistry().register(stat.getId(), stat, plugin.getStatManager().getSupplier());
    }

    @Override
    public void registerTrait(CustomTrait trait) {
        validateNamespace(trait);
        plugin.getTraitRegistry().register(trait.getId(), trait, plugin.getTraitManager().getSupplier());
    }

    @Override
    public SourceType registerSourceType(String name, XpSourceParser<?> parser) {
        NamespacedId id = NamespacedId.of(namespace, name.toLowerCase(Locale.ROOT));
        SourceType sourceType = new ApiSourceType(plugin, id, parser);
        plugin.getSourceTypeRegistry().register(id, sourceType);
        return sourceType;
    }

    @Override
    public File getContentDirectory() {
        return contentDirectory;
    }

    @Override
    public void setContentDirectory(File contentDirectory) {
        this.contentDirectory = contentDirectory;
    }

    @Override
    @NotNull
    public Optional<File> getMenuDirectory() {
        return Optional.ofNullable(menuDirectory);
    }

    @Override
    public void setMenuDirectory(@Nullable File menuDirectory) {
        this.menuDirectory = menuDirectory;
    }

    @Override
    public @NotNull Optional<File> getLootDirectory() {
        return Optional.ofNullable(lootDirectory);
    }

    @Override
    public void setLootDirectory(@Nullable File lootDirectory) {
        this.lootDirectory = lootDirectory;
    }

    private void validateNamespace(NamespaceIdentified identified) {
        if (!identified.getId().getNamespace().equals(namespace)) {
            throw new IllegalArgumentException("The namespace of NamespacedId " + identified.getId() + " must match the registry namespace " + namespace);
        }
    }

}
