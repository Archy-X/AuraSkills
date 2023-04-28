package dev.aurelium.skills.common.skill;

import com.google.common.collect.ImmutableList;
import dev.aurelium.skills.api.ability.Abilities;
import dev.aurelium.skills.api.ability.Ability;
import dev.aurelium.skills.api.ability.ManaAbilities;
import dev.aurelium.skills.api.ability.ManaAbility;
import dev.aurelium.skills.api.skill.Skills;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkillDefaults {

    @NotNull
    public static ImmutableList<Ability> getDefaultAbilities(Skills skill) {
        switch (skill) {
            case FARMING:
                return ImmutableList.of(Abilities.BOUNTIFUL_HARVEST, Abilities.FARMER, Abilities.SCYTHE_MASTER, Abilities.GENETICIST, Abilities.TRIPLE_HARVEST);
            case FORAGING:
                return ImmutableList.of(Abilities.LUMBERJACK, Abilities.FORAGER, Abilities.AXE_MASTER, Abilities.VALOR, Abilities.SHREDDER);
            case MINING:
                return ImmutableList.of(Abilities.LUCKY_MINER, Abilities.MINER, Abilities.PICK_MASTER, Abilities.STAMINA, Abilities.HARDENED_ARMOR);
            case FISHING:
                return ImmutableList.of(Abilities.LUCKY_CATCH, Abilities.FISHER, Abilities.TREASURE_HUNTER, Abilities.GRAPPLER, Abilities.EPIC_CATCH);
            case EXCAVATION:
                return ImmutableList.of(Abilities.METAL_DETECTOR, Abilities.EXCAVATOR, Abilities.SPADE_MASTER, Abilities.BIGGER_SCOOP, Abilities.LUCKY_SPADES);
            case ARCHERY:
                return ImmutableList.of(Abilities.CRIT_CHANCE, Abilities.ARCHER, Abilities.BOW_MASTER, Abilities.PIERCING, Abilities.STUN);
            case DEFENSE:
                return ImmutableList.of(Abilities.SHIELDING, Abilities.DEFENDER, Abilities.MOB_MASTER, Abilities.IMMUNITY, Abilities.NO_DEBUFF);
            case FIGHTING:
                return ImmutableList.of(Abilities.CRIT_DAMAGE, Abilities.FIGHTER, Abilities.SWORD_MASTER, Abilities.FIRST_STRIKE, Abilities.BLEED);
            case ENDURANCE:
                return ImmutableList.of(Abilities.ANTI_HUNGER, Abilities.RUNNER, Abilities.GOLDEN_HEAL, Abilities.RECOVERY, Abilities.MEAL_STEAL);
            case AGILITY:
                return ImmutableList.of(Abilities.LIGHT_FALL, Abilities.JUMPER, Abilities.SUGAR_RUSH, Abilities.FLEETING, Abilities.THUNDER_FALL);
            case ALCHEMY:
                return ImmutableList.of(Abilities.ALCHEMIST, Abilities.BREWER, Abilities.SPLASHER, Abilities.LINGERING, Abilities.WISE_EFFECT);
            case ENCHANTING:
                return ImmutableList.of(Abilities.XP_CONVERT, Abilities.ENCHANTER, Abilities.XP_WARRIOR, Abilities.ENCHANTED_STRENGTH, Abilities.LUCKY_TABLE);
            case SORCERY:
                return ImmutableList.of(Abilities.SORCERER);
            case HEALING:
                return ImmutableList.of(Abilities.LIFE_ESSENCE, Abilities.HEALER, Abilities.LIFE_STEAL, Abilities.GOLDEN_HEART, Abilities.REVIVAL);
            case FORGING:
                return ImmutableList.of(Abilities.DISENCHANTER, Abilities.FORGER, Abilities.REPAIRING, Abilities.ANVIL_MASTER, Abilities.SKILL_MENDER);
            default:
                return ImmutableList.of();
        }
    }

    @Nullable
    public static ManaAbility getDefaultManaAbility(Skills skill) {
        switch (skill) {
            case FARMING:
                return ManaAbilities.REPLENISH;
            case FORAGING:
                return ManaAbilities.TREECAPITATOR;
            case MINING:
                return ManaAbilities.SPEED_MINE;
            case FISHING:
                return ManaAbilities.SHARP_HOOK;
            case EXCAVATION:
                return ManaAbilities.TERRAFORM;
            case ARCHERY:
                return ManaAbilities.CHARGED_SHOT;
            case DEFENSE:
                return ManaAbilities.ABSORPTION;
            case FIGHTING:
                return ManaAbilities.LIGHTNING_BLADE;
            default:
                return null;
        }
    }

}
