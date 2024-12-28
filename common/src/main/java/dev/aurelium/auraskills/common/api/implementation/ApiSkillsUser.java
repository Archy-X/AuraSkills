package dev.aurelium.auraskills.common.api.implementation;


import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.api.user.SkillsUser;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.ui.ActionBarManager;
import dev.aurelium.auraskills.common.user.User;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ApiSkillsUser implements SkillsUser {

    private final User user;
    private final AuraSkillsPlugin plugin;

    public ApiSkillsUser(User user) {
        this.user = user;
        this.plugin = user.getPlugin();
    }

    public User getUser() {
        return user;
    }

    @Override
    public UUID getUuid() {
        return user.getUuid();
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public double getSkillXp(Skill skill) {
        return user.getSkillXp(skill);
    }

    @Override
    public void addSkillXp(Skill skill, double amountToAdd) {
        plugin.getLevelManager().addXp(user, skill, null, amountToAdd);
    }

    @Override
    public void addSkillXp(Skill skill, double amountToAdd, XpSource source) {
        plugin.getLevelManager().addXp(user, skill, source, amountToAdd);
    }

    @Override
    public void addSkillXpRaw(Skill skill, double amountToAdd) {
        user.addSkillXp(skill, amountToAdd);
        plugin.getLevelManager().checkLevelUp(user, skill);
    }

    @Override
    public void setSkillXp(Skill skill, double amount) {
        user.setSkillXp(skill, amount);
        plugin.getLevelManager().checkLevelUp(user, skill);
    }

    @Override
    public int getSkillLevel(Skill skill) {
        return user.getSkillLevel(skill);
    }

    @Override
    public void setSkillLevel(Skill skill, int level) {
        setSkillLevel(skill, level, true);
    }

    @Override
    public void setSkillLevel(Skill skill, int level, boolean refresh) {
        int oldLevel = user.getSkillLevel(skill);
        user.setSkillLevel(skill, level);

        if (refresh) {
            plugin.getStatManager().updateStats(user);
            plugin.getRewardManager().updatePermissions(user);
            plugin.getRewardManager().applyRevertCommands(user, skill, oldLevel, level);
            plugin.getRewardManager().applyLevelUpCommands(user, skill, oldLevel, level);
            // Reload items and armor to check for newly met requirements
            this.plugin.getModifierManager().reloadUser(user);
        }
    }

    @Override
    public double getSkillAverage() {
        return user.getSkillAverage();
    }

    @Override
    public double getStatLevel(Stat stat) {
        return user.getStatLevel(stat);
    }

    @Override
    public double getBaseStatLevel(Stat stat) {
        return user.getBaseStatLevel(stat);
    }

    @Override
    public double getMana() {
        return user.getMana();
    }

    @Override
    public double getMaxMana() {
        return user.getMaxMana();
    }

    @Override
    public void setMana(double mana) {
        user.setMana(mana);
    }

    @Override
    public boolean consumeMana(double amount) {
        if (user.getMana() >= amount) {
            user.setMana(user.getMana() - amount);
            return true;
        } else {
            plugin.getManaAbilityManager().sendNotEnoughManaMessage(user, amount);
            return false;
        }
    }

    @Override
    public int getPowerLevel() {
        return user.getPowerLevel();
    }

    @Override
    public void addStatModifier(StatModifier statModifier) {
        user.addStatModifier(statModifier);
    }

    @Override
    public void removeStatModifier(String name) {
        user.removeStatModifier(name);
    }

    @Override
    public @Nullable StatModifier getStatModifier(String name) {
        return user.getStatModifier(name);
    }

    @Override
    public Map<String, StatModifier> getStatModifiers() {
        return user.getStatModifiers();
    }

    @Override
    public void addTraitModifier(TraitModifier traitModifier) {
        user.addTraitModifier(traitModifier);
    }

    @Override
    public void removeTraitModifier(String name) {
        user.removeTraitModifier(name);
    }

    @Override
    public @Nullable TraitModifier getTraitModifier(String name) {
        return user.getTraitModifier(name);
    }

    @Override
    public Map<String, TraitModifier> getTraitModifiers() {
        return user.getTraitModifiers();
    }

    @Override
    public double getEffectiveTraitLevel(Trait trait) {
        return user.getEffectiveTraitLevel(trait);
    }

    @Override
    public double getBonusTraitLevel(Trait trait) {
        return user.getBonusTraitLevel(trait);
    }

    @Override
    public int getAbilityLevel(Ability ability) {
        return user.getAbilityLevel(ability);
    }

    @Override
    public int getManaAbilityLevel(ManaAbility manaAbility) {
        return user.getManaAbilityLevel(manaAbility);
    }

    @Override
    public Locale getLocale() {
        return user.getLocale();
    }

    @Override
    public boolean hasSkillPermission(Skill skill) {
        return user.hasSkillPermission(skill);
    }

    @Override
    public Set<Skill> getJobs() {
        return user.getJobs();
    }

    @Override
    public void addJob(Skill job) {
        user.addJob(job);
    }

    @Override
    public void removeJob(Skill job) {
        user.removeJob(job);
    }

    @Override
    public void clearAllJobs() {
        user.clearAllJobs();
    }

    @Override
    public int getJobLimit() {
        return user.getJobLimit();
    }

    @Override
    public void sendActionBar(String message) {
        plugin.getUiProvider().sendActionBar(user, message);
        plugin.getUiProvider().getActionBarManager().setPaused(user, ActionBarManager.PAUSE_MS, TimeUnit.MILLISECONDS);
    }

    @Override
    public void sendActionBar(String message, int duration, TimeUnit timeUnit) {
        plugin.getUiProvider().sendActionBar(user, message);
        plugin.getUiProvider().getActionBarManager().setPaused(user, duration, timeUnit);
    }

    @Override
    public void pauseActionBar(int duration, TimeUnit timeUnit) {
        plugin.getUiProvider().getActionBarManager().setPaused(user, duration, timeUnit);
    }

    @Override
    public CompletableFuture<Boolean> save(boolean removeFromMemory) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        plugin.getScheduler().executeAsync(() -> {
            try {
                plugin.getStorageProvider().saveSafely(user);
                if (removeFromMemory) {
                    plugin.getUserManager().removeUser(user.getUuid());
                }
                future.complete(true);
            } catch (Exception e) {
                e.printStackTrace();
                future.complete(false);
            }
        });
        return future;
    }

}
