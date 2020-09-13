package com.archyx.aureliumskills;

import co.aikar.commands.PaperCommandManager;
import com.archyx.aureliumskills.commands.SkillCommands;
import com.archyx.aureliumskills.commands.SkillsCommand;
import com.archyx.aureliumskills.commands.StatsCommand;
import com.archyx.aureliumskills.modifier.ArmorModifierListener;
import com.archyx.aureliumskills.modifier.ItemListener;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.listeners.CheckBlockReplace;
import com.archyx.aureliumskills.listeners.DamageListener;
import com.archyx.aureliumskills.listeners.PlayerJoin;
import com.archyx.aureliumskills.loot.LootTableManager;
import com.archyx.aureliumskills.magic.ManaManager;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.abilities.*;
import com.archyx.aureliumskills.skills.abilities.mana_abilities.ManaAbilityManager;
import com.archyx.aureliumskills.skills.levelers.*;
import com.archyx.aureliumskills.stats.*;
import com.archyx.aureliumskills.util.*;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import fr.minuskube.inv.InventoryManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AureliumSkills extends JavaPlugin{

	private final File dataFile = new File(getDataFolder(), "data.yml");
	private final FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
	private final SkillLoader skillLoader = new SkillLoader(dataFile, config, this);
	private MySqlSupport mySqlSupport;
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
	public static boolean placeholderAPIEnabled;
	public static boolean vaultEnabled;
	private static Economy economy = null;
	
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
			}
		}
		else {
			worldGuardEnabled = false;
		}
		//Checks for PlaceholderAPI
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new PlaceholderSupport(this).register();
			placeholderAPIEnabled = true;
			Bukkit.getLogger().info("[AureliumSkills] PlaceholderAPI Support Enabled!");
		}
		else {
			placeholderAPIEnabled = false;
		}
		//Checks for Vault
		if (setupEconomy()) {
			vaultEnabled = true;
			Bukkit.getLogger().info("[AureliumSkills] Vault Support Enabled!");
		}
		else {
			vaultEnabled = false;
		}
		//Load config
		loadConfig();
		Options options = new Options(this);
		options.loadConfig();
		//Checks for holographic displays
		if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
			holographicDisplaysEnabled = true;
			getServer().getPluginManager().registerEvents(new HologramSupport(this), this);
			Bukkit.getLogger().info("[AureliumSkills] HolographicDisplays Support Enabled!");
		}
		else {
			holographicDisplaysEnabled = false;
		}
		//Load languages
		lang = new Lang(this);
		lang.matchConfig();
		lang.loadDefaultMessages();
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
		if (Options.mySqlEnabled) {
			//Mysql
			mySqlSupport = new MySqlSupport(this, this);
			new BukkitRunnable() {
				@Override
				public void run() {
					mySqlSupport.init();
				}
			}.runTaskAsynchronously(this);
		}
		else {
			if (dataFile.exists()) {
				skillLoader.loadSkillData();
			} else {
				saveResource("data.yml", false);
			}
			skillLoader.startSaving();
		}
		//Load leveler
		Leveler.plugin = this;
		Leveler.loadLevelReqs();
		//Load loot tables
		lootTableManager = new LootTableManager(this);
		//Load world manager
		worldManager = new WorldManager(this);
		worldManager.loadWorlds();
		Bukkit.getLogger().info("[AureliumSkills] Aurelium Skills has been enabled");
		checkUpdates();
	}
	
	public void onDisable() {
		//Reloads config
		reloadConfig();
		//Save config
		saveConfig();
		//Save Data
		if (Options.mySqlEnabled) {
			if (mySqlSupport != null) {
				mySqlSupport.saveData(false);
				mySqlSupport.closeConnection();
			}
			else {
				Bukkit.getLogger().warning("MySql wasn't enabled on server startup, saving data to file instead! MySql will be enabled next time the server starts.");
				skillLoader.saveSkillData(false);
			}
		}
		else {
			skillLoader.saveSkillData(false);
		}
	}

	public void checkUpdates() {
		//Check for updates
		new UpdateChecker(this, 81069).getVersion(version -> {
			if (!this.getDescription().getVersion().contains("Pre-Release")) {
				if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
					getLogger().info("No new updates found");
				} else {
					getLogger().info("New update available! You are on version " + this.getDescription().getVersion() + ", latest version is " +
							version);
					getLogger().info("Download it on Spigot:");
					getLogger().info("http://spigotmc.org/resources/81069");
				}
			}
			else {
				getLogger().info("You are on a Pre-Release version, plugin may be buggy or unstable!");
				getLogger().info("Report any bugs to Archy#2011 on discord or submit an issue here: https://github.com/Archy-X/AureliumSkills/issues");
			}
		});
	}

	public void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		try {
			InputStream is = getResource("config.yml");
			if (is != null) {
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(is));
				ConfigurationSection config = defConfig.getConfigurationSection("");
				if (config != null) {
					for (String key : config.getKeys(true)) {
						if (!getConfig().contains(key)) {
							getConfig().set(key, defConfig.get(key));
						}
					}
				}
				saveConfig();
			}
		} catch (Exception e) {
            e.printStackTrace();
        }
			
	}

	public SkillLoader getSkillLoader() {
		return skillLoader;
	}

	private void registerCommands() {
		PaperCommandManager commandManager = new PaperCommandManager(this);
		commandManager.enableUnstableAPI("help");
		commandManager.getCommandCompletions().registerAsyncCompletion("skills", c -> {
			List<String> values = new ArrayList<>();
			for (Skill skill : Skill.values()) {
				if (Options.isEnabled(skill)) {
					values.add(skill.toString().toLowerCase());
				}
			}
			return values;
		});
		commandManager.getCommandCompletions().registerAsyncCompletion("abilities", c -> {
			List<String> values = new ArrayList<>();
			for (Ability value : Ability.values()) {
				values.add(value.toString().toLowerCase());
			}
			return values;
		});
		commandManager.getCommandCompletions().registerAsyncCompletion("stats", c -> {
			List<String> values = new ArrayList<>();
			for (Stat stat : Stat.values()) {
				values.add(stat.toString().toLowerCase());
			}
			return values;
		});
		commandManager.getCommandCompletions().registerAsyncCompletion("lang", c -> lang.getConfig().getStringList("languages"));
		commandManager.registerCommand(new SkillsCommand(this));
		commandManager.registerCommand(new StatsCommand());
		if (Options.enableSkillCommands) {
			if (Options.isEnabled(Skill.FARMING)) { commandManager.registerCommand(new SkillCommands.FarmingCommand()); }
			if (Options.isEnabled(Skill.FORAGING)) { commandManager.registerCommand(new SkillCommands.ForagingCommand()); }
			if (Options.isEnabled(Skill.MINING)) { commandManager.registerCommand(new SkillCommands.MiningCommand()); }
			if (Options.isEnabled(Skill.FISHING)) { commandManager.registerCommand(new SkillCommands.FishingCommand()); }
			if (Options.isEnabled(Skill.EXCAVATION)) { commandManager.registerCommand(new SkillCommands.ExcavationCommand()); }
			if (Options.isEnabled(Skill.ARCHERY)) { commandManager.registerCommand(new SkillCommands.ArcheryCommand()); }
			if (Options.isEnabled(Skill.DEFENSE)) { commandManager.registerCommand(new SkillCommands.DefenseCommand()); }
			if (Options.isEnabled(Skill.FIGHTING)) { commandManager.registerCommand(new SkillCommands.FightingCommand()); }
			if (Options.isEnabled(Skill.ENDURANCE)) { commandManager.registerCommand(new SkillCommands.EnduranceCommand()); }
			if (Options.isEnabled(Skill.AGILITY)) { commandManager.registerCommand(new SkillCommands.AgilityCommand()); }
			if (Options.isEnabled(Skill.ALCHEMY)) { commandManager.registerCommand(new SkillCommands.AlchemyCommand()); }
			if (Options.isEnabled(Skill.ENCHANTING)) { commandManager.registerCommand(new SkillCommands.EnchantingCommand()); }
			if (Options.isEnabled(Skill.SORCERY)) { commandManager.registerCommand(new SkillCommands.SorceryCommand()); }
			if (Options.isEnabled(Skill.HEALING)) { commandManager.registerCommand(new SkillCommands.HealingCommand()); }
			if (Options.isEnabled(Skill.FORGING)) { commandManager.registerCommand(new SkillCommands.ForgingCommand()); }
		}
	}

	public void registerEvents() {
		//Registers Events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerJoin(this), this);
		pm.registerEvents(new CheckBlockReplace(this), this);
		pm.registerEvents(new FarmingLeveler(), this);
		pm.registerEvents(new ForagingLeveler(), this);
		pm.registerEvents(new MiningLeveler(), this);
		pm.registerEvents(new ExcavationLeveler(), this);
		pm.registerEvents(new FishingLeveler(), this);
		pm.registerEvents(new FightingLeveler(), this);
		pm.registerEvents(new ArcheryLeveler(), this);
		pm.registerEvents(new DefenseLeveler(), this);
		pm.registerEvents(new AgilityLeveler(this), this);
		pm.registerEvents(new AlchemyLeveler(this), this);
		pm.registerEvents(new EnchantingLeveler(), this);
		pm.registerEvents(new ForgingLeveler(), this);
		pm.registerEvents(new HealingLeveler(), this);
		pm.registerEvents(new Health(), this);
		pm.registerEvents(new Luck(), this);
		pm.registerEvents(new Wisdom(), this);
		pm.registerEvents(new FarmingAbilities(), this);
		pm.registerEvents(new ForagingAbilities(this), this);
		pm.registerEvents(new MiningAbilities(this), this);
		pm.registerEvents(new FishingAbilities(), this);
		pm.registerEvents(new ExcavationAbilities(), this);
		pm.registerEvents(new ArcheryAbilities(this), this);
		pm.registerEvents(new DefenseAbilities(this), this);
		pm.registerEvents(new FightingAbilities(this), this);
		pm.registerEvents(new EnduranceAbilities(), this);
		pm.registerEvents(new DamageListener(this), this);
		ItemListener itemListener = new ItemListener(this);
		pm.registerEvents(itemListener, this);
		itemListener.scheduleTask();
		pm.registerEvents(new ArmorListener(Options.armorEquipBlockedMaterials), this);
		pm.registerEvents(new ArmorModifierListener(), this);
	}

	private boolean setupEconomy() {
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		economy = rsp.getProvider();
		return true;
	}

	public static Economy getEconomy() {
		return economy;
	}

}
