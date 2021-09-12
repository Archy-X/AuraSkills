package com.archyx.aureliumskills.skills.endurance;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.source.Source;
import com.archyx.aureliumskills.util.item.ItemUtils;
import org.bukkit.inventory.ItemStack;

public enum EnduranceSource implements Source {

    WALK_PER_METER("IRON_BOOTS"),
    SPRINT_PER_METER("LEATHER_BOOTS"),
    SWIM_PER_METER("WATER_BUCKET");

    private final String material;

    EnduranceSource(String material) {
        this.material = material;
    }

    @Override
    public Skill getSkill() {
        return Skills.ENDURANCE;
    }

    @Override
    public String getUnitName() {
        return "meter";
    }

    @Override
    public ItemStack getMenuItem() {
        return ItemUtils.parseItem(material);
    }
}
