package dev.aurelium.auraskills.bukkit.menus.shared;

import dev.aurelium.auraskills.api.registry.NamespaceIdentified;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.api.util.AuraSkillsModifier.Operation;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.modifier.TraitModifiers;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.slate.builder.TemplateBuilder;
import dev.aurelium.slate.info.TemplatePlaceholderInfo;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public record ModifierInstance(
        NamespaceIdentified parent,
        String id,
        double value,
        Operation operation,
        @Nullable ItemStack item,
        String displayName,
        String description,
        int index
) {

    public static List<ModifierInstance> getInstances(AuraSkills plugin, User user, NamespaceIdentified parent) {
        List<ModifierInstance> instances = new ArrayList<>();
        int index = 0;
        if (parent instanceof Stat stat) {
            // Add instances for each skill that gives the stat as a reward
            for (Entry<Skill, Double> entry : user.getUserStats().getLevelRewardedBySkill(stat).entrySet()) {
                if (entry.getValue() == 0.0) continue;
                instances.add(new ModifierInstance(
                        stat,
                        "stat_reward_" + entry.getKey().getId().toString(),
                        entry.getValue(),
                        Operation.ADD,
                        SkillItem.getBaseItem(entry.getKey(), user.getPlugin()),
                        entry.getKey().getDisplayName(user.getLocale()),
                        plugin.getMsg(MenuMessage.STAT_REWARD_DESC, user.getLocale()).replace("{skill}",
                                entry.getKey().getDisplayName(user.getLocale())),
                        index
                ));
                index++;
            }
            for (StatModifier modifier : user.getStatModifiers().values()) {
                if (modifier.value() == 0.0) continue;
                if (!modifier.stat().equals(stat)) continue;
                instances.add(new ModifierInstance(
                        stat,
                        "stat_modifier_" + modifier.name(),
                        modifier.value(),
                        modifier.operation(),
                        ModifierInstance.getFallbackItem(),
                        modifier.name(),
                        "",
                        index
                ));
                index++;
            }
        } else if (parent instanceof Trait trait) {
            // Base value if it exists
            double base = plugin.getTraitManager().getBaseLevel(user, trait);
            if (base >= 0.00001) {
                instances.add(new ModifierInstance(
                        trait,
                        "trait_base_level",
                        base,
                        Operation.ADD,
                        ModifierInstance.getFallbackItem(),
                        plugin.getMsg(MenuMessage.BASE_LEVEL, user.getLocale()),
                        plugin.getMsg(MenuMessage.BASE_LEVEL_DESC, user.getLocale()),
                        index
                ));
                index++;
            }
            // Linked stats
            for (Stat stat : plugin.getTraitManager().getLinkedStats(trait)) {
                double modifier = stat.getTraitModifier(trait);
                double level = user.getStatLevel(stat);
                if (level == 0.0) continue;
                instances.add(new ModifierInstance(
                        trait,
                        "linked_stat_" + stat.getId(),
                        level * modifier,
                        Operation.ADD,
                        ModifierInstance.getFallbackItem(),
                        stat.getDisplayName(user.getLocale()),
                        plugin.getMsg(MenuMessage.LINKED_STAT_DESC, user.getLocale()).replace("{stat}",
                                stat.getDisplayName(user.getLocale())),
                        index
                ));
                index++;
            }
            // Modifiers
            for (TraitModifier modifier : user.getTraitModifiers().values()) {
                if (!modifier.trait().equals(trait) || modifier.value() == 0.0) continue;

                // Name and description info for known static trait modifiers (e.g. from abilities)
                TraitModifiers builtIn = TraitModifiers.fromId(modifier.name());

                instances.add(new ModifierInstance(
                        trait,
                        "trait_modifier_" + modifier.name(),
                        modifier.value(),
                        modifier.operation(),
                        ModifierInstance.getFallbackItem(),
                        builtIn != null ? builtIn.getNameMessage(plugin, user.getLocale()) : modifier.name(),
                        builtIn != null ? builtIn.getDescriptionMessage(plugin, user.getLocale()) : "",
                        index
                ));
                index++;
            }
        }
        return instances;
    }

    public static void setPlaceholders(TemplateBuilder<ModifierInstance> template) {
        template.replace("display_name", t -> t.value().displayName());
        template.replace("id", t -> t.value().id());
        template.replace("description", t -> t.value().description());
        template.replace("operation", t -> t.value().operation().getDisplayName());
        template.replace("value", t -> NumberUtil.format2(t.value().value()));
        template.replace("value_format", t -> switch (t.value().operation()) {
            case ADD -> formatValue(t, "value_add");
            case MULTIPLY -> formatValue(t, "value_multiply");
            case ADD_PERCENT -> formatValue(t, "value_add_percent");
        });

    }

    private static String formatValue(TemplatePlaceholderInfo<ModifierInstance> t, String format) {
        return t.menu().getFormat(format).replace("{value}", NumberUtil.format2(t.value().value()));
    }

    public static ItemStack getFallbackItem() {
        return new ItemStack(Material.GRAY_DYE);
    }

}
