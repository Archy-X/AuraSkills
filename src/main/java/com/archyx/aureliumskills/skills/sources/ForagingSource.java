package com.archyx.aureliumskills.skills.sources;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

public enum ForagingSource implements Source {

    OAK_LOG("LOG", new byte[] {0, 4, 8, 12}),
    SPRUCE_LOG("LOG", new byte[] {1, 5, 9, 13}),
    BIRCH_LOG("LOG", new byte[] {2, 6, 10, 14}),
    JUNGLE_LOG("LOG", new byte[] {3, 7, 11, 15}),
    ACACIA_LOG("LOG_2", new byte[] {0, 4, 8, 12}),
    DARK_OAK_LOG("LOG_2", new byte[] {1, 5, 9, 13}),
    OAK_LEAVES("LEAVES", new byte[] {0, 8}),
    SPRUCE_LEAVES("LEAVES", new byte[] {1, 9}),
    BIRCH_LEAVES("LEAVES", new byte[] {2, 10}),
    JUNGLE_LEAVES("LEAVES", new byte[] {3, 11}),
    ACACIA_LEAVES("LEAVES_2", new byte[] {0, 8}),
    DARK_OAK_LEAVES("LEAVES_2", new byte[] {1, 9}),
    CRIMSON_STEM,
    WARPED_STEM,
    NETHER_WART_BLOCK,
    WARPED_WART_BLOCK,
    MOSS_BLOCK,
    MOSS_CARPET(true),
    AZALEA(true),
    FLOWERING_AZALEA(true),
    AZALEA_LEAVES,
    FLOWERING_AZALEA_LEAVES;

    private String legacyMaterial;
    private byte[] legacyData;
    private boolean requiresSupportBlock;

    ForagingSource() {

    }

    ForagingSource(boolean requiresSupportBlock) {
        this.requiresSupportBlock = requiresSupportBlock;
    }

    ForagingSource(String legacyMaterial) {
        this.legacyMaterial = legacyMaterial;
    }

    ForagingSource(String legacyMaterial, byte[] legacyData) {
        this(legacyMaterial);
        this.legacyData = legacyData;
    }

    public String getLegacyMaterial() {
        return legacyMaterial;
    }

    public byte[] getLegacyData() {
        return legacyData;
    }

    public boolean requiresSupportBlock() {
        return requiresSupportBlock;
    }

    @SuppressWarnings("deprecation")
    public boolean isMatch(Block block) {
        boolean matched = false;
        String materialName = block.getType().toString();
        if (XMaterial.isNewVersion() || getLegacyMaterial() == null) { // Standard block handling
            if (toString().equalsIgnoreCase(materialName)) {
                matched = true;
            }
        } else { // Legacy block handling
            if (getLegacyData() == null) { // No data value
                if (getLegacyMaterial().equalsIgnoreCase(materialName)) {
                    matched = true;
                }
            } else { // With data value
                if (getLegacyMaterial().equalsIgnoreCase(materialName) && byteArrayContains(legacyData, block.getData())) {
                    matched = true;
                }
            }
        }
        return matched;
    }

    private boolean byteArrayContains(byte[] array, byte input) {
        for (byte b : array) {
            if (b == input) return true;
        }
        return false;
    }

    @Override
    public Skill getSkill() {
        return Skills.FORAGING;
    }

    @Nullable
    public static ForagingSource getSource(Block block) {
        for (ForagingSource source : values()) {
            if (source.isMatch(block)) {
                return source;
            }
        }
        return null;
    }
}
