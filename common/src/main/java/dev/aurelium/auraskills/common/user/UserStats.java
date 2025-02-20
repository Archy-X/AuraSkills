package dev.aurelium.auraskills.common.user;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.api.util.AuraSkillsModifier;
import dev.aurelium.auraskills.api.util.AuraSkillsModifier.Operation;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class UserStats {

    private final AuraSkillsPlugin plugin;
    private final User user;

    private final Map<Stat, Double> statLevels = new ConcurrentHashMap<>();
    private final Map<Stat, Double> baseStatLevels = new ConcurrentHashMap<>();
    private final Map<String, StatModifier> statModifiers = new ConcurrentHashMap<>();

    private final Map<Trait, Double> traitLevels = new ConcurrentHashMap<>();
    private final Map<Trait, Double> bonusTraitLevels = new ConcurrentHashMap<>();
    private final Map<String, TraitModifier> traitModifiers = new ConcurrentHashMap<>();

    public UserStats(AuraSkillsPlugin plugin, User user) {
        this.plugin = plugin;
        this.user = user;
    }

    public double getStatLevel(Stat stat) {
        return statLevels.getOrDefault(stat, 0.0);
    }

    public double getBaseStatLevel(Stat stat) {
        return baseStatLevels.getOrDefault(stat, 0.0);
    }

    @Nullable
    public StatModifier getStatModifier(String name) {
        return statModifiers.get(name);
    }

    public Map<String, StatModifier> getStatModifiers() {
        return statModifiers;
    }

    public void addStatModifier(StatModifier modifier, boolean reload) {
        addModifier(modifier, reload, statModifiers);
    }

    public boolean removeStatModifier(String name, boolean reload) {
        return removeModifier(name, reload, statModifiers);
    }

    public double getEffectiveTraitLevel(Trait trait) {
        return traitLevels.getOrDefault(trait, 0.0);
    }

    public double getBonusTraitLevel(Trait trait) {
        return bonusTraitLevels.getOrDefault(trait, 0.0);
    }

    @Nullable
    public TraitModifier getTraitModifier(String name) {
        return traitModifiers.get(name);
    }

    public Map<String, TraitModifier> getTraitModifiers() {
        return traitModifiers;
    }

    public void addTraitModifier(TraitModifier modifier, boolean reload) {
        addModifier(modifier, reload, traitModifiers);
    }

    public boolean removeTraitModifier(String name, boolean reload) {
        return removeModifier(name, reload, traitModifiers);
    }

    private <T extends AuraSkillsModifier<V>, V> void addModifier(T modifier, boolean reload, Map<String, T> map) {
        if (map.containsKey(modifier.name())) {
            AuraSkillsModifier<V> oldModifier = map.get(modifier.name());
            if (oldModifier.type() == modifier.type() && oldModifier.value() == modifier.value()) {
                return;
            }
            // Do not reload on remove since that would reset health and other stuff (mainly for 3rd party plugins)
            // Reload will happen at the end of this method if it was true either way.
            // So here we are just preventing double stat reload.
            removeModifier(modifier.name(), false, map);
        }
        map.put(modifier.name(), modifier);

        if (modifier instanceof StatModifier statModifier) {
            recalculateStat(statModifier.stat());
        } else if (modifier instanceof TraitModifier traitModifier) {
            recalculateTrait(traitModifier.trait());
        }
        // Reloads modifier type
        if (reload) {
            plugin.getStatManager().reload(user, modifier.type());
        }
    }

    private <T extends AuraSkillsModifier<V>, V> boolean removeModifier(String name, boolean reload, Map<String, T> map) {
        AuraSkillsModifier<V> modifier = map.get(name);
        if (modifier == null) return false;
        map.remove(name);

        if (modifier instanceof StatModifier statModifier) {
            recalculateStat(statModifier.stat());
        } else if (modifier instanceof TraitModifier traitModifier) {
            recalculateTrait(traitModifier.trait());
        }

        // Reloads modifier type
        if (reload) {
            plugin.getStatManager().reload(user, modifier.type());
        }
        return true;
    }

    /**
     * Fully recalculates a stat including base and modifiers, and recalculates its traits.
     *
     * @param stat the stat to recalculate
     */
    public void recalculateStat(Stat stat) {
        double base = calculateRewardsStat(stat);
        double level = calculateModifiers(base, statModifiers.values(), stat);

        statLevels.put(stat, level);
        baseStatLevels.put(stat, base);

        for (Trait trait : stat.getTraits()) {
            recalculateTrait(trait);
        }
    }

    public void recalculateTrait(Trait trait) {
        double level = plugin.getTraitManager().getBaseLevel(user, trait);
        double base = level;

        if (!trait.isEnabled()) {
            traitLevels.put(trait, level);
            bonusTraitLevels.put(trait, 0.0);
            return;
        }

        level = getTraitLevelFromStats(trait, level);
        level = calculateModifiers(level, traitModifiers.values(), trait);

        traitLevels.put(trait, level);
        bonusTraitLevels.put(trait, level - base);
    }

    private <T> double calculateModifiers(double base, Collection<? extends AuraSkillsModifier<T>> modifiers, T filter) {
        double addModSum = 0.0;
        double multiplyModProduct = 1.0;
        double addPercentSum = 0.0;

        for (AuraSkillsModifier<T> modifier : modifiers) {
            if (!modifier.type().equals(filter)) continue;

            switch (modifier.operation()) {
                case ADD -> addModSum += modifier.value();
                case MULTIPLY -> multiplyModProduct *= modifier.value();
                case ADD_PERCENT -> addPercentSum += modifier.value();
            }
        }

        return (base + addModSum) * multiplyModProduct * (1 + addPercentSum / 100);
    }

    private double getTraitLevelFromStats(Trait trait, double base) {
        for (Stat stat : plugin.getTraitManager().getLinkedStats(trait)) {
            base += getStatLevel(stat) * stat.getTraitModifier(trait);
        }
        return base;
    }

    // Includes base from rewards plus all ADD operation modifiers
    public double getStatBaseAddSum(Stat stat) {
        double base = getBaseStatLevel(stat);
        for (StatModifier modifier : statModifiers.values()) {
            if (!modifier.stat().equals(stat)) continue;
            if (modifier.operation() != Operation.ADD) continue;

            base += modifier.value();
        }
        return base;
    }

    public double getTraitBaseAddSum(Trait trait) {
        double base = plugin.getTraitManager().getBaseLevel(user, trait);
        base = getTraitLevelFromStats(trait, base);
        for (TraitModifier modifier : traitModifiers.values()) {
            if (!modifier.trait().equals(trait)) continue;
            if (modifier.operation() != Operation.ADD) continue;

            base += modifier.value();
        }
        return base;
    }

    // Sum of all ADD_PERCENT operation modifiers
    public double getStatAddPercentSum(Stat stat) {
        double sum = 0;
        for (StatModifier modifier : statModifiers.values()) {
            if (!modifier.stat().equals(stat)) continue;
            if (modifier.operation() != Operation.ADD_PERCENT) continue;

            sum += modifier.value();
        }
        return sum;
    }

    public double getTraitAddPercentSum(Trait trait) {
        double sum = 0;
        for (TraitModifier modifier : traitModifiers.values()) {
            if (!modifier.trait().equals(trait)) continue;
            if (modifier.operation() != Operation.ADD_PERCENT) continue;

            sum += modifier.value();
        }
        return sum;
    }

    // Product of all MULTIPLY operation modifiers
    public double getStatMultiplyProduct(Stat stat) {
        double prod = 1.0;
        for (StatModifier modifier : statModifiers.values()) {
            if (!modifier.stat().equals(stat)) continue;
            if (modifier.operation() != Operation.MULTIPLY) continue;

            prod *= modifier.value();
        }
        return prod;
    }

    public double getTraitMultiplyProduct(Trait trait) {
        double prod = 1.0;
        for (TraitModifier modifier : traitModifiers.values()) {
            if (!modifier.trait().equals(trait)) continue;
            if (modifier.operation() != Operation.MULTIPLY) continue;

            prod *= modifier.value();
        }
        return prod;
    }

    public double calculateRewardsStat(Stat stat) {
        double level = 0.0;

        for (Skill skill : plugin.getSkillManager().getEnabledSkills()) {
            for (Entry<Stat, Double> entry : getStatLevelRewards(stat, skill)) {
                level += entry.getValue();
            }
        }

        return level;
    }

    // Gets the total amount of the stat awarded by each skill
    public Map<Skill, Double> getLevelRewardedBySkill(Stat stat) {
        Map<Skill, Double> map = new HashMap<>();
        for (Skill skill : plugin.getSkillManager().getEnabledSkills()) {
            double sum = 0;
            for (Entry<Stat, Double> entry : getStatLevelRewards(stat, skill)) {
                sum += entry.getValue();
            }
            if (sum > 0) {
                map.put(skill, sum);
            }
        }
        return map;
    }

    private List<Entry<Stat, Double>> getStatLevelRewards(Stat stat, Skill skill) {
        List<Entry<Stat, Double>> entries = new ArrayList<>();

        Map<Stat, Double> skillRewardedStats = plugin.getRewardManager().getRewardTable(skill).getStatLevels(user.getSkillLevel(skill));
        for (Entry<Stat, Double> entry : skillRewardedStats.entrySet()) {
            if (!entry.getKey().equals(stat)) continue;

            entries.add(entry);
        }
        return entries;
    }

}
