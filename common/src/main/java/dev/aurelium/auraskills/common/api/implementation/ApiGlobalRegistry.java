package dev.aurelium.auraskills.common.api.implementation;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.registry.GlobalRegistry;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;

public class ApiGlobalRegistry implements GlobalRegistry {

    private final AuraSkillsPlugin plugin;

    public ApiGlobalRegistry(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Skill getSkill(NamespacedId id) {
        return plugin.getSkillRegistry().getOrNull(id);
    }

    @Override
    public Stat getStat(NamespacedId id) {
        return plugin.getStatRegistry().getOrNull(id);
    }

    @Override
    public Trait getTrait(NamespacedId id) {
        return plugin.getTraitRegistry().getOrNull(id);
    }

    @Override
    public Ability getAbility(NamespacedId id) {
        return plugin.getAbilityRegistry().getOrNull(id);
    }

    @Override
    public ManaAbility getManaAbility(NamespacedId id) {
        return plugin.getManaAbilityRegistry().getOrNull(id);
    }
}
