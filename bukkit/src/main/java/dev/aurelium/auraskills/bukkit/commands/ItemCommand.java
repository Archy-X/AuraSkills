package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.util.AuraSkillsModifier.Operation;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.item.SkillsItem;
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

@CommandAlias("%skills_alias")
@Subcommand("item")
public class ItemCommand extends BaseCommand {

    private final AuraSkills plugin;
    private final BaseItemCommand baseItemCommand;

    public ItemCommand(AuraSkills plugin) {
        this.plugin = plugin;
        this.baseItemCommand = new BaseItemCommand(plugin, "ITEM", ModifierType.ITEM);
    }

    @Subcommand("modifier add")
    @CommandCompletion("@stats @nothing @modifier_operations true|false false|true @players")
    @CommandPermission("auraskills.command.item.modifier")
    @Description("%desc_item_modifier_add")
    public void onItemModifierAdd(CommandIssuer issuer, Stat stat, double value, @Default("add") Operation operation, @Default("true") boolean lore,
            @Default("false") boolean overwrite, @Flags("other") @CommandPermission("auraskills.command.item.modifier.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemModifierAdd(issuer, player, stat, value, operation, lore, overwrite)
        );
    }

    @Subcommand("modifier remove")
    @CommandCompletion("@stats true|false @players")
    @CommandPermission("auraskills.command.item.modifier")
    @Description("%desc_item_modifier_remove")
    public void onItemModifierRemove(CommandIssuer issuer, Stat stat, @Default("true") boolean lore,
            @Flags("other") @CommandPermission("auraskills.command.item.modifier.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemModifierRemove(issuer, player, stat, lore)
        );
    }

    @Subcommand("modifier list")
    @CommandCompletion("@players")
    @CommandPermission("auraskills.command.item.modifier")
    @Description("%desc_item_modifier_list")
    public void onItemModifierList(CommandIssuer issuer, @Flags("other") @CommandPermission("auraskills.command.item.modifier.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemModifierList(issuer, player)
        );
    }

    @Subcommand("modifier removeall")
    @CommandCompletion("@players")
    @CommandPermission("auraskills.command.item.modifier")
    @Description("%desc_item_modifier_removeall")
    public void onItemModifierRemoveAll(CommandIssuer issuer, @Flags("other") @CommandPermission("auraskills.command.item.modifier.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemModifierRemoveAll(issuer, player)
        );
    }

    @Subcommand("trait add")
    @CommandCompletion("@traits @nothing @modifier_operations true|false false|true @players")
    @CommandPermission("auraskills.command.item.trait")
    @Description("%desc_item_trait_add")
    public void onItemTraitAdd(CommandIssuer issuer, Trait trait, double value, @Default("add") Operation operation, @Default("true") boolean lore,
            @Default("false") boolean overwrite, @Flags("other") @CommandPermission("auraskills.command.item.trait.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemTraitAdd(issuer, player, trait, value, operation, lore, overwrite)
        );
    }

    @Subcommand("trait remove")
    @CommandCompletion("@traits true|false @players")
    @CommandPermission("auraskills.command.item.trait")
    @Description("%desc_item_trait_remove")
    public void onItemTraitRemove(CommandIssuer issuer, Trait trait, @Default("true") boolean lore, @Flags("other") @CommandPermission("auraskills.command.item.trait.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemTraitRemove(issuer, player, trait, lore)
        );
    }

    @Subcommand("trait list")
    @CommandCompletion("@players")
    @CommandPermission("auraskills.command.item.trait")
    @Description("%desc_item_trait_list")
    public void onItemTraitList(CommandIssuer issuer, @Flags("other") @CommandPermission("auraskills.command.item.trait.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemTraitList(issuer, player)
        );
    }

    @Subcommand("trait removeall")
    @CommandCompletion("@players")
    @CommandPermission("auraskills.command.item.trait")
    @Description("%desc_item_trait_removeall")
    public void onItemTraitRemoveAll(CommandIssuer issuer, @Flags("other") @CommandPermission("auraskills.command.item.trait.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemTraitRemoveAll(issuer, player)
        );
    }

    @Subcommand("requirement add")
    @CommandCompletion("@skills @nothing true|false false|true @players")
    @CommandPermission("auraskills.command.item.requirement")
    @Description("%desc_item_requirement_add")
    public void onItemRequirementAdd(CommandIssuer issuer, Skill skill, int level, @Default("true") boolean lore,
            @Default("false") boolean overwrite, @Flags("other") @CommandPermission("auraskills.command.item.requirement.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemRequirementAdd(issuer, player, skill, level, lore, overwrite)
        );
    }

    @Subcommand("requirement remove")
    @CommandCompletion("@skills true|false @players")
    @CommandPermission("auraskills.command.item.requirement")
    @Description("%desc_item_requirement_remove")
    public void onItemRequirementRemove(CommandIssuer issuer, Skill skill, @Default("true") boolean lore,
            @Flags("other") @CommandPermission("auraskills.command.item.requirement.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemRequirementRemove(issuer, player, skill, lore)
        );
    }

    @Subcommand("requirement list")
    @CommandCompletion("@players")
    @CommandPermission("auraskills.command.item.requirement")
    @Description("%desc_item_requirement_list")
    public void onItemRequirementList(CommandIssuer issuer, @Flags("other") @CommandPermission("auraskills.command.item.requirement.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemRequirementList(issuer, player)
        );
    }

    @Subcommand("requirement removeall")
    @CommandCompletion("@players")
    @CommandPermission("auraskills.command.item.requirement")
    @Description("%desc_item_requirement_removeall")
    public void onItemRequirementRemoveAll(CommandIssuer issuer, @Flags("other") @CommandPermission("auraskills.command.item.requirement.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemRequirementRemoveAll(issuer, player)
        );
    }

    @Subcommand("multiplier add")
    @CommandCompletion("@skills_global @nothing true|false false|true @players")
    @CommandPermission("auraskills.command.item.multiplier")
    @Description("%desc_item_multiplier_add")
    public void onItemMultiplierAdd(CommandIssuer issuer, String target, double value, @Default("true") boolean lore, @Default("false") boolean overwrite,
            @Flags("other") @CommandPermission("auraskills.command.item.multiplier.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemMultiplierAdd(issuer, player, target, value, lore, overwrite)
        );
    }

    @Subcommand("multiplier remove")
    @CommandCompletion("@skills_global true|false @players")
    @CommandPermission("auraskills.command.item.multiplier")
    @Description("%desc_item_multiplier_remove")
    public void onItemMultiplierRemove(CommandIssuer issuer, String target, @Default("true") boolean lore, @Flags("other") @CommandPermission("auraskills.command.item.multiplier.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemMultiplierRemove(issuer, player, target, lore)
        );
    }

    @Subcommand("multiplier list")
    @CommandCompletion("@players")
    @CommandPermission("auraskills.command.item.multiplier")
    @Description("%desc_item_multiplier_list")
    public void onItemMultiplierList(CommandIssuer issuer, @Flags("other") @CommandPermission("auraskills.command.item.multiplier.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemMultiplierList(issuer, player)
        );
    }

    @Subcommand("multiplier removeall")
    @CommandCompletion("@players")
    @CommandPermission("auraskills.command.item.multiplier")
    @Description("%desc_item_multiplier_removeall")
    public void onItemMultiplierRemoveAll(CommandIssuer issuer, @Flags("other") @CommandPermission("auraskills.command.item.multiplier.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemMultiplierRemoveAll(issuer, player)
        );
    }

    @Subcommand("register")
    @CommandPermission("auraskills.command.item.register")
    @Description("%desc_item_register")
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
    @Description("%desc_item_unregister")
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
    @Description("%desc_item_give")
    public void onItemGive(CommandSender sender, @Flags("other") Player player, String key, @Default("-1") int amount) {
        ItemStack item = plugin.getItemRegistry().getItem(NamespacedId.fromDefault(key)).clone();
        Locale locale = plugin.getLocale(sender);
        if (item != null) {
            if (amount != -1) {
                item.setAmount(amount);
            }
            plugin.getScheduler().executeAtEntity(player, (task) -> {
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
            });
        } else {
            sender.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ITEM_UNREGISTER_NOT_REGISTERED, locale), "{key}", key));
        }
    }

    @Subcommand("ignore add")
    @CommandPermission("auraskills.command.item.ignore")
    @Description("%desc_item_ignore_add")
    public void onItemIgnoreAdd(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();

        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.addIgnore();
        item = skillsItem.getItem();

        player.getInventory().setItemInMainHand(item);
        player.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.ITEM_IGNORE_ADD_ADDED, locale));
    }

    @Subcommand("ignore remove")
    @CommandPermission("auraskills.command.item.ignore")
    @Description("%desc_item_ignore_remove")
    public void onItemIgnoreRemove(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();

        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.removeIgnore();
        item = skillsItem.getItem();

        player.getInventory().setItemInMainHand(item);
        player.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.ITEM_IGNORE_REMOVE_REMOVED, locale));
    }

}
