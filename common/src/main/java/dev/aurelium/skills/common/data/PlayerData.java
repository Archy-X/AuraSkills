package dev.aurelium.skills.common.data;

import dev.aurelium.skills.api.ability.Ability;
import dev.aurelium.skills.api.mana.ManaAbility;
import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.stat.Stat;
import dev.aurelium.skills.api.stat.StatModifier;
import dev.aurelium.skills.api.stat.Stats;
import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.config.Option;
import dev.aurelium.skills.common.modifier.Multiplier;
import dev.aurelium.skills.common.util.data.KeyIntPair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class PlayerData {

    private final AureliumSkillsPlugin plugin;

    private final UUID uuid;

    private final Map<Skill, Integer> skillLevels;
    private final Map<Skill, Double> skillXp;

    private final Map<Stat, Double> statLevels;
    private final Map<String, StatModifier> statModifiers;

    private double mana;
    private Locale locale;

    private final Map<String, Object> metadata;
    private List<KeyIntPair> unclaimedItems;

    private boolean saving;
    private boolean shouldSave;

    // Not persistent data
    private final Map<String, Multiplier> multipliers;

    public PlayerData(UUID uuid, AureliumSkillsPlugin plugin) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.skillLevels = new HashMap<>();
        this.skillXp = new HashMap<>();
        this.statLevels = new HashMap<>();
        this.statModifiers = new HashMap<>();
        this.metadata = new HashMap<>();
        this.unclaimedItems = new LinkedList<>();
        this.saving = false;
        this.shouldSave = true;
        this.mana = plugin.getConfigProvider().getDouble(Option.BASE_MANA);
        this.multipliers = new HashMap<>();
    }

    public AureliumSkillsPlugin getPlugin() {
        return plugin;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getSkillLevel(Skill skill) {
        return skillLevels.getOrDefault(skill, 1);
    }

    public Map<Skill, Integer> getSkillLevelMap() {
        return skillLevels;
    }

    public double getSkillAverage() {
        int sum = 0;
        int numEnabled = 0;
        // Only add enabled skills
        for (Map.Entry<Skill, Integer> entry : skillLevels.entrySet()) {
            if (plugin.getConfigProvider().isEnabled(entry.getKey())) {
                sum += entry.getValue();
                numEnabled ++;
            }
        }
        return sum / (double) numEnabled;
    }

    public void setSkillLevel(Skill skill, int level) {
        skillLevels.put(skill, level);
    }

    public double getSkillXp(Skill skill) {
        return skillXp.getOrDefault(skill, 0.0);
    }

    public Map<Skill, Double> getSkillXpMap() {
        return skillXp;
    }

    public void setSkillXp(Skill skill, double xp) {
        skillXp.put(skill, xp);
    }

    public void addSkillXp(Skill skill, double amount) {
        if (!plugin.getConfigProvider().isEnabled(skill)) return; // Ignore disabled skills

        skillXp.merge(skill, amount, Double::sum);
    }

    public double getStatLevel(Stat stat) {
        return statLevels.getOrDefault(stat, 0.0);
    }

    public void setStatLevel(Stat stat, double level) {
        statLevels.put(stat, level);
    }

    public void addStatLevel(Stat stat, double level) {
        statLevels.merge(stat, level, Double::sum);
    }

    public void addStatLevel(Stat stat, int level) {
        Double currentLevel = statLevels.get(stat);
        if (currentLevel != null) {
            statLevels.put(stat, currentLevel + level);
        } else {
            statLevels.put(stat, (double) level);
        }
    }

    public double getBaseStatLevel(Stat stat) {
        double level = getStatLevel(stat);
        for (StatModifier modifier : statModifiers.values()) {
            if (modifier.getStat() == stat) {
                level -= modifier.getValue();
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
        if (statModifiers.containsKey(modifier.getName())) {
            StatModifier oldModifier = statModifiers.get(modifier.getName());
            if (oldModifier.getStat() == modifier.getStat() && oldModifier.getValue() == modifier.getValue()) {
                return;
            }
            removeStatModifier(modifier.getName());
        }
        statModifiers.put(modifier.getName(), modifier);
        setStatLevel(modifier.getStat(), getStatLevel(modifier.getStat()) + modifier.getValue());
        // Reloads stats
        if (reload) {
            plugin.getStatManager().reloadStat(this, modifier.getStat());
        }
    }

    public boolean removeStatModifier(String name) {
        return removeStatModifier(name, true);
    }

    public boolean removeStatModifier(String name, boolean reload) {
        StatModifier modifier = statModifiers.get(name);
        if (modifier == null) return false;
        setStatLevel(modifier.getStat(), statLevels.get(modifier.getStat()) - modifier.getValue());
        statModifiers.remove(name);
        // Reloads stats
        if (reload) {
            plugin.getStatManager().reloadStat(this, modifier.getStat());
        }
        return true;
    }

    public double getMana() {
        return mana;
    }

    public double getMaxMana() {
        double baseMana = plugin.getConfigProvider().getDouble(Option.BASE_MANA);
        double maxManaPerWisdom = plugin.getConfigProvider().getDouble(Option.WISDOM_MAX_MANA_PER_WISDOM);
        return baseMana + (maxManaPerWisdom * getStatLevel(Stats.WISDOM));
    }

    public double getManaRegen() {
        double baseManaRegen = plugin.getConfigProvider().getDouble(Option.REGENERATION_BASE_REGEN);
        double manaModifier = plugin.getConfigProvider().getDouble(Option.REGENERATION_MANA_MODIFIER);
        return baseManaRegen + getStatLevel(Stats.REGENERATION) * manaModifier;
    }

    public void setMana(double mana) {
        this.mana = mana;
    }

    public Locale getLocale() {
        return locale != null ? locale : plugin.getMessageProvider().getDefaultLanguage();
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public int getAbilityLevel(Ability ability) {
        Skill skill = plugin.getAbilityRegistry().getSkill(ability);
        if (getSkillLevel(skill) < plugin.getAbilityManager().getUnlock(ability)) {
            return 0;
        }
        int level = (getSkillLevel(skill) - plugin.getAbilityManager().getUnlock(ability)) / plugin.getAbilityManager().getLevelUp(ability) + 1;
        // Check max level
        if (level <= plugin.getAbilityManager().getMaxLevel(ability) || plugin.getAbilityManager().getMaxLevel(ability) == 0) {
            return level;
        } else {
            return plugin.getAbilityManager().getMaxLevel(ability);
        }
    }

    public int getManaAbilityLevel(ManaAbility mAbility) {
        // Check if unlocked
        Skill skill = plugin.getManaAbilityRegistry().getSkill(mAbility);
        if (getSkillLevel(skill) < plugin.getManaAbilityManager().getUnlock(mAbility)) {
            return 0;
        }
        int level = (getSkillLevel(skill) - plugin.getManaAbilityManager().getUnlock(mAbility)) / plugin.getManaAbilityManager().getLevelUp(mAbility) + 1;
        // Check max level
        if (level <= plugin.getManaAbilityManager().getMaxLevel(mAbility) || plugin.getManaAbilityManager().getMaxLevel(mAbility) == 0) {
            return level;
        } else {
            return plugin.getManaAbilityManager().getMaxLevel(mAbility);
        }
    }

    public int getPowerLevel() {
        int power = 0;
        for (Map.Entry<Skill, Integer> entry : skillLevels.entrySet()) {
            if (plugin.getConfigProvider().isEnabled(entry.getKey())) {
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
            if (!plugin.getItemRegistry().containsItem(unclaimedItem.getKey())) {
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
                totalMultiplier += multiplier.getValue();
            } else if (multiplier.getSkill() != null && multiplier.getSkill().equals(skill)) {
                totalMultiplier += multiplier.getValue();
            }
        }
        return totalMultiplier;
    }

    public Map<String, Multiplier> getMultipliers() {
        return multipliers;
    }

    public void addMultiplier(Multiplier multiplier) {
        multipliers.put(multiplier.getName(), multiplier);
    }

    public void removeMultiplier(String name) {
        multipliers.remove(name);
    }

    /**
     * Checks if the profile has not had any changes since creation
     * @return True if profile has not been modified, false if player has leveled profile
     */
    public boolean isBlankProfile() {
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
        this.skillLevels.putAll(state.getSkillLevels());

        this.skillXp.clear();
        this.skillXp.putAll(state.getSkillXp());

        this.statModifiers.clear();
        this.statModifiers.putAll(state.getStatModifiers());

        this.mana = state.getMana();

        plugin.getLeveler().updateStats(this);
    }

}
