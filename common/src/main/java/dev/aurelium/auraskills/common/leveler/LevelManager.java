package dev.aurelium.auraskills.common.leveler;

import dev.aurelium.auraskills.api.event.skill.SkillLevelUpEvent;
import dev.aurelium.auraskills.api.event.skill.XpGainEvent;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.hooks.EconomyHook;
import dev.aurelium.auraskills.common.reward.SkillReward;
import dev.aurelium.auraskills.common.scheduler.Tick;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Interface with methods to add xp and level up players.
 */
public abstract class LevelManager {

    private final AuraSkillsPlugin plugin;
    private final XpRequirements xpRequirements;

    public LevelManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.xpRequirements = plugin.getXpRequirements();
    }

    public double getPermissionMultiplier(@NotNull User user, Skill skill) {
        return user.getPermissionMultiplier(skill);
    }

    public abstract void playLevelUpSound(@NotNull User user);

    public void addXp(User user, Skill skill, double amount) {
        if (amount == 0) return; // Ignore if source amount is 0

        double amountToAdd = amount * calculateMultiplier(user, skill);

        // Call event
        XpGainEvent event = new XpGainEvent(plugin.getApi(), user.toApi(), skill, amountToAdd);
        plugin.getEventManager().callEvent(event);
        if (event.isCancelled()) return;

        // Add XP to player
        amountToAdd = event.getAmount();
        user.addSkillXp(skill, amountToAdd);

        checkLevelUp(user, skill);

        // Send action bar and boss bar
        sendXpUi(user, skill, amountToAdd);
    }

    private void sendXpUi(User user, Skill skill, double xpGained) {
        double currentXp = user.getSkillXp(skill);
        int level = user.getSkillLevel(skill);
        double levelXp = xpRequirements.getXpRequired(skill, level + 1);
        boolean maxed = xpRequirements.getListSize(skill) <= user.getSkillLevel(skill) - 1 || level >= plugin.config().getMaxLevel(skill);

        if (plugin.configBoolean(Option.ACTION_BAR_XP)) {
            plugin.getUiProvider().getActionBarManager().sendXpActionBar(user, skill, currentXp, levelXp, xpGained, level, maxed);
        }
        if (plugin.configBoolean(Option.BOSS_BAR_ENABLED)) {
            plugin.getUiProvider().sendXpBossBar(user, skill, currentXp, levelXp, xpGained, level, maxed);
        }
    }

    public void checkLevelUp(User user, Skill skill) {
        int currentLevel = user.getSkillLevel(skill);
        double currentXp = user.getSkillXp(skill);

        if (currentLevel >= plugin.config().getMaxLevel(skill)) return; // Check max level options
        if (xpRequirements.getListSize(skill) <= currentLevel - 1) return; // Check if skill is maxed

        if (currentXp >= xpRequirements.getXpRequired(skill, currentLevel + 1)) {
            levelUpSkill(user, skill);
        }
    }

    private void levelUpSkill(User user, Skill skill) {
        Locale locale = user.getLocale();

        double currentXp = user.getSkillXp(skill);
        int level = user.getSkillLevel(skill) + 1;

        user.setSkillXp(skill, currentXp - xpRequirements.getXpRequired(skill, level));
        user.setSkillLevel(skill, level);
        // Give custom rewards
        List<SkillReward> rewards = plugin.getRewardManager().getRewardTable(skill).getRewards(level);
        for (SkillReward reward : rewards) {
            reward.giveReward(user, skill, level);
        }
        giveLegacyMoneyRewards(user, level);

        // Reload items and armor to check for newly met requirements
        plugin.getStatManager().reloadPlayer(user);
        // Calls event
        SkillLevelUpEvent event = new SkillLevelUpEvent(plugin.getApi(), user.toApi(), skill, level);
        plugin.getEventManager().callEvent(event);

        // Sends messages
        LevelUpMessenger messenger = new LevelUpMessenger(plugin, user, locale, skill, level, rewards);
        if (plugin.configBoolean(Option.LEVELER_TITLE_ENABLED)) {
            messenger.sendTitle();
        }
        if (plugin.configBoolean(Option.LEVELER_SOUND_ENABLED)) {
            playLevelUpSound(user);
        }
        messenger.sendChatMessage();

        // Check for multiple level ups in a row after a delay
        plugin.getScheduler().scheduleSync(() -> checkLevelUp(user, skill), Tick.MS * plugin.configInt(Option.LEVELER_DOUBLE_CHECK_DELAY), TimeUnit.MILLISECONDS);
    }

    private void giveLegacyMoneyRewards(User user, int level) {
        // Adds money rewards if enabled
        if (plugin.getHookManager().isRegistered(EconomyHook.class)) {
            if (plugin.configBoolean(Option.SKILL_MONEY_REWARDS_ENABLED)) {
                double base = plugin.configDouble(Option.SKILL_MONEY_REWARDS_BASE);
                double multiplier = plugin.configDouble(Option.SKILL_MONEY_REWARDS_MULTIPLIER);
                double moneyToAdd = base + (multiplier * level * level);

                plugin.getHookManager().getHook(EconomyHook.class).deposit(user, moneyToAdd);
            }
        }
    }

    private double calculateMultiplier(@NotNull User user, Skill skill) {
        double multiplier = 1.0;
        multiplier += getItemMultiplier(user, skill);
        multiplier += getPermissionMultiplier(user, skill);
        return multiplier;
    }

    private double getItemMultiplier(@NotNull User user, Skill skill) {
        return user.getTotalMultiplier(skill) / 100;
    }

}
