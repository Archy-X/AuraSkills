package com.archyx.aureliumskills.menu;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.menu.items.ItemType;
import com.archyx.aureliumskills.menu.items.SkullItem;
import com.archyx.aureliumskills.menu.templates.StatTemplate;
import com.archyx.aureliumskills.menu.templates.TemplateType;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.stats.PlayerStat;
import com.archyx.aureliumskills.stats.Stat;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.entity.Player;

import java.util.Locale;

public class StatsMenu implements InventoryProvider{

	private final Locale locale;
	private final MenuOption options;

	public StatsMenu(Locale locale, MenuOption menuOption) {
		this.locale = locale;
		this.options = menuOption;
	}
	
	@Override
	public void init(Player player, InventoryContents contents) {
		PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
		if (playerStat != null) {
			// Fill item
			if (options.isFillEnabled()) {
				contents.fill(ClickableItem.empty(options.getFillItem()));
			}
			SkullItem skullItem = (SkullItem) options.getItem(ItemType.SKULL);
			contents.set(skullItem.getPos(), ClickableItem.empty(skullItem.getItem(player, playerStat, locale)));
			StatTemplate statTemplate = (StatTemplate) options.getTemplate(TemplateType.STAT);
			contents.set(statTemplate.getPos(Stat.STRENGTH), ClickableItem.empty(statTemplate.getItem(Stat.STRENGTH, playerStat, locale)));
			contents.set(statTemplate.getPos(Stat.HEALTH), ClickableItem.empty(statTemplate.getItem(Stat.HEALTH, playerStat, locale)));
			contents.set(statTemplate.getPos(Stat.REGENERATION), ClickableItem.empty(statTemplate.getItem(Stat.REGENERATION, playerStat, locale)));
			contents.set(statTemplate.getPos(Stat.LUCK), ClickableItem.empty(statTemplate.getItem(Stat.LUCK, playerStat, locale)));
			contents.set(statTemplate.getPos(Stat.WISDOM), ClickableItem.empty(statTemplate.getItem(Stat.WISDOM, playerStat, locale)));
			contents.set(statTemplate.getPos(Stat.TOUGHNESS), ClickableItem.empty(statTemplate.getItem(Stat.TOUGHNESS, playerStat, locale)));
		}
	}

	@Override
	public void update(Player player, InventoryContents contents) {

	}
	
	public static SmartInventory getInventory(Player player, AureliumSkills plugin) {
		Locale locale = Lang.getLanguage(player);
		MenuOption menuOption = plugin.getMenuLoader().getMenu(MenuType.STATS);
		return SmartInventory.builder()
				.provider(new StatsMenu(locale, menuOption))
				.size(menuOption.getRows(), 9)
				.title(Lang.getMessage(MenuMessage.STATS_MENU_TITLE, locale))
				.manager(plugin.getInventoryManager())
				.build();
	}
	
}
