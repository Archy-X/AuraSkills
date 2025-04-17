package dev.aurelium.auraskills.bukkit.menus.shared;

import dev.aurelium.auraskills.api.registry.NamespaceIdentified;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.api.util.AuraSkillsModifier;
import dev.aurelium.auraskills.api.util.AuraSkillsModifier.Operation;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.item.BuiltInModifier;
import dev.aurelium.auraskills.bukkit.item.StatModifiers;
import dev.aurelium.auraskills.bukkit.item.TraitModifiers;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.bukkit.util.VersionUtils;
import dev.aurelium.auraskills.common.message.MessageKey;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;

public class ModifierInstances {

    private static final String STAT_PREFIX = "stat_modifier_";
    private static final String TRAIT_PREFIX = "trait_modifier_";

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

            String id = builtIn != null ? "ability_" + builtIn.getMessageName() : TRAIT_PREFIX + modifier.name();
            instances.put(id, createInstance(trait, id, modifier, builtIn, user, index));
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
            if (!modifier.stat().equals(stat) || modifier.value() == 0.0) continue;

            // Name and description info for known static trait modifiers (e.g. from abilities)
            StatModifiers builtIn = StatModifiers.fromId(modifier.name());

            String id = builtIn != null ? "ability_" + builtIn.getMessageName() : STAT_PREFIX + modifier.name();
            instances.put(id, createInstance(stat, id, modifier, builtIn, user, index));
            index++;
        }
        return instances;
    }

    private ModifierInstance createInstance(NamespaceIdentified identified, String id, AuraSkillsModifier<?> modifier, BuiltInModifier builtIn, User user, int index) {
        @Nullable ItemModifierData itemData = getItemModifierData(user, id);

        String displayName;
        if (builtIn != null) {
            displayName = builtIn.getDisplayName(plugin, user.getLocale());
        } else if (itemData != null) {
            displayName = itemData.displayName();
        } else {
            displayName = modifier.name();
        }

        String desc;
        if (builtIn != null) {
            desc = builtIn.getDescriptionMessage(plugin, user);
        } else if (itemData != null) {
            desc = itemData.description();
        } else {
            desc = getFallbackDescription(id, user.getLocale());
        }

        return new ModifierInstance(
                identified,
                id,
                modifier.value(),
                modifier.operation(),
                itemData != null ? itemData.item() : getFallbackItem(),
                displayName,
                desc,
                index
        );
    }

    @Nullable
    private ItemModifierData getItemModifierData(User user, String id) {
        Player player = ((BukkitUser) user).getPlayer();
        if (player == null) {
            return null;
        }
        MenuMessage descKey;
        @Nullable ItemStack item;

        if (id.startsWith(STAT_PREFIX + StatModifier.ITEM_PREFIX + "Item.") || id.startsWith(TRAIT_PREFIX + TraitModifier.ITEM_PREFIX + "Item.")) {
            if (id.endsWith(".Offhand")) {
                descKey = MenuMessage.ITEM_OFF_HAND_DESC;
                item = player.getInventory().getItemInOffHand();
            } else {
                descKey = MenuMessage.ITEM_HAND_DESC;
                item = player.getInventory().getItemInMainHand();
            }
        } else if (id.startsWith(STAT_PREFIX + StatModifier.ITEM_PREFIX + "Armor.") || id.startsWith(TRAIT_PREFIX + TraitModifier.ITEM_PREFIX + "Armor.")) {
            String withoutPrefix = TextUtil.replace(id,
                    STAT_PREFIX, "",
                    TRAIT_PREFIX, "",
                    StatModifier.ITEM_PREFIX, "",
                    TraitModifier.ITEM_PREFIX, "",
                    "Armor.", "");
            String slotName = withoutPrefix.split("\\.", 2)[0];
            try {
                descKey = MenuMessage.valueOf(slotName.toUpperCase(Locale.ROOT) + "_DESC");
                item = switch (slotName.toLowerCase(Locale.ROOT)) {
                    case "helmet" -> player.getInventory().getHelmet();
                    case "chestplate" -> player.getInventory().getChestplate();
                    case "leggings" -> player.getInventory().getLeggings();
                    case "boots" -> player.getInventory().getBoots();
                    default -> null;
                };
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        } else {
            return null;
        }

        if (item == null) {
            item = getFallbackItem();
        }
        item = item.clone();
        // Hide attributes and enchants and clear lore
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (VersionUtils.isAtLeastVersion(20, 5)) {
                meta.setAttributeModifiers(Material.IRON_SWORD.getDefaultAttributeModifiers(EquipmentSlot.HAND));
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setLore(null); // Clear existing lore
            item.setItemMeta(meta);
        }

        return new ItemModifierData(getDisplayName(item), plugin.getMsg(descKey, user.getLocale()), item);
    }

    private String getDisplayName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return meta == null || !meta.hasDisplayName() ? "!!REMOVE!!" : meta.getDisplayName();
    }

    record ItemModifierData(String displayName, String description, ItemStack item) {

    }

    private String getFallbackDescription(String modifierName, Locale locale) {
        String path = "menus.stat_info.modifiers." + modifierName;
        String customMsg = plugin.getMsg(MessageKey.of(path), locale);
        if (!customMsg.equals(path)) {
            return customMsg;
        }
        // Fallback default description
        return plugin.getMsg(MenuMessage.CUSTOM_MODIFIER_DESC, locale);
    }

    public static ItemStack getFallbackItem() {
        return new ItemStack(Material.GRAY_DYE);
    }

}
