package dev.aurelium.auraskills.bukkit.menus.levelprogression;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.common.AbstractItem;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AbilitiesItem extends AbstractItem implements SingleItemProvider {

    public AbilitiesItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu menu, PlaceholderData data) {
        Locale locale = plugin.getUser(player).getLocale();
        switch (placeholder) {
            case "abilities":
                return plugin.getMsg(MenuMessage.ABILITIES, locale);
            case "abilities_desc":
                return plugin.getMsg(MenuMessage.ABILITIES_DESC, locale);
            case "abilities_click":
                Skill skill = (Skill) menu.getProperty("skill");
                return TextUtil.replace(plugin.getMsg(MenuMessage.ABILITIES_CLICK, locale), "{skill}", skill.getDisplayName(locale));
        }
        return placeholder;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, ItemStack item, SlotPos pos, ActiveMenu activeMenu) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("skill", activeMenu.getProperty("skill"));
        properties.put("previous_menu", "level_progression");
        plugin.getMenuManager().openMenu(player, "abilities", properties);
    }

    @Override
    public ItemStack onItemModify(ItemStack baseItem, Player player, ActiveMenu activeMenu) {
        Skill skill = (Skill) activeMenu.getProperty("skill");
        if (skill == Skills.SORCERY) { // Disable for sorcery abilities REMOVE ONCE SORCERY ABILITIES ARE ADDED
            return null;
        }
        // Check if the skill has an enabled ability
        boolean hasEnabledAbility = false;
        for (Ability ability : skill.getAbilities()) {
           if (ability.isEnabled()) {
                hasEnabledAbility = true;
                break;
            }
        }
        if (hasEnabledAbility) {
            return baseItem;
        } else {
            return null; // Don't show item if no abilities are enabled
        }
    }
}
