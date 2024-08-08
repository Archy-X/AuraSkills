package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Multiplier;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.item.SkillsItem;
import dev.aurelium.auraskills.bukkit.item.SkillsItem.MetaType;
import dev.aurelium.auraskills.bukkit.stat.StatFormat;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.message.type.LevelerMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.KeyIntPair;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Map;

@CommandAlias("%skills_alias")
@Subcommand("item")
public class ItemCommand extends BaseCommand {

    private final AuraSkills plugin;
    private final StatFormat format;

    public ItemCommand(AuraSkills plugin) {
        this.plugin = plugin;
        this.format = new StatFormat(plugin);
    }

    @Subcommand("modifier add")
    @CommandCompletion("@stats @nothing false|true")
    @CommandPermission("auraskills.command.item.modifier")
    @Description("Adds an item stat modifier to the item held, along with lore by default.")
    public void onItemModifierAdd(@Flags("itemheld") Player player, Stat stat, double value, @Default("true") boolean lore) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);

        for (StatModifier statModifier : skillsItem.getStatModifiers(ModifierType.ITEM)) {
            if (statModifier.stat() == stat) {
                player.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.ITEM_MODIFIER_ADD_ALREADY_EXISTS, locale), stat, locale));
                return;
            }
        }
        if (lore) {
            skillsItem.addModifierLore(ModifierType.ITEM, stat, value, locale);
        }
        skillsItem.addModifier(MetaType.MODIFIER, ModifierType.ITEM, stat, value);
        ItemStack newItem = skillsItem.getItem();
        player.getInventory().setItemInMainHand(newItem);
        player.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.ITEM_MODIFIER_ADD_ADDED, locale), stat, value, locale));
    }

    @Subcommand("modifier remove")
    @CommandCompletion("@stats false|true")
    @CommandPermission("auraskills.command.item.modifier")
    @Description("Removes an item stat modifier from the item held, and the lore associated with it by default.")
    public void onItemModifierRemove(@Flags("itemheld") Player player, Stat stat, @Default("true") boolean lore) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        boolean removed = false;
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        for (StatModifier modifier : skillsItem.getStatModifiers(ModifierType.ITEM)) {
            if (modifier.stat() == stat) {
                skillsItem.removeModifier(MetaType.MODIFIER, ModifierType.ITEM, stat);
                removed = true;
                break;
            }
        }
        if (lore) {
            skillsItem.removeModifierLore(stat, locale);
        }
        item = skillsItem.getItem();
        player.getInventory().setItemInMainHand(item);
        if (removed) {
            player.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.ITEM_MODIFIER_REMOVE_REMOVED, locale), stat, locale));
        } else {
            player.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.ITEM_MODIFIER_REMOVE_DOES_NOT_EXIST, locale), stat, locale));
        }
    }

    @Subcommand("modifier list")
    @CommandPermission("auraskills.command.item.modifier")
    @Description("Lists all item stat modifiers on the item held.")
    public void onItemModifierList(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        StringBuilder message = new StringBuilder(plugin.getMsg(CommandMessage.ITEM_MODIFIER_LIST_HEADER, locale));
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        for (StatModifier modifier : skillsItem.getStatModifiers(ModifierType.ITEM)) {
            message.append("\n").append(format.applyPlaceholders(plugin.getMsg(CommandMessage.ITEM_MODIFIER_LIST_ENTRY, locale), modifier, locale));
        }
        player.sendMessage(message.toString());
    }

    @Subcommand("modifier removeall")
    @CommandPermission("auraskills.command.item.modifier")
    @Description("Removes all item stat modifiers from the item held.")
    public void onItemModifierRemoveAll(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();

        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);

        skillsItem.removeAll(MetaType.MODIFIER, ModifierType.ITEM);
        item = skillsItem.getItem();

        player.getInventory().setItemInMainHand(item);
        player.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.ITEM_MODIFIER_REMOVEALL_REMOVED, locale));
    }

    @Subcommand("trait add")
    @CommandCompletion("@traits @nothing false|true")
    @CommandPermission("auraskills.command.item.modifier")
    @Description("Adds an item trait modifier to the item held, along with lore by default.")
    public void onItemTraitAdd(@Flags("itemheld") Player player, Trait trait, double value, @Default("true") boolean lore) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);

        for (TraitModifier modifier : skillsItem.getTraitModifiers(ModifierType.ITEM)) {
            if (modifier.trait().equals(trait)) {
                player.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.ITEM_TRAIT_ADD_ALREADY_EXISTS, locale), trait, locale));
                return;
            }
        }
        if (lore) {
            skillsItem.addModifierLore(ModifierType.ITEM, trait, value, locale);
        }
        skillsItem.addModifier(MetaType.TRAIT_MODIFIER, ModifierType.ITEM, trait, value);
        ItemStack newItem = skillsItem.getItem();
        player.getInventory().setItemInMainHand(newItem);
        player.sendMessage(plugin.getPrefix(locale) +
                format.applyPlaceholders(plugin.getMsg(CommandMessage.ITEM_MODIFIER_ADD_ADDED, locale), trait, value, locale));
    }

    @Subcommand("trait remove")
    @CommandCompletion("@traits")
    @CommandPermission("auraskills.command.item.modifier")
    @Description("Removes an item trait modifier from the item held.")
    public void onItemTraitRemove(@Flags("itemheld") Player player, Trait trait) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        boolean removed = false;
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        for (TraitModifier modifier : skillsItem.getTraitModifiers(ModifierType.ITEM)) {
            if (modifier.trait().equals(trait)) {
                skillsItem.removeModifier(MetaType.TRAIT_MODIFIER, ModifierType.ITEM, trait);
                removed = true;
                break;
            }
        }
        // Lore removal not implemented yet
        item = skillsItem.getItem();
        player.getInventory().setItemInMainHand(item);
        if (removed) {
            player.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.ITEM_MODIFIER_REMOVE_REMOVED, locale), trait, locale));
        } else {
            player.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.ITEM_MODIFIER_REMOVE_DOES_NOT_EXIST, locale), trait, locale));
        }
    }

    @Subcommand("trait list")
    @CommandPermission("auraskills.command.item.modifier")
    @Description("Lists all item trait modifiers on the item held.")
    public void onItemTraitList(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        StringBuilder message = new StringBuilder(plugin.getMsg(CommandMessage.ITEM_MODIFIER_LIST_HEADER, locale));
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        for (TraitModifier modifier : skillsItem.getTraitModifiers(ModifierType.ITEM)) {
            message.append("\n").append(format.applyPlaceholders(plugin.getMsg(CommandMessage.ITEM_MODIFIER_LIST_ENTRY, locale), modifier, locale));
        }
        player.sendMessage(message.toString());
    }

    @Subcommand("trait removeall")
    @CommandPermission("auraskills.command.item.modifier")
    @Description("Removes all item trait modifiers from the item held.")
    public void onItemTraitRemoveAll(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();

        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);

        skillsItem.removeAll(MetaType.TRAIT_MODIFIER, ModifierType.ITEM);
        item = skillsItem.getItem();

        player.getInventory().setItemInMainHand(item);
        player.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.ITEM_MODIFIER_REMOVEALL_REMOVED, locale));
    }

    @Subcommand("requirement add")
    @CommandPermission("auraskills.command.item.requirement")
    @CommandCompletion("@skills @nothing false|true")
    @Description("Adds an item requirement to the item held, along with lore by default.")
    public void onItemRequirementAdd(@Flags("itemheld") Player player, Skill skill, int level, @Default("true") boolean lore) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();

        SkillsItem skillsItem = new SkillsItem(item, plugin);
        if (skillsItem.getRequirements(ModifierType.ITEM).containsKey(skill)) {
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ITEM_REQUIREMENT_ADD_ALREADY_EXISTS, locale), "{skill}", skill.getDisplayName(locale)));
            return;
        }
        skillsItem.addRequirement(ModifierType.ITEM, skill, level);
        if (lore) {
            skillsItem.addRequirementLore(ModifierType.ITEM, skill, level, locale);
        }
        item = skillsItem.getItem();

        player.getInventory().setItemInMainHand(item);
        player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ITEM_REQUIREMENT_ADD_ADDED, locale),
                "{skill}", skill.getDisplayName(locale),
                "{level}", String.valueOf(level)));
    }

    @Subcommand("requirement remove")
    @CommandPermission("auraskills.command.item.requirement")
    @CommandCompletion("@skills false|true")
    @Description("Removes an item requirement from the item held, and the lore associated with it by default.")
    public void onItemRequirementRemove(@Flags("itemheld") Player player, Skill skill, @Default("true") boolean lore) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();

        SkillsItem skillsItem = new SkillsItem(item, plugin);
        if (skillsItem.getRequirements(ModifierType.ITEM).containsKey(skill)) {
            skillsItem.removeRequirement(ModifierType.ITEM, skill);
            if (lore) {
                skillsItem.removeRequirementLore(skill);
            }
            item = skillsItem.getItem();

            player.getInventory().setItemInMainHand(item);
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ITEM_REQUIREMENT_REMOVE_REMOVED, locale),
                    "{skill}", skill.getDisplayName(locale)));
        }
        else {
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ITEM_REQUIREMENT_REMOVE_DOES_NOT_EXIST, locale),
                    "{skill}", skill.getDisplayName(locale)));
        }
    }

    @Subcommand("requirement list")
    @CommandPermission("auraskills.command.item.requirement")
    @Description("Lists the item requirements on the item held.")
    public void onItemRequirementList(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();
        player.sendMessage(plugin.getMsg(CommandMessage.ITEM_REQUIREMENT_LIST_HEADER, locale));

        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        for (Map.Entry<Skill, Integer> entry : skillsItem.getRequirements(ModifierType.ITEM).entrySet()) {
            player.sendMessage(TextUtil.replace(plugin.getMsg(CommandMessage.ITEM_REQUIREMENT_LIST_ENTRY, locale),
                    "{skill}", entry.getKey().getDisplayName(locale),
                    "{level}", String.valueOf(entry.getValue())));
        }
    }

    @Subcommand("requirement removeall")
    @CommandPermission("auraskills.command.item.requirement")
    @Description("Removes all item requirements from the item held.")
    public void onItemRequirementRemoveAll(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();

        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.removeAll(SkillsItem.MetaType.REQUIREMENT, ModifierType.ITEM);
        item = skillsItem.getItem();

        player.getInventory().setItemInMainHand(item);
        player.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.ITEM_REQUIREMENT_REMOVEALL_REMOVED, locale));
    }

    @Subcommand("register")
    @CommandPermission("auraskills.command.item.register")
    public void onItemRegister(@Flags("itemheld") Player player, String key) {
        Locale locale = plugin.getUser(player).getLocale();
        if (key.contains(" ")) { // Disallow spaces in key name
            player.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.ITEM_REGISTER_NO_SPACES, locale));
            return;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (plugin.getItemRegistry().getItem(NamespacedId.fromDefault(key)) == null) { // Check that no item has been registered on the key
            plugin.getItemRegistry().register(NamespacedId.fromDefault(key), item);
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ITEM_REGISTER_REGISTERED, locale), "{key}", key));
        } else {
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ITEM_REGISTER_ALREADY_REGISTERED, locale), "{key}", key));
        }
    }

    @Subcommand("unregister")
    @CommandPermission("auraskills.command.item.register")
    @CommandCompletion("@item_keys")
    public void onItemUnregister(Player player, String key) {
        Locale locale = plugin.getUser(player).getLocale();
        if (plugin.getItemRegistry().getItem(NamespacedId.fromDefault(key)) != null) { // Check that there is an item registered on the key
            plugin.getItemRegistry().unregister(NamespacedId.fromDefault(key));
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ITEM_UNREGISTER_UNREGISTERED, locale), "{key}", key));
        } else {
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ITEM_UNREGISTER_NOT_REGISTERED, locale), "{key}", key));
        }
    }

    @Subcommand("give")
    @CommandPermission("auraskills.command.item.give")
    @CommandCompletion("@players @item_keys")
    public void onItemGive(CommandSender sender, @Flags("other") Player player, String key, @Default("-1") int amount) {
        ItemStack item = plugin.getItemRegistry().getItem(NamespacedId.fromDefault(key));
        Locale locale = plugin.getLocale(sender);
        if (item != null) {
            if (amount != -1) {
                item.setAmount(amount);
            }
            ItemStack leftoverItem = ItemUtils.addItemToInventory(player, item);

            String senderMsg = TextUtil.replace(plugin.getMsg(CommandMessage.ITEM_GIVE_SENDER, locale),
                    "{amount}", String.valueOf(item.getAmount()), "{key}", key, "{player}", player.getName());
            if (!senderMsg.isEmpty()) {
                sender.sendMessage(plugin.getPrefix(locale) + senderMsg);
            }

            if (!sender.equals(player)) {
                String message = TextUtil.replace(plugin.getMsg(CommandMessage.ITEM_GIVE_RECEIVER, locale),
                        "{amount}", String.valueOf(item.getAmount()), "{key}", key);
                if (!message.isEmpty()) {
                    player.sendMessage(plugin.getPrefix(locale) + message);
                }
            }
            // Add to unclaimed items if leftover
            if (leftoverItem != null) {
                User user = plugin.getUser(player);
                user.getUnclaimedItems().add(new KeyIntPair(key, leftoverItem.getAmount()));

                String message = plugin.getMsg(LevelerMessage.UNCLAIMED_ITEM, locale);
                if (!message.isEmpty()) {
                    player.sendMessage(plugin.getPrefix(locale) + message);
                }
            }
        } else {
            sender.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ITEM_UNREGISTER_NOT_REGISTERED, locale), "{key}", key));
        }
    }

    @Subcommand("multiplier add")
    @CommandCompletion("@skills_global @nothing true|false")
    @CommandPermission("auraskills.command.item.multiplier")
    @Description("Adds an item multiplier to the held item to global or a specific skill where value is the percent more XP gained.")
    public void onItemMultiplierAdd(@Flags("itemheld") Player player, String target, double value, @Default("true") boolean lore) {
        ItemStack item = player.getInventory().getItemInMainHand();
        Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(target));
        Locale locale = plugin.getUser(player).getLocale();

        SkillsItem skillsItem = new SkillsItem(item, plugin);
        if (skill != null) { // Add multiplier for specific skill
            for (Multiplier multiplier : skillsItem.getMultipliers(ModifierType.ITEM)) {
                if (multiplier.skill() == skill) {
                    player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ITEM_MULTIPLIER_ADD_ALREADY_EXISTS, locale),
                            "{target}", skill.getDisplayName(locale)));
                    return;
                }
            }
            if (lore) {
                skillsItem.addMultiplierLore(ModifierType.ITEM, skill, value, locale);
            }
            skillsItem.addMultiplier(ModifierType.ITEM, skill, value);
            ItemStack newItem = skillsItem.getItem();
            player.getInventory().setItemInMainHand(newItem);
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ITEM_MULTIPLIER_ADD_ADDED, locale),
                    "{target}", skill.getDisplayName(locale), "{value}", String.valueOf(value)));
        } else if (target.equalsIgnoreCase("global")) { // Add multiplier for all skills
            String global = plugin.getMsg(CommandMessage.MULTIPLIER_GLOBAL, locale);
            for (Multiplier multiplier : skillsItem.getMultipliers(ModifierType.ITEM)) {
                if (multiplier.skill() == null) {
                    player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ITEM_MULTIPLIER_ADD_ALREADY_EXISTS, locale),
                            "{target}", global));
                    return;
                }
            }
            if (lore) {
                skillsItem.addMultiplierLore(ModifierType.ITEM, null, value, locale);
            }
            skillsItem.addMultiplier(ModifierType.ITEM, null, value);
            ItemStack newItem = skillsItem.getItem();
            player.getInventory().setItemInMainHand(newItem);
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ITEM_MULTIPLIER_ADD_ADDED, locale),
                    "{target}", global, "{value}", String.valueOf(value)));
        } else {
            throw new InvalidCommandArgument("Target must be valid skill name or global");
        }
    }

    @Subcommand("multiplier remove")
    @CommandCompletion("@skills_global")
    @CommandPermission("auraskills.command.item.multiplier")
    @Description("Removes an item multiplier of a the specified skill or global from the held item.")
    public void onItemMultiplierRemove(@Flags("itemheld") Player player, String target) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(target));
        boolean removed = false;

        SkillsItem skillsItem = new SkillsItem(item, plugin);
        for (Multiplier multiplier : skillsItem.getMultipliers(ModifierType.ITEM)) {
            if (multiplier.skill() == skill) {
                skillsItem.removeMultiplier(ModifierType.ITEM, skill);
                removed = true;
                break;
            }
        }
        item = skillsItem.getItem();
        player.getInventory().setItemInMainHand(item);
        // Use skill display name if skill is not null, otherwise use global name
        String targetName;
        if (skill != null) {
            targetName = skill.getDisplayName(locale);
        } else if (target.equalsIgnoreCase("global")) {
            targetName = plugin.getMsg(CommandMessage.MULTIPLIER_GLOBAL, locale);
        } else {
            throw new InvalidCommandArgument("Target must be valid skill name or global");
        }
        if (removed) {
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ITEM_MULTIPLIER_REMOVE_REMOVED, locale),
                    "{target}", targetName));
        } else {
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ITEM_MULTIPLIER_REMOVE_DOES_NOT_EXIST, locale),
                    "{target}", targetName));
        }
    }

    @Subcommand("multiplier list")
    @CommandPermission("auraskills.command.item.multiplier")
    @Description("Lists all item multipliers on the held item.")
    public void onItemMultiplierList(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        StringBuilder message = new StringBuilder(plugin.getMsg(CommandMessage.ITEM_MULTIPLIER_LIST_HEADER, locale));
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        for (Multiplier multiplier : skillsItem.getMultipliers(ModifierType.ITEM)) {
            String targetName;
            if (multiplier.skill() != null) {
                targetName = multiplier.skill().getDisplayName(locale);
            } else {
                targetName = plugin.getMsg(CommandMessage.MULTIPLIER_GLOBAL, locale);
            }
            message.append("\n").append(TextUtil.replace(plugin.getMsg(CommandMessage.ITEM_MULTIPLIER_LIST_ENTRY, locale),
                    "{target}", targetName, "{value}", String.valueOf(multiplier.value())));
        }
        player.sendMessage(message.toString());
    }

    @Subcommand("multiplier removeall")
    @CommandPermission("auraskills.command.item.multiplier")
    @Description("Removes all item multipliers from the item held.")
    public void onItemMultiplierRemoveAll(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();

        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.removeAll(SkillsItem.MetaType.MULTIPLIER, ModifierType.ITEM);
        item = skillsItem.getItem();

        player.getInventory().setItemInMainHand(item);
        player.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.ITEM_MULTIPLIER_REMOVEALL_REMOVED, locale));
    }

    @Subcommand("ignore add")
    @CommandPermission("auraskills.command.item.ignore")
    @Description("Adds the tag that ignores the held item from mana ability interactions.")
    public void onItemIgnoreAdd(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();

        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.addIgnore();
        item = skillsItem.getItem();

        player.getInventory().setItemInMainHand(item);
        player.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.ITEM_IGNORE_ADD, locale));
    }

    @Subcommand("ignore remove")
    @CommandPermission("auraskills.command.item.ignore")
    @Description("Removes the tag that ignores the held item from mana ability interactions.")
    public void onItemIgnoreRemove(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();

        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.removeIgnore();
        item = skillsItem.getItem();

        player.getInventory().setItemInMainHand(item);
        player.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.ITEM_IGNORE_REMOVE, locale));
    }

}
