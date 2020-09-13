package com.archyx.aureliumskills.util;

public class ArmorBlockedMaterials {

    private static String[] defBlocked = new String[] {
            "CHEST", "TRAPPED_CHEST", "ENDER_CHEST",
            "FURNACE", "WORKBENCH", "CRAFTING_TABLE",
            "ANVIL", "ENCHANTING_TABLE", "ENCHANTMENT_TABLE",
            "*SHULKER_BOX", "*BED", "BED_BLOCK",
            "NOTE_BLOCK", "BREWING_STAND", "HOPPER",
            "DISPENSER", "DROPPER", "*BUTTON",
            "REPEATER", "?DIODE", "LEVER",
            "?COMPARATOR", "*DOOR!?IRON_DOOR", "*FENCE_GATE",
            "?DAYLIGHT_DETECTOR", "BEACON", "?COMMAND",
            "CARTOGRAPHY_TABLE", "LECTERN", "GRINDSTONE",
            "SMITHING_TABLE", "STONECUTTER", "BLAST_FURNACE",
            "BELL", "SMOKER", "BARREL", "LOOM"
    };

    public static String[] getDefBlocked() {
        return defBlocked;
    }
}
