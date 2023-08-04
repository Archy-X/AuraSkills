package dev.aurelium.auraskills.bukkit.util;

/**
 * This enum is to provide the minimum modified enchantment levels used for grindstone calculations
 */
public enum GrindstoneEnchant {

    PROTECTION(1, 12, 23, 34),
    FIRE_PROTECTION(10, 18, 26, 34),
    FEATHER_FALLING(5, 11, 17, 23),
    BLAST_PROTECTION(5, 13, 21, 29),
    PROJECTILE_PROTECTION(3, 9, 15, 21),
    RESPIRATION(10, 20, 30),
    AQUA_AFFINITY(1),
    THORNS(10, 30, 50),
    DEPTH_STRIDER(10, 20, 30),
    FROST_WALKER(10, 20),
    SOUL_SPEED(10, 20, 30),
    SHARPNESS(1, 12, 23, 34, 45),
    SMITE(5, 13, 21, 29, 37),
    BANE_OF_ARTHROPODS(5, 13, 21, 29, 37),
    KNOCKBACK(5, 25),
    FIRE_ASPECT(10, 30),
    LOOTING(15, 24, 33),
    SWEEPING(5, 14, 23),
    POWER(1, 11, 21, 31, 41),
    PUNCH(12, 32),
    FLAME(20),
    INFINITY(20),
    EFFICIENCY(1, 11, 21, 31, 41),
    SILK_TOUCH(15),
    FORTUNE(15, 24, 33),
    LUCK_OF_THE_SEA(15, 24, 33),
    LURE(15, 24, 33),
    UNBREAKING(5, 13, 21),
    MENDING(25),
    CHANNELING(25),
    IMPALING(1, 9, 17, 25, 33),
    LOYALTY(12, 19, 26),
    RIPTIDE(17, 24, 31),
    MULTISHOT(20),
    PIERCING(1, 11, 21, 31),
    QUICK_CHARGE(12, 32, 52),
    VANISHING_CURSE(0),
    BINDING_CURSE(0);

    private final int level1;
    private int level2;
    private int level3;
    private int level4;
    private int level5;

    GrindstoneEnchant(int level1) {
        this.level1 = level1;
    }

    GrindstoneEnchant(int level1, int level2) {
        this.level1 = level1;
        this.level2 = level2;
    }

    GrindstoneEnchant(int level1, int level2, int level3) {
        this.level1 = level1;
        this.level2 = level2;
        this.level3 = level3;
    }

    GrindstoneEnchant(int level1, int level2, int level3, int level4) {
        this.level1 = level1;
        this.level2 = level2;
        this.level3 = level3;
        this.level4 = level4;
    }

    GrindstoneEnchant(int level1, int level2, int level3, int level4, int level5) {
        this.level1 = level1;
        this.level2 = level2;
        this.level3 = level3;
        this.level4 = level4;
        this.level5 = level5;
    }

    public int getLevel(int level) {
        return switch (level) {
            case 2 -> level2;
            case 3 -> level3;
            case 4 -> level4;
            case 5 -> level5;
            default -> level1;
        };
    }


}
