package com.archyx.aureliumskills.skills.sources;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import org.jetbrains.annotations.Nullable;

public enum MiningSources implements SourceProvider {

    STONE,
    COBBLESTONE,
    GRANITE,
    DIORITE,
    ANDESITE,
    COAL_ORE,
    IRON_ORE,
    QUARTZ_ORE,
    REDSTONE_ORE,
    GOLD_ORE,
    LAPIS_ORE,
    DIAMOND_ORE,
    EMERALD_ORE,
    TERRACOTTA,
    WHITE_TERRACOTTA,
    ORANGE_TERRACOTTA,
    YELLOW_TERRACOTTA,
    LIGHT_GRAY_TERRACOTTA,
    BROWN_TERRACOTTA,
    RED_TERRACOTTA,
    NETHERRACK,
    BLACKSTONE,
    BASALT,
    MAGMA_BLOCK,
    NETHER_GOLD_ORE,
    ANCIENT_DEBRIS,
    END_STONE,
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

    MiningSources() {
        this(null, (byte) -1);
    }

    MiningSources(String legacyMaterial) {
        this(legacyMaterial, (byte) -1);
    }

    MiningSources(String legacyMaterial, byte legacyData) {
        this.legacyMaterial = legacyMaterial;
        this.legacyData = legacyData;
    }

    @Nullable
    public String getLegacyMaterial() {
        return legacyMaterial;
    }

    public byte getLegacyData() {
        return legacyData;
    }

    @Override
    public Skill getSkill() {
        return Skills.MINING;
    }
}
