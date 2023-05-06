package dev.auramc.auraskills.common.rewards;

import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.common.hooks.HookManager;
import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.data.PlayerData;

import java.util.Locale;

public abstract class Reward {

    protected final AuraSkillsPlugin plugin;
    protected final HookManager hooks;

    public Reward(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.hooks = plugin.getHookManager();
    }

    public abstract void giveReward(PlayerData playerData, Skill skill, int level);

    public abstract String getMenuMessage(PlayerData playerData, Locale locale, Skill skill, int level);

    public abstract String getChatMessage(PlayerData playerData, Locale locale, Skill skill, int level);

}
