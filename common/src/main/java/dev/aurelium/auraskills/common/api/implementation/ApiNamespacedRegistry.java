package dev.aurelium.auraskills.common.api.implementation;

import dev.aurelium.auraskills.api.ability.CustomAbility;
import dev.aurelium.auraskills.api.mana.CustomManaAbility;
import dev.aurelium.auraskills.api.registry.NamespacedRegistry;
import dev.aurelium.auraskills.api.skill.CustomSkill;
import dev.aurelium.auraskills.api.stat.CustomStat;
import dev.aurelium.auraskills.api.trait.CustomTrait;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;

import java.io.File;

public class ApiNamespacedRegistry implements NamespacedRegistry {

    private final String namespace;
    private final AuraSkillsPlugin plugin;
    private File contentDirectory;

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
        plugin.getSkillRegistry().register(skill.getId(), skill, plugin.getSkillManager().getSupplier());
    }

    @Override
    public void registerAbility(CustomAbility ability) {
        plugin.getAbilityRegistry().register(ability.getId(), ability, plugin.getAbilityManager().getSupplier());
    }

    @Override
    public void registerManaAbility(CustomManaAbility manaAbility) {
        plugin.getManaAbilityRegistry().register(manaAbility.getId(), manaAbility, plugin.getManaAbilityManager().getSupplier());
    }

    @Override
    public void registerStat(CustomStat stat) {
        plugin.getStatRegistry().register(stat.getId(), stat, plugin.getStatManager().getSupplier());
    }

    @Override
    public void registerTrait(CustomTrait trait) {
        plugin.getTraitRegistry().register(trait.getId(), trait, plugin.getTraitManager().getSupplier());
    }

    @Override
    public File getContentDirectory() {
        return contentDirectory;
    }

    @Override
    public void setContentDirectory(File file) {
        this.contentDirectory = file;
    }
}
