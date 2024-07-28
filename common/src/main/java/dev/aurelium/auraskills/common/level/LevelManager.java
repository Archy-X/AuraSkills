package dev.aurelium.auraskills.common.level;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.hooks.EconomyHook;
import dev.aurelium.auraskills.common.jobs.JobsBatchData;
import dev.aurelium.auraskills.common.reward.SkillReward;
import dev.aurelium.auraskills.common.scheduler.Tick;
import dev.aurelium.auraskills.common.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Interface with methods to add xp and level up players.
 */
public abstract class LevelManager {

    private final AuraSkillsPlugin plugin;
    protected final XpRequirements xpRequirements;

    public LevelManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.xpRequirements = plugin.getXpRequirements();
    }

    public double getPermissionMultiplier(@NotNull User user, @Nullable Skill skill) {
        return user.getPermissionMultiplier(skill);
    }

    public double getAbilityMultiplier(User user, Skill skill) {
        Ability ability = skill.getXpMultiplierAbility();
        double multiplier = 1.0;
        if (ability != null && user.getAbilityLevel(ability) > 0) {
            double abilityValue = ability.getValue(user.getAbilityLevel(ability));
            double addedMultiplier = abilityValue / 100;
            multiplier += addedMultiplier;
        }
        return multiplier;
    }

    public abstract void playLevelUpSound(@NotNull User user);

    public abstract void reloadModifiers(User user);

    public void addXp(User user, Skill skill, @Nullable XpSource source, double amount) {
        if (amount == 0) return; // Ignore if source amount is 0

        double amountToAdd = amount * calculateMultiplier(user, skill);

        // Call event
        var res = plugin.getEventHandler().callXpGainEvent(user, skill, source, amountToAdd);
        if (res.first()) return;

        addXpRaw(user, skill, res.second(), source);
    }

    protected void addXpRaw(User user, Skill skill, double amount, @Nullable XpSource xpSource) {
        if (amount <= 0.0) return;

        double income = addJobsIncome(user, skill, amount, xpSource);

        user.addSkillXp(skill, amount);
        checkLevelUp(user, skill);
        // Send action bar and boss bar
        sendXpUi(user, skill, amount, income);
    }

    private double addJobsIncome(User user, Skill skill, double amount, @Nullable XpSource source) {
        if (source == null) {
            return 0.0;
        }
        if (!plugin.configBoolean(Option.JOBS_ENABLED)) {
            return 0.0;
        }
        // Selection is required and job is not selected
        if (plugin.config().jobSelectionEnabled() && !user.getJobs().contains(skill)) {
            return 0.0;
        }
        if (!plugin.getHookManager().isRegistered(EconomyHook.class)) {
            return 0.0;
        }

        double income = source.getIncome().getIncomeEarned(user.toApi(), source.getValues(), skill, amount);
        double originalIncome = income;
        boolean displayIndividual = false;

        if (plugin.configBoolean(Option.JOBS_INCOME_BATCHING_ENABLED)) {
            income = handleBatching(user, income); // Sets income to 0 if not enough time has passed, or gets batched income
            displayIndividual = plugin.configBoolean(Option.JOBS_INCOME_BATCHING_DISPLAY_INDIVIDUAL);
        }

        if (income > 0 && plugin.getHookManager().isRegistered(EconomyHook.class)) {
            EconomyHook economy = plugin.getHookManager().getHook(EconomyHook.class);
            economy.deposit(user, income);
        }
        if (displayIndividual) {
            // If batched and display_individual is true, show the individual income to UI instead of batched
            return originalIncome;
        } else {
            return income;
        }
    }

    private double handleBatching(User user, double income) {
        JobsBatchData batchData = user.getJobsBatchData();

        int interval = plugin.configInt(Option.JOBS_INCOME_BATCHING_INTERVAL_MS);
        long lastAdd = batchData.getLastAddTime();
        long now = System.currentTimeMillis();

        if (now > lastAdd + interval) {
            // Execute batch, enough time has passed
            double toAdd = batchData.getAccumulatedIncome() + income;
            batchData.setAccumulatedIncome(0.0);
            batchData.setLastAddTime(now);
            return toAdd;
        } else {
            // Add to next batch, don't add income this time
            batchData.addAccumulatedIncome(income);
            return 0.0;
        }
    }

    public void setXp(User user, Skill skill, double amount) {
        double originalAmount = user.getSkillXp(skill);
        // Sets Xp
        user.setSkillXp(skill, amount);
        // Check if player leveled up
        checkLevelUp(user, skill);
        // Sends action bar message
        double xpAmount = amount - originalAmount;

        sendXpUi(user, skill, xpAmount, 0.0);
    }

    private void sendXpUi(User user, Skill skill, double xpGained, double income) {
        double currentXp = user.getSkillXp(skill);
        int level = user.getSkillLevel(skill);
        double levelXp = xpRequirements.getXpRequired(skill, level + 1);
        boolean maxed = xpRequirements.getListSize(skill) <= user.getSkillLevel(skill) - 1 || level >= skill.getMaxLevel();

        plugin.getUiProvider().getActionBarManager().sendXpActionBar(user, skill, currentXp, levelXp, xpGained, level, maxed, income);
        if (plugin.configBoolean(Option.BOSS_BAR_ENABLED)) {
            plugin.getUiProvider().sendXpBossBar(user, skill, currentXp, levelXp, xpGained, level, maxed, income);
        }
    }

    public void checkLevelUp(User user, Skill skill) {
        int currentLevel = user.getSkillLevel(skill);
        double currentXp = user.getSkillXp(skill);

        if (currentLevel >= skill.getMaxLevel()) return; // Check max level options
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

        // Reload items and armor to check for newly met requirements
        reloadModifiers(user);
        // Calls event
        plugin.getEventHandler().callSkillLevelUpEvent(user, skill, level);

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

    public double calculateMultiplier(@NotNull User user, Skill skill) {
        double multiplier = 1.0;
        multiplier += getItemMultiplier(user, skill);
        multiplier += getPermissionMultiplier(user, skill);
        return getAbilityMultiplier(user, skill) * multiplier;
    }

    public double getGenericMultiplier(User user) {
        double multiplier = 1.0;
        multiplier += getItemMultiplier(user, null);
        multiplier += getPermissionMultiplier(user, null);
        return multiplier;
    }

    public double getItemMultiplier(@NotNull User user, Skill skill) {
        return user.getTotalMultiplier(skill) / 100;
    }

}
