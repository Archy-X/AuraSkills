package dev.aurelium.auraskills.common.skill;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skills;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class SkillDefaults {

    @NotNull
    public static ImmutableList<Abilities> getDefaultAbilities(Skills skill) {
        return switch (skill) {
            case FARMING ->
                    ImmutableList.of(Abilities.BOUNTIFUL_HARVEST, Abilities.FARMER, Abilities.SCYTHE_MASTER, Abilities.GENETICIST, Abilities.TRIPLE_HARVEST);
            case FORAGING ->
                    ImmutableList.of(Abilities.LUMBERJACK, Abilities.FORAGER, Abilities.AXE_MASTER, Abilities.VALOR, Abilities.SHREDDER);
            case MINING ->
                    ImmutableList.of(Abilities.LUCKY_MINER, Abilities.MINER, Abilities.PICK_MASTER, Abilities.STAMINA, Abilities.HARDENED_ARMOR);
            case FISHING ->
                    ImmutableList.of(Abilities.LUCKY_CATCH, Abilities.FISHER, Abilities.TREASURE_HUNTER, Abilities.GRAPPLER, Abilities.EPIC_CATCH);
            case EXCAVATION ->
                    ImmutableList.of(Abilities.METAL_DETECTOR, Abilities.EXCAVATOR, Abilities.SPADE_MASTER, Abilities.BIGGER_SCOOP, Abilities.LUCKY_SPADES);
            case ARCHERY ->
                    ImmutableList.of(Abilities.CRIT_CHANCE, Abilities.ARCHER, Abilities.BOW_MASTER, Abilities.PIERCING, Abilities.STUN);
            case DEFENSE ->
                    ImmutableList.of(Abilities.SHIELDING, Abilities.DEFENDER, Abilities.MOB_MASTER, Abilities.IMMUNITY, Abilities.NO_DEBUFF);
            case FIGHTING ->
                    ImmutableList.of(Abilities.CRIT_DAMAGE, Abilities.FIGHTER, Abilities.SWORD_MASTER, Abilities.FIRST_STRIKE, Abilities.BLEED);
            case ENDURANCE ->
                    ImmutableList.of(Abilities.ANTI_HUNGER, Abilities.RUNNER, Abilities.GOLDEN_HEAL, Abilities.RECOVERY, Abilities.MEAL_STEAL);
            case AGILITY ->
                    ImmutableList.of(Abilities.LIGHT_FALL, Abilities.JUMPER, Abilities.SUGAR_RUSH, Abilities.FLEETING, Abilities.THUNDER_FALL);
            case ALCHEMY ->
                    ImmutableList.of(Abilities.ALCHEMIST, Abilities.BREWER, Abilities.SPLASHER, Abilities.LINGERING, Abilities.WISE_EFFECT);
            case ENCHANTING ->
                    ImmutableList.of(Abilities.XP_CONVERT, Abilities.ENCHANTER, Abilities.XP_WARRIOR, Abilities.ENCHANTED_STRENGTH, Abilities.LUCKY_TABLE);
            case SORCERY ->
                    ImmutableList.of(Abilities.SORCERER);
            case HEALING ->
                    ImmutableList.of(Abilities.LIFE_ESSENCE, Abilities.HEALER, Abilities.LIFE_STEAL, Abilities.GOLDEN_HEART, Abilities.REVIVAL);
            case FORGING ->
                    ImmutableList.of(Abilities.DISENCHANTER, Abilities.FORGER, Abilities.REPAIRING, Abilities.ANVIL_MASTER, Abilities.SKILL_MENDER);
        };
    }

    @Nullable
    public static ManaAbilities getDefaultManaAbility(Skills skill) {
        return switch (skill) {
            case FARMING -> ManaAbilities.REPLENISH;
            case FORAGING -> ManaAbilities.TREECAPITATOR;
            case MINING -> ManaAbilities.SPEED_MINE;
            case FISHING -> ManaAbilities.SHARP_HOOK;
            case EXCAVATION -> ManaAbilities.TERRAFORM;
            case ARCHERY -> ManaAbilities.CHARGED_SHOT;
            case DEFENSE -> ManaAbilities.ABSORPTION;
            case FIGHTING -> ManaAbilities.LIGHTNING_BLADE;
            default -> null;
        };
    }

    public static Set<String> getOptionKeys(Ability ability) {
        if (!(ability instanceof Abilities)) {
            return new HashSet<>();
        }
        return switch ((Abilities) ability) {
            case TREASURE_HUNTER, EPIC_CATCH, METAL_DETECTOR, LUCKY_SPADES ->
                    Sets.newHashSet("scale_base_chance");
            case FIRST_STRIKE ->
                    Sets.newHashSet("enable_message", "cooldown_ticks");
            case BLEED ->
                    Sets.newHashSet("enable_enemy_message", "enable_self_message", "enable_stop_message", "base_ticks", "added_ticks", "max_ticks", "tick_period", "show_particles");
            case FLEETING ->
                    Sets.newHashSet("health_percent_required");
            case ALCHEMIST ->
                    Sets.newHashSet("add_item_lore");
            case REVIVAL ->
                    Sets.newHashSet("enable_message");
            default -> new HashSet<>();
        };
    }

    public static Set<String> getOptionKeys(ManaAbility manaAbility) {
        if (!(manaAbility instanceof ManaAbilities)) {
            return new HashSet<>();
        }
        return switch ((ManaAbilities) manaAbility) {
            case REPLENISH ->
                    Sets.newHashSet("require_sneak", "check_offhand", "sneak_offhand_bypass", "replant_delay", "show_particles", "prevent_unripe_break");
            case TREECAPITATOR ->
                    Sets.newHashSet("require_sneak", "check_offhand", "sneak_offhand_bypass", "max_blocks_multiplier");
            case SPEED_MINE ->
                    Sets.newHashSet("require_sneak", "check_offhand", "sneak_offhand_bypass", "haste_level");
            case SHARP_HOOK ->
                    Sets.newHashSet("display_damage_with_scaling", "enable_sound");
            case TERRAFORM ->
                    Sets.newHashSet("require_sneak", "check_offhand", "sneak_offhand_bypass", "max_blocks");
            case CHARGED_SHOT ->
                    Sets.newHashSet("enable_message", "enable_sound");
            case ABSORPTION ->
                    Sets.newHashSet("enable_particles");
            case LIGHTNING_BLADE ->
                    Sets.newHashSet("base_duration", "duration_per_level");
        };
    }

}
