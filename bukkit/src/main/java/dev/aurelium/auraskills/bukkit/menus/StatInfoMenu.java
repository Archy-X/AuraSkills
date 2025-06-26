package dev.aurelium.auraskills.bukkit.menus;

import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.shared.GlobalItems;
import dev.aurelium.auraskills.bukkit.menus.shared.ModifierInstance;
import dev.aurelium.auraskills.bukkit.menus.shared.ModifierInstances;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.DataUtil;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import dev.aurelium.slate.builder.ItemBuilder;
import dev.aurelium.slate.builder.MenuBuilder;
import dev.aurelium.slate.info.PlaceholderInfo;
import dev.aurelium.slate.info.TemplateInfo;
import dev.aurelium.slate.info.TemplatePlaceholderInfo;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.menu.ActiveMenu;
import dev.aurelium.slate.util.VersionUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class StatInfoMenu {

    private final AuraSkills plugin;
    private final ModifierInstances instances;

    public StatInfoMenu(AuraSkills plugin) {
        this.plugin = plugin;
        this.instances = new ModifierInstances(plugin);
    }

    private boolean isDirectTrait(ActiveMenu menu) {
        return menu.property("direct_trait", false);
    }

    public void build(MenuBuilder menu) {
        menu.onOpen(m -> {
            Stat stat = (Stat) m.menu().getProperty("stat");
            Map<String, ModifierInstance> modifiers = instances.getInstances(plugin.getUser(m.player()), stat);
            if (stat.hasDirectTrait()) {
                m.menu().setProperty("direct_trait", true);
                Trait trait = stat.getTraits().getFirst();
                m.menu().setProperty("trait", trait);
                // Add trait modifiers to modifiers set
                modifiers.putAll(instances.getInstances(plugin.getUser(m.player()), trait, true));
            }
            if (m.menu().property("selected") instanceof Trait trait) {
                m.menu().setProperty("trait", trait);
                // Add trait modifiers instead
                modifiers.clear();
                modifiers.putAll(instances.getInstances(plugin.getUser(m.player()), trait));
            }

            Map<String, ModifierInstance> sortedModifiers = instances.sortAndReindex(modifiers);
            m.menu().setProperty("modifiers", sortedModifiers);
        });

        menu.replace("color", p -> stat(p).getColor(p.locale()));
        menu.replace("stat_name", p -> stat(p).getDisplayName(p.locale(), false));

        var globalItems = new GlobalItems(plugin);
        menu.item("back", item -> getBackItem(plugin, item));
        menu.fillItem(globalItems::fill);

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

            template.definedContexts(m -> Set.of(((Stat) m.menu().property("stat"))));
            template.modify(t -> {
                ItemStack item = hideAttributes(t);
                // Glow if selected
                if (!(t.menu().property("selected") instanceof Trait)) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        meta.addEnchant(Enchantment.MENDING, 1, true);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        item.setItemMeta(meta);
                    }
                }
                return item;
            });

            template.onClick(t -> {
                Map<String, Object> props = Map.of(
                        "stat", t.menu().property("stat"),
                        "previous_menu", t.menu().property("previous_menu"));
                plugin.getSlate().openMenu(t.player(), "stat_info", props);
            });
        });

        menu.template("trait", Trait.class, template -> {
            template.replace("trait_name", p -> p.value().getDisplayName(p.locale()));
            template.replace("level_modifier", p -> NumberUtil.format2(((Stat) p.menu().property("stat")).getTraitModifier(p.value())));
            template.replace("level", p -> NumberUtil.format1(plugin.getUser(p.player()).getEffectiveTraitLevel(p.value())));
            template.replace("base_level", p -> NumberUtil.format1(plugin.getUser(p.player()).getUserStats().getTraitBaseAddSum(p.value())));
            template.replace("add_percent_modifiers", p -> NumberUtil.format1(plugin.getUser(p.player()).getUserStats().getTraitAddPercentSum(p.value())));
            template.replace("multiply_modifiers", p -> NumberUtil.format2(plugin.getUser(p.player()).getUserStats().getTraitMultiplyProduct(p.value())));

            template.onClick(t -> {
                var props = Map.of(
                        "stat", t.menu().property("stat"),
                        "selected", t.value(),
                        "previous_menu", (String) t.menu().property("previous_menu"));
                plugin.getSlate().openMenu(t.player(), "stat_info", props);
            });

            template.definedContexts(m -> {
                Set<Trait> contexts = new HashSet<>();
                for (Trait trait : ((Stat) m.menu().property("stat")).getTraits()) {
                    if (trait.isEnabled()) {
                        contexts.add(trait);
                    }
                }
                return contexts;
            });

            template.slotPos(t -> getTraitSlot(t.menu(), t.value()));

            template.modify(t -> {
                Stat stat = t.menu().property("stat");
                if (stat.hasDirectTrait()) { // Hide the trait item if stat has direct trait
                    return null;
                }
                if (t.item() == null) {
                    return null;
                }

                ItemStack item = hideAttributes(t);
                // Glow if selected
                if (t.menu().property("selected") instanceof Trait trait && t.value().equals(trait)) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        meta.addEnchant(Enchantment.MENDING, 1, true);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        item.setItemMeta(meta);
                    }
                }
                return item;
            });

        });

        menu.template("modifier", String.class, template -> {
            template.replace("display_name", t -> instance(t.menu(), t.value()).displayName());
            template.replace("id", t -> instance(t.menu(), t.value()).id());
            template.replace("description", t -> instance(t.menu(), t.value()).description());
            template.replace("operation", t -> instance(t.menu(), t.value()).operation().getDisplayName());
            template.replace("value", t -> NumberUtil.format2(instance(t.menu(), t.value()).value()));
            template.replace("value_format", t ->
                    switch (instance(t.menu(), t.value()).operation()) {
                        case ADD -> formatValue(t, "value_add");
                        case MULTIPLY -> formatValue(t, "value_multiply");
                        case ADD_PERCENT -> formatValue(t, "value_add_percent");
                    }
            );
            template.replace("type_name", t -> {
                var type = instance(t.menu(), t.value()).parent();
                if (type instanceof Stat || stat(t).hasDirectTrait()) {
                    return stat(t).getDisplayName(t.locale());
                } else if (type instanceof Trait trait) {
                    return trait.getDisplayName(t.locale());
                }
                return t.placeholder();
            });

            template.definedContexts(m -> {
                Map<String, ModifierInstance> map = m.menu().property("modifiers");
                return new HashSet<>(map.keySet());
            });

            template.slotPos(t -> {
                var instance = instance(t.menu(), t.value());
                SlotPos start;
                SlotPos end;

                Object startObj = t.menu().getItemOption("modifier", "start");
                if (startObj != null) {
                    start = GlobalItems.parseSlot(String.valueOf(startObj));
                } else {
                    start = SlotPos.of(2, 1);
                }
                Object endObj = t.menu().getItemOption("modifier", "end");
                if (endObj != null) {
                    end = GlobalItems.parseSlot(String.valueOf(endObj));
                } else {
                    end = SlotPos.of(4, 1);
                }

                return getRectangleIndex(start, end, instance.index());
            });

            template.modify(t -> {
                if (t.item().equals(ModifierInstances.getFallbackItem())) {
                    ItemStack item = instance(t.menu(), t.value()).item();
                    return Objects.requireNonNullElseGet(item, ModifierInstances::getFallbackItem);
                } else {
                    return t.item();
                }
            });
        });

        menu.component("traits_leveled", Stat.class, component -> {
            component.replace("color", p -> stat(p).getColor(p.locale()));

            component.shouldShow(c -> !c.value().hasDirectTrait() && !c.value().getTraits().isEmpty());
        });

        menu.component("stat_modifiers_click", Stat.class, component ->
                component.shouldShow(c -> c.menu().property("selected") instanceof Trait));

        menu.component("showing_stat_modifiers", Stat.class, component -> {
            component.replace("modifier_count", p -> String.valueOf(((Map<?, ?>) p.menu().property("modifiers")).size()));

            component.shouldShow(c -> !(c.menu().property("selected") instanceof Trait));
        });

        menu.component("trait_modifiers_click", Trait.class, component ->
                component.shouldShow(c -> !c.value().equals(c.menu().property("selected"))));

        menu.component("showing_trait_modifiers", Trait.class, component -> {
            component.replace("modifier_count", p -> String.valueOf(((Map<?, ?>) p.menu().property("modifiers")).size()));

            component.shouldShow(c -> c.menu().property("selected") instanceof Trait trait && c.value().equals(trait));
        });

        menu.component("effective_trait_value", Trait.class, component -> {
            component.replace("trait_name", p -> p.value().getDisplayName(p.locale()));
            component.replace("effective_value", p -> {
                double level = plugin.getUser(p.player()).getEffectiveTraitLevel(p.value());
                return p.value().getMenuDisplay(level, p.locale());
            });

            component.shouldShow(c -> {
                var impl = plugin.getTraitManager().getTraitImpl(c.value());
                if (impl != null) {
                    return !impl.displayMatchesValue();
                }
                return false;
            });
        });
    }

    private ModifierInstance instance(ActiveMenu menu, String id) {
        Map<String, ModifierInstance> map = menu.property("modifiers");
        return map.get(id);
    }

    private String formatValue(TemplatePlaceholderInfo<String> t, String format) {
        return t.menu().getFormat(format).replace("{value}",
                NumberUtil.format2(instance(t.menu(), t.value()).value()));
    }

    private void getBackItem(AuraSkills plugin, ItemBuilder item) {
        item.replace("menu_name", p -> TextUtil.capitalizeWord(TextUtil.replace((String) p.menu().getProperty("previous_menu"), "_", " ")));
        item.onClick(c -> {
            Map<String, Object> props = Map.of(
                    "stat", c.menu().property("stat"),
                    "previous_menu", "skills");
            plugin.getSlate().openMenu(c.player(), (String) c.menu().getProperty("previous_menu"), props);
        });
        item.modify(i -> i.menu().getProperty("previous_menu") == null ? null : i.item());
    }

    private ItemStack hideAttributes(TemplateInfo<?> t) {
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
    private SlotPos getRectangleIndex(SlotPos topLeft, SlotPos bottomRight, int index) {
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
