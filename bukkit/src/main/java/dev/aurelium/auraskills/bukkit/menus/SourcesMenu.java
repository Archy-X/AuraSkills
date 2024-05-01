package dev.aurelium.auraskills.bukkit.menus;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.shared.GlobalItems;
import dev.aurelium.auraskills.bukkit.menus.util.SourceComparator;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.DataUtil;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import dev.aurelium.slate.builder.MenuBuilder;
import dev.aurelium.slate.info.PlaceholderInfo;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.item.provider.ListBuilder;
import dev.aurelium.slate.menu.ActiveMenu;
import dev.aurelium.slate.menu.LoadedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SourcesMenu {

    public static final int DEF_ITEMS_PER_PAGE = 28;
    public static final String DEF_SOURCE_START = "1,1";
    public static final String DEF_SOURCE_END = "4,8";
    private final List<Integer> track;

    private final AuraSkills plugin;

    public SourcesMenu(AuraSkills plugin) {
        this.plugin = plugin;
        this.track = new ArrayList<>();
    }

    private void initTrack() {
        LoadedMenu menu = plugin.getSlate().getLoadedMenu("sources");
        if (menu != null) {
            Object trackObj = menu.options().get("track");
            if (trackObj != null) {
                this.track.clear();
                this.track.addAll(DataUtil.castIntegerList(trackObj));
            }
        }
    }

    public void build(MenuBuilder menu) {
        menu.onOpen(m -> initTrack());

        menu.defaultOptions(Map.of(
                "source_start", SourcesMenu.DEF_SOURCE_START,
                "source_end", SourcesMenu.DEF_SOURCE_END,
                "items_per_page", SourcesMenu.DEF_ITEMS_PER_PAGE,
                "use_track", false,
                "track", new int[0]));

        menu.replaceTitle("current_page", p -> String.valueOf(p.menu().getCurrentPage() + 1));
        menu.replaceTitle("total_pages", p -> String.valueOf(p.menu().getTotalPages()));
        menu.replaceTitle("skill", p -> ((Skill) p.menu().getProperty("skill")).getDisplayName(p.locale(), false));

        menu.pages(m -> {
            var skill = (Skill) m.menu().getProperty("skill");
            int itemsPerPage = (Integer) m.menu().getProperty("items_per_page");
            int numSources = plugin.getSkillManager().getSkill(skill).sources().size();
            return (numSources - 1) / itemsPerPage + 1;
        });

        menu.properties(m -> Map.of(
                "skill", m.menu().getProperty("skill", Skills.FARMING),
                "items_per_page", getItemsPerPage(),
                "sort_type", SortType.ASCENDING,
                "previous_menu", "level_progression"));

        var globalItems = new GlobalItems(plugin);
        menu.item("back", globalItems::backToLevelProgression);
        menu.item("previous_page", globalItems::previousPage);
        menu.item("next_page", globalItems::nextPage);
        menu.fillItem(globalItems::fill);

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
            template.replace("source_name", p -> p.value().getDisplayName(p.locale()));
            template.replace("source_xp", p -> {
                XpSource source = p.value();
                String unitName = source.getUnitName(p.locale());
                return TextUtil.replace(unitName == null ? p.menu().getFormat("source_xp") : p.menu().getFormat("source_xp_rate"),
                        "{xp}", NumberUtil.format2(source.getXp()),
                        "{unit}", unitName);
            });

            template.slotPos(t -> {
                int index = getSortedSources(t.menu()).indexOf(t.value());
                if (index != -1) {
                    boolean useTrack = t.menu().getOption(Boolean.class, "use_track", false);
                    if (useTrack) {
                        if (index < track.size()) {
                            int pos = track.get(index);
                            return SlotPos.of(pos / 9, pos % 9);
                        }
                    } else {
                        // Convert index of source into position on menu
                        SlotPos start = parsePos(t.menu().getOption(String.class, "source_start", DEF_SOURCE_START));
                        SlotPos end = parsePos(t.menu().getOption(String.class, "source_end", DEF_SOURCE_END));
                        int numRows = end.getRow() - start.getRow();
                        int numCols = end.getColumn() - start.getColumn();
                        int row = Math.min(start.getRow() + index / numCols, start.getRow() + numRows);
                        int column = start.getColumn() + index % numCols;
                        return SlotPos.of(row, column);
                    }
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
                filteredSources.sort(((SortType) activeMenu.getProperty("sort_type")).getComparator(plugin, m.locale()));
                // Gets a sublist of the sources displayed based on the current page
                int toIndex = Math.min((page + 1) * itemsPerPage, filteredSources.size());
                List<XpSource> shownSources = filteredSources.subList(page * itemsPerPage, toIndex);
                activeMenu.setProperty("sources", shownSources); // Set sorted sources property for easy access in other methods
                return new HashSet<>(shownSources);
            });

            template.modify(t -> {
                if (t.item().getType() != Material.GRAY_DYE) return t.item();
                ItemStack item = plugin.getItemRegistry().getSourceMenuItems().getMenuItem(t.value());
                if (item == null) {
                    plugin.logger().warn("Item of source " + t.value().getId() + " not found");
                }
                return item;
            });
        });

        menu.component("multiplied_xp", XpSource.class, component -> {
            component.replace("source_xp", p -> {
                double multiplier = getMultiplier(p.player(), (Skill) p.menu().getProperty("skill"));
                String unitName = p.value().getUnitName(p.locale());
                return TextUtil.replace(unitName == null ? p.menu().getFormat("source_xp") : p.menu().getFormat("source_xp_rate"),
                        "{xp}", NumberUtil.format2(p.value().getXp() * multiplier),
                        "{unit}", unitName);
            });
            component.shouldShow(t -> getMultiplier(t.player(), (Skill) t.menu().getProperty("skill")) > 1.0);
        });
    }

    private double getMultiplier(Player player, Skill skill) {
        User user = plugin.getUser(player);
        double permissionMultiplier = 1 + plugin.getLevelManager().getPermissionMultiplier(user, skill);
        return plugin.getLevelManager().getAbilityMultiplier(user, skill) * permissionMultiplier;
    }

    private int getItemsPerPage() {
        LoadedMenu menu = plugin.getSlate().getLoadedMenu("sources");
        if (menu != null) {
            return (int) menu.options().getOrDefault("items_per_page", DEF_ITEMS_PER_PAGE);
        }
        return DEF_ITEMS_PER_PAGE;
    }

    private String getSortedTypesLore(PlaceholderInfo info) {
        ListBuilder builder = new ListBuilder(info.data().listData());

        SortType selected = (SortType) info.menu().getProperty("sort_type");
        for (SortType sortType : SortType.values()) {
            String typeString = TextUtil.replace(info.menu().getFormat("sort_type_entry"), "{type_name}",
                    plugin.getMsg(MenuMessage.valueOf(sortType.toString()), info.locale()));
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
            return switch (this) {
                case DESCENDING -> new SourceComparator.Descending(plugin);
                case ALPHABETICAL -> new SourceComparator.Alphabetical(plugin, locale);
                case REVERSE_ALPHABETICAL -> new SourceComparator.ReverseAlphabetical(plugin, locale);
                default -> new SourceComparator.Ascending(plugin);
            };
        }

    }

}
