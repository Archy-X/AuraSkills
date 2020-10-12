package com.archyx.aureliumskills.menu;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.stats.PlayerStat;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.ItemUtils;
import com.cryptomorin.xseries.XMaterial;
import dev.dbassett.skullcreator.SkullCreator;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class StatsMenu implements InventoryProvider{

	private final Player player;
	private final MenuOption options;

	public StatsMenu(Player player) {
		this.player = player;
		options = AureliumSkills.getMenuLoader().getMenu("stats_menu");
	}
	
	@Override
	public void init(Player player, InventoryContents contents) {
		long start = System.nanoTime();
		// Fill item
		if (options.isFillEnabled()) {
			contents.fill(ClickableItem.empty(options.getFillItem()));
		}
		ItemOption skull = options.getItem("skull");
		contents.set(SlotPos.of(skull.getRow(), skull.getColumn()), ClickableItem.empty(getPlayerHead(SkillLoader.playerStats.get(player.getUniqueId()), skull)));
		ItemTemplate template = options.getTemplate("stat");
		contents.set(template.getPosition(Stat.STRENGTH), ClickableItem.empty(getStatItem(Stat.STRENGTH, template)));
		contents.set(template.getPosition(Stat.HEALTH), ClickableItem.empty(getStatItem(Stat.HEALTH, template)));
		contents.set(template.getPosition(Stat.REGENERATION), ClickableItem.empty(getStatItem(Stat.REGENERATION, template)));
		contents.set(template.getPosition(Stat.LUCK), ClickableItem.empty(getStatItem(Stat.LUCK, template)));
		contents.set(template.getPosition(Stat.WISDOM), ClickableItem.empty(getStatItem(Stat.WISDOM, template)));
		contents.set(template.getPosition(Stat.TOUGHNESS), ClickableItem.empty(getStatItem(Stat.TOUGHNESS, template)));
		long end = System.nanoTime();
		player.sendMessage("Menu opened in " + ((double) (end - start))/1000000 + " ms");
	}

	@Override
	public void update(Player player, InventoryContents contents) {

	}

	private ItemStack getStatItem(Stat stat, ItemTemplate template) {
		ItemStack item = template.getBaseItem(stat).clone();
		ItemMeta meta = item.getItemMeta();
		PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
		if (playerStat != null) {
			if (meta != null) {
				meta.setDisplayName(meta.getDisplayName()
						.replace("{color}", stat.getColor())
						.replace("{stat}", stat.getDisplayName()));
				String primarySkillsTwo = "";
				if (stat.getPrimarySkills().length == 2) {
					primarySkillsTwo = Lang.getMessage(MenuMessage.PRIMARY_SKILLS_TWO)
							.replace("{skill_1}", stat.getPrimarySkills()[0].get().getDisplayName())
							.replace("{skill_2}", stat.getPrimarySkills()[1].get().getDisplayName());
				}
				String primarySkillsThree = "";
				if (stat.getPrimarySkills().length == 3) {
					primarySkillsTwo = Lang.getMessage(MenuMessage.PRIMARY_SKILLS_THREE)
							.replace("{skill_1}", stat.getPrimarySkills()[0].get().getDisplayName())
							.replace("{skill_2}", stat.getPrimarySkills()[1].get().getDisplayName())
							.replace("{skill_3}", stat.getPrimarySkills()[2].get().getDisplayName());
				}
				String secondarySkillsTwo = "";
				if (stat.getSecondarySkills().length == 2) {
					secondarySkillsTwo = Lang.getMessage(MenuMessage.SECONDARY_SKILLS_TWO)
							.replace("{skill_1}", stat.getSecondarySkills()[0].get().getDisplayName())
							.replace("{skill_2}", stat.getSecondarySkills()[1].get().getDisplayName());
				}
				String secondarySkillsThree = "";
				if (stat.getSecondarySkills().length == 3) {
					secondarySkillsTwo = Lang.getMessage(MenuMessage.SECONDARY_SKILLS_THREE)
							.replace("{skill_1}", stat.getSecondarySkills()[0].get().getDisplayName())
							.replace("{skill_2}", stat.getSecondarySkills()[1].get().getDisplayName())
							.replace("{skill_3}", stat.getSecondarySkills()[2].get().getDisplayName());
				}
				List<String> lore = new ArrayList<>();
				List<String> baseLore = meta.getLore();
				if (baseLore != null) {
					for (String line : baseLore) {
						lore.add(line.replace("{stat_desc}", stat.getDescription())
								.replace("{primary_skills_two}", primarySkillsTwo)
								.replace("{primary_skills_three}", primarySkillsThree)
								.replace("{secondary_skills_two}", secondarySkillsTwo)
								.replace("{secondary_skills_three}", secondarySkillsThree)
								.replace("{your_level}", Lang.getMessage(MenuMessage.YOUR_LEVEL)
										.replace("{color}", stat.getColor())
										.replace("{level}", String.valueOf(playerStat.getStatLevel(stat))))
								.replace("{descriptors}", getDescriptors(stat, playerStat)));
					}
				}
				meta.setLore(ItemUtils.formatLore(lore));
				item.setItemMeta(meta);
			}
		}
		return item;
	}
	
	private ItemStack getPlayerHead(PlayerStat playerStat, ItemOption option) {
		ItemStack item = option.getBaseItem().clone();
		if (item.getType().equals(XMaterial.PLAYER_HEAD.parseMaterial())) {
			SkullCreator.itemWithUuid(item, player.getUniqueId());
		}
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(meta.getDisplayName().replace("{player}", player.getName()));
			List<String> lore = new ArrayList<>();
			List<String> baseLore = meta.getLore();
			if (baseLore != null) {
				for (String line : baseLore) {
					for (Stat stat : Stat.values()) {
						line = line.replace("{player_stat_entry:" + stat.name().toLowerCase() + "}", Lang.getMessage(MenuMessage.PLAYER_STAT_ENTRY)
								.replace("{color}", stat.getColor())
								.replace("{symbol}", stat.getSymbol())
								.replace("{stat}", stat.getDisplayName())
								.replace("{level}", String.valueOf(playerStat.getStatLevel(stat))));
					}
					lore.add(line);
				}
			}
			meta.setLore(ItemUtils.formatLore(lore));
			item.setItemMeta(meta);
		}
		return item;
	}
	
	private String getDescriptors(Stat stat, PlayerStat ps) {
		NumberFormat nf = new DecimalFormat("##.##");
		switch(stat) {
			case STRENGTH:
				double strengthLevel = ps.getStatLevel(Stat.STRENGTH);
				double attackDamage = strengthLevel * OptionL.getDouble(Option.STRENGTH_MODIFIER);
				if (OptionL.getBoolean(Option.STRENGTH_DISPLAY_DAMAGE_WITH_HEALTH_SCALING)) {
					attackDamage *= OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
				}
				return Lang.getMessage(MenuMessage.ATTACK_DAMAGE).replace("{value}", nf.format(attackDamage));
			case HEALTH:
				double modifier = ((double) ps.getStatLevel(Stat.HEALTH)) * OptionL.getDouble(Option.HEALTH_MODIFIER);
				double scaledHealth = modifier * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
				return Lang.getMessage(MenuMessage.HP).replace("{value}", nf.format(scaledHealth));
			case LUCK:
				double luck = ps.getStatLevel(Stat.LUCK) * OptionL.getDouble(Option.LUCK_MODIFIER);
				double doubleDropChance = (double) ps.getStatLevel(Stat.LUCK) * OptionL.getDouble(Option.LUCK_DOUBLE_DROP_MODIFIER) * 100;
				if (doubleDropChance > OptionL.getDouble(Option.LUCK_DOUBLE_DROP_PERCENT_MAX)) {
					doubleDropChance = OptionL.getDouble(Option.LUCK_DOUBLE_DROP_PERCENT_MAX);
				}
				return Lang.getMessage(MenuMessage.LUCK).replace("{value}", nf.format(luck))
						+ "\n" + Lang.getMessage(MenuMessage.DOUBLE_DROP_CHANCE).replace("{value}", nf.format(doubleDropChance));
			case REGENERATION:
				double saturatedRegen = ps.getStatLevel(Stat.REGENERATION) * OptionL.getDouble(Option.REGENERATION_SATURATED_MODIFIER) * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
				double hungerFullRegen = ps.getStatLevel(Stat.REGENERATION) *  OptionL.getDouble(Option.REGENERATION_HUNGER_FULL_MODIFIER) * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
				double almostFullRegen = ps.getStatLevel(Stat.REGENERATION) *  OptionL.getDouble(Option.REGENERATION_HUNGER_ALMOST_FULL_MODIFIER) * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
				double manaRegen = ps.getStatLevel(Stat.REGENERATION) * OptionL.getDouble(Option.REGENERATION_MANA_MODIFIER);
				return Lang.getMessage(MenuMessage.SATURATED_REGEN).replace("{value}", nf.format(saturatedRegen))
						+ "\n" + Lang.getMessage(MenuMessage.FULL_HUNGER_REGEN).replace("{value}", nf.format(hungerFullRegen))
						+ "\n" + Lang.getMessage(MenuMessage.ALMOST_FULL_HUNGER_REGEN).replace("{value}", nf.format(almostFullRegen))
						+ "\n" + Lang.getMessage(MenuMessage.MANA_REGEN).replace("{value}", String.valueOf((int) manaRegen));
			case TOUGHNESS:
				double toughness = ps.getStatLevel(Stat.TOUGHNESS) * OptionL.getDouble(Option.TOUGHNESS_NEW_MODIFIER);
				double damageReduction = (-1.0 * Math.pow(1.01, -1.0 * toughness) + 1) * 100;
				return Lang.getMessage(MenuMessage.INCOMING_DAMAGE).replace("{value}", nf.format(damageReduction));
			case WISDOM:
				double xpModifier = ps.getStatLevel(Stat.WISDOM) * OptionL.getDouble(Option.WISDOM_EXPERIENCE_MODIFIER) * 100;
				int anvilCostReduction = (int) (ps.getStatLevel(Stat.WISDOM) * OptionL.getDouble(Option.WISDOM_ANVIL_COST_MODIFIER));
				return Lang.getMessage(MenuMessage.XP_GAIN).replace("{value}", nf.format(xpModifier))
						+ "\n" + Lang.getMessage(MenuMessage.ANVIL_COST_REDUCTION).replace("{value}", String.valueOf(anvilCostReduction));
			default:
				return "";
		}
	}
	
	public static SmartInventory getInventory(Player player) {
		return SmartInventory.builder()
				.provider(new StatsMenu(player))
				.size(3, 9)
				.title(Lang.getMessage(MenuMessage.STATS_MENU_TITLE))
				.manager(AureliumSkills.invManager)
				.build();
	}
	
}
