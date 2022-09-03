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
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import com.cryptomorin.xseries.XMaterial;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SourceItem extends AbstractItem implements TemplateItemProvider<Source> {

    public SourceItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public @NotNull Class<Source> getContext() {
        return Source.class;
    }

    @Override
    public @NotNull String onPlaceholderReplace(@NotNull String placeholder, @NotNull Player player, @NotNull ActiveMenu activeMenu, @NotNull PlaceholderType placeholderType, @NotNull Source source) {
        @Nullable Locale locale = plugin.getLang().getLocale(player);
        @Nullable String m = placeholder;
        switch (placeholder) {
            case "source_name":
                m = TextUtil.replace(Lang.getMessage(MenuMessage.SOURCE_NAME, locale),
                        "{name}", source.getDisplayName(locale));
            case "source_xp":
                String unitName = source.getUnitName();
                if (unitName == null) {
                    m = TextUtil.replace(Lang.getMessage(MenuMessage.SOURCE_XP, locale),
                            "{xp}", NumberUtil.format2(plugin.getSourceManager().getXp(source)));
                } else {
                    m = TextUtil.replace(Lang.getMessage(MenuMessage.SOURCE_XP_RATE, locale),
                            "{xp}", NumberUtil.format2(plugin.getSourceManager().getXp(source)),
                            "{unit}", getCustomMessage("sources.units." + unitName, locale));
                }
            case "multiplied_xp":
                Skill skill = (Skill) activeMenu.getProperty("skill");
                assert (null != skill);
                double multiplier = getMultiplier(player, skill);
                if (multiplier > 1.0) {
                    String unit = source.getUnitName();
                    if (unit == null) {
                        m = TextUtil.replace(Lang.getMessage(MenuMessage.MULTIPLIED_XP, locale),
                                "{xp}", NumberUtil.format1(plugin.getSourceManager().getXp(source) * multiplier));
                    } else {
                        m = TextUtil.replace(Lang.getMessage(MenuMessage.MULTIPLIED_XP_RATE, locale),
                                "{xp}", NumberUtil.format1(plugin.getSourceManager().getXp(source) * multiplier),
                                "{unit}", getCustomMessage("sources.units." + unit, locale));
                    }
                } else {
                    m = "";
                }
            case "multiplied_desc":
                Skill skill1 = (Skill) activeMenu.getProperty("skill");
                assert (null != skill1);
                if (getMultiplier(player, skill1) > 1.0) {
                    m = Lang.getMessage(MenuMessage.MULTIPLIED_DESC, locale);
                } else {
                    m = "";
                }
        }
        assert (null != m);
        return m;
    }

    @Override
    public @NotNull Set<@NotNull Source> getDefinedContexts(@NotNull Player player, @NotNull ActiveMenu activeMenu) {
        // Gets the needed properties from the menu
        @Nullable Object property = activeMenu.getProperty("sort_type");
        assert (null != property);
        SortType sortType = (SortType) property;
        property = activeMenu.getProperty("skill");
        assert (null != property);
        Skill skill = (Skill) property;
        assert (null != skill);
        property = activeMenu.getProperty("items_per_page");
        assert (null != property);
        int itemsPerPage = (Integer) property;
        int page = activeMenu.getCurrentPage();
        @Nullable Locale locale = plugin.getLang().getLocale(player);
        // Sort the sources in the skill by the selected sort type
        Source[] allSources = plugin.getSourceRegistry().values(skill);
        // Filter valid sources
        List<@NotNull Source> filteredSources = new ArrayList<>();
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
        List<@NotNull Source> shownSources = filteredSources.subList(page * itemsPerPage, toIndex);
        activeMenu.setProperty("sources", shownSources); // Set sorted sources property for easy access in other methods
        return new HashSet<>(shownSources);
    }

    @Override
    public @Nullable SlotPos getSlotPos(@NotNull Player player, @NotNull ActiveMenu activeMenu, @NotNull Source source) {
        List<@NotNull Source> sources = getSortedSources(activeMenu);
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
    public @Nullable ItemStack onItemModify(@NotNull ItemStack baseItem, @NotNull Player player, @NotNull ActiveMenu activeMenu, @NotNull Source source) {
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
    private @NotNull List<@NotNull Source> getSortedSources(@NotNull ActiveMenu activeMenu) {
        Object object = activeMenu.getProperty("sources");
        List<@NotNull Source> sources = new ArrayList<>();
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

    private double getMultiplier(@NotNull Player player, @NotNull Skill skill) {
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

    private @NotNull String getCustomMessage(@NotNull String path, @Nullable Locale locale) {
        String message;
        try {
            message = Lang.getMessage(new CustomMessageKey(path), locale);
        }
        catch (IllegalStateException ex) {
            plugin.getLogger().warning("Unknown message with path: " + path);
        }
        return path;
    }

}
