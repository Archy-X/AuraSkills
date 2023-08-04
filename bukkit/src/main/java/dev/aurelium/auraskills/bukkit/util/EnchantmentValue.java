package dev.aurelium.auraskills.bukkit.util;

import org.bukkit.enchantments.Enchantment;

public class EnchantmentValue {

    private final Enchantment enchantment;
    private final int level;

    public EnchantmentValue(Enchantment enchantment, int level) {
        this.enchantment = enchantment;
        this.level = level;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public int getLevel() {
        return level;
    }

}
