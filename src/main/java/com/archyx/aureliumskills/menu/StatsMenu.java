package com.archyx.aureliumskills.menu;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.menu.items.ItemType;
import com.archyx.aureliumskills.menu.items.SkullItem;
import com.archyx.aureliumskills.menu.templates.StatTemplate;
import com.archyx.aureliumskills.menu.templates.TemplateType;
import com.archyx.aureliumskills.stats.Stat;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.entity.Player;

import java.util.Locale;

public class StatsMenu implements InventoryProvider{

	private final AureliumSkills plugin;
	private final Locale locale;
	private final MenuOption options;

	public StatsMenu(AureliumSkills plugin, Locale locale, MenuOption menuOption) {
		this.plugin = plugin;
		this.locale = locale;
		this.options = menuOption;
	}
	
	@Override
	public void init(Player player, InventoryContents contents) {
		PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
		if (playerData != null) {
			// Fill item
			if (options.isFillEnabled()) {
				contents.fill(ClickableItem.empty(options.getFillItem()));
			}
			SkullItem skullItem = (SkullItem) options.getItem(ItemType.SKULL);
			contents.set(skullItem.getPos(), ClickableItem.empty(skullItem.getItem(player, playerData, locale)));
			StatTemplate statTemplate = (StatTemplate) options.getTemplate(TemplateType.STAT);
			contents.set(statTemplate.getPos(Stat.STRENGTH), ClickableItem.empty(statTemplate.getItem(Stat.STRENGTH, playerData, player, locale)));
			contents.set(statTemplate.getPos(Stat.HEALTH), ClickableItem.empty(statTemplate.getItem(Stat.HEALTH, playerData, player, locale)));
			contents.set(statTemplate.getPos(Stat.REGENERATION), ClickableItem.empty(statTemplate.getItem(Stat.REGENERATION, playerData, player, locale)));
			contents.set(statTemplate.getPos(Stat.LUCK), ClickableItem.empty(statTemplate.getItem(Stat.LUCK, playerData, player, locale)));
			contents.set(statTemplate.getPos(Stat.WISDOM), ClickableItem.empty(statTemplate.getItem(Stat.WISDOM, playerData, player, locale)));
			contents.set(statTemplate.getPos(Stat.TOUGHNESS), ClickableItem.empty(statTemplate.getItem(Stat.TOUGHNESS, playerData, player, locale)));
		}
	}

	@Override
	public void update(Player player, InventoryContents contents) {

	}
	
	public static SmartInventory getInventory(Player player, AureliumSkills plugin) {
		Locale locale = plugin.getLang().getLocale(player);
		MenuOption menuOption = plugin.getMenuLoader().getMenu(MenuType.STATS);
		return SmartInventory.builder()
				.provider(new StatsMenu(plugin, locale, menuOption))
				.size(menuOption.getRows(), 9)
				.title(Lang.getMessage(MenuMessage.STATS_MENU_TITLE, locale))
				.manager(plugin.getInventoryManager())
				.build();
	}
	
}
