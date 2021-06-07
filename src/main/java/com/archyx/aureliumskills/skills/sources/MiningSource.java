package com.archyx.aureliumskills.skills.sources;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import org.jetbrains.annotations.Nullable;

public enum MiningSource implements SourceProvider {

    STONE("stone", 0),
    COBBLESTONE,
    GRANITE("stone", 1),
    DIORITE("stone", 2),
    ANDESITE("stone", 5),
    COAL_ORE,
    IRON_ORE,
    NETHER_QUARTZ_ORE("quartz_ore"),
    REDSTONE_ORE("glowing_redstone_ore", true),
    GOLD_ORE,
    LAPIS_ORE,
    DIAMOND_ORE,
    EMERALD_ORE,
    TERRACOTTA("hard_clay"),
    WHITE_TERRACOTTA("stained_clay", 0),
    ORANGE_TERRACOTTA("stained_clay", 1),
    YELLOW_TERRACOTTA("stained_clay", 4),
    LIGHT_GRAY_TERRACOTTA("stained_clay", 8),
    BROWN_TERRACOTTA("stained_clay", 12),
    RED_TERRACOTTA("stained_clay", 14),
    NETHERRACK,
    BLACKSTONE,
    BASALT,
    MAGMA_BLOCK("magma"),
    NETHER_GOLD_ORE,
    ANCIENT_DEBRIS,
    END_STONE("ender_stone"),
    OBSIDIAN,
    DEEPSLATE,
    COPPER_ORE,
    TUFF,
    CALCITE,
    SMOOTH_BASALT,
    AMETHYST_BLOCK,
    AMETHYST_CLUSTER,
    DEEPSLATE_COAL_ORE,
    DEEPSLATE_IRON_ORE,
    DEEPSLATE_COPPER_ORE,
    DEEPSLATE_GOLD_ORE,
    DEEPSLATE_REDSTONE_ORE,
    DEEPSLATE_EMERALD_ORE,
    DEEPSLATE_LAPIS_ORE,
    DEEPSLATE_DIAMOND_ORE,
    DRIPSTONE_BLOCK;

    private final String legacyMaterial;
    private final byte legacyData;
    private final boolean allowBothIfLegacy;

    MiningSource() {
        this(null, -1, false);
    }

    MiningSource(String legacyMaterial) {
        this(legacyMaterial, -1, false);
    }

    MiningSource(String legacyMaterial, int legacyData) {
        this(legacyMaterial, legacyData, false);
    }

    MiningSource(String legacyMaterial, boolean allowBothIfLegacy) {
        this(legacyMaterial, -1, allowBothIfLegacy);
    }

    MiningSource(String legacyMaterial, int legacyData, boolean allowBothIfLegacy) {
        this.legacyMaterial = legacyMaterial;
        this.legacyData = (byte) legacyData;
        this.allowBothIfLegacy = allowBothIfLegacy;
    }

    @Nullable
    public String getLegacyMaterial() {
        return legacyMaterial;
    }

    public byte getLegacyData() {
        return legacyData;
    }

    public boolean allowBothIfLegacy() {
        return allowBothIfLegacy;
    }

    @Override
    public Skill getSkill() {
        return Skills.MINING;
    }
}
