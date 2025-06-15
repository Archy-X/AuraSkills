package dev.aurelium.auraskills.common.user;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.ability.AbstractAbility;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Multiplier;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.api.user.SkillsUser;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.ability.AbilityData;
import dev.aurelium.auraskills.common.antiafk.CheckData;
import dev.aurelium.auraskills.common.antiafk.CheckType;
import dev.aurelium.auraskills.common.api.implementation.ApiSkillsUser;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.jobs.JobsBatchData;
import dev.aurelium.auraskills.common.mana.ManaAbilityData;
import dev.aurelium.auraskills.common.ui.ActionBarType;
import dev.aurelium.auraskills.common.util.data.KeyIntPair;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class User {

    protected final AuraSkillsPlugin plugin;

    protected final UUID uuid;

    private final Map<Skill, Integer> skillLevels;
    private final Map<Skill, Double> skillXp;

    private final UserStats userStats;

    private double mana;
    private Locale locale;

    private final Map<AbstractAbility, AbilityData> abilityData;
    private final Map<ManaAbility, ManaAbilityData> manaAbilityData;
    private final Map<String, Object> metadata;
    private List<KeyIntPair> unclaimedItems;
    private final Map<ActionBarType, Boolean> actionBarSettings;
    private final Set<Skill> jobs;
    private long lastJobSelectTime;
    private final List<AntiAfkLog> sessionAntiAfkLogs;

    private boolean shouldSave;
    private boolean blank = true;

    // Not persistent data
    private final Map<String, Multiplier> multipliers;
    private final JobsBatchData jobsBatchData;
    private final Map<CheckType, CheckData> checkData = new HashMap<>();
    @Nullable
    private List<AntiAfkLog> storedAntiAfkLogs;
    private double currentOriginalDamage;

    public User(UUID uuid, AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.skillLevels = new ConcurrentHashMap<>();
        this.skillXp = new ConcurrentHashMap<>();
        this.userStats = new UserStats(plugin, this);
        this.abilityData = new ConcurrentHashMap<>();
        this.manaAbilityData = new ConcurrentHashMap<>();
        this.metadata = new ConcurrentHashMap<>();
        this.actionBarSettings = new ConcurrentHashMap<>();
        this.unclaimedItems = new LinkedList<>();
        this.shouldSave = true;
        this.mana = Traits.MAX_MANA.isEnabled() ? Traits.MAX_MANA.optionDouble("base") : 0.0;
        this.multipliers = new HashMap<>();
        this.jobs = new HashSet<>();
        this.jobsBatchData = new JobsBatchData();
        this.sessionAntiAfkLogs = new ArrayList<>();
        this.lastJobSelectTime = 0;
    }

    public AuraSkillsPlugin getPlugin() {
        return plugin;
    }

    public UUID getUuid() {
        return uuid;
    }

    // Abstract methods to be implemented by the platform-specific PlayerData class
    public abstract String getUsername();

    public abstract void sendMessage(String message);

    public abstract void sendMessage(Component component);

    public abstract double getPermissionMultiplier(Skill skill);

    public abstract boolean hasSkillPermission(Skill skill);

    public abstract void setCommandLocale(Locale locale);

    public abstract int getPermissionJobLimit();

    public abstract String getWorld();

    public abstract boolean hasPermission(String permission);

    public int getSkillLevel(Skill skill) {
        return skillLevels.getOrDefault(skill, plugin.config().getStartLevel());
    }

    public Map<Skill, Integer> getSkillLevelMap() {
        return skillLevels;
    }

    public double getSkillAverage() {
        int sum = 0;
        int numEnabled = 0;
        // Only add enabled skills
        for (Map.Entry<Skill, Integer> entry : skillLevels.entrySet()) {
            if (entry.getKey().isEnabled()) {
                sum += entry.getValue();
                numEnabled++;
            }
        }
        return sum / (double) numEnabled;
    }

    public void setSkillLevel(Skill skill, int level) {
        skillLevels.put(skill, level);
        if (level > plugin.config().getStartLevel()) { // Mark as modified
            blank = false;
        }
    }

    public double getSkillXp(Skill skill) {
        return skillXp.getOrDefault(skill, 0.0);
    }

    public Map<Skill, Double> getSkillXpMap() {
        return skillXp;
    }

    public void setSkillXp(Skill skill, double xp) {
        skillXp.put(skill, xp);
        if (xp > 0.0) { // Mark as modified
            blank = false;
        }
    }

    public void addSkillXp(Skill skill, double amount) {
        if (!skill.isEnabled()) return; // Ignore disabled skills

        // Add XP while ensuring it is not negative
        skillXp.merge(skill, amount, (existing, added) -> Math.max(existing + added, 0.0));
        if (amount > 0.0) { // Mark as modified
            blank = false;
        }
    }

    public int resetSkill(Skill skill) {
        int oldLevel = getSkillLevel(skill);
        int startLevel = plugin.config().getStartLevel();
        setSkillLevel(skill, startLevel);
        setSkillXp(skill, 0);
        plugin.getStatManager().recalculateStats(this);
        plugin.getRewardManager().updatePermissions(this);
        plugin.getRewardManager().applyRevertCommands(this, skill, oldLevel, startLevel);
        return startLevel;
    }

    public UserStats getUserStats() {
        return userStats;
    }

    public double getStatLevel(Stat stat) {
        return userStats.getStatLevel(stat);
    }

    public double getBaseStatLevel(Stat stat) {
        return userStats.getBaseStatLevel(stat);
    }

    @Nullable
    public StatModifier getStatModifier(String name) {
        return userStats.getStatModifier(name);
    }

    public Map<String, StatModifier> getStatModifiers() {
        return userStats.getStatModifiers();
    }

    public void addStatModifier(StatModifier modifier) {
        userStats.addStatModifier(modifier, true);
    }

    public void addStatModifier(StatModifier modifier, boolean reload) {
        userStats.addStatModifier(modifier, reload);
    }

    public boolean removeStatModifier(String name) {
        return userStats.removeStatModifier(name, true);
    }

    public boolean removeStatModifier(String name, boolean reload) {
        return userStats.removeStatModifier(name, reload);
    }

    public double getEffectiveTraitLevel(Trait trait) {
        return userStats.getEffectiveTraitLevel(trait);
    }

    public double getBonusTraitLevel(Trait trait) {
        return userStats.getBonusTraitLevel(trait);
    }

    @Nullable
    public TraitModifier getTraitModifier(String name) {
        return userStats.getTraitModifier(name);
    }

    public Map<String, TraitModifier> getTraitModifiers() {
        return userStats.getTraitModifiers();
    }

    public void addTraitModifier(TraitModifier modifier) {
        userStats.addTraitModifier(modifier, true);
    }

    public void addTraitModifier(TraitModifier modifier, boolean reload) {
        userStats.addTraitModifier(modifier, reload);
    }

    public boolean removeTraitModifier(String name) {
        return userStats.removeTraitModifier(name, true);
    }

    public boolean removeTraitModifier(String name, boolean reload) {
        return userStats.removeTraitModifier(name, reload);
    }

    public double getMana() {
        return mana;
    }

    public double getMaxMana() {
        return getEffectiveTraitLevel(Traits.MAX_MANA);
    }

    public void setMana(double mana) {
        this.mana = mana;
    }

    public Locale getLocale() {
        return locale != null ? locale : plugin.getMessageProvider().getDefaultLanguage();
    }

    public boolean hasLocale() {
        return locale != null;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        setCommandLocale(locale);
    }

    public AbilityData getAbilityData(AbstractAbility ability) {
        return abilityData.computeIfAbsent(ability, AbilityData::new);
    }

    public Map<AbstractAbility, AbilityData> getAbilityDataMap() {
        return abilityData;
    }

    public ManaAbilityData getManaAbilityData(ManaAbility manaAbility) {
        return manaAbilityData.computeIfAbsent(manaAbility, ManaAbilityData::new);
    }

    public Map<ManaAbility, ManaAbilityData> getManaAbilityDataMap() {
        return manaAbilityData;
    }

    public int getAbilityLevel(Ability ability) {
        Skill skill = ability.getSkill();
        if (getSkillLevel(skill) < ability.getUnlock()) {
            return 0;
        }
        int level = (getSkillLevel(skill) - ability.getUnlock()) / ability.getLevelUp() + 1;
        // Check max level
        if (level <= ability.getMaxLevel() || ability.getMaxLevel() == 0) {
            return level;
        } else {
            return ability.getMaxLevel();
        }
    }

    public int getAbstractAbilityLevel(AbstractAbility abstractAbility) {
        if (abstractAbility instanceof Ability ability) {
            return getAbilityLevel(ability);
        } else if (abstractAbility instanceof ManaAbility manaAbility) {
            return getManaAbilityLevel(manaAbility);
        }
        return 0;
    }

    public int getManaAbilityLevel(ManaAbility mAbility) {
        // Check if unlocked
        Skill skill = mAbility.getSkill();
        if (getSkillLevel(skill) < mAbility.getUnlock()) {
            return 0;
        }
        int level = (getSkillLevel(skill) - mAbility.getUnlock()) / mAbility.getLevelUp() + 1;
        // Check max level
        if (level <= mAbility.getMaxLevel() || mAbility.getMaxLevel() == 0) {
            return level;
        } else {
            return mAbility.getMaxLevel();
        }
    }

    public int getPowerLevel() {
        int power = 0;
        for (Map.Entry<Skill, Integer> entry : skillLevels.entrySet()) {
            if (entry.getKey().isEnabled()) {
                power += entry.getValue();
            }
        }
        return power;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public boolean metadataBoolean(String key) {
        Object o = metadata.getOrDefault(key, false);
        if (o instanceof Boolean) {
            return (boolean) o;
        }
        return false;
    }

    public List<KeyIntPair> getUnclaimedItems() {
        return unclaimedItems;
    }

    public void clearInvalidItems() {
        // Find items that are not registered
        List<KeyIntPair> toRemove = new ArrayList<>();
        for (KeyIntPair unclaimedItem : unclaimedItems) {
            if (!plugin.getItemRegistry().containsItem(NamespacedId.fromDefault(unclaimedItem.getKey()))) {
                toRemove.add(unclaimedItem);
            }
        }
        // Remove from unclaimed items list
        for (KeyIntPair unclaimedItemToRemove : toRemove) {
            unclaimedItems.remove(unclaimedItemToRemove);
        }
    }

    public void setUnclaimedItems(@NotNull List<KeyIntPair> unclaimedItems) {
        this.unclaimedItems = unclaimedItems;
    }

    public Set<Skill> getJobs() {
        return Collections.unmodifiableSet(jobs);
    }

    public void addJob(Skill skill) {
        if (jobs.size() < getJobLimit() && canSelectJob(skill)) {
            jobs.add(skill);
            setLastJobSelectTime(System.currentTimeMillis());
            blank = false;
        }
    }

    public void removeJob(Skill skill) {
        jobs.remove(skill);
        blank = false;
    }

    public void clearAllJobs() {
        jobs.clear();
    }

    public int getJobLimit() {
        int permLimit = getPermissionJobLimit();
        if (permLimit > 0) {
            return permLimit;
        }
        return plugin.configInt(Option.JOBS_SELECTION_DEFAULT_JOB_LIMIT);
    }

    public abstract boolean canSelectJob(@NotNull Skill skill);

    public boolean shouldNotSave() {
        return !shouldSave;
    }

    public void setShouldSave(boolean shouldSave) {
        this.shouldSave = shouldSave;
    }

    public double getTotalMultiplier(@Nullable Skill skill) {
        double totalMultiplier = 0.0;
        for (Multiplier multiplier : getMultipliers().values()) {
            if (multiplier.isGlobal()) {
                totalMultiplier += multiplier.value();
            } else if (multiplier.skill() != null && skill != null && multiplier.skill().equals(skill)) {
                totalMultiplier += multiplier.value();
            }
        }
        return totalMultiplier;
    }

    public Map<String, Multiplier> getMultipliers() {
        return multipliers;
    }

    public void addMultiplier(Multiplier multiplier) {
        multipliers.put(multiplier.name(), multiplier);
    }

    public void removeMultiplier(String name) {
        multipliers.remove(name);
    }

    public boolean isActionBarEnabled(ActionBarType type) {
        return actionBarSettings.getOrDefault(type, true);
    }

    public void setActionBarSetting(ActionBarType type, boolean enabled) {
        this.actionBarSettings.put(type, enabled);
    }

    public JobsBatchData getJobsBatchData() {
        return jobsBatchData;
    }

    public long getLastJobSelectTime() {
        return lastJobSelectTime;
    }

    public void setLastJobSelectTime(long lastJobSelectTime) {
        this.lastJobSelectTime = lastJobSelectTime;
    }

    public List<AntiAfkLog> getSessionAntiAfkLogs() {
        return sessionAntiAfkLogs;
    }

    @NotNull
    public CheckData getCheckData(CheckType type) {
        return checkData.computeIfAbsent(type, t -> new CheckData());
    }

    public Optional<List<AntiAfkLog>> getStoredAntiAfkLogs() {
        return Optional.ofNullable(storedAntiAfkLogs);
    }

    public void setStoredAntiAfkLogs(@NotNull List<AntiAfkLog> logs) {
        this.storedAntiAfkLogs = logs;
    }

    public double getCurrentOriginalDamage() {
        return currentOriginalDamage;
    }

    public void setCurrentOriginalDamage(double currentOriginalDamage) {
        this.currentOriginalDamage = currentOriginalDamage;
    }

    /**
     * Checks if the profile has not had any changes since creation
     *
     * @return True if profile has not been modified, false if player has leveled profile
     */
    public boolean isBlankProfile() {
        if (blank) {
            return true;
        }
        for (int level : skillLevels.values()) {
            if (level > 1) {
                return false;
            }
        }
        for (double xp : skillXp.values()) {
            if (xp > 0.0) {
                return false;
            }
        }
        if (!jobs.isEmpty()) {
            return false;
        }
        return userStats.getStatModifiers().isEmpty() && userStats.getTraitModifiers().isEmpty() && unclaimedItems.isEmpty();
    }

    public UserState getState() {
        Map<Skill, Integer> copiedLevels = new HashMap<>(skillLevels);
        Map<Skill, Double> copiedXp = new HashMap<>(skillXp);
        Map<String, StatModifier> copiedStatModifiers = new HashMap<>(userStats.getStatModifiers());
        Map<String, TraitModifier> copiedTraitModifiers = new HashMap<>(userStats.getTraitModifiers());
        return new UserState(uuid, copiedLevels, copiedXp, copiedStatModifiers, copiedTraitModifiers, mana);
    }

    public void applyState(UserState state) {
        this.skillLevels.clear();
        this.skillLevels.putAll(state.skillLevels());

        this.skillXp.clear();
        this.skillXp.putAll(state.skillXp());

        this.userStats.getStatModifiers().clear();
        this.userStats.getStatModifiers().putAll(state.statModifiers());

        this.userStats.getTraitModifiers().clear();
        this.userStats.getTraitModifiers().putAll(state.traitModifiers());

        this.mana = state.mana();

        plugin.getStatManager().recalculateStats(this);
    }

    public void cleanUp() {

    }

    public SkillsUser toApi() {
        return new ApiSkillsUser(this);
    }

}
