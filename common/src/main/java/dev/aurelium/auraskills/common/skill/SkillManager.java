package dev.aurelium.auraskills.common.skill;

import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.SkillProvider;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SkillManager implements SkillProvider {

    private final AuraSkillsPlugin plugin;
    private final Map<Skill, LoadedSkill> skillMap;

    public SkillManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.skillMap = new HashMap<>();
    }

    public void register(Skill skill, LoadedSkill loadedSkill) {
        skillMap.put(skill, loadedSkill);
    }

    @NotNull
    public LoadedSkill getSkill(Skill skill) {
        LoadedSkill loadedSkill = skillMap.get(skill);
        if (loadedSkill == null) {
            throw new IllegalArgumentException("Skill " + skill + " is not loaded!");
        }
        return loadedSkill;
    }

    @Override
    public @NotNull ImmutableList<Ability> getAbilities(Skill skill) {
        return getSkill(skill).abilities();
    }

    @Override
    public @Nullable ManaAbility getManaAbility(Skill skill) {
        return getSkill(skill).manaAbility();
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
