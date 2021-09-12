package com.archyx.aureliumskills.skills.enchanting;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.source.Source;
import com.archyx.aureliumskills.util.item.ItemUtils;
import org.bukkit.inventory.ItemStack;

public enum EnchantingSource implements Source {

    WEAPON_PER_LEVEL("DIAMOND_SWORD"),
    ARMOR_PER_LEVEL("DIAMOND_CHESTPLATE"),
    TOOL_PER_LEVEL("DIAMOND_PICKAXE"),
    BOOK_PER_LEVEL("ENCHANTED_BOOK");

    private final String material;

    EnchantingSource(String material) {
        this.material = material;
    }

    @Override
    public Skill getSkill() {
        return Skills.ENCHANTING;
    }

    @Override
    public String getUnitName() {
        return "enchant_level";
    }

    @Override
    public ItemStack getMenuItem() {
        return ItemUtils.parseItem(material);
    }
}
