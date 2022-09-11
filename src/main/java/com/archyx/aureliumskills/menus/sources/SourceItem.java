package com.archyx.aureliumskills.menus.sources;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.CustomMessageKey;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.menus.common.AbstractItem;
import com.archyx.aureliumskills.menus.sources.SorterItem.SortType;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.source.Source;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import com.cryptomorin.xseries.XMaterial;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SourceItem extends AbstractItem implements TemplateItemProvider<Source> {

    public SourceItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public Class<Source> getContext() {
        return Source.class;
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, Source source) {
        Locale locale = plugin.getLang().getLocale(player);
        switch (placeholder) {
            case "source_name":
                return TextUtil.replace(Lang.getMessage(MenuMessage.SOURCE_NAME, locale),
                        "{name}", source.getDisplayName(locale));
            case "source_xp":
                String unitName = source.getUnitName();
                if (unitName == null) {
                    return TextUtil.replace(Lang.getMessage(MenuMessage.SOURCE_XP, locale),
                            "{xp}", NumberUtil.format2(plugin.getSourceManager().getXp(source)));
                } else {
                    return TextUtil.replace(Lang.getMessage(MenuMessage.SOURCE_XP_RATE, locale),
                            "{xp}", NumberUtil.format2(plugin.getSourceManager().getXp(source)),
                            "{unit}", getCustomMessage("sources.units." + unitName, locale));
                }
            case "multiplied_xp":
                Skill skill = (Skill) activeMenu.getProperty("skill");
                double multiplier = getMultiplier(player, skill);
                if (multiplier > 1.0) {
                    String unit = source.getUnitName();
                    if (unit == null) {
                        return TextUtil.replace(Lang.getMessage(MenuMessage.MULTIPLIED_XP, locale),
                                "{xp}", NumberUtil.format1(plugin.getSourceManager().getXp(source) * multiplier));
                    } else {
                        return TextUtil.replace(Lang.getMessage(MenuMessage.MULTIPLIED_XP_RATE, locale),
                                "{xp}", NumberUtil.format1(plugin.getSourceManager().getXp(source) * multiplier),
                                "{unit}", getCustomMessage("sources.units." + unit, locale));
                    }
                } else {
                    return "";
                }
            case "multiplied_desc":
                Skill skill1 = (Skill) activeMenu.getProperty("skill");
                if (getMultiplier(player, skill1) > 1.0) {
                    return Lang.getMessage(MenuMessage.MULTIPLIED_DESC, locale);
                } else {
                    return "";
                }
        }
        return placeholder;
    }

    @Override
    public Set<Source> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        // Gets the needed properties from the menu
        SortType sortType = (SortType) activeMenu.getProperty("sort_type");
        Skill skill = (Skill) activeMenu.getProperty("skill");
        int itemsPerPage = (Integer) activeMenu.getProperty("items_per_page");
        int page = activeMenu.getCurrentPage();
        Locale locale = plugin.getLang().getLocale(player);
        // Sort the sources in the skill by the selected sort type
        Source[] allSources = plugin.getSourceRegistry().values(skill);
        // Filter valid sources
        List<Source> filteredSources = new ArrayList<>();
        for (Source source : allSources) {
            if (plugin.getSourceManager().getXp(source) == 0.0) {
                continue;
            }
            if (source.getMenuItem() == null) {
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
        List<Source> shownSources = filteredSources.subList(page * itemsPerPage, toIndex);
        activeMenu.setProperty("sources", shownSources); // Set sorted sources property for easy access in other methods
        return new HashSet<>(shownSources);
    }

    @Override
    public SlotPos getSlotPos(Player player, ActiveMenu activeMenu, Source source) {
        List<Source> sources = getSortedSources(activeMenu);
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
    public ItemStack onItemModify(ItemStack baseItem, Player player, ActiveMenu activeMenu, Source source) {
        if (baseItem.getType() != XMaterial.GRAY_DYE.parseMaterial()) {
            return baseItem;
        }
        ItemStack item = source.getMenuItem();
        if (item == null) {
            plugin.getLogger().warning("Item of source " + source.getPath() + " not found");
        }
        return item;
    }

    // Safely get list of sources from property
    private List<Source> getSortedSources(ActiveMenu activeMenu) {
        Object object = activeMenu.getProperty("sources");
        List<Source> sources = new ArrayList<>();
        if (object instanceof List<?>) {
            List<?> list = (List<?>) object;
            for (Object element : list) {
                if (element instanceof Source) {
                    sources.add((Source) element);
                }
            }
        }
        return sources;
    }

    private double getMultiplier(Player player, Skill skill) {
        Ability ability = skill.getXpMultiplierAbility();
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData == null) {
            return 1.0;
        }
        double multiplier = 1.0;
        if (playerData.getAbilityLevel(ability) > 0) {
            double abilityValue = plugin.getAbilityManager().getValue(ability, playerData.getAbilityLevel(ability));
            double addedMultiplier = abilityValue / 100;
            multiplier += addedMultiplier;
        }
        multiplier *= plugin.getLeveler().getMultiplier(player, skill);
        return multiplier;
    }

    private String getCustomMessage(String path, Locale locale) {
        String message = Lang.getMessage(new CustomMessageKey(path), locale);
        if (message != null) {
            return message;
        } else {
            plugin.getLogger().warning("Unknown message with path " + path);
            return path;
        }
    }

}
