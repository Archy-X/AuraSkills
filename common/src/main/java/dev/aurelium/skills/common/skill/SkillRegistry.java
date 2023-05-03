package dev.aurelium.skills.common.skill;

import com.google.common.collect.ImmutableList;
import dev.aurelium.skills.api.ability.Ability;
import dev.aurelium.skills.api.mana.ManaAbility;
import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.skill.SkillProvider;
import dev.aurelium.skills.api.skill.Skills;
import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class SkillRegistry extends Registry<Skill, SkillProperties> implements SkillProvider {

    private final AureliumSkillsPlugin plugin;

    public SkillRegistry(AureliumSkillsPlugin plugin) {
        super(Skill.class, SkillProperties.class);
        this.plugin = plugin;
    }

    @Override
    public void registerDefaults() {
        for (Skills skill : Skills.values()) { // Register each default skill
            // Construct registered skill
            ImmutableList<Ability> abilities = SkillDefaults.getDefaultAbilities(skill)
                    .stream().map(a -> (Ability) a).collect(ImmutableList.toImmutableList()); // Cast ImmutableList<Abilities> to ImmutableList<Ability>
            ManaAbility manaAbility = SkillDefaults.getDefaultManaAbility(skill);
            SkillProperties skillProperties = new DefaultSkill(skill, abilities, manaAbility);
            // Register
            register(skill.getId(), skill, skillProperties);
            // Inject skill provider
            injectSelf(skill, SkillProvider.class);
        }
    }

    @Override
    public @NotNull ImmutableList<Ability> getAbilities(Skill skill) {
        return getProperties(skill).abilities();
    }

    @Override
    public @Nullable ManaAbility getManaAbility(Skill skill) {
        return getProperties(skill).manaAbility();
    }

    @Override
    public String getDisplayName(Skill skill, Locale locale) {
        return plugin.getMessageProvider().getSkillDisplayName(skill, locale);
    }

    @Override
    public String getDescription(Skill skill, Locale locale) {
        return plugin.getMessageProvider().getSkillDescription(skill, locale);
    }
}
