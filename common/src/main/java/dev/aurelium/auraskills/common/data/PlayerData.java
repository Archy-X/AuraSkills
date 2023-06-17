package dev.aurelium.auraskills.common.data;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.ability.AbstractAbility;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.player.SkillsPlayer;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.ability.AbilityData;
import dev.aurelium.auraskills.common.api.implementation.ApiSkillsPlayer;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.modifier.Multiplier;
import dev.aurelium.auraskills.common.util.data.KeyIntPair;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PlayerData {

    private final AuraSkillsPlugin plugin;

    private final UUID uuid;

    private final Map<Skill, Integer> skillLevels;
    private final Map<Skill, Double> skillXp;

    private final Map<Stat, Double> statLevels;
    private final Map<String, StatModifier> statModifiers;

    private double mana;
    private Locale locale;

    private final Map<AbstractAbility, AbilityData> abilityData;
    private final Map<String, Object> metadata;
    private List<KeyIntPair> unclaimedItems;

    private boolean saving;
    private boolean shouldSave;
    private boolean blank = true;

    // Not persistent data
    private final Map<String, Multiplier> multipliers;

    public PlayerData(UUID uuid, AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.skillLevels = new ConcurrentHashMap<>();
        this.skillXp = new ConcurrentHashMap<>();
        this.statLevels = new ConcurrentHashMap<>();
        this.statModifiers = new ConcurrentHashMap<>();
        this.abilityData = new ConcurrentHashMap<>();
        this.metadata = new ConcurrentHashMap<>();
        this.unclaimedItems = new LinkedList<>();
        this.saving = false;
        this.shouldSave = true;
        this.mana = plugin.config().getDouble(Option.BASE_MANA);
        this.multipliers = new HashMap<>();
    }

    public AuraSkillsPlugin getPlugin() {
        return plugin;
    }

    public UUID getUuid() {
        return uuid;
    }

    // Abstract methods to be implemented by the platform-specific PlayerData class
    public abstract String getUsername();

    public abstract void sendMessage(Component component);

    public abstract double getPermissionMultiplier(Skill skill);

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
            if (plugin.config().isEnabled(entry.getKey())) {
                sum += entry.getValue();
                numEnabled ++;
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
        if (!plugin.config().isEnabled(skill)) return; // Ignore disabled skills

        skillXp.merge(skill, amount, Double::sum);
        if (amount > 0.0) { // Mark as modified
            blank = false;
        }
    }

    public double getStatLevel(Stat stat) {
        return statLevels.getOrDefault(stat, 0.0);
    }

    public void setStatLevel(Stat stat, double level) {
        statLevels.put(stat, level);
        if (level > 0.0) { // Mark as modified
            blank = false;
        }
    }

    public void addStatLevel(Stat stat, double level) {
        statLevels.merge(stat, level, Double::sum);
        if (level > 0.0) { // Mark as modified
            blank = false;
        }
    }

    public double getBaseStatLevel(Stat stat) {
        double level = getStatLevel(stat);
        for (StatModifier modifier : statModifiers.values()) {
            if (modifier.stat() == stat) {
                level -= modifier.value();
            }
        }
        return level;
    }

    public StatModifier getStatModifier(String name) {
        return statModifiers.get(name);
    }

    public Map<String, StatModifier> getStatModifiers() {
        return statModifiers;
    }

    public void addStatModifier(StatModifier modifier) {
        addStatModifier(modifier, true);
    }

    public void addStatModifier(StatModifier modifier, boolean reload) {
        // Removes if already existing
        if (statModifiers.containsKey(modifier.name())) {
            StatModifier oldModifier = statModifiers.get(modifier.name());
            if (oldModifier.stat() == modifier.stat() && oldModifier.value() == modifier.value()) {
                return;
            }
            removeStatModifier(modifier.name());
        }
        statModifiers.put(modifier.name(), modifier);
        setStatLevel(modifier.stat(), getStatLevel(modifier.stat()) + modifier.value());
        // Reloads stats
        if (reload) {
            plugin.getStatManager().reloadStat(this, modifier.stat());
        }
        blank = false; // Mark as modified
    }

    public boolean removeStatModifier(String name) {
        return removeStatModifier(name, true);
    }

    public boolean removeStatModifier(String name, boolean reload) {
        StatModifier modifier = statModifiers.get(name);
        if (modifier == null) return false;
        setStatLevel(modifier.stat(), statLevels.get(modifier.stat()) - modifier.value());
        statModifiers.remove(name);
        // Reloads stats
        if (reload) {
            plugin.getStatManager().reloadStat(this, modifier.stat());
        }
        return true;
    }

    public double getMana() {
        return mana;
    }

    public double getMaxMana() {
        double baseMana = plugin.config().getDouble(Option.BASE_MANA);
        double maxManaPerWisdom = plugin.config().getDouble(Option.WISDOM_MAX_MANA_PER_WISDOM);
        return baseMana + (maxManaPerWisdom * getStatLevel(Stats.WISDOM));
    }

    public double getManaRegen() {
        double baseManaRegen = plugin.config().getDouble(Option.REGENERATION_BASE_REGEN);
        double manaModifier = plugin.config().getDouble(Option.REGENERATION_MANA_MODIFIER);
        return baseManaRegen + getStatLevel(Stats.REGENERATION) * manaModifier;
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
    }

    public AbilityData getAbilityData(AbstractAbility ability) {
        AbilityData data = abilityData.get(ability);
        if (data == null) {
            data = new AbilityData(ability);
            abilityData.put(ability, data);
        }
        return data;
    }

    public boolean containsAbilityData(AbstractAbility ability) {
        return abilityData.containsKey(ability);
    }

    public Map<AbstractAbility, AbilityData> getAbilityDataMap() {
        return abilityData;
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
            if (plugin.config().isEnabled(entry.getKey())) {
                power += entry.getValue();
            }
        }
        return power;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public List<KeyIntPair> getUnclaimedItems() {
        return unclaimedItems;
    }

    public void clearInvalidItems() {
        // Find items that are not registered
        List<KeyIntPair> toRemove = new ArrayList<>();
        for (KeyIntPair unclaimedItem : unclaimedItems) {
            if (!plugin.getItemRegistry().containsItem(NamespacedId.fromStringOrDefault(unclaimedItem.getKey()))) {
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

    public boolean isSaving() {
        return saving;
    }

    public void setSaving(boolean saving) {
        this.saving = saving;
    }

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

    /**
     * Checks if the profile has not had any changes since creation
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
        for (double statLevel : statLevels.values()) {
            if (statLevel > 0.0) {
                return false;
            }
        }
        if (statModifiers.size() > 0) {
            return false;
        }
        return true;
    }

    public PlayerDataState getState() {
        Map<Skill, Integer> copiedLevels = new HashMap<>(skillLevels);
        Map<Skill, Double> copiedXp = new HashMap<>(skillXp);
        Map<String, StatModifier> copiedStatModifiers = new HashMap<>(statModifiers);
        return new PlayerDataState(uuid, copiedLevels, copiedXp, copiedStatModifiers, mana);
    }

    public void applyState(PlayerDataState state) {
        this.skillLevels.clear();
        this.skillLevels.putAll(state.skillLevels());

        this.skillXp.clear();
        this.skillXp.putAll(state.skillXp());

        this.statModifiers.clear();
        this.statModifiers.putAll(state.statModifiers());

        this.mana = state.mana();

        plugin.getStatManager().updateStats(this);
    }

    public SkillsPlayer toApi() {
        return new ApiSkillsPlayer(this);
    }

}
