package com.archyx.aureliumskills.menus.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.menus.common.AbstractItem;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractAbilityItem extends AbstractItem implements TemplateItemProvider<Ability> {

    public AbstractAbilityItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public SlotPos getSlotPos(Player player, ActiveMenu activeMenu, Ability ability) {
        Skill skill = (Skill) activeMenu.getProperty("skill");
        List<Ability> abilityList = new ArrayList<>();
        for (Supplier<Ability> abilitySupplier : skill.getAbilities()) {
            abilityList.add(abilitySupplier.get());
        }
        int index = abilityList.indexOf(ability);
        return SlotPos.of(1, 2 + index);
    }

}
