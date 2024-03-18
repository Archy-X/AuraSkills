package dev.aurelium.auraskills.bukkit.menus.abilities;

import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.common.AbstractItem;
import dev.aurelium.auraskills.common.mana.ManaAbilityManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractManaAbilityItem extends AbstractItem implements TemplateItemProvider<ManaAbility> {

    protected final ManaAbilityManager manager;

    public AbstractManaAbilityItem(AuraSkills plugin) {
        super(plugin);
        this.manager = plugin.getManaAbilityManager();
    }

    @Override
    public Class<ManaAbility> getContext() {
        return ManaAbility.class;
    }

    @Override
    public ItemStack onItemModify(ItemStack baseItem, Player player, ActiveMenu activeMenu, ManaAbility manaAbility) {
        // Hide disabled mana abilities
        if (!manaAbility.isEnabled()) {
            return null;
        }
        return baseItem;
    }
}
