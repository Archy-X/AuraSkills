package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("%skills_alias")
public class SkillsRootCommand extends BaseCommand {
 
	private final AuraSkills plugin;

	public SkillsRootCommand(AuraSkills plugin) {
		this.plugin = plugin;
	}

	@Default
	@CommandPermission("auraskills.command.skills")
	@Description("Opens the Skills menu, where you can browse skills, progress, and abilities.")
	public void onSkills(Player player) {
		if (plugin.getUserManager().hasUser(player.getUniqueId())) {
			plugin.getMenuManager().openMenu(player, "skills");
		} else {
			player.sendMessage(plugin.getMsg(CommandMessage.NO_PROFILE, plugin.getDefaultLanguage()));
		}
	}

	@Subcommand("reload")
	@CommandPermission("auraskills.command.reload")
	@Description("Reloads the config, messages, menus, loot tables, action bars, boss bars, and health and luck stats.")
	public void reload(CommandSender sender) {
		new ReloadExecutor(plugin).reload(sender);
	}

	@Subcommand("help")
	@CommandPermission("auraskills.command.help")
	public void onHelp(CommandSender sender, CommandHelp help) {
		help.showHelp();
	}
}
