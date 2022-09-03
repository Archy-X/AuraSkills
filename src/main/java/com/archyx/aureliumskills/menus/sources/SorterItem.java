package com.archyx.aureliumskills.menus.sources;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.menus.common.AbstractItem;
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

import java.util.Locale;

public class SorterItem extends AbstractItem implements SingleItemProvider {

    public SorterItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String onPlaceholderReplace(@NotNull String placeholder, @NotNull Player player, @NotNull ActiveMenu activeMenu, @NotNull PlaceholderType placeholderType) {
        @Nullable Locale locale = plugin.getLang().getLocale(player);
        @Nullable String m = placeholder;
        switch (placeholder) {
            case "sorter":
                m = Lang.getMessage(MenuMessage.SORTER, locale);
                break;
            case "sorted_types":
                m = getSortedTypesLore(locale, activeMenu);
                break;
            case "sort_click":
                m = Lang.getMessage(MenuMessage.SORT_CLICK, locale);
                break;
        }
        assert (null != m);
        return m;
    }

    @Override
    public void onClick(@NotNull Player player, @NotNull InventoryClickEvent event, @NotNull ItemStack item, @NotNull SlotPos pos, @NotNull ActiveMenu activeMenu) {
        SortType[] sortTypes = SortType.values();
        SortType currentType = (SortType) activeMenu.getProperty("sort_type");
        // Get the index of the current sort type in the array
        int currentTypeIndex = 0;
        for (int i = 0; i < sortTypes.length; i++) {
            SortType type = sortTypes[i];
            if (type == currentType) {
                currentTypeIndex = i;
            }
        }
        // Find the next type in the array
        SortType nextType;
        if (currentTypeIndex < sortTypes.length - 1) {
            nextType = sortTypes[currentTypeIndex + 1];
        } else {
            nextType = sortTypes[0];
        }
        // Set new sort type and reload menu
        activeMenu.setProperty("sort_type", nextType);
        activeMenu.reload();
        activeMenu.setCooldown("sorter", 5);
    }

    private @NotNull String getSortedTypesLore(@Nullable Locale locale, @NotNull ActiveMenu activeMenu) {
        StringBuilder builder = new StringBuilder();
        SortType selectedSort = (SortType) activeMenu.getProperty("sort_type");
        for (SortType sortType : SortType.values()) {
            String typeString = TextUtil.replace(Lang.getMessage(MenuMessage.SORT_TYPE, locale)
                    , "{type_name}", Lang.getMessage(MenuMessage.valueOf(sortType.toString()), locale));
            if (selectedSort == sortType) {
                typeString = TextUtil.replace(typeString, "{selected}", Lang.getMessage(MenuMessage.SELECTED, locale));
            } else {
                typeString = TextUtil.replace(typeString, "{selected}", "");
            }
            builder.append(typeString);
        }
        return builder.toString();
    }

    public enum SortType {

        ASCENDING,
        DESCENDING,
        ALPHABETICAL,
        REVERSE_ALPHABETICAL;

        public @NotNull SourceComparator getComparator(AureliumSkills plugin, Locale locale) {
            switch (this) {
                case DESCENDING:
                    return new SourceComparator.Descending(plugin);
                case ALPHABETICAL:
                    return new SourceComparator.Alphabetical(plugin, locale);
                case REVERSE_ALPHABETICAL:
                    return new SourceComparator.ReverseAlphabetical(plugin, locale);
                default:
                    return new SourceComparator.Ascending(plugin);
            }
        }

    }

}
