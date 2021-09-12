package com.archyx.aureliumskills.skills.healing;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.source.Source;
import com.archyx.aureliumskills.util.item.ItemUtils;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.Locale;

public enum HealingSource implements Source {

    DRINK_REGULAR("POTION", PotionType.SPEED),
    DRINK_EXTENDED("POTION", PotionType.NIGHT_VISION),
    DRINK_UPGRADED("POTION", PotionType.STRENGTH),
    SPLASH_REGULAR("SPLASH", PotionType.SPEED),
    SPLASH_EXTENDED("SPLASH", PotionType.NIGHT_VISION),
    SPLASH_UPGRADED("SPLASH", PotionType.STRENGTH),
    LINGERING_REGULAR("LINGERING", PotionType.SPEED),
    LINGERING_EXTENDED("LINGERING", PotionType.NIGHT_VISION),
    LINGERING_UPGRADED("LINGERING", PotionType.STRENGTH),
    GOLDEN_APPLE("GOLDEN_APPLE"),
    ENCHANTED_GOLDEN_APPLE("ENCHANTED_GOLDEN_APPLE");

    private final String material;
    private final PotionType potionType;

    HealingSource(String material) {
        this.material = material;
        this.potionType = null;
    }

    HealingSource(String material, PotionType potionType) {
        this.material = material.toUpperCase(Locale.ROOT);
        this.potionType = potionType;
    }

    @Override
    public Skill getSkill() {
        return Skills.HEALING;
    }

    @Override
    public ItemStack getMenuItem() {
        ItemStack baseItem = ItemUtils.parseItem(material);
        if (baseItem != null && baseItem.getItemMeta() instanceof PotionMeta) {
            PotionMeta meta = (PotionMeta) baseItem.getItemMeta();
            meta.setBasePotionData(new PotionData(potionType));
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            baseItem.setItemMeta(meta);
        }
        return baseItem;
    }
}
