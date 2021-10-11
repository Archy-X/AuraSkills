package com.archyx.aureliumskills.menus.levelprogression;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.menus.common.AbstractItem;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AbilitiesItem extends AbstractItem implements SingleItemProvider {

    public AbilitiesItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu menu, PlaceholderType type) {
        Locale locale = plugin.getLang().getLocale(player);
        switch (placeholder) {
            case "abilities":
                return "&bAbilities";
            case "abilities_desc":
                return "&bAbilities &7are passive perks you unlock and" +
                        "\n&7upgrade as you level up skills." +
                        "\n&dMana Abilities &7are a special type of ability" +
                        "\n&7that require activation and consume mana.";
            case "abilities_click":
                Skill skill = (Skill) menu.getProperty("skill");
                return "&eClick to view " + skill.getDisplayName(locale) + " abilities";
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
}
