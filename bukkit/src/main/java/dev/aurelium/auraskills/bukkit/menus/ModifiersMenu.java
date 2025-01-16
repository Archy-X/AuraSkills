package dev.aurelium.auraskills.bukkit.menus;

import dev.aurelium.auraskills.api.registry.NamespaceIdentified;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.util.AuraSkillsModifier.Operation;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.shared.GlobalItems;
import dev.aurelium.auraskills.bukkit.menus.shared.SkillItem;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.DataUtil;
import dev.aurelium.slate.builder.MenuBuilder;
import dev.aurelium.slate.info.PlaceholderInfo;
import dev.aurelium.slate.inv.content.SlotPos;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class ModifiersMenu {

    private final AuraSkills plugin;

    public ModifiersMenu(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void build(MenuBuilder menu) {
        menu.replace("color", p -> stat(p).getColor(p.locale()));
        menu.replace("stat_name", p -> stat(p).getDisplayName(p.locale(), false));

        var globalItems = new GlobalItems(plugin);
        menu.item("back", globalItems::back);

        menu.template("stat", Stat.class, template -> {
            template.replace("stat_desc", p -> stat(p).getDescription(p.locale(), false));
            template.replace("level", p -> NumberUtil.format1(user(p).getStatLevel(stat(p))));
            template.replace("base_level", p -> NumberUtil.format1(user(p).getUserStats().getStatBaseAddSum(stat(p))));
            template.replace("add_percent_modifiers", p -> NumberUtil.format1(user(p).getUserStats().getStatAddPercentSum(stat(p))));
            template.replace("multiply_modifiers", p -> NumberUtil.format2(user(p).getUserStats().getStatMultiplyProduct(stat(p))));

            template.definedContexts(m -> Set.of(((Stat) m.menu().property("stat"))));
        });

        menu.template("trait", Trait.class, template -> {
            template.replace("trait_name", p -> p.value().getDisplayName(p.locale()));
            template.replace("level_modifier", p -> NumberUtil.format2(stat(p).getTraitModifier(p.value())));
            template.replace("level", p -> NumberUtil.format1(user(p).getEffectiveTraitLevel(p.value())));
            template.replace("base_level", p -> NumberUtil.format1(user(p).getUserStats().getTraitBaseAddSum(p.value())));
            template.replace("add_percent_modifiers", p -> NumberUtil.format1(user(p).getUserStats().getTraitAddPercentSum(p.value())));
            template.replace("multiply_modifiers", p -> NumberUtil.format2(user(p).getUserStats().getTraitMultiplyProduct(p.value())));

            template.definedContexts(m -> new HashSet<>(((Stat) m.menu().property("stat")).getTraits()));

            template.slotPos(t -> {
                Object obj = t.menu().getItemOption("trait", "slots");
                if (obj instanceof List<?>) {
                    List<String> slots = DataUtil.castStringList(obj);
                    int index = getTraitIndex(t.menu().property("stat"), t.value());
                    if (slots.size() > index) {
                        String slot = slots.get(index);
                        return GlobalItems.parseSlot(slot);
                    }
                }
                int index = getTraitIndex(t.menu().property("stat"), t.value());
                return SlotPos.of(1, 2 + index); // Fallback default pos
            });
        });

        menu.template("modifier", ModifierInstance.class, template -> {

        });
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

    public record ModifierInstance(
            NamespaceIdentified parent,
            String id,
            double value,
            Operation operation,
            ItemStack item,
            String displayName,
            String description
    ) {

        public static List<ModifierInstance> getInstances(User user, NamespaceIdentified parent) {
            List<ModifierInstance> instances = new ArrayList<>();
            if (parent instanceof Stat stat) {
                // Add instances for each skill that gives the stat as a reward
                for (Entry<Skill, Double> entry : user.getUserStats().getLevelRewardedBySkill(stat).entrySet()) {
                    instances.add(new ModifierInstance(
                            stat,
                            "stat_reward_" + entry.getKey().getId().toString(),
                            entry.getValue(),
                            Operation.ADD,
                            SkillItem.getBaseItem(entry.getKey(), user.getPlugin()),
                            entry.getKey().getDisplayName(user.getLocale()),
                            "Rewarded by leveling this skill"
                    ));
                }
                // TODO add instances for modifiers and traits
            }
            return instances;
        }

        public static ItemStack getFallbackItem() {
            return new ItemStack(Material.GRAY_DYE);
        }

    }

}
