package com.archyx.aureliumskills.menu;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.skills.Skill;
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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

public class StatsMenu implements InventoryProvider{

	private final Player player;
	
	public StatsMenu(Player player) {
		this.player = player;
	}
	
	@Override
	public void init(Player player, InventoryContents contents) {
		contents.fill(ClickableItem.empty(MenuItems.getEmptyPane()));
		contents.set(SlotPos.of(1, 4), ClickableItem.empty(getPlayerHead(SkillLoader.playerStats.get(player.getUniqueId()))));
		contents.set(SlotPos.of(1, 1), ClickableItem.empty(getStatItem(
			Stat.STRENGTH, 14, new Skill[] {Skill.FORAGING, Skill.FIGHTING, Skill.SORCERY}, 
			new Skill[] {Skill.FARMING, Skill.ARCHERY})));
		contents.set(SlotPos.of(1, 2), ClickableItem.empty(getStatItem(
			Stat.HEALTH, 1, new Skill[] {Skill.FARMING, Skill.ALCHEMY}, 
			new Skill[] {Skill.FISHING, Skill.DEFENSE, Skill.HEALING})));
		contents.set(SlotPos.of(1, 3), ClickableItem.empty(getStatItem(
			Stat.REGENERATION, 4, new Skill[] {Skill.EXCAVATION, Skill.ENDURANCE, Skill.HEALING},
			new Skill[] {Skill.FIGHTING, Skill.AGILITY})));
		contents.set(SlotPos.of(1, 5), ClickableItem.empty(getStatItem(
			Stat.LUCK, 13, new Skill[] {Skill.FISHING, Skill.ARCHERY}, 
			new Skill[] {Skill.MINING, Skill.EXCAVATION, Skill.ENCHANTING})));
		contents.set(SlotPos.of(1, 6), ClickableItem.empty(getStatItem(
			Stat.WISDOM, 11, new Skill[] {Skill.AGILITY, Skill.ENCHANTING}, 
			new Skill[] {Skill.ALCHEMY, Skill.SORCERY, Skill.FORAGING})));
		contents.set(SlotPos.of(1, 7), ClickableItem.empty(getStatItem(
			Stat.TOUGHNESS, 10, new Skill[] {Skill.MINING, Skill.DEFENSE, Skill.FORAGING}, 
			new Skill[] {Skill.FORAGING, Skill.ENDURANCE})));
	}

	@Override
	public void update(Player player, InventoryContents contents) {
		
		
	}

	private ItemStack getStatItem(Stat stat, int color, Skill[] primarySkills, Skill[] secondarySkills) {
		//Creates item and sets it to correct color
		ItemStack item = XMaterial.WHITE_STAINED_GLASS_PANE.parseItem();
		if (color == 14) {
			item = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
		}
		else if (color == 1) {
			item = XMaterial.ORANGE_STAINED_GLASS_PANE.parseItem();
		}
		else if (color == 4) {
			item = XMaterial.YELLOW_STAINED_GLASS_PANE.parseItem();
		}
		else if (color == 13) {
			item = XMaterial.GREEN_STAINED_GLASS_PANE.parseItem();
		}
		else if (color == 11) {
			item = XMaterial.BLUE_STAINED_GLASS_PANE.parseItem();
		}
		else if (color == 10) {
			item = XMaterial.PURPLE_STAINED_GLASS_PANE.parseItem();
		}
		//Sets item name
		if (item != null) {
			ItemMeta meta = item.getItemMeta();
			if (meta != null) {
				meta.setDisplayName(stat.getColor() + stat.getDisplayName());
				//Adds name
				List<String> lore = new LinkedList<>();
				lore.add(stat.getDescription());
				lore.add(" ");
				//Add primary and secondary skill lists
				if (primarySkills.length == 2) {
					lore.add(Lang.getMessage(MenuMessage.PRIMARY_SKILLS_TWO)
							.replace("{skill_1}", primarySkills[0].getDisplayName())
							.replace("{skill_2}", primarySkills[1].getDisplayName()));
				} else if (primarySkills.length == 3) {
					lore.add(Lang.getMessage(MenuMessage.PRIMARY_SKILLS_THREE)
							.replace("{skill_1}", primarySkills[0].getDisplayName())
							.replace("{skill_2}", primarySkills[1].getDisplayName())
							.replace("{skill_3}", primarySkills[2].getDisplayName()));
				}
				if (secondarySkills.length == 2) {
					lore.add(Lang.getMessage(MenuMessage.SECONDARY_SKILLS_TWO)
							.replace("{skill_1}", secondarySkills[0].getDisplayName())
							.replace("{skill_2}", secondarySkills[1].getDisplayName()));
				} else if (secondarySkills.length == 3) {
					lore.add(Lang.getMessage(MenuMessage.SECONDARY_SKILLS_THREE)
							.replace("{skill_1}", secondarySkills[0].getDisplayName())
							.replace("{skill_2}", secondarySkills[1].getDisplayName())
							.replace("{skill_3}", secondarySkills[2].getDisplayName()));
				}
				lore.add(" ");
				//Add player stat levels and values
				if (SkillLoader.playerStats.containsKey(player.getUniqueId())) {
					PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
					lore.add(Lang.getMessage(MenuMessage.YOUR_LEVEL)
							.replace("{color}", stat.getColor())
							.replace("{level}", String.valueOf(playerStat.getStatLevel(stat))));
					lore.add(" ");
					lore.add(getStatValue(stat, playerStat));
				}
				meta.setLore(ItemUtils.formatLore(lore));
				item.setItemMeta(meta);
			}
		}
		return item;
	}
	
	private ItemStack getPlayerHead(PlayerStat stat) {
		ItemStack item = SkullCreator.itemFromUuid(player.getUniqueId());
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(ChatColor.YELLOW + player.getName());
			List<String> lore = new LinkedList<>();
			lore.add(ChatColor.DARK_RED + "  ➽ " + Stat.STRENGTH.getDisplayName() + " " + ChatColor.WHITE + stat.getStatLevel(Stat.STRENGTH));
			lore.add(ChatColor.RED + "  ❤ " + Stat.HEALTH.getDisplayName() + " " + ChatColor.WHITE + stat.getStatLevel(Stat.HEALTH));
			lore.add(ChatColor.GOLD + "  ❥ " + Stat.REGENERATION.getDisplayName() + " " + ChatColor.WHITE + stat.getStatLevel(Stat.REGENERATION));
			lore.add(ChatColor.DARK_GREEN + "  ☘ " + Stat.LUCK.getDisplayName() + " " + ChatColor.WHITE + stat.getStatLevel(Stat.LUCK));
			lore.add(ChatColor.BLUE + "  ✿ " + Stat.WISDOM.getDisplayName() + " " + ChatColor.WHITE + stat.getStatLevel(Stat.WISDOM));
			lore.add(ChatColor.DARK_PURPLE + "  ✦ " + Stat.TOUGHNESS.getDisplayName() + " " + ChatColor.WHITE + stat.getStatLevel(Stat.TOUGHNESS));
			meta.setLore(ItemUtils.formatLore(lore));
			item.setItemMeta(meta);
		}
		return item;
	}
	
	private String getStatValue(Stat stat, PlayerStat ps) {
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
