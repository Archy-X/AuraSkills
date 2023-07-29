package dev.aurelium.auraskills.bukkit.menus.sources;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import com.cryptomorin.xseries.XMaterial;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.common.AbstractItem;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.math.NumberUtil;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SourceItem extends AbstractItem implements TemplateItemProvider<XpSource> {

    public SourceItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public Class<XpSource> getContext() {
        return XpSource.class;
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, XpSource source) {
        Locale locale = plugin.getUser(player).getLocale();
        switch (placeholder) {
            case "source_name":
                return TextUtil.replace(plugin.getMsg(MenuMessage.SOURCE_NAME, locale),
                        "{name}", source.getDisplayName(locale));
            case "source_xp":
                String unitName = source.getUnitName(locale);
                if (unitName == null) {
                    return TextUtil.replace(plugin.getMsg(MenuMessage.SOURCE_XP, locale),
                            "{xp}", NumberUtil.format2(source.getXp()));
                } else {
                    return TextUtil.replace(plugin.getMsg(MenuMessage.SOURCE_XP_RATE, locale),
                            "{xp}", NumberUtil.format2(source.getXp()),
                            "{unit}", unitName);
                }
            case "multiplied_xp":
                Skill skill = (Skill) activeMenu.getProperty("skill");
                double multiplier = getMultiplier(player, skill);
                if (multiplier > 1.0) {
                    String unit = source.getUnitName(locale);
                    if (unit == null) {
                        return TextUtil.replace(plugin.getMsg(MenuMessage.MULTIPLIED_XP, locale),
                                "{xp}", NumberUtil.format2(source.getXp() * multiplier));
                    } else {
                        return TextUtil.replace(plugin.getMsg(MenuMessage.MULTIPLIED_XP_RATE, locale),
                                "{xp}", NumberUtil.format2(source.getXp() * multiplier),
                                "{unit}", unit);
                    }
                } else {
                    return "";
                }
            case "multiplied_desc":
                Skill skill1 = (Skill) activeMenu.getProperty("skill");
                if (getMultiplier(player, skill1) > 1.0) {
                    return plugin.getMsg(MenuMessage.MULTIPLIED_DESC, locale);
                } else {
                    return "";
                }
        }
        return placeholder;
    }

    @Override
    public Set<XpSource> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        // Gets the needed properties from the menu
        SorterItem.SortType sortType = (SorterItem.SortType) activeMenu.getProperty("sort_type");
        Skill skill = (Skill) activeMenu.getProperty("skill");
        int itemsPerPage = (Integer) activeMenu.getProperty("items_per_page");
        int page = activeMenu.getCurrentPage();
        Locale locale = plugin.getUser(player).getLocale();
        // Sort the sources in the skill by the selected sort type
        List<XpSource> allSources = skill.getSources();
        // Filter valid sources
        List<XpSource> filteredSources = new ArrayList<>();
        for (XpSource source : allSources) {
            if (source.getXp() == 0.0) {
                continue;
            }
            if (plugin.getItemRegistry().getSourceMenuItems().getMenuItem(source) == null) {
                continue;
            }
            filteredSources.add(source);
        }
        filteredSources.sort(sortType.getComparator(plugin, locale));
        // Gets a sublist of the sources displayed based on the current page
        int toIndex = (page + 1) * itemsPerPage;
        if (toIndex > filteredSources.size()) {
            toIndex = filteredSources.size();
        }
        List<XpSource> shownSources = filteredSources.subList(page * itemsPerPage, toIndex);
        activeMenu.setProperty("sources", shownSources); // Set sorted sources property for easy access in other methods
        return new HashSet<>(shownSources);
    }

    @Override
    public SlotPos getSlotPos(Player player, ActiveMenu activeMenu, XpSource source) {
        List<XpSource> sources = getSortedSources(activeMenu);
        int index = sources.indexOf(source);
        if (index != -1) {
            // Convert index of source into position on menu
            int row = 1 + index / 7;
            int column = 1 + index % 7;
            return SlotPos.of(row, column);
        } else {
            return null;
        }
    }

    @Override
    public ItemStack onItemModify(ItemStack baseItem, Player player, ActiveMenu activeMenu, XpSource source) {
        if (baseItem.getType() != XMaterial.GRAY_DYE.parseMaterial()) {
            return baseItem;
        }
        ItemStack item = plugin.getItemRegistry().getSourceMenuItems().getMenuItem(source);
        if (item == null) {
            plugin.getLogger().warning("Item of source " + source.getId() + " not found");
        }
        return item;
    }

    // Safely get list of sources from property
    private List<XpSource> getSortedSources(ActiveMenu activeMenu) {
        Object object = activeMenu.getProperty("sources");
        List<XpSource> sources = new ArrayList<>();
        if (object instanceof List<?> list) {
            for (Object element : list) {
                if (element instanceof XpSource) {
                    sources.add((XpSource) element);
                }
            }
        }
        return sources;
    }

    private double getMultiplier(Player player, Skill skill) {
        User user = plugin.getUser(player);
        double permissionMultiplier = 1 + plugin.getLevelManager().getPermissionMultiplier(user, skill);
        return plugin.getLevelManager().getAbilityMultiplier(user, skill) * permissionMultiplier;
    }

}
