package dev.aurelium.auraskills.common.loot;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.loot.Loot;
import dev.aurelium.auraskills.api.loot.LootContext;
import dev.aurelium.auraskills.api.loot.LootPool;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.common.user.User;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractLootHandler {

    public double getCommonChance(LootPool pool, User user) {
        double chancePerLuck = pool.getOption("chance_per_luck", Double.class, 0.0) / 100;
        return pool.getBaseChance() + chancePerLuck * user.getStatLevel(Stats.LUCK);
    }

    public double getAbilityModifiedChance(double chance, Ability ability, User user) {
        // Check option to scale base chance
        if (ability.optionBoolean("scale_base_chance", false)) {
            chance *= 1 + (ability.getValue(user.getAbilityLevel(ability)) / 100);
        } else { // Otherwise add to base chance
            chance += (ability.getValue(user.getAbilityLevel(ability)) / 100);
        }
        return chance;
    }

    protected boolean isPoolUnobtainable(LootPool pool, XpSource source) {
        for (Loot loot : pool.getLoot()) {
            Set<LootContext> contexts = loot.getValues().getContexts().getOrDefault("sources", new HashSet<>());
            // Loot will be reachable if it has no contexts
            if (contexts.isEmpty()) {
                return false;
            }
            // Loot is reachable if at least one context matches the entity type
            for (LootContext context : contexts) {
                if (context instanceof SourceContext(XpSource contextSource)) {
                    if (contextSource.equals(source)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    protected int generateAmount(int minAmount, int maxAmount) {
        return ThreadLocalRandom.current().nextInt(maxAmount - minAmount + 1) + minAmount;
    }

}
