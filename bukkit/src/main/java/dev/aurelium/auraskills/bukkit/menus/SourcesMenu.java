package dev.aurelium.auraskills.bukkit.menus;

import com.archyx.slate.builder.MenuBuilder;
import com.archyx.slate.info.PlaceholderInfo;
import com.archyx.slate.item.provider.ListBuilder;
import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.menu.ConfigurableMenu;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import fr.minuskube.inv.content.SlotPos;

import java.util.*;

public class SourcesMenu {

    public static final int DEF_ITEMS_PER_PAGE = 28;
    public static final String DEF_SOURCE_START = "1,1";
    public static final String DEF_SOURCE_END = "4,8";

    private final AuraSkills plugin;

    public SourcesMenu(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void build(MenuBuilder menu) {
        menu.replaceTitle("current_page", p -> String.valueOf(p.menu().getCurrentPage() + 1));
        menu.replaceTitle("total_pages", p -> String.valueOf(p.menu().getTotalPages()));
        menu.replaceTitle("skill", p -> ((Skill) p.menu().getProperty("skill")).getDisplayName(plugin.getLocale(p.player())));

        menu.pages(m -> {
            var skill = (Skill) m.menu().getProperty("skill");
            int itemsPerPage = (Integer) m.menu().getProperty("items_per_page");
            int numSources = plugin.getSkillManager().getSkill(skill).sources().size();
            return (numSources - 1) / itemsPerPage + 1;
        });

        menu.properties(m -> Map.of(
                "skill", m.menu().getProperty("skill"),
                "items_per_page", getItemsPerPage(),
                "sort_type", SortType.ASCENDING,
                "previous_menu", "level_progression"));

        menu.item("sorter", item -> {
            item.replace("sort_types", this::getSortedTypesLore);
            item.onClick(c -> {
                SortType[] types = SortType.values();
                SortType current = (SortType) c.menu().getProperty("sort_type");
                // Get the index of the current sort type in the array
                int currentTypeIndex = 0;
                for (int i = 0; i < types.length; i++) {
                    SortType type = types[i];
                    if (type == current) {
                        currentTypeIndex = i;
                    }
                }
                // Find the next type in the array
                SortType nextType;
                if (currentTypeIndex < types.length - 1) {
                    nextType = types[currentTypeIndex + 1];
                } else {
                    nextType = types[0];
                }
                // Set new sort type and reload menu
                c.menu().setProperty("sort_type", nextType);
                c.menu().reload();
                c.menu().setCooldown("sorter", 5);
            });
        });

        menu.template("source", XpSource.class, template -> {
            template.replace("source_name", p -> p.value().getDisplayName(plugin.getLocale(p.player())));
            template.replace("source_xp", p -> {
                XpSource source = p.value();
                String unitName = source.getUnitName(plugin.getLocale(p.player()));
                return TextUtil.replace(unitName == null ? p.menu().getFormat("source_xp") : p.menu().getFormat("source_xp_rate"),
                        "{xp}", NumberUtil.format2(source.getXp()),
                        "{unit}", unitName);
            });

            template.slotPos(t -> {
                int index = getSortedSources(t.menu()).indexOf(t.value());
                if (index != -1) {
                    // Convert index of source into position on menu
                    SlotPos start = parsePos(t.menu().getOption(String.class, "source_start", DEF_SOURCE_START));
                    SlotPos end = parsePos(t.menu().getOption(String.class, "source_end", DEF_SOURCE_END));
                    int numRows = end.getRow() - start.getRow();
                    int numCols = end.getColumn() - start.getColumn();
                    int row = Math.min(start.getRow() + index / numCols, start.getRow() + numRows);
                    int column = start.getColumn() + index % numCols;
                    return SlotPos.of(row, column);
                }
                return null;
            });

            template.definedContexts(m -> {
                ActiveMenu activeMenu = m.menu();
                int itemsPerPage = (Integer) activeMenu.getProperty("items_per_page");
                int page = activeMenu.getCurrentPage();
                // Filter valid sources
                List<XpSource> filteredSources = new ArrayList<>();
                for (XpSource source : ((Skill) activeMenu.getProperty("skill")).getSources()) {
                    if (source.getXp() == 0.0 || plugin.getItemRegistry().getSourceMenuItems().getMenuItem(source) == null) continue;
                    filteredSources.add(source);
                }
                filteredSources.sort(((SortType) activeMenu.getProperty("sort_type")).getComparator(plugin, plugin.getLocale(m.player())));
                // Gets a sublist of the sources displayed based on the current page
                int toIndex = Math.max((page + 1) * itemsPerPage, filteredSources.size());
                List<XpSource> shownSources = filteredSources.subList(page * itemsPerPage, toIndex);
                activeMenu.setProperty("sources", shownSources); // Set sorted sources property for easy access in other methods
                return new HashSet<>(shownSources);
            });
        });
    }

    private int getItemsPerPage() {
        ConfigurableMenu menu = plugin.getMenuManager().getMenu("sources");
        if (menu != null) {
            return (int) menu.getOptions().getOrDefault("items_per_page", DEF_ITEMS_PER_PAGE);
        }
        return DEF_ITEMS_PER_PAGE;
    }

    private String getSortedTypesLore(PlaceholderInfo info) {
        Locale locale = plugin.getLocale(info.player());
        ListBuilder builder = new ListBuilder(info.data().getListData());

        SortType selected = (SortType) info.menu().getProperty("sort_type");
        for (SortType sortType : SortType.values()) {
            String typeString = TextUtil.replace(info.menu().getFormat("sort_type_entry"), "{type_name}",
                    plugin.getMsg(MenuMessage.valueOf(sortType.toString()), locale));
            if (selected == sortType) {
                typeString = TextUtil.replace(typeString, "{selected}", info.menu().getFormat("selected"));
            } else {
                typeString = TextUtil.replace(typeString, "{selected}", "");
            }
            builder.append(typeString);
        }
        return builder.build();
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

    private SlotPos parsePos(String input) {
        String[] split = input.split(",");
        if (split.length == 2) {
            return SlotPos.of(NumberUtil.toInt(split[0]), NumberUtil.toInt(split[1]));
        }
        return SlotPos.of(0, 0);
    }

    public enum SortType {

        ASCENDING,
        DESCENDING,
        ALPHABETICAL,
        REVERSE_ALPHABETICAL;

        public SourceComparator getComparator(AuraSkills plugin, Locale locale) {
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
