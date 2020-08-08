package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.Setting;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.Message;
import com.archyx.aureliumskills.magic.ManaManager;
import com.archyx.aureliumskills.AureliumSkills;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.UUID;

public class ActionBar {

	private Plugin plugin;
	private ManaManager mana;

	public ActionBar(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public static HashMap<UUID, Boolean> isGainingXp = new HashMap<UUID, Boolean>();
	public static HashMap<UUID, Integer> timer = new HashMap<UUID, Integer>();
	public static HashMap<UUID, Integer> currentAction = new HashMap<UUID, Integer>();

	public void startUpdateActionBar() {
		mana = AureliumSkills.manaManager;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (Options.enable_action_bar) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (!currentAction.containsKey(player.getUniqueId())) {
							currentAction.put(player.getUniqueId(), 0);
						}
						if (isGainingXp.containsKey(player.getUniqueId())) {
							if (!isGainingXp.get(player.getUniqueId())) {
								if (Options.enable_health_on_action_bar && Options.enable_mana_on_action_bar) {
										player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Options.health_text_color + "" + (int) (player.getHealth() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + "/" + (int) (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + " " + Lang.getMessage(Message.HP) +
												"                " + Options.mana_text_color + mana.getMana(player.getUniqueId()) + "/" + mana.getMaxMana(player.getUniqueId()) + " " + Lang.getMessage(Message.MANA)));
								}
								else if (Options.enable_health_on_action_bar) {
									player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Options.health_text_color + "" + (int) (player.getHealth() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + "/" + (int) (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + " " + Lang.getMessage(Message.HP)));
								}
								else if (Options.enable_mana_on_action_bar) {
									player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Options.mana_text_color + "" + mana.getMana(player.getUniqueId()) + "/" + mana.getMaxMana(player.getUniqueId()) + " " + Lang.getMessage(Message.MANA)));
								}
							}
						}
						else {
							isGainingXp.put(player.getUniqueId(), false);
						}
					}
				}
			}
		}, 0L, Options.actionBarUpdatePeriod);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (Options.enable_action_bar) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (timer.containsKey(player.getUniqueId())) {
							if (timer.get(player.getUniqueId()) != 0) {
								timer.put(player.getUniqueId(), timer.get(player.getUniqueId()) - 1);
							}
						} else {
							timer.put(player.getUniqueId(), 0);
						}
					}
				}
			}
		}, 0L, 2L);
	}
	
}
