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
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.util.AuraSkillsModifier.Operation;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.entity.Player;

@CommandAlias("%skills_alias")
@Subcommand("armor")
public class ArmorCommand extends BaseCommand {

    private final BaseItemCommand baseItemCommand;

    public ArmorCommand(AuraSkills plugin) {
        this.baseItemCommand = new BaseItemCommand(plugin, "ARMOR", ModifierType.ARMOR);
    }

    @Subcommand("modifier add")
    @CommandCompletion("@stats @nothing @modifier_operations true|false false|true @players")
    @CommandPermission("auraskills.command.armor.modifier")
    @Description("%desc_armor_modifier_add")
    public void onItemModifierAdd(CommandIssuer issuer, Stat stat, double value, @Default("add") Operation operation, @Default("true") boolean lore,
            @Default("false") boolean overwrite, @Flags("other") @CommandPermission("auraskills.command.armor.modifier.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemModifierAdd(issuer, player, stat, value, operation, lore, overwrite)
        );
    }

    @Subcommand("modifier remove")
    @CommandCompletion("@stats true|false @players")
    @CommandPermission("auraskills.command.armor.modifier")
    @Description("%desc_armor_modifier_remove")
    public void onItemModifierRemove(CommandIssuer issuer, Stat stat, @Default("true") boolean lore,
            @Flags("other") @CommandPermission("auraskills.command.armor.modifier.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemModifierRemove(issuer, player, stat, lore)
        );
    }

    @Subcommand("modifier list")
    @CommandCompletion("@players")
    @CommandPermission("auraskills.command.armor.modifier")
    @Description("%desc_armor_modifier_list")
    public void onItemModifierList(CommandIssuer issuer, @Flags("other") @CommandPermission("auraskills.command.armor.modifier.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemModifierList(issuer, player)
        );
    }

    @Subcommand("modifier removeall")
    @CommandCompletion("@players")
    @CommandPermission("auraskills.command.armor.modifier")
    @Description("%desc_armor_modifier_removeall")
    public void onItemModifierRemoveAll(CommandIssuer issuer, @Flags("other") @CommandPermission("auraskills.command.armor.modifier.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemModifierRemoveAll(issuer, player)
        );
    }

    @Subcommand("trait add")
    @CommandCompletion("@traits @nothing @modifier_operations true|false false|true @players")
    @CommandPermission("auraskills.command.armor.trait")
    @Description("%desc_armor_trait_add")
    public void onItemTraitAdd(CommandIssuer issuer, Trait trait, double value, @Default("add") Operation operation, @Default("true") boolean lore,
            @Default("false") boolean overwrite, @Flags("other") @CommandPermission("auraskills.command.armor.trait.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemTraitAdd(issuer, player, trait, value, operation, lore, overwrite)
        );
    }

    @Subcommand("trait remove")
    @CommandCompletion("@traits true|false @players")
    @CommandPermission("auraskills.command.armor.trait")
    @Description("%desc_armor_trait_remove")
    public void onItemTraitRemove(CommandIssuer issuer, Trait trait, @Default("true") boolean lore, @Flags("other") @CommandPermission("auraskills.command.armor.trait.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemTraitRemove(issuer, player, trait, lore)
        );
    }

    @Subcommand("trait list")
    @CommandCompletion("@players")
    @CommandPermission("auraskills.command.armor.trait")
    @Description("%desc_armor_trait_list")
    public void onItemTraitList(CommandIssuer issuer, @Flags("other") @CommandPermission("auraskills.command.armor.trait.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemTraitList(issuer, player)
        );
    }

    @Subcommand("trait removeall")
    @CommandCompletion("@players")
    @CommandPermission("auraskills.command.armor.trait")
    @Description("%desc_armor_trait_removeall")
    public void onItemTraitRemoveAll(CommandIssuer issuer, @Flags("other") @CommandPermission("auraskills.command.armor.trait.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemTraitRemoveAll(issuer, player)
        );
    }

    @Subcommand("requirement add")
    @CommandCompletion("@skills @nothing true|false false|true @players")
    @CommandPermission("auraskills.command.armor.requirement")
    @Description("%desc_armor_requirement_add")
    public void onItemRequirementAdd(CommandIssuer issuer, Skill skill, int level, @Default("true") boolean lore,
            @Default("false") boolean overwrite, @Flags("other") @CommandPermission("auraskills.command.armor.requirement.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemRequirementAdd(issuer, player, skill, level, lore, overwrite)
        );
    }

    @Subcommand("requirement remove")
    @CommandCompletion("@skills true|false @players")
    @CommandPermission("auraskills.command.armor.requirement")
    @Description("%desc_armor_requirement_remove")
    public void onItemRequirementRemove(CommandIssuer issuer, Skill skill, @Default("true") boolean lore,
            @Flags("other") @CommandPermission("auraskills.command.armor.requirement.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemRequirementRemove(issuer, player, skill, lore)
        );
    }

    @Subcommand("requirement list")
    @CommandCompletion("@players")
    @CommandPermission("auraskills.command.armor.requirement")
    @Description("%desc_armor_requirement_list")
    public void onItemRequirementList(CommandIssuer issuer, @Flags("other") @CommandPermission("auraskills.command.armor.requirement.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemRequirementList(issuer, player)
        );
    }

    @Subcommand("requirement removeall")
    @CommandCompletion("@players")
    @CommandPermission("auraskills.command.armor.requirement")
    @Description("%desc_armor_requirement_removeall")
    public void onItemRequirementRemoveAll(CommandIssuer issuer, @Flags("other") @CommandPermission("auraskills.command.armor.requirement.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemRequirementRemoveAll(issuer, player)
        );
    }

    @Subcommand("multiplier add")
    @CommandCompletion("@skills_global @nothing true|false false|true @players")
    @CommandPermission("auraskills.command.armor.multiplier")
    @Description("%desc_armor_multiplier_add")
    public void onItemMultiplierAdd(CommandIssuer issuer, String target, double value, @Default("true") boolean lore, @Default("false") boolean overwrite,
            @Flags("other") @CommandPermission("auraskills.command.armor.multiplier.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemMultiplierAdd(issuer, player, target, value, lore, overwrite)
        );
    }

    @Subcommand("multiplier remove")
    @CommandCompletion("@skills_global true|false @players")
    @CommandPermission("auraskills.command.armor.multiplier")
    @Description("%desc_armor_multiplier_remove")
    public void onItemMultiplierRemove(CommandIssuer issuer, String target, @Default("true") boolean lore, @Flags("other") @CommandPermission("auraskills.command.armor.multiplier.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemMultiplierRemove(issuer, player, target, lore)
        );
    }

    @Subcommand("multiplier list")
    @CommandCompletion("@players")
    @CommandPermission("auraskills.command.armor.multiplier")
    @Description("%desc_armor_multiplier_list")
    public void onItemMultiplierList(CommandIssuer issuer, @Flags("other") @CommandPermission("auraskills.command.armor.multiplier.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemMultiplierList(issuer, player)
        );
    }

    @Subcommand("multiplier removeall")
    @CommandCompletion("@players")
    @CommandPermission("auraskills.command.armor.multiplier")
    @Description("%desc_armor_multiplier_removeall")
    public void onItemMultiplierRemoveAll(CommandIssuer issuer, @Flags("other") @CommandPermission("auraskills.command.armor.multiplier.other") @Optional Player other) {
        baseItemCommand.handlePlayerTarget(issuer, other, (player, locale) ->
                baseItemCommand.onItemMultiplierRemoveAll(issuer, player)
        );
    }

}
