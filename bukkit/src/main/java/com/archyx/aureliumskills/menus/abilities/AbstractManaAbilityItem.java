package com.archyx.aureliumskills.menus.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.mana.ManaAbilityManager;
import com.archyx.aureliumskills.menus.common.AbstractItem;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractManaAbilityItem extends AbstractItem implements TemplateItemProvider<MAbility> {

    protected final ManaAbilityManager manager;

    public AbstractManaAbilityItem(AureliumSkills plugin) {
        super(plugin);
        this.manager = plugin.getManaAbilityManager();
    }

    @Override
    public Class<MAbility> getContext() {
        return MAbility.class;
    }

    @Override
    public ItemStack onItemModify(ItemStack baseItem, Player player, ActiveMenu activeMenu, MAbility mAbility) {
        // Hide disabled mana abilities
        if (!plugin.getAbilityManager().isEnabled(mAbility)) {
            return null;
        }
        return baseItem;
    }
}
