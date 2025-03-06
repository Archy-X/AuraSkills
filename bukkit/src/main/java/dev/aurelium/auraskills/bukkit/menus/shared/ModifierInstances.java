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
import dev.aurelium.slate.info.TemplateInfo;
import dev.aurelium.slate.info.TemplatePlaceholderInfo;
import dev.aurelium.slate.menu.ActiveMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.Map.Entry;

public class ModifierInstances {

    private final AuraSkills plugin;

    public ModifierInstances(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public Map<String, ModifierInstance> getInstances(User user, NamespaceIdentified parent) {
        return getInstances(user, parent, false);
    }

    public Map<String, ModifierInstance> getInstances(User user, NamespaceIdentified parent, boolean excludeLinkedStats) {
        if (parent instanceof Stat stat) {
            return getStatInstances(user, stat);
        } else if (parent instanceof Trait trait) {
            return getTraitInstances(user, excludeLinkedStats, trait);
        }
        return new HashMap<>();
    }

    public Map<String, ModifierInstance> sortAndReindex(Map<String, ModifierInstance> instances) {
        List<ModifierInstance> sortedList = new ArrayList<>(instances.values());

        // Sort the list:
        // 1. First by the custom order for Operation: ADD, ADD_PERCENT, MULTIPLY.
        // 2. Then by descending value within the same operation.
        sortedList.sort(Comparator
                .comparingInt((ModifierInstance m) -> {
                    // Map each Operation to an int representing its priority. Lower numbers come first.
                    return switch (m.operation()) {
                        case ADD -> 0;
                        case ADD_PERCENT -> 1;
                        case MULTIPLY -> 2;
                    };
                })
                .thenComparing(Comparator.comparingDouble(ModifierInstance::value).reversed())
        );

        // Reindex the sorted ModifierInstance objects
        Map<String, ModifierInstance> reindexedList = new HashMap<>();
        for (int i = 0; i < sortedList.size(); i++) {
            // Use withIndex to create a new instance with the new index
            ModifierInstance indexed = sortedList.get(i).withIndex(i);
            reindexedList.put(indexed.id(), indexed);
        }

        return reindexedList;
    }

    private Map<String, ModifierInstance> getTraitInstances(User user, boolean excludeLinkedStats, Trait trait) {
        Map<String, ModifierInstance> instances = new HashMap<>();
        int index = 0;
        // Base value if it exists
        double base = plugin.getTraitManager().getBaseLevel(user, trait);
        if (base >= 0.00001) {
            String id = "base_level_" + trait.getId().getSimpleName();
            instances.put(id, new ModifierInstance(trait, id, base, Operation.ADD,
                    getFallbackItem(),
                    plugin.getMsg(MenuMessage.BASE_LEVEL, user.getLocale()),
                    plugin.getMsg(MenuMessage.BASE_LEVEL_DESC, user.getLocale()),
                    index
            ));
            index++;
        }
        // Linked stats
        if (!excludeLinkedStats) {
            for (Stat stat : plugin.getTraitManager().getLinkedStats(trait)) {
                double modifier = stat.getTraitModifier(trait);
                double level = user.getStatLevel(stat);
                if (level == 0.0) continue;
                String id = "linked_stat_" + stat.getId().getSimpleName();
                instances.put(id, new ModifierInstance(trait, id, level * modifier, Operation.ADD,
                        getFallbackItem(),
                        stat.getDisplayName(user.getLocale()),
                        plugin.getMsg(MenuMessage.LINKED_STAT_DESC, user.getLocale()).replace("{stat}",
                                stat.getDisplayName(user.getLocale())),
                        index
                ));
                index++;
            }
        }
        // Modifiers
        for (TraitModifier modifier : user.getTraitModifiers().values()) {
            if (!modifier.trait().equals(trait) || modifier.value() == 0.0) continue;

            // Name and description info for known static trait modifiers (e.g. from abilities)
            TraitModifiers builtIn = TraitModifiers.fromId(modifier.name());

            String id = "trait_modifier_" + modifier.name();
            instances.put(id, new ModifierInstance(trait, id, modifier.value(), modifier.operation(),
                    getFallbackItem(),
                    builtIn != null ? builtIn.getNameMessage(plugin, user.getLocale()) : modifier.name(),
                    builtIn != null ? builtIn.getDescriptionMessage(plugin, user.getLocale()) : "",
                    index
            ));
            index++;
        }
        return instances;
    }

    private Map<String, ModifierInstance> getStatInstances(User user, Stat stat) {
        Map<String, ModifierInstance> instances = new HashMap<>();
        int index = 0;
        // Add instances for each skill that gives the stat as a reward
        for (Entry<Skill, Double> entry : user.getUserStats().getLevelRewardedBySkill(stat).entrySet()) {
            if (entry.getValue() == 0.0) continue;
            String id = "stat_reward_" + entry.getKey().getId().getSimpleName();
            instances.put(id, new ModifierInstance(
                    stat, id, entry.getValue(), Operation.ADD,
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
            String id = "stat_modifier_" + modifier.name();
            instances.put(id, new ModifierInstance(stat, id, modifier.value(), modifier.operation(),
                    getFallbackItem(),
                    modifier.name(),
                    "",
                    index
            ));
            index++;
        }
        return instances;
    }

    public void setPlaceholders(TemplateBuilder<String> template) {
        template.replace("display_name", t -> instance(t.menu(), t.value()).displayName());
        template.replace("id", t -> instance(t.menu(), t.value()).id());
        template.replace("description", t -> instance(t.menu(), t.value()).description());
        template.replace("operation", t -> instance(t.menu(), t.value()).operation().getDisplayName());
        template.replace("value", t -> NumberUtil.format2(instance(t.menu(), t.value()).value()));
        template.replace("value_format", t -> switch (instance(t.menu(), t.value()).operation()) {
            case ADD -> formatValue(t, "value_add");
            case MULTIPLY -> formatValue(t, "value_multiply");
            case ADD_PERCENT -> formatValue(t, "value_add_percent");
        });

    }

    public ModifierInstance instance(ActiveMenu menu, String id) {
        Map<String, ModifierInstance> map = menu.property("modifiers");
        return map.get(id);
    }

    public ItemStack modifyItem(TemplateInfo<String> t) {
        if (t.item().equals(ModifierInstances.getFallbackItem())) {
            ItemStack item = instance(t.menu(), t.value()).item();
            return Objects.requireNonNullElseGet(item, ModifierInstances::getFallbackItem);
        } else {
            return t.item();
        }
    }

    private String formatValue(TemplatePlaceholderInfo<String> t, String format) {
        return t.menu().getFormat(format).replace("{value}",
                NumberUtil.format2(instance(t.menu(), t.value()).value()));
    }

    public static ItemStack getFallbackItem() {
        return new ItemStack(Material.GRAY_DYE);
    }

}
