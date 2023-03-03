package com.archyx.aureliumskills.skills.enchanting;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.source.Source;
import com.archyx.aureliumskills.util.item.ItemUtils;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
