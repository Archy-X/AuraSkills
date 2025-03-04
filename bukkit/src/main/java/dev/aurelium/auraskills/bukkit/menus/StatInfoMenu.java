package dev.aurelium.auraskills.bukkit.menus;

import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.shared.GlobalItems;
import dev.aurelium.auraskills.bukkit.menus.shared.ModifierInstance;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.DataUtil;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import dev.aurelium.slate.builder.ItemBuilder;
import dev.aurelium.slate.builder.MenuBuilder;
import dev.aurelium.slate.info.PlaceholderInfo;
import dev.aurelium.slate.info.TemplateInfo;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.menu.ActiveMenu;
import dev.aurelium.slate.util.VersionUtil;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class StatInfoMenu {

    private final AuraSkills plugin;

    public StatInfoMenu(AuraSkills plugin) {
        this.plugin = plugin;
    }

    private boolean isDirectTrait(ActiveMenu menu) {
        return menu.property("direct_trait", false);
    }

    public void build(MenuBuilder menu) {
        menu.onOpen(m -> {
            Stat stat = (Stat) m.menu().getProperty("stat");
            var statMods = ModifierInstance.getInstances(plugin, plugin.getUser(m.player()), stat);
            List<ModifierInstance> modList = new ArrayList<>(statMods);
            if (stat.hasDirectTrait()) {
                m.menu().setProperty("direct_trait", true);
                Trait trait = stat.getTraits().get(0);
                m.menu().setProperty("trait", trait);
                // Add trait modifiers to modifiers set
                List<ModifierInstance> traitMods = ModifierInstance.getInstances(plugin, plugin.getUser(m.player()), trait, true);
                // Reindex trait modifier instances so indices don't collide
                if (!traitMods.isEmpty()) {
                    if (!statMods.isEmpty()) {
                        List<ModifierInstance> indexed = new ArrayList<>();
                        int index = statMods.get(statMods.size() - 1).index() + 1; // Start with last index of stats + 1
                        for (ModifierInstance instance : traitMods) {
                            indexed.add(instance.withIndex(index));
                            index++;
                        }
                        modList.addAll(indexed);
                    } else {
                        modList.addAll(traitMods);
                    }
                }
            }
            List<ModifierInstance> sortedModifiers = ModifierInstance.sortAndReindex(modList);
            m.menu().setProperty("modifiers", new HashSet<>(sortedModifiers));
        });

        menu.replace("color", p -> stat(p).getColor(p.locale()));
        menu.replace("stat_name", p -> stat(p).getDisplayName(p.locale(), false));

        menu.item("back", item -> StatInfoMenu.getBackItem(plugin, item, "skills"));

        menu.template("stat", Stat.class, template -> {
            template.replace("stat_desc", p -> stat(p).getDescription(p.locale(), false));
            template.replace("level", p -> {
                if (isDirectTrait(p.menu())) {
                    return NumberUtil.format1(user(p).getEffectiveTraitLevel(p.menu().property("trait")));
                } else {
                    return NumberUtil.format1(user(p).getStatLevel(stat(p)));
                }
            });
            template.replace("base_level", p -> {
                if (isDirectTrait(p.menu())) {
                    return NumberUtil.format1(user(p).getUserStats().getTraitBaseAddSum(p.menu().property("trait")));
                } else {
                    return NumberUtil.format1(user(p).getUserStats().getStatBaseAddSum(stat(p)));
                }
            });
            template.replace("add_percent_modifiers", p -> {
                if (isDirectTrait(p.menu())) {
                    Trait trait = p.menu().property("trait");
                    double traitSum = user(p).getUserStats().getTraitAddPercentSum(trait);
                    double statSum = user(p).getUserStats().getStatAddPercentSum(stat(p));
                    return NumberUtil.format1(traitSum + statSum);
                } else {
                    return NumberUtil.format1(user(p).getUserStats().getStatAddPercentSum(stat(p)));
                }
            });
            template.replace("multiply_modifiers", p -> {
                if (isDirectTrait(p.menu())) {
                    Trait trait = p.menu().property("trait");
                    double traitProd = user(p).getUserStats().getTraitMultiplyProduct(trait);
                    double statProd = user(p).getUserStats().getStatMultiplyProduct(stat(p));
                    return NumberUtil.format2(traitProd * statProd);
                } else {
                    return NumberUtil.format2(user(p).getUserStats().getStatMultiplyProduct(stat(p)));
                }
            });
            template.replace("modifier_count", p -> String.valueOf(((Set<?>) p.menu().property("modifiers")).size()));

            template.definedContexts(m -> Set.of(((Stat) m.menu().property("stat"))));
            template.modify(StatInfoMenu::hideAttributes);
        });

        menu.template("trait", Trait.class, template -> {
            TraitInfoMenu.setTraitPlaceholders(template, plugin);

            template.onClick(t -> {
                var props = Map.of(
                        "stat", t.menu().property("stat"),
                        "trait", t.value(),
                        "previous_menu", "stat_info");
                plugin.getSlate().openMenu(t.player(), "trait_info", props);
            });

            template.definedContexts(m -> new HashSet<>(((Stat) m.menu().property("stat")).getTraits()));

            template.slotPos(t -> getTraitSlot(t.menu(), t.value()));

            template.modify(t -> {
                Stat stat = t.menu().property("stat");
                if (stat.hasDirectTrait()) { // Hide the trait item if stat has direct trait
                    return null;
                } else {
                    return StatInfoMenu.hideAttributes(t);
                }
            });

        });

        menu.template("stat_modifier", ModifierInstance.class, template -> {
            ModifierInstance.setPlaceholders(template);

            template.definedContexts(m -> m.menu().property("modifiers"));

            template.slotPos(t -> {
                var instance = t.value();
                SlotPos start;
                SlotPos end;

                Object startObj = t.menu().getItemOption("stat_modifier", "start");
                if (startObj != null) {
                    start = GlobalItems.parseSlot(String.valueOf(startObj));
                } else {
                    start = SlotPos.of(2, 1);
                }
                Object endObj = t.menu().getItemOption("stat_modifier", "end");
                if (endObj != null) {
                    end = GlobalItems.parseSlot(String.valueOf(endObj));
                } else {
                    end = SlotPos.of(4, 1);
                }

                return getRectangleIndex(start, end, instance.index());
            });

            template.modify(t -> {
                ItemStack item = t.value().item();
                return Objects.requireNonNullElseGet(item, ModifierInstance::getFallbackItem);
            });
        });
    }

    public static void getBackItem(AuraSkills plugin, ItemBuilder item, String previousMenu) {
        item.replace("menu_name", p -> TextUtil.capitalizeWord(TextUtil.replace((String) p.menu().getProperty("previous_menu"), "_", " ")));
        item.onClick(c -> {
            Map<String, Object> props = Map.of(
                    "stat", c.menu().property("stat"),
                    "previous_menu", previousMenu);
            plugin.getSlate().openMenu(c.player(), (String) c.menu().getProperty("previous_menu"), props);
        });
        item.modify(i -> i.menu().getProperty("previous_menu") == null ? null : i.item());
    }

    public static ItemStack hideAttributes(TemplateInfo<?> t) {
        ItemMeta meta = t.item().getItemMeta();
        if (meta == null) return t.item();
        if (VersionUtil.isAtLeastVersion(20, 5)) {
            meta.setAttributeModifiers(Material.IRON_SWORD.getDefaultAttributeModifiers(EquipmentSlot.HAND));
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        t.item().setItemMeta(meta);
        return t.item();
    }

    @Nullable
    public static SlotPos getRectangleIndex(SlotPos topLeft, SlotPos bottomRight, int index) {
        int topRow = topLeft.getRow();
        int leftColumn = topLeft.getColumn();
        int bottomRow = bottomRight.getRow();
        int rightColumn = bottomRight.getColumn();

        int numRows = bottomRow - topRow + 1;
        int numColumns = rightColumn - leftColumn + 1;

        int totalSlots = numRows * numColumns;
        if (index < 0 || index >= totalSlots) {
            return null;
        }

        int rowOffset = index / numColumns;
        int columnOffset = index % numColumns;

        int resultRow = topRow + rowOffset;
        int resultColumn = leftColumn + columnOffset;

        return SlotPos.of(resultRow, resultColumn);
    }

    private SlotPos getTraitSlot(ActiveMenu menu, Trait trait) {
        if (menu.getProperties().containsKey(trait.getId().toString())) { // Check if in cache
            return menu.property(trait.getId().toString());
        }
        Object obj = menu.getItemOption("trait", "slots");
        if (obj instanceof List<?>) {
            List<String> slots = DataUtil.castStringList(obj);
            int index = getTraitIndex(menu.property("stat"), trait);
            if (slots.size() > index) {
                String slot = slots.get(index);
                SlotPos slotPos = GlobalItems.parseSlot(slot);
                menu.setProperty(trait.getId().toString(), slotPos); // Save to property as cache
                return slotPos;
            }
        }
        int index = getTraitIndex(menu.property("stat"), trait);
        SlotPos fallback = SlotPos.of(1, 2 + index);
        menu.setProperty(trait.getId().toString(), fallback);
        return fallback;
    }

    private int getTraitIndex(Stat stat, Trait current) {
        int index = 0;
        for (Trait trait : stat.getTraits()) {
            if (!trait.isEnabled()) continue;
            if (trait.equals(current)) break;
            index++;
        }
        return index;
    }

    private Stat stat(PlaceholderInfo info) {
        return info.menu().property("stat");
    }

    private User user(PlaceholderInfo info) {
        return plugin.getUser(info.player());
    }

}
