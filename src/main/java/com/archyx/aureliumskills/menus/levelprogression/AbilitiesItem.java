package com.archyx.aureliumskills.menus.levelprogression;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.menus.common.AbstractItem;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class AbilitiesItem extends AbstractItem implements SingleItemProvider {

    public AbilitiesItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String onPlaceholderReplace(@NotNull String placeholder, @NotNull Player player, @NotNull ActiveMenu menu, @NotNull PlaceholderType type) {
        @Nullable Locale locale = plugin.getLang().getLocale(player);
        @Nullable String m = placeholder;
        switch (placeholder) {
            case "abilities":
                m = Lang.getMessage(MenuMessage.ABILITIES, locale);
                break;
            case "abilities_desc":
                m = Lang.getMessage(MenuMessage.ABILITIES_DESC, locale);
                break;
            case "abilities_click":
                Skill skill = (Skill) menu.getProperty("skill");
                assert (null != skill);
                m = Lang.getMessage(MenuMessage.ABILITIES_CLICK, locale);
                assert (null != m);
                m = TextUtil.replace(m, "{skill}", skill.getDisplayName(locale));
                break;
        }
        assert (null != m);
        return m;
    }

    @Override
    public void onClick(@NotNull Player player, @NotNull InventoryClickEvent event, @NotNull ItemStack item, @NotNull SlotPos pos, @NotNull ActiveMenu activeMenu) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("skill", activeMenu.getProperty("skill"));
        properties.put("previous_menu", "level_progression");
        plugin.getMenuManager().openMenu(player, "abilities", properties);
    }

    @Override
    public @Nullable ItemStack onItemModify(@NotNull ItemStack baseItem, @NotNull Player player, @NotNull ActiveMenu activeMenu) {
        @Nullable Skill skill = (Skill) activeMenu.getProperty("skill");
        assert (null != skill);
        if (skill == Skills.SORCERY) { // Disable for sorcery abilities REMOVE ONCE SORCERY ABILITIES ARE ADDED
            return null;
        }
        // Check if the skill has an enabled ability
        boolean hasEnabledAbility = false;
        for (Supplier<@NotNull Ability> abilitySupplier : skill.getAbilities()) {
           if (plugin.getAbilityManager().isEnabled(abilitySupplier.get())) {
                hasEnabledAbility = true;
                break;
            }
        }
        if (hasEnabledAbility) {
            return baseItem;
        }
        return null; // Don't show item if no abilities are enabled
    }
}
