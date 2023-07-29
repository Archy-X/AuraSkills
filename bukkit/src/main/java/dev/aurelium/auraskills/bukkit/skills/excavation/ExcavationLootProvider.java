package dev.aurelium.auraskills.bukkit.skills.excavation;

import com.archyx.lootmanager.loot.LootPool;
import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.event.loot.LootDropEvent;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.loot.handler.BlockLootHandler;
import dev.aurelium.auraskills.bukkit.loot.provider.SkillLootProvider;
import dev.aurelium.auraskills.common.user.User;

public class ExcavationLootProvider extends SkillLootProvider {

    public ExcavationLootProvider(AuraSkills plugin, BlockLootHandler handler) {
        super(plugin, handler);
    }

    @Override
    public double getChance(LootPool pool, User user) {
        double chance = handler.getCommonChance(pool, user);
        if (pool.getName().equals("rare") && Abilities.METAL_DETECTOR.isEnabled()) {
            chance = handler.getAbilityModifiedChance(chance, Abilities.METAL_DETECTOR, user);
        } else if (pool.getName().equals("epic") && Abilities.LUCKY_SPADES.isEnabled()) {
            chance = handler.getAbilityModifiedChance(chance, Abilities.LUCKY_SPADES, user);
        }
        return chance;
    }

    @Override
    public LootDropEvent.Cause getCause(LootPool pool) {
        LootDropEvent.Cause cause;
        if (pool.getName().equals("rare") && Abilities.METAL_DETECTOR.isEnabled()) {
            cause = LootDropEvent.Cause.METAL_DETECTOR;
        } else if (pool.getName().equals("epic") && Abilities.LUCKY_SPADES.isEnabled()) {
            cause = LootDropEvent.Cause.LUCKY_SPADES;
        } else {
            cause = LootDropEvent.Cause.EXCAVATION_OTHER_LOOT;
        }
        return cause;
    }
}
