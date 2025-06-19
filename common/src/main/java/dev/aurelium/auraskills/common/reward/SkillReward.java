package dev.aurelium.auraskills.common.reward;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.hooks.HookManager;
import dev.aurelium.auraskills.common.user.User;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public abstract class SkillReward {

    protected final AuraSkillsPlugin plugin;
    protected final HookManager hooks;
    private final Skill skill;

    public SkillReward(AuraSkillsPlugin plugin, Skill skill) {
        this.plugin = plugin;
        this.hooks = plugin.getHookManager();
        this.skill = skill;
    }

    public Skill getSkill() {
        return skill;
    }

    public abstract void giveReward(User user, Skill skill, int level);

    @Nullable
    public abstract String getMenuMessage(User user, Locale locale, Skill skill, int level);

    @Nullable
    public abstract String getChatMessage(User user, Locale locale, Skill skill, int level);

}
