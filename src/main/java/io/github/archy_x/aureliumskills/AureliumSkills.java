package io.github.archy_x.aureliumskills;

import co.aikar.commands.PaperCommandManager;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import fr.minuskube.inv.InventoryManager;
import io.github.archy_x.aureliumskills.commands.SkillsCommand;
import io.github.archy_x.aureliumskills.commands.StatsCommand;
import io.github.archy_x.aureliumskills.lang.Lang;
import io.github.archy_x.aureliumskills.listeners.BlockBreak;
import io.github.archy_x.aureliumskills.listeners.BlockPlace;
import io.github.archy_x.aureliumskills.listeners.PlayerJoin;
import io.github.archy_x.aureliumskills.loot.LootTableManager;
import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import io.github.archy_x.aureliumskills.skills.abilities.*;
import io.github.archy_x.aureliumskills.skills.levelers.*;
import io.github.archy_x.aureliumskills.stats.*;
import io.github.archy_x.aureliumskills.util.WorldGuardSupport;
import io.github.archy_x.aureliumskills.util.WorldManager;
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
	public static AbilityManager abilityManager;
	public static AbilityOptionManager abilityOptionManager;
	public static WorldGuardSupport worldGuardSupport;
	public static WorldManager worldManager;
	public static boolean worldGuardEnabled;
	
	public static String tag = ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "Skills" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET;
	
	public void onEnable() {
		invManager = new InventoryManager(this);
		invManager.init();
		//Checks for world guard
		if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
			if (WorldGuardPlugin.inst().getDescription().getVersion().contains("7.0.3")) {
				worldGuardEnabled = true;
				worldGuardSupport = new WorldGuardSupport(this);
				worldGuardSupport.loadRegions();
				Bukkit.getConsoleSender().sendMessage(tag + ChatColor.AQUA + "World Guard Support Enabled!");
			}
		}
		else {
			worldGuardEnabled = false;
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
		abilityManager = new AbilityManager(this);
		getServer().getPluginManager().registerEvents(abilityManager, this);
		abilityManager.init();
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
	}
	
}
