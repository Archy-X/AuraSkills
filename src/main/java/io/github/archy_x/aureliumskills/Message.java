package io.github.archy_x.aureliumskills;

public enum Message {

	LEVEL("basic.level.singular", "Level"),
	LEVELS("basic.level.plural", "Levels"),
	PAGE("basic.page", "Page _"),
	PROGRESS_TO_LEVEL("basic.progress-to-level", "Progress to Level _"),
	CLOSE("basic.close", "Close"),
	GO_BACK("basic.go-back", "Go Back"),
	PREVIOUS_PAGE("basic.previous-page", "Previous Page"),
	PREVIOUS_PAGE_CLICK("basic.previous-page-click", "Click to go to previous page"),
	NEXT_PAGE("basic.next-page", "Next Page"),
	NEXT_PAGE_CLICK("basic.next-page-click", "Click to go to next page"),
	REWARDS("basic.rewards", "Rewards"),
	LOCKED("basic.locked", "LOCKED"),
	IN_PROGRESS("basic.in-progress", "IN PROGRESS"),
	UNLOCKED("basic.unlocked", "UNLOCKED"),
	HP("basic.hp", "HP"),
	XP("basic.xp", "XP"),
	MAXED("basic.maxed", "MAXED"),
	MAX_LEVEL("basic.max-level", "MAX LEVEL"),
	LEVEL_UP("basic.level-up", "Level Up"),
	MANA("basic.mana", "Mana"),
	YOUR_SKILLS("skills.your-skills", "Your Skills"),
	YOUR_SKILLS_DESCRIPTION("skills.your-skills-description", "Upgrade Skills by doing various tasks to unlock valuable stat boosts, abilities, and more!"),
	SKILL_POINTS_SINGULAR("skills.skill-points.singular", "Skill Point"),
	SKILL_POINTS_PLURAL("skills.skill-points.plural", "Skill Points"),
	PRIMARY_SKILLS("skills.primary-skills", "Primary Skills"),
	SECONDARY_SKILLS("skills.secondary-skills", "Secondary Skills"),
	SKILL_HOVER("skills.skill-hover", "Hover over a Skill for more information!"),
	SKILL_CLICK("skills.skill-click", "Click on a Skill to view Level Progression!"),
	LEFT_CLICK_SKILL("skills.left-click-skill", "Left Click to view Level Progression!"),
	RIGHT_CLICK_SKILL("skills.right-click-skill", "Right Click to view Skill Tree!"),
	BACK_SKILLS_MENU("skills.back-skills-menu", "Back to Skills Menu"),
	SKILL_TREE_NAME("skills.skill-tree.name", "Skill Tree"),
	SKILL_TREE_DESCRIPTION("skills.skill-tree.description", "Unlock and upgrade powerful abilities using Skill Points"),
	SKILL_TREE_CLICK("skills.skill-tree.click", "Click to open Skill Tree Menu!"),
	FARMING_NAME("skills.farming.name", "Farming"),
	FARMING_DESCRIPTION("skills.farming.description", "Harvest crops to earn Farming XP"),
	FORAGING_NAME("skills.foraging.name", "Foraging"),
	FORAGING_DESCRIPTION("skills.foraging.description", "Cut trees to earn Foraging XP"),
	MINING_NAME("skills.mining.name", "Mining"),
	MINING_DESCRIPTION("skills.mining.description", "Mine stone and ores to earn Mining XP"),
	FISHING_NAME("skills.fishing.name", "Fishing"),
	FISHING_DESCRIPTION("skills.fishing.description", "Catch fish to earn Fishing XP"),
	EXCAVATION_NAME("skills.excavation.name", "Excavation"),
	EXCAVATION_DESCRIPTION("skills.excavation.description", "Dig with a shovel to earn Excavation XP"),
	ARCHERY_NAME("skills.archery.name", "Archery"),
	ARCHERY_DESCRIPTION("skills.archery.description", "Shoot mobs and players with a bow to earn Archery XP"),
	DEFENSE_NAME("skills.defense.name", "Defense"),
	DEFENSE_DESCRIPTION("skills.defense.description", "Take damage from entities to earn Defense XP"),
	FIGHTING_NAME("skills.fighting.name", "Fighting"),
	FIGHTING_DESCRIPTION("skills.fighting.description", "Fight mobs with melee weapons to earn Fighting XP"),
	ENDURANCE_NAME("skills.endurance.name", "Endurance"),
	ENDURANCE_DESCRIPTION("skills.endurance.description", "Walk and run to earn Endurance XP"),
	AGILITY_NAME("skills.agility.name", "Agility"),
	AGILITY_DESCRIPTION("skills.agility.description", "Jump and take fall damage to earn Agility XP"),
	ALCHEMY_NAME("skills.alchemy.name", "Alchemy"),
	ALCHEMY_DESCRIPTION("skills.alchemy.description", "Brew potions to earn Alchemy XP"),
	ENCHANTING_NAME("skills.enchanting.name", "Enchanting"),
	ENCHANTING_DESCRIPTION("skills.enchanting.description", "Enchant items and books to earn Enchanting XP"),
	SORCERY_NAME("skills.sorcery.name", "Sorcery"),
	SORCERY_DESCRIPTION("skills.sorcery.description", "Cast spells to earn Sorcery XP"),
	HEALING_NAME("skills.healing.name", "Healing"),
	HEALING_DESCRIPTION("skills.healing.description", "Drink and splash potions to earn Healing XP"),
	FORGING_NAME("skills.forging.name", "Forging"),
	FORGING_DESCRIPTION("skills.forging.description", "Combine and apply books in an anvil to earn Forging XP"),
	YOUR_STATS("stats.your-stats", "Your Stats"),
	PRIMARY_STAT("stats.primary-stat", "Primary Stat"),
	SECONDARY_STAT("stats.secondary-stat", "Secondary Stat"),
	STRENGTH_NAME("stats.strength.name", "Strength"),
	STRENGTH_DESCRIPTION("stats.strength.description", "Strength increases your attack damage with various different weapons"),
	HEALTH_NAME("stats.health.name", "Health"),
	HEALTH_DESCRIPTION("stats.health.description", "Health increases the amount of HP you have, allowing you to last longer in fights"),
	REGENERATION_NAME("stats.regeneration.name", "Regeneration"),
	REGENERATION_DESCRIPTION("stats.regeneration.description", "Regeneration increases how fast you recover both health and mana"),
	LUCK_NAME("stats.luck.name", "Luck"),
	LUCK_DESCRIPTION("stats.luck.description", "Luck improves luck from loot chests, fishing, and gives a chance to drop double items on certain blocks"),
	WISDOM_NAME("stats.wisdom.name", "Wisdom"),
	WISDOM_DESCRIPTION("stats.wisdom.description", "Wisdom increases your experience gain any source, decreases anvil costs, and increases your mana pool"),
	TOUGHNESS_NAME("stats.toughness.name", "Toughness"),
	TOUGHNESS_DESCRIPTION("stats.toughness.description", "Toughness increases the amount of damage reduced from enemy attacks");
	
	private String path;
	private String defaultMessage;
	
	private Message(String path, String defaultMessage) {
		this.path = path;
		this.defaultMessage = defaultMessage;
	}
	
	public String getDefaultMessage() {
		return defaultMessage;
	}
	
	public String getPath() {
		return path;
	}
}
