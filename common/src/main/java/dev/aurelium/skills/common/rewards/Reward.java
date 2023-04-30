package dev.aurelium.skills.common.rewards;

import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.data.PlayerData;
import dev.aurelium.skills.common.hooks.HookManager;

import java.util.Locale;

public abstract class Reward {

    protected final AureliumSkillsPlugin plugin;
    protected final HookManager hooks;

    public Reward(AureliumSkillsPlugin plugin) {
        this.plugin = plugin;
        this.hooks = plugin.getHookManager();
    }

    public abstract void giveReward(PlayerData playerData, Skill skill, int level);

    public abstract String getMenuMessage(PlayerData playerData, Locale locale, Skill skill, int level);

    public abstract String getChatMessage(PlayerData playerData, Locale locale, Skill skill, int level);

}
