package dev.aurelium.skills.api.ability;

import dev.aurelium.skills.api.util.NamespacedId;

public enum Abilities implements Ability {

    BOUNTIFUL_HARVEST,
    FARMER,
    SCYTHE_MASTER,
    GENETICIST,
    TRIPLE_HARVEST,
    LUMBERJACK,
    FORAGER,
    AXE_MASTER,
    VALOR,
    SHREDDER,
    LUCKY_MINER,
    MINER,
    PICK_MASTER,
    STAMINA,
    HARDENED_ARMOR,
    LUCKY_CATCH,
    FISHER,
    TREASURE_HUNTER,
    GRAPPLER,
    EPIC_CATCH,
    METAL_DETECTOR,
    EXCAVATOR,
    SPADE_MASTER,
    BIGGER_SCOOP,
    LUCKY_SPADES,
    CRIT_CHANCE,
    ARCHER,
    BOW_MASTER,
    PIERCING,
    STUN,
    SHIELDING,
    DEFENDER,
    MOB_MASTER,
    IMMUNITY,
    NO_DEBUFF,
    CRIT_DAMAGE,
    FIGHTER,
    SWORD_MASTER,
    FIRST_STRIKE,
    BLEED,
    ANTI_HUNGER,
    RUNNER,
    GOLDEN_HEAL,
    RECOVERY,
    MEAL_STEAL,
    LIGHT_FALL,
    JUMPER,
    SUGAR_RUSH,
    FLEETING,
    THUNDER_FALL,
    ALCHEMIST,
    BREWER,
    SPLASHER,
    LINGERING,
    WISE_EFFECT,
    XP_CONVERT,
    ENCHANTER,
    XP_WARRIOR,
    ENCHANTED_STRENGTH,
    LUCKY_TABLE,
    SORCERER,
    LIFE_ESSENCE,
    HEALER,
    LIFE_STEAL,
    GOLDEN_HEART,
    REVIVAL,
    DISENCHANTER,
    FORGER,
    REPAIRING,
    ANVIL_MASTER,
    SKILL_MENDER;

    private final NamespacedId id;

    Abilities() {
        this.id = NamespacedId.from("aureliumskills", this.name().toLowerCase());
    }

    @Override
    public NamespacedId getId() {
        return id;
    }
}
