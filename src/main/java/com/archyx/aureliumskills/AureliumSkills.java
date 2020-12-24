package com.archyx.aureliumskills;

import co.aikar.commands.PaperCommandManager;
import com.archyx.aureliumskills.abilities.*;
import com.archyx.aureliumskills.commands.ManaCommand;
import com.archyx.aureliumskills.commands.SkillCommands;
import com.archyx.aureliumskills.commands.SkillsCommand;
import com.archyx.aureliumskills.commands.StatsCommand;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.CommandMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.listeners.CheckBlockReplace;
import com.archyx.aureliumskills.listeners.DamageListener;
import com.archyx.aureliumskills.listeners.PlayerJoinQuit;
import com.archyx.aureliumskills.loot.LootTableManager;
import com.archyx.aureliumskills.mana.ManaAbilityManager;
import com.archyx.aureliumskills.mana.ManaManager;
import com.archyx.aureliumskills.menu.MenuLoader;
import com.archyx.aureliumskills.modifier.ArmorModifierListener;
import com.archyx.aureliumskills.modifier.ItemListener;
import com.archyx.aureliumskills.modifier.ModifierManager;
import com.archyx.aureliumskills.requirement.ArmorRequirementListener;
import com.archyx.aureliumskills.requirement.ItemRequirementListener;
import com.archyx.aureliumskills.requirement.RequirementManager;
import com.archyx.aureliumskills.skills.*;
import com.archyx.aureliumskills.skills.levelers.*;
import com.archyx.aureliumskills.stats.*;
import com.archyx.aureliumskills.util.*;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import fr.minuskube.inv.InventoryManager;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AureliumSkills extends JavaPlugin {

	private SkillLoader skillLoader;
	public MySqlSupport mySqlSupport;
	private static MenuLoader menuLoader;
	public static LootTableManager lootTableManager;
	public static InventoryManager invManager;
	public static AbilityManager abilityManager;
	public static WorldGuardSupport worldGuardSupport;
	public static WorldManager worldManager;
	public static ManaManager manaManager;
	public static ManaAbilityManager manaAbilityManager;
	public static boolean holographicDisplaysEnabled;
	public static boolean worldGuardEnabled;
	public static boolean placeholderAPIEnabled;
	public static boolean vaultEnabled;
	public static boolean protocolLibEnabled;
	public static boolean mythicMobsEnabled;
	public static Leaderboard leaderboard;
	private static Economy economy = null;
	private OptionL optionLoader;
	private PaperCommandManager commandManager;
	private ActionBar actionBar;
	private SkillBossBar bossBar;
	private SourceManager sourceManager;
	private SorceryLeveler sorceryLeveler;
	private CheckBlockReplace checkBlockReplace;
	private RequirementManager requirementManager;
	private ModifierManager modifierManager;
	private Lang lang;
	public static long releaseTime = 1607886993234L;

	public void onEnable() {
		invManager = new InventoryManager(this);
		invManager.init();
		// Checks for world guard
		if (getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
			if (WorldGuardPlugin.inst().getDescription().getVersion().contains("7.0")) {
				worldGuardEnabled = true;
				worldGuardSupport = new WorldGuardSupport(this);
				worldGuardSupport.loadRegions();
			}
		}
		else {
			worldGuardEnabled = false;
		}
		// Checks for PlaceholderAPI
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			new PlaceholderSupport(this).register();
			placeholderAPIEnabled = true;
			Bukkit.getLogger().info("[AureliumSkills] PlaceholderAPI Support Enabled!");
		}
		else {
			placeholderAPIEnabled = false;
		}
		// Checks for Vault
		if (setupEconomy()) {
			vaultEnabled = true;
			Bukkit.getLogger().info("[AureliumSkills] Vault Support Enabled!");
		}
		else {
			vaultEnabled = false;
		}
		// Check for protocol lib
		protocolLibEnabled = Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");
		// Load config
		loadConfig();
		// Load config
		this.requirementManager = new RequirementManager(this);
		optionLoader = new OptionL(this);
		optionLoader.loadOptions();
		requirementManager.load();
		this.modifierManager = new ModifierManager(this);
		// Load sources
		sourceManager = new SourceManager(this);
		sourceManager.loadSources();
		// Check for MythicMobs
		if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
			mythicMobsEnabled = true;
			Bukkit.getPluginManager().registerEvents(new MythicMobsSupport(this), this);
			Bukkit.getLogger().info("[AureliumSkills] MythicMobs Support Enabled!");
		} else {
			mythicMobsEnabled = false;
		}
		// Load boss bar
		bossBar = new SkillBossBar(this);
		bossBar.loadOptions();
		// Checks for holographic displays
		if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
			holographicDisplaysEnabled = true;
			getServer().getPluginManager().registerEvents(new HologramSupport(this), this);
			Bukkit.getLogger().info("[AureliumSkills] HolographicDisplays Support Enabled!");
		}
		else {
			holographicDisplaysEnabled = false;
		}
		// Registers Commands
		registerCommands();
		// Load languages
		lang = new Lang(this);
		getServer().getPluginManager().registerEvents(lang, this);
		lang.init();
		lang.loadEmbeddedMessages(commandManager);
		lang.loadLanguages(commandManager);
		// Load menu
		menuLoader = new MenuLoader(this);
		try {
			menuLoader.load();
		}
		catch (IllegalAccessException|InstantiationException e) {
			e.printStackTrace();
			Bukkit.getLogger().warning("[AureliumSkills] Error loading menus!");
		}
		// Load leaderboard
		leaderboard = new Leaderboard(this);
		// Registers events
		registerEvents();
		// Load ability manager
		manaAbilityManager = new ManaAbilityManager(this);
		getServer().getPluginManager().registerEvents(manaAbilityManager, this);
		manaAbilityManager.init();
		// Load ability options
		abilityManager = new AbilityManager(this);
		abilityManager.loadOptions();
		// Load stats
		Regeneration regeneration = new Regeneration(this);
		getServer().getPluginManager().registerEvents(regeneration, this);
		regeneration.startRegen();
		regeneration.startSaturationRegen();
		EnduranceLeveler enduranceLeveler = new EnduranceLeveler(this);
		enduranceLeveler.startTracking();
		// Load mana manager
		manaManager = new ManaManager(this);
		getServer().getPluginManager().registerEvents(manaManager, this);
		manaManager.startRegen();
		// Load Action Bar
		actionBar = new ActionBar(this);
		if (protocolLibEnabled) {
			ProtocolUtil.init();
		}
		actionBar.startUpdateActionBar();
		// Load Data
		skillLoader = new SkillLoader(this);
		if (OptionL.getBoolean(Option.MYSQL_ENABLED)) {
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
			skillLoader.loadSkillData();
			skillLoader.startSaving();
		}
		// Load leveler
		Leveler.plugin = this;
		Leveler.loadLevelReqs();
		// Load loot tables
		lootTableManager = new LootTableManager(this);
		// Load world manager
		worldManager = new WorldManager(this);
		worldManager.loadWorlds();
		// B-stats
		int pluginId = 8629;
		new Metrics(this, pluginId);
		Bukkit.getLogger().info("[AureliumSkills] Aurelium Skills has been enabled");
		if (System.currentTimeMillis() > releaseTime + 21600000L) {
			checkUpdates();
		}
	}
	
	public void onDisable() {
		File file = new File(this.getDataFolder(), "config.yml");
		if (file.exists()) {
			// Reloads config
			reloadConfig();
			// Save config
			saveConfig();
		}
		// Save Data
		if (OptionL.getBoolean(Option.MYSQL_ENABLED)) {
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
		// Check for updates
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
		commandManager = new PaperCommandManager(this);
		commandManager.enableUnstableAPI("help");
		commandManager.usePerIssuerLocale(true, false);
		commandManager.getCommandCompletions().registerAsyncCompletion("skills", c -> {
			List<String> values = new ArrayList<>();
			for (Skill skill : Skill.values()) {
				if (OptionL.isEnabled(skill)) {
					values.add(skill.toString().toLowerCase());
				}
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
		commandManager.getCommandCompletions().registerAsyncCompletion("lang", c -> Lang.getDefinedLanguagesSet());
		commandManager.getCommandCompletions().registerAsyncCompletion("modifiers", c -> {
			Player player = c.getPlayer();
			PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
			if (playerStat != null) {
				return playerStat.getModifiers().keySet();
			}
			return null;
		});
		commandManager.registerCommand(new SkillsCommand(this));
		commandManager.registerCommand(new StatsCommand());
		commandManager.registerCommand(new ManaCommand());
		if (OptionL.getBoolean(Option.ENABLE_SKILL_COMMANDS)) {
			if (OptionL.isEnabled(Skill.FARMING)) { commandManager.registerCommand(new SkillCommands.FarmingCommand()); }
			if (OptionL.isEnabled(Skill.FORAGING)) { commandManager.registerCommand(new SkillCommands.ForagingCommand()); }
			if (OptionL.isEnabled(Skill.MINING)) { commandManager.registerCommand(new SkillCommands.MiningCommand()); }
			if (OptionL.isEnabled(Skill.FISHING)) { commandManager.registerCommand(new SkillCommands.FishingCommand()); }
			if (OptionL.isEnabled(Skill.EXCAVATION)) { commandManager.registerCommand(new SkillCommands.ExcavationCommand()); }
			if (OptionL.isEnabled(Skill.ARCHERY)) { commandManager.registerCommand(new SkillCommands.ArcheryCommand()); }
			if (OptionL.isEnabled(Skill.DEFENSE)) { commandManager.registerCommand(new SkillCommands.DefenseCommand()); }
			if (OptionL.isEnabled(Skill.FIGHTING)) { commandManager.registerCommand(new SkillCommands.FightingCommand()); }
			if (OptionL.isEnabled(Skill.ENDURANCE)) { commandManager.registerCommand(new SkillCommands.EnduranceCommand()); }
			if (OptionL.isEnabled(Skill.AGILITY)) { commandManager.registerCommand(new SkillCommands.AgilityCommand()); }
			if (OptionL.isEnabled(Skill.ALCHEMY)) { commandManager.registerCommand(new SkillCommands.AlchemyCommand()); }
			if (OptionL.isEnabled(Skill.ENCHANTING)) { commandManager.registerCommand(new SkillCommands.EnchantingCommand()); }
			if (OptionL.isEnabled(Skill.SORCERY)) { commandManager.registerCommand(new SkillCommands.SorceryCommand()); }
			if (OptionL.isEnabled(Skill.HEALING)) { commandManager.registerCommand(new SkillCommands.HealingCommand()); }
			if (OptionL.isEnabled(Skill.FORGING)) { commandManager.registerCommand(new SkillCommands.ForgingCommand()); }
		}
	}

	public void registerEvents() {
		// Registers Events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerJoinQuit(this), this);
		checkBlockReplace = new CheckBlockReplace(this);
		pm.registerEvents(checkBlockReplace, this);
		pm.registerEvents(new FarmingLeveler(this), this);
		pm.registerEvents(new ForagingLeveler(this), this);
		pm.registerEvents(new MiningLeveler(this), this);
		pm.registerEvents(new ExcavationLeveler(this), this);
		pm.registerEvents(new FishingLeveler(this), this);
		pm.registerEvents(new FightingLeveler(this), this);
		pm.registerEvents(new ArcheryLeveler(this), this);
		pm.registerEvents(new DefenseLeveler(this), this);
		pm.registerEvents(new AgilityLeveler(this), this);
		pm.registerEvents(new AlchemyLeveler(this), this);
		pm.registerEvents(new EnchantingLeveler(this), this);
		sorceryLeveler = new SorceryLeveler(this);
		pm.registerEvents(new HealingLeveler(this), this);
		pm.registerEvents(new ForgingLeveler(this), this);
		pm.registerEvents(new Health(this), this);
		pm.registerEvents(new Luck(), this);
		pm.registerEvents(new Wisdom(), this);
		pm.registerEvents(new FarmingAbilities(this), this);
		pm.registerEvents(new ForagingAbilities(this), this);
		pm.registerEvents(new MiningAbilities(this), this);
		pm.registerEvents(new FishingAbilities(this), this);
		pm.registerEvents(new ExcavationAbilities(this), this);
		pm.registerEvents(new ArcheryAbilities(this), this);
		pm.registerEvents(new DefenseAbilities(this), this);
		pm.registerEvents(new FightingAbilities(this), this);
		pm.registerEvents(new EnduranceAbilities(this), this);
		pm.registerEvents(new AgilityAbilities(this), this);
		pm.registerEvents(new AlchemyAbilities(this), this);
		pm.registerEvents(new EnchantingAbilities(this), this);
		pm.registerEvents(new DamageListener(this), this);
		ItemListener itemListener = new ItemListener(this);
		pm.registerEvents(itemListener, this);
		itemListener.scheduleTask();
		pm.registerEvents(new ArmorListener(OptionL.getList(Option.MODIFIER_ARMOR_EQUIP_BLOCKED_MATERIALS)), this);
		pm.registerEvents(new ArmorModifierListener(requirementManager), this);
		ItemRequirementListener itemRequirementListener = new ItemRequirementListener(requirementManager);
		pm.registerEvents(itemRequirementListener, this);
		pm.registerEvents(new ArmorRequirementListener(requirementManager), this);
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

	public static MenuLoader getMenuLoader() {
		return menuLoader;
	}

	public PaperCommandManager getCommandManager() {
		return commandManager;
	}

	public static String getPrefix(Locale locale) {
		return Lang.getMessage(CommandMessage.PREFIX, locale);
	}

	public ActionBar getActionBar() {
		return actionBar;
	}

	public SkillBossBar getBossBar() {
		return bossBar;
	}

	public SourceManager getSourceManager() {
		return sourceManager;
	}

	public SorceryLeveler getSorceryLeveler() {
		return sorceryLeveler;
	}

	public CheckBlockReplace getCheckBlockReplace() {
		return checkBlockReplace;
	}

	public RequirementManager getRequirementManager() {
		return requirementManager;
	}

	public OptionL getOptionLoader() {
		return optionLoader;
	}

	public ModifierManager getModifierManager() {
		return modifierManager;
	}

	public Lang getLang() {
		return lang;
	}

}
