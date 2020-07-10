package io.github.archy_x.aureliumskills.skills;

public enum Source {

	WHEAT("farming.types.wheat", 2.0),
	POTATO("farming.types.potato", 2.5),
	CARROT("farming.types.carrot", 2.7),
	BEETROOT("farming.types.beetroot", 3.0),
	NETHER_WART("farming.types.nether-wart", 3.0),
	PUMPKIN("farming.types.pumpkin", 3.4),
	MELON("farming.types.melon", 3.4),
	OAK_LOG("foraging.types.oak-log", 4),
	SPRUCE_LOG("foraging.types.spruce-log", 4),
	BIRCH_LOG("foraging.types.birch-log", 4),
	JUNGLE_LOG("foraging.types.jungle-log", 4),
	ACACIA_LOG("foraging.types.acacia-log", 4),
	DARK_OAK_LOG("foraging.types.dark-oak-log", 4),
	OAK_LEAVES("foraging.types.oak-leaves", 0.4),
	BIRCH_LEAVES("foraging.types.birch-leaves", 0.4),
	SPRUCE_LEAVES("foraging.types.spruce-leaves", 0.4),
	JUNGLE_LEAVES("foraging.types.jungle-leaves", 0.4),
	DARK_OAK_LEAVES("foraging.types.dark-oak-leaves", 0.4),
	ACACIA_LEAVES("foraging.types.acacia-leaves", 0.4),
	STONE("mining.types.stone", 0.2),
	COBBLESTONE("mining.types.cobblestone", 0.2),
	GRANITE("mining.types.granite", 0.2),
	DIORITE("mining.types.diorite", 0.2),
	ANDESITE("mining.types.andesite", 0.2),
	COAL_ORE("mining.types.coal-ore", 1),
	IRON_ORE("mining.types.iron-ore", 1.8),
	QUARTZ_ORE("mining.types.quartz-ore", 1.8),
	REDSTONE_ORE("mining.types.redstone-ore", 5.7),
	GOLD_ORE("mining.types.gold-ore", 17.8),
	LAPIS_ORE("mining.types.lapis-ore", 40.6),
	DIAMOND_ORE("mining.types.diamond-ore", 47.3),
	EMERALD_ORE("mining.types.emerald-ore", 142),
    RAW_FISH("fishing.types.raw-fish", 25),
    RAW_SALMON("fishing.types.raw-salmon", 60),
    CLOWNFISH("fishing.types.clownfish", 750),
    PUFFERFISH("fishing.types.pufferfish", 115),
    TREASURE("fishing.types.treasure", 1000),
    JUNK("fishing.types.junk", 30),
    DIRT("excavation.types.dirt", 0.3),
    GRASS_BLOCK("excavation.types.grass-block", 0.7),
    SAND("excavation.types.sand", 0.4),
    GRAVEL("excavation.types.gravel", 1.5),
    MYCELIUM("excavation.types.mycelium", 3.7),
    CLAY("excavation.types.clay", 2.4),
    SOUL_SAND("excavation.types.soul-sand", 2.7),
    ARCHERY_SMALL_PASSIVE("archery.types.small-passive", 1),
    ARCHERY_PASSIVE("archery.types.passive", 2),
    ARCHERY_WEAK_HOSTILE("archery.types.weak-hostile", 5),
    ARCHERY_COMMON_HOSTILE("archery.types.common-hostile", 7),
    ARCHERY_UNCOMMON_HOSTILE("archery.types.uncommon-hostile", 10),
    ARCHERY_STRONG_HOSTILE("archery.types.strong-hostile", 17),
    ARCHERY_STRONGER_HOSTILE("archery.types.stronger-hostile", 35),
    ARCHERY_MINI_BOSS("archery.types.mini-boss", 100),
    ARCHERY_BOSS("archery.types.boss", 7000),
    MOB_DAMAGE("defense.types.mob-damage", 1),
    PLAYER_DAMAGE("defense.types.player-damage", 2),
    FIGHTING_SMALL_PASSIVE("fighting.types.small-passive", 1),
    FIGHTING_PASSIVE("fighting.types.passive", 2),
    FIGHTING_WEAK_HOSTILE("fighting.types.weak-hostile", 5),
    FIGHTING_COMMON_HOSTILE("fighting.types.common-hostile", 7),
    FIGHTING_UNCOMMON_HOSTILE("fighting.types.uncommon-hostile", 10),
    FIGHTING_STRONG_HOSTILE("fighting.types.strong-hostile", 17),
    FIGHTING_STRONGER_HOSTILE("fighting.types.stronger-hostile", 35),
    FIGHTING_MINI_BOSS("fighting.types.mini-boss", 100),
    FIGHTING_BOSS("fighting.types.boss", 7000),
    WALK_PER_METER("endurance.types.walk-per-meter", 0.333),
    SPRINT_PER_METER("endurance.types.sprint-per-meter", 0.1),
    SWIM_PER_METER("endurance.types.swim-per-meter", 1),
    JUMP_PER_100("agility.types.jump-per-100", 10),
    FALL_DAMAGE("agility.types.fall-damage", 1),
	AWKWARD("alchemy.types.awkward", 10),
	REGULAR("alchemy.types.regular", 15),
	EXTENDED("alchemy.types.extended", 25),
	UPGRADED("alchemy.types.upgraded", 25),
	SPLASH("alchemy.types.splash", 35),
	LINGERING("alchemy.types.lingering", 50),
	WEAPON_PER_LEVEL("enchanting.types.weapon-per-level", 2),
	ARMOR_PER_LEVEL("enchanting.types.armor-per-level", 2),
	TOOL_PER_LEVEL("enchanting.types.tool-per-level", 2),
	BOOK_PER_LEVEL("enchanting.types.book-per-level", 2),
	DRINK_REGULAR("healing.types.drink-regular", 10),
	DRINK_EXTENDED("healing.types.drink-extended", 15),
	DRINK_UPGRADED("healing.types.drink-upgraded", 15),
	SPLASH_REGULAR("healing.types.splash-regular", 20),
	SPLASH_EXTENDED("healing.types.splash-extended", 25),
	SPLASH_UPGRADED("healing.types.splash-upgraded", 25),
	COMBINE_BOOKS_PER_LEVEL("forging.types.combine-books-per-level", 10),
	COMBINE_WEAPON_PER_LEVEL("forging.types.combine-weapon-per-level", 20),
	COMBINE_ARMOR_PER_LEVEL("forging.types.combine-armor-per-level", 25),
	COMBINE_TOOL_PER_LEVEL("forging.types.combine-tool-per-level", 30);
	
	private String path;
	private double def;
	
	private Source(String path, double def) {
		this.path = path;
		this.def = def;
	}
	
	public String getPath() {
		return path;
	}
	
	public double getDefault() {
		return def;
	}
	
}
