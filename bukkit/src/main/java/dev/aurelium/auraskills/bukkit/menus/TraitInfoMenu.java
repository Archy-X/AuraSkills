package dev.aurelium.auraskills.bukkit.menus;

import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.shared.GlobalItems;
import dev.aurelium.auraskills.bukkit.menus.shared.ModifierInstance;
import dev.aurelium.auraskills.bukkit.menus.shared.ModifierInstances;
import dev.aurelium.slate.builder.MenuBuilder;
import dev.aurelium.slate.builder.TemplateBuilder;
import dev.aurelium.slate.info.PlaceholderInfo;
import dev.aurelium.slate.inv.content.SlotPos;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TraitInfoMenu {

    private final AuraSkills plugin;
    private final ModifierInstances instances;

    public TraitInfoMenu(AuraSkills plugin) {
        this.plugin = plugin;
        this.instances = new ModifierInstances(plugin);
    }

    public void build(MenuBuilder menu) {
        menu.onOpen(m -> {
            var modifiers = instances.getInstances(plugin.getUser(m.player()), m.menu().property("trait"));
            Map<String, ModifierInstance> sortedModifiers = instances.sortAndReindex(modifiers);
            m.menu().setProperty("modifiers", sortedModifiers);
        });

        menu.replace("color", p -> stat(p).getColor(p.locale()));
        menu.replace("trait_name", p -> trait(p).getDisplayName(p.locale(), false));
        menu.replace("stat_name", p -> stat(p).getDisplayName(p.locale(), false));

        menu.item("back", item -> StatInfoMenu.getBackItem(plugin, item, "stats"));

        menu.template("trait", Trait.class, template -> {
            setTraitPlaceholders(template, plugin);

            template.replace("modifier_count", p -> String.valueOf(((Map<?, ?>) p.menu().property("modifiers")).size()));

            template.definedContexts(m -> Set.of((Trait) m.menu().property("trait")));
            template.modify(StatInfoMenu::hideAttributes);
        });

        menu.template("trait_modifier", String.class, template -> {
            instances.setPlaceholders(template);

            template.definedContexts(m -> {
                Map<String, ModifierInstance> map = m.menu().property("modifiers");
                return new HashSet<>(map.keySet());
            });

            template.slotPos(t -> {
                var instance = instances.instance(t.menu(), t.value());
                SlotPos start;
                SlotPos end;
                if (instance.parent() instanceof Trait) {
                    Object startObj = t.menu().getItemOption("trait_modifier", "start");
                    if (startObj != null) {
                        start = GlobalItems.parseSlot(String.valueOf(startObj));
                    } else {
                        start = SlotPos.of(2, 1);
                    }
                    Object endObj = t.menu().getItemOption("trait_modifier", "end");
                    if (endObj != null) {
                        end = GlobalItems.parseSlot(String.valueOf(endObj));
                    } else {
                        end = SlotPos.of(4, 1);
                    }
                } else {
                    return null;
                }

                return StatInfoMenu.getRectangleIndex(start, end, instance.index());
            });

            template.modify(instances::modifyItem);
        });
    }

    public static void setTraitPlaceholders(TemplateBuilder<Trait> template, AuraSkills plugin) {
        template.replace("trait_name", p -> p.value().getDisplayName(p.locale()));
        template.replace("level_modifier", p -> NumberUtil.format2(((Stat) p.menu().property("stat")).getTraitModifier(p.value())));
        template.replace("level", p -> NumberUtil.format1(plugin.getUser(p.player()).getEffectiveTraitLevel(p.value())));
        template.replace("base_level", p -> NumberUtil.format1(plugin.getUser(p.player()).getUserStats().getTraitBaseAddSum(p.value())));
        template.replace("add_percent_modifiers", p -> NumberUtil.format1(plugin.getUser(p.player()).getUserStats().getTraitAddPercentSum(p.value())));
        template.replace("multiply_modifiers", p -> NumberUtil.format2(plugin.getUser(p.player()).getUserStats().getTraitMultiplyProduct(p.value())));
    }

    private Stat stat(PlaceholderInfo info) {
        return info.menu().property("stat");
    }

    private Trait trait(PlaceholderInfo info) {
        return info.menu().property("trait");
    }

}
