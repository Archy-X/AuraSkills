package com.archyx.aureliumskills;

import co.aikar.commands.PaperCommandManager;
import com.archyx.aureliumskills.commands.SkillsCommand;
import com.archyx.aureliumskills.commands.StatsCommand;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.listeners.BlockBreak;
import com.archyx.aureliumskills.listeners.BlockPlace;
import com.archyx.aureliumskills.listeners.BlockTracker;
import com.archyx.aureliumskills.listeners.PlayerJoin;
import com.archyx.aureliumskills.loot.LootTableManager;
import com.archyx.aureliumskills.magic.ManaManager;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.abilities.*;
import com.archyx.aureliumskills.skills.abilities.mana_abilities.ManaAbilityManager;
import com.archyx.aureliumskills.skills.levelers.*;
import com.archyx.aureliumskills.stats.*;
import com.archyx.aureliumskills.util.HologramSupport;
import com.archyx.aureliumskills.util.PlaceholderSupport;
import com.archyx.aureliumskills.util.WorldManager;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import fr.minuskube.inv.InventoryManager;
import com.archyx.aureliumskills.util.WorldGuardSupport;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AureliumSkills extends JavaPlugin{

	private PaperCommandManager commandManager;
	private File dataFile = new File(getDataFolder(), "data.yml");
	private FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
	private SkillLoader skillLoader = new SkillLoader(dataFile, config, this);
	private Options options;
	private Lang lang;
	public static LootTableManager lootTableManager;
	public static InventoryManager invManager;
	public static AbilityOptionManager abilityOptionManager;
	public static WorldGuardSupport worldGuardSupport;
	public static WorldManager worldManager;
	public static ManaManager manaManager;
	public static ManaAbilityManager manaAbilityManager;
	public static boolean holographicDisplaysEnabled;
	public static boolean worldGuardEnabled;
	
	public static String tag = ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "Skills" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET;
	
	public void onEnable() {
		invManager = new InventoryManager(this);
		invManager.init();
		//Checks for world guard
		if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
			if (WorldGuardPlugin.inst().getDescription().getVersion().contains("7.0")) {
				worldGuardEnabled = true;
				worldGuardSupport = new WorldGuardSupport(this);
				worldGuardSupport.loadRegions();
				Bukkit.getConsoleSender().sendMessage(tag + ChatColor.AQUA + "WorldGuard Support Enabled!");
			}
		}
		else {
			worldGuardEnabled = false;
		}
		//Checks for PlaceholderAPI
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new PlaceholderSupport(this).register();
			Bukkit.getConsoleSender().sendMessage(tag + ChatColor.AQUA + "PlaceholderAPI Support Enabled!");
		}
		//Checks for holographic displays
		if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
			holographicDisplaysEnabled = true;
			getServer().getPluginManager().registerEvents(new HologramSupport(this), this);
			Bukkit.getConsoleSender().sendMessage(tag + ChatColor.AQUA + "HolographicDisplays Support Enabled!");
		}
		else {
			holographicDisplaysEnabled = false;
		}
		//Load config
		loadConfig();
		options = new Options(this);
		options.loadConfig();
		//Load languages
		lang = new Lang(this);
		lang.matchConfig();
		lang.loadLanguages();
		//Registers Commands
		registerCommands();
		//Registers events
		registerEvents();
		//Load ability manager
		manaAbilityManager = new ManaAbilityManager(this);
		getServer().getPluginManager().registerEvents(manaAbilityManager, this);
		manaAbilityManager.init();
		//Load ability options
		abilityOptionManager = new AbilityOptionManager(this);
		abilityOptionManager.loadOptions();
		//Load stats
		Regeneration regeneration = new Regeneration(this);
		getServer().getPluginManager().registerEvents(regeneration, this);
		regeneration.startRegen();
		regeneration.startSaturationRegen();
		EnduranceLeveler enduranceLeveler = new EnduranceLeveler(this);
		enduranceLeveler.startTracking();
		//Load mana manager
		manaManager = new ManaManager(this);
		getServer().getPluginManager().registerEvents(manaManager, this);
		manaManager.startRegen();
		//Load Action Bar
		ActionBar actionBar = new ActionBar(this);
		actionBar.startUpdateActionBar();
		//Load Data
		if (dataFile.exists()) {
			skillLoader.loadSkillData();
		}	
		else {
			saveResource("data.yml", false);
		}
		skillLoader.startSaving();
		//Load leveler
		Leveler.plugin = this;
		Leveler.loadLevelReqs();
		//Load loot tables
		lootTableManager = new LootTableManager(this);
		//Load world manager
		worldManager = new WorldManager(this);
		worldManager.loadWorlds();
		Bukkit.getConsoleSender().sendMessage(tag + ChatColor.GREEN + "Aurelium Skills has been enabled");
	}
	
	public void onDisable() {
		//Reloads config
		reloadConfig();
		//Save config
		saveConfig();
		//Save Data
		skillLoader.saveSkillData(false);
	}
	
	public void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		try {
			InputStream is = getResource("config.yml");
			if (is != null) {
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(is));
				for (String key : defConfig.getConfigurationSection("").getKeys(true)) {
					if (!getConfig().contains(key)) {
						getConfig().set(key, defConfig.get(key));
					}
				}
				saveConfig();
			}
		} catch (Exception e) {
            e.printStackTrace();
        }
			
	}
	
	private void registerCommands() {
		commandManager = new PaperCommandManager(this);
		commandManager.getCommandCompletions().registerAsyncCompletion("skills", c -> {
			List<String> values = new ArrayList<String>();
			for (Skill skill : Skill.values()) {
				if (Options.isEnabled(skill)) {
					values.add(skill.toString().toLowerCase());
				}
			}
			return values;
		});
		commandManager.getCommandCompletions().registerAsyncCompletion("abilities", c -> {
			List<String> values = new ArrayList<String>();
			for (Ability value : Ability.values()) {
				values.add(value.toString().toLowerCase());
			}
			return values;
		});
		commandManager.getCommandCompletions().registerAsyncCompletion("lang", c -> {
			return lang.getConfig().getStringList("languages");
		});
		commandManager.registerCommand(new SkillsCommand(this));
		commandManager.registerCommand(new StatsCommand());
	}

	public void registerEvents() {
		//Registers Events
		getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
		getServer().getPluginManager().registerEvents(new BlockPlace(this), this);
		getServer().getPluginManager().registerEvents(new BlockBreak(this), this);
		getServer().getPluginManager().registerEvents(new BlockTracker(this), this);
		getServer().getPluginManager().registerEvents(new FarmingLeveler(), this);
		getServer().getPluginManager().registerEvents(new ForagingLeveler(), this);
		getServer().getPluginManager().registerEvents(new MiningLeveler(), this);
		getServer().getPluginManager().registerEvents(new ExcavationLeveler(), this);
		getServer().getPluginManager().registerEvents(new FishingLeveler(), this);
		getServer().getPluginManager().registerEvents(new FightingLeveler(), this);
		getServer().getPluginManager().registerEvents(new ArcheryLeveler(), this);
		getServer().getPluginManager().registerEvents(new DefenseLeveler(), this);
		getServer().getPluginManager().registerEvents(new AgilityLeveler(this), this);
		getServer().getPluginManager().registerEvents(new AlchemyLeveler(this), this);
		getServer().getPluginManager().registerEvents(new EnchantingLeveler(), this);
		getServer().getPluginManager().registerEvents(new ForgingLeveler(), this);
		getServer().getPluginManager().registerEvents(new HealingLeveler(), this);
		getServer().getPluginManager().registerEvents(new Health(), this);
		getServer().getPluginManager().registerEvents(new Toughness(), this);
		getServer().getPluginManager().registerEvents(new Strength(), this);
		getServer().getPluginManager().registerEvents(new Luck(), this);
		getServer().getPluginManager().registerEvents(new Wisdom(), this);
		getServer().getPluginManager().registerEvents(new FarmingAbilities(), this);
		getServer().getPluginManager().registerEvents(new ForagingAbilities(this), this);
		getServer().getPluginManager().registerEvents(new MiningAbilities(this), this);
		getServer().getPluginManager().registerEvents(new FishingAbilities(), this);
		getServer().getPluginManager().registerEvents(new ExcavationAbilities(), this);
		getServer().getPluginManager().registerEvents(new ArcheryAbilities(this), this);
		getServer().getPluginManager().registerEvents(new DefenseAbilities(this), this);
	}
	
}
