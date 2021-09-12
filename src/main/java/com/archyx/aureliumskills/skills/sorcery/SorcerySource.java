package com.archyx.aureliumskills.skills.sorcery;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.source.Source;
import com.archyx.aureliumskills.util.item.ItemUtils;
import org.bukkit.inventory.ItemStack;

public enum SorcerySource implements Source {

    MANA_ABILITY_USE("LIGHT_BLUE_DYE");

    private final String material;

    SorcerySource(String material) {
        this.material = material;
    }

    @Override
    public Skill getSkill() {
        return Skills.SORCERY;
    }

    @Override
    public String getUnitName() {
        return "mana";
    }

    @Override
    public ItemStack getMenuItem() {
        return ItemUtils.parseItem(material);
    }
}
