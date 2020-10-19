package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.ActionBarMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.magic.ManaManager;
import com.archyx.aureliumskills.util.ProtocolUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.UUID;

public class ActionBar {

	private final Plugin plugin;
	private ManaManager mana;

	public ActionBar(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public static HashMap<UUID, Boolean> isGainingXp = new HashMap<>();
	public static HashMap<UUID, Integer> timer = new HashMap<>();
	public static HashMap<UUID, Integer> currentAction = new HashMap<>();
	public static HashSet<UUID> actionBarDisabled = new HashSet<>();

	public void startUpdateActionBar() {
		mana = AureliumSkills.manaManager;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			if (OptionL.getBoolean(Option.ENABLE_ACTION_BAR)) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					Locale locale = Lang.getLanguage(player);
					//Check disabled worlds
					if (!actionBarDisabled.contains(player.getUniqueId())) {
						if (!AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
							if (!currentAction.containsKey(player.getUniqueId())) {
								currentAction.put(player.getUniqueId(), 0);
							}
							if (isGainingXp.containsKey(player.getUniqueId())) {
								if (!isGainingXp.get(player.getUniqueId())) {
									AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
									if (attribute != null) {
										player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Lang.getMessage(ActionBarMessage.IDLE, locale)
												.replace("{hp}", String.valueOf((int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING))))
												.replace("{max_hp}", String.valueOf((int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING))))
												.replace("{mana}", String.valueOf(mana.getMana(player.getUniqueId())))
												.replace("{max_mana}", String.valueOf(mana.getMaxMana(player.getUniqueId())))));

									}
								}
							} else {
								isGainingXp.put(player.getUniqueId(), false);
							}
						}
					}
				}
			}
		}, 0L, OptionL.getInt(Option.ACTION_BAR_UPDATE_PERIOD));
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			if (OptionL.getBoolean(Option.ENABLE_ACTION_BAR)) {
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
		}, 0L, 2L);
	}

	public void startUpdatingActionBarProtocolLib() {
		mana = AureliumSkills.manaManager;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			if (OptionL.getBoolean(Option.ENABLE_ACTION_BAR)) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					Locale locale = Lang.getLanguage(player);
					//Check disabled worlds
					if (!actionBarDisabled.contains(player.getUniqueId())) {
						if (!AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
							if (!currentAction.containsKey(player.getUniqueId())) {
								currentAction.put(player.getUniqueId(), 0);
							}
							if (isGainingXp.containsKey(player.getUniqueId())) {
								if (!isGainingXp.get(player.getUniqueId())) {
									AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
									if (attribute != null) {
										ProtocolUtil.sendActionBar(player, Lang.getMessage(ActionBarMessage.IDLE, locale)
												.replace("{hp}", String.valueOf((int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING))))
												.replace("{max_hp}", String.valueOf((int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING))))
												.replace("{mana}", String.valueOf(mana.getMana(player.getUniqueId())))
												.replace("{max_mana}", String.valueOf(mana.getMaxMana(player.getUniqueId()))));
									}
								}
							} else {
								isGainingXp.put(player.getUniqueId(), false);
							}
						}
					}
				}
			}
		}, 0L, OptionL.getInt(Option.ACTION_BAR_UPDATE_PERIOD));
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			if (OptionL.getBoolean(Option.ENABLE_ACTION_BAR)) {
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
		}, 0L, 2L);
	}
	
}
