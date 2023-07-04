package dev.aurelium.auraskills.common.skill;

import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.SkillProvider;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.common.message.MessageProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class SkillSupplier implements SkillProvider {

    private final SkillManager skillManager;
    private final MessageProvider messageProvider;

    public SkillSupplier(SkillManager skillManager, MessageProvider messageProvider) {
        this.skillManager = skillManager;
        this.messageProvider = messageProvider;
    }

    private LoadedSkill get(Skill skill) {
        return this.skillManager.getSkill(skill);
    }

    @Override
    public boolean isEnabled(Skill skill) {
        return get(skill).options().enabled();
    }

    @Override
    public @NotNull ImmutableList<Ability> getAbilities(Skill skill) {
        return get(skill).abilities();
    }

    @Override
    public @Nullable ManaAbility getManaAbility(Skill skill) {
        return get(skill).manaAbility();
    }

    @Override
    public @NotNull ImmutableList<XpSource> getSources(Skill skill) {
        return get(skill).sources();
    }

    @Override
    public int getMaxLevel(Skill skill) {
        return get(skill).options().maxLevel();
    }

    @Override
    public String getDisplayName(Skill skill, Locale locale) {
        return messageProvider.getSkillDisplayName(skill, locale);
    }

    @Override
    public String getDescription(Skill skill, Locale locale) {
        return messageProvider.getSkillDescription(skill, locale);
    }

    @Override
    public boolean optionBoolean(Skill type, String key) {
        return get(type).options().getBoolean(key);
    }

    @Override
    public boolean optionBoolean(Skill type, String key, boolean def) {
        return get(type).options().getBoolean(key, def);
    }

    @Override
    public int optionInt(Skill type, String key) {
        return get(type).options().getInt(key);
    }

    @Override
    public int optionInt(Skill type, String key, int def) {
        return get(type).options().getInt(key, def);
    }

    @Override
    public double optionDouble(Skill type, String key) {
        return get(type).options().getDouble(key);
    }

    @Override
    public double optionDouble(Skill type, String key, double def) {
        return get(type).options().getDouble(key, def);
    }

    @Override
    public String optionString(Skill type, String key) {
        return get(type).options().getString(key);
    }

    @Override
    public String optionString(Skill type, String key, String def) {
        return get(type).options().getString(key, def);
    }

    @Override
    public List<String> optionStringList(Skill type, String key) {
        return get(type).options().getStringList(key);
    }
}
