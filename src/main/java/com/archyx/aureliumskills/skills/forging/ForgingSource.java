package com.archyx.aureliumskills.skills.forging;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.source.Source;
import com.archyx.aureliumskills.util.item.ItemUtils;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum ForgingSource implements Source {
    
    COMBINE_BOOKS_PER_LEVEL("ENCHANTED_BOOK", "combine_level"),
    COMBINE_WEAPON_PER_LEVEL("IRON_SWORD", "combine_level"),
    COMBINE_ARMOR_PER_LEVEL("IRON_LEGGINGS", "combine_level"),
    COMBINE_TOOL_PER_LEVEL("IRON_SHOVEL", "combine_level"),
    GRINDSTONE_PER_LEVEL("GRINDSTONE", "grindstone_level");

    private final String material;
    private final String unitName;

    ForgingSource(String material, String unitName) {
        this.material = material;
        this.unitName = unitName;
    }

    @Override
    public Skill getSkill() {
        return Skills.FORGING;
    }

    @Override
    public String getUnitName() {
        return unitName;
    }

    @Override
    public ItemStack getMenuItem() {
        ItemStack item = ItemUtils.parseItem(material);
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && (ItemUtils.isArmor(item.getType()) || ItemUtils.isWeapon(item.getType()) || ItemUtils.isTool(item.getType()))) {
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(meta);
            }
        }
        return item;
    }
}
