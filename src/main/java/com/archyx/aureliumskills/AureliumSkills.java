package com.archyx.aureliumskills;

import co.aikar.commands.PaperCommandManager;
import com.archyx.aureliumskills.abilities.*;
import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.commands.ManaCommand;
import com.archyx.aureliumskills.commands.SkillCommands;
import com.archyx.aureliumskills.commands.SkillsCommand;
import com.archyx.aureliumskills.commands.StatsCommand;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.data.PlayerManager;
import com.archyx.aureliumskills.data.backup.BackupProvider;
import com.archyx.aureliumskills.data.backup.LegacyFileBackup;
import com.archyx.aureliumskills.data.backup.MysqlBackup;
import com.archyx.aureliumskills.data.backup.YamlBackup;
import com.archyx.aureliumskills.data.converter.LegacyFileToYamlConverter;
import com.archyx.aureliumskills.data.converter.LegacyMysqlToMysqlConverter;
import com.archyx.aureliumskills.data.storage.MySqlStorageProvider;
import com.archyx.aureliumskills.data.storage.StorageProvider;
import com.archyx.aureliumskills.data.storage.YamlStorageProvider;
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
import com.archyx.aureliumskills.requirement.RequirementListener;
import com.archyx.aureliumskills.requirement.RequirementManager;
import com.archyx.aureliumskills.rewards.RewardException;
import com.archyx.aureliumskills.rewards.RewardManager;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillBossBar;
import com.archyx.aureliumskills.skills.SourceManager;
import com.archyx.aureliumskills.skills.leaderboard.LeaderboardManager;
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
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AureliumSkills extends JavaPlugin {

	private PlayerManager playerManager;
	private StorageProvider storageProvider;
	private BackupProvider backupProvider;
	private MenuLoader menuLoader;
	private LootTableManager lootTableManager;
	private InventoryManager inventoryManager;
	private AbilityManager abilityManager;
	private WorldGuardSupport worldGuardSupport;
	private WorldManager worldManager;
	private ManaManager manaManager;
	private ManaAbilityManager manaAbilityManager;
	private RewardManager rewardManager;
	private boolean holographicDisplaysEnabled;
	private boolean worldGuardEnabled;
	private boolean placeholderAPIEnabled;
	private boolean vaultEnabled;
	private boolean protocolLibEnabled;
	private boolean mythicMobsEnabled;
	private boolean luckPermsEnabled;
	private Economy economy;
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
	private Leveler leveler;
	private Health health;
	private LeaderboardManager leaderboardManager;
	private final long releaseTime = 1612831625310L;

	public void onEnable() {
		inventoryManager = new InventoryManager(this);
		inventoryManager.init();
		AureliumAPI.setPlugin(this);
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
		// Check for LuckPerms
		luckPermsEnabled = Bukkit.getPluginManager().isPluginEnabled("LuckPerms");
		// Load health
		Health health = new Health(this);
		this.health = health;
		getServer().getPluginManager().registerEvents(health, this);
		// Load config
		loadConfig();
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
		commandManager = new PaperCommandManager(this);
		// Load languages
		lang = new Lang(this);
		getServer().getPluginManager().registerEvents(lang, this);
		lang.init();
		lang.loadEmbeddedMessages(commandManager);
		lang.loadLanguages(commandManager);
		// Load rewards
		rewardManager = new RewardManager(this);
		loadRewards();
		// Registers Commands
		registerCommands();
		// Load menu
		menuLoader = new MenuLoader(this);
		try {
			menuLoader.load();
		}
		catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			e.printStackTrace();
			Bukkit.getLogger().warning("[AureliumSkills] Error loading menus!");
		}
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
		// Load Action Bar
		if (protocolLibEnabled) {
			ProtocolUtil.init();
		}
		actionBar.startUpdateActionBar();
		// Initialize storage
		this.playerManager = new PlayerManager();
		this.leaderboardManager = new LeaderboardManager();
		// Set proper storage provider
		if (OptionL.getBoolean(Option.MYSQL_ENABLED)) {
			MySqlStorageProvider mySqlStorageProvider = new MySqlStorageProvider(this);
			mySqlStorageProvider.init();

			MysqlBackup mysqlBackup = new MysqlBackup(this, mySqlStorageProvider);
			if (!mySqlStorageProvider.localeColumnExists()) {
				mysqlBackup.saveBackup(Bukkit.getConsoleSender());
			}

			new LegacyMysqlToMysqlConverter(this, mySqlStorageProvider).convert();
			setStorageProvider(mySqlStorageProvider);
			this.backupProvider = mysqlBackup;
		} else {
			// Try to backup and convert legacy files
			new LegacyFileBackup(this).saveBackup(Bukkit.getConsoleSender());
			new LegacyFileToYamlConverter(this).convert();
			setStorageProvider(new YamlStorageProvider(this));
			this.backupProvider = new YamlBackup(this);
		}
		// Initialize leaderboards
		new BukkitRunnable() {
			@Override
			public void run() {
				if (leaderboardManager.isNotSorting()) {
					storageProvider.updateLeaderboards();
				}
			}
		}.runTaskTimerAsynchronously(this, 0, 12000);
		// Load leveler
		leveler = new Leveler(this);
		leveler.loadLevelRequirements();
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
		for (PlayerData playerData : playerManager.getPlayerDataMap().values()) {
			storageProvider.save(playerData.getPlayer());
		}
		File file = new File(this.getDataFolder(), "config.yml");
		if (file.exists()) {
			// Reloads config
			reloadConfig();
			// Save config
			saveConfig();
		}
	}

	public void checkUpdates() {
		// Check for updates
		new UpdateChecker(this, 81069).getVersion(version -> {
			if (!this.getDescription().getVersion().contains("Pre-Release") && !this.getDescription().getVersion().contains("Build")) {
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

	private void registerCommands() {
		commandManager.enableUnstableAPI("help");
		commandManager.usePerIssuerLocale(true, false);
		commandManager.getCommandCompletions().registerAsyncCompletion("skills", c -> {
			List<String> values = new ArrayList<>();
			for (Skill skill : Skill.values()) {
				if (OptionL.isEnabled(skill)) {
					values.add(skill.toString().toLowerCase(Locale.ENGLISH));
				}
			}
			return values;
		});
		commandManager.getCommandCompletions().registerAsyncCompletion("skillTop", c -> {
			List<String> values = new ArrayList<>();
			for (Skill skill : Skill.values()) {
				if (OptionL.isEnabled(skill)) {
					values.add(skill.toString().toLowerCase(Locale.ENGLISH));
				}
			}
			values.add("average");
			return values;
		});
		commandManager.getCommandCompletions().registerAsyncCompletion("stats", c -> {
			List<String> values = new ArrayList<>();
			for (Stat stat : Stat.values()) {
				values.add(stat.toString().toLowerCase(Locale.ENGLISH));
			}
			return values;
		});
		commandManager.getCommandCompletions().registerAsyncCompletion("lang", c -> Lang.getDefinedLanguagesSet());
		commandManager.getCommandCompletions().registerAsyncCompletion("modifiers", c -> {
			Player player = c.getPlayer();
			PlayerData playerData = getPlayerManager().getPlayerData(player);
			if (playerData != null) {
				return playerData.getStatModifiers().keySet();
			}
			return null;
		});
		commandManager.registerCommand(new SkillsCommand(this));
		commandManager.registerCommand(new StatsCommand(this));
		commandManager.registerCommand(new ManaCommand(this));
		if (OptionL.getBoolean(Option.ENABLE_SKILL_COMMANDS)) {
			if (OptionL.isEnabled(Skill.FARMING)) { commandManager.registerCommand(new SkillCommands.FarmingCommand(this)); }
			if (OptionL.isEnabled(Skill.FORAGING)) { commandManager.registerCommand(new SkillCommands.ForagingCommand(this)); }
			if (OptionL.isEnabled(Skill.MINING)) { commandManager.registerCommand(new SkillCommands.MiningCommand(this)); }
			if (OptionL.isEnabled(Skill.FISHING)) { commandManager.registerCommand(new SkillCommands.FishingCommand(this)); }
			if (OptionL.isEnabled(Skill.EXCAVATION)) { commandManager.registerCommand(new SkillCommands.ExcavationCommand(this)); }
			if (OptionL.isEnabled(Skill.ARCHERY)) { commandManager.registerCommand(new SkillCommands.ArcheryCommand(this)); }
			if (OptionL.isEnabled(Skill.DEFENSE)) { commandManager.registerCommand(new SkillCommands.DefenseCommand(this)); }
			if (OptionL.isEnabled(Skill.FIGHTING)) { commandManager.registerCommand(new SkillCommands.FightingCommand(this)); }
			if (OptionL.isEnabled(Skill.ENDURANCE)) { commandManager.registerCommand(new SkillCommands.EnduranceCommand(this)); }
			if (OptionL.isEnabled(Skill.AGILITY)) { commandManager.registerCommand(new SkillCommands.AgilityCommand(this)); }
			if (OptionL.isEnabled(Skill.ALCHEMY)) { commandManager.registerCommand(new SkillCommands.AlchemyCommand(this)); }
			if (OptionL.isEnabled(Skill.ENCHANTING)) { commandManager.registerCommand(new SkillCommands.EnchantingCommand(this)); }
			if (OptionL.isEnabled(Skill.SORCERY)) { commandManager.registerCommand(new SkillCommands.SorceryCommand(this)); }
			if (OptionL.isEnabled(Skill.HEALING)) { commandManager.registerCommand(new SkillCommands.HealingCommand(this)); }
			if (OptionL.isEnabled(Skill.FORGING)) { commandManager.registerCommand(new SkillCommands.ForgingCommand(this)); }
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
		EnduranceLeveler enduranceLeveler = new EnduranceLeveler(this);
		enduranceLeveler.startTracking();
		pm.registerEvents(enduranceLeveler, this);
		pm.registerEvents(new AgilityLeveler(this), this);
		pm.registerEvents(new AlchemyLeveler(this), this);
		pm.registerEvents(new EnchantingLeveler(this), this);
		sorceryLeveler = new SorceryLeveler(this);
		pm.registerEvents(new HealingLeveler(this), this);
		pm.registerEvents(new ForgingLeveler(this), this);
		pm.registerEvents(new Luck(this), this);
		pm.registerEvents(new Wisdom(this), this);
		pm.registerEvents(new FarmingAbilities(this), this);
		pm.registerEvents(new ForagingAbilities(this), this);
		pm.registerEvents(new MiningAbilities(this), this);
		pm.registerEvents(new FishingAbilities(this), this);
		pm.registerEvents(new ExcavationAbilities(this), this);
		pm.registerEvents(new ArcheryAbilities(this), this);
		DefenseAbilities defenseAbilities = new DefenseAbilities(this);
		pm.registerEvents(defenseAbilities, this);
		FightingAbilities fightingAbilities = new FightingAbilities(this);
		pm.registerEvents(fightingAbilities, this);
		pm.registerEvents(new FightingAbilities(this), this);
		pm.registerEvents(new EnduranceAbilities(this), this);
		pm.registerEvents(new AgilityAbilities(this), this);
		pm.registerEvents(new AlchemyAbilities(this), this);
		pm.registerEvents(new EnchantingAbilities(this), this);
		pm.registerEvents(new HealingAbilities(this), this);
		pm.registerEvents(new ForgingAbilities(this), this);
		pm.registerEvents(new DamageListener(this, defenseAbilities, fightingAbilities), this);
		// Load mana manager
		manaManager = new ManaManager(this);
		getServer().getPluginManager().registerEvents(manaManager, this);
		manaManager.startRegen();
		ItemListener itemListener = new ItemListener(this);
		pm.registerEvents(itemListener, this);
		itemListener.scheduleTask();
		pm.registerEvents(new ArmorListener(OptionL.getList(Option.MODIFIER_ARMOR_EQUIP_BLOCKED_MATERIALS)), this);
		pm.registerEvents(new ArmorModifierListener(this), this);
		pm.registerEvents(new RequirementListener(this), this);
		this.actionBar = new ActionBar(this);
		pm.registerEvents(actionBar, this);
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

	private void loadRewards() {
		try {
			rewardManager.loadRewards();
		} catch (FileNotFoundException e) {
			Bukkit.getLogger().warning("[AureliumSkills] Error while loading rewards: " + e.getMessage());
		} catch (RewardException e) {
			String fileName = e.getFileName();
			String path = e.getPath();
			String message = e.getMessage();
			Bukkit.getLogger().warning("[AureliumSkills] Error while loading rewards file " + fileName + " at path " + path + ": " + message);
		}
	}

	public RewardManager getRewardManager() {
		return rewardManager;
	}

	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	public Economy getEconomy() {
		return economy;
	}

	public MenuLoader getMenuLoader() {
		return menuLoader;
	}

	public LootTableManager getLootTableManager() {
		return lootTableManager;
	}

	public InventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public AbilityManager getAbilityManager() {
		return abilityManager;
	}

	public WorldGuardSupport getWorldGuardSupport() {
		return worldGuardSupport;
	}

	public WorldManager getWorldManager() {
		return worldManager;
	}

	public ManaManager getManaManager() {
		return manaManager;
	}

	public ManaAbilityManager getManaAbilityManager() {
		return manaAbilityManager;
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

	public Leveler getLeveler() {
		return leveler;
	}

	public boolean isHolographicDisplaysEnabled() {
		return holographicDisplaysEnabled;
	}

	public boolean isWorldGuardEnabled() {
		return worldGuardEnabled;
	}

	public void setWorldGuardEnabled(boolean worldGuardEnabled) {
		this.worldGuardEnabled = worldGuardEnabled;
	}

	public boolean isPlaceholderAPIEnabled() {
		return placeholderAPIEnabled;
	}

	public boolean isVaultEnabled() {
		return vaultEnabled;
	}

	public boolean isProtocolLibEnabled() {
		return protocolLibEnabled;
	}

	public boolean isMythicMobsEnabled() {
		return mythicMobsEnabled;
	}

	public boolean isLuckPermsEnabled() {
		return luckPermsEnabled;
	}

	public long getReleaseTime() {
		return releaseTime;
	}

	public Health getHealth() {
		return health;
	}

	public StorageProvider getStorageProvider() {
		return storageProvider;
	}

	public void setStorageProvider(StorageProvider storageProvider) {
		this.storageProvider = storageProvider;
	}

	public BackupProvider getBackupProvider() {
		return backupProvider;
	}

	public LeaderboardManager getLeaderboardManager() {
		return leaderboardManager;
	}

}
