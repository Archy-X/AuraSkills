package dev.aurelium.auraskills.bukkit;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.PaperCommandManager;
import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.slate.Slate;
import com.archyx.slate.menu.MenuManager;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.item.ItemManager;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.bukkit.ability.BukkitAbilityManager;
import dev.aurelium.auraskills.bukkit.commands.CommandRegistrar;
import dev.aurelium.auraskills.bukkit.config.BukkitConfigProvider;
import dev.aurelium.auraskills.bukkit.event.BukkitEventHandler;
import dev.aurelium.auraskills.bukkit.item.BukkitItemRegistry;
import dev.aurelium.auraskills.bukkit.item.ItemManagerImpl;
import dev.aurelium.auraskills.bukkit.level.BukkitLevelManager;
import dev.aurelium.auraskills.bukkit.listeners.DamageListener;
import dev.aurelium.auraskills.bukkit.listeners.PlayerDeath;
import dev.aurelium.auraskills.bukkit.listeners.PlayerJoinQuit;
import dev.aurelium.auraskills.bukkit.logging.BukkitLogger;
import dev.aurelium.auraskills.bukkit.loot.LootTableManager;
import dev.aurelium.auraskills.bukkit.loot.handler.BlockLootHandler;
import dev.aurelium.auraskills.bukkit.loot.handler.FishingLootHandler;
import dev.aurelium.auraskills.bukkit.mana.BukkitManaAbilityManager;
import dev.aurelium.auraskills.bukkit.menus.MenuFileManager;
import dev.aurelium.auraskills.bukkit.menus.MenuRegistrar;
import dev.aurelium.auraskills.bukkit.menus.SlateMenuHelper;
import dev.aurelium.auraskills.bukkit.modifier.ArmorModifierListener;
import dev.aurelium.auraskills.bukkit.modifier.ItemListener;
import dev.aurelium.auraskills.bukkit.modifier.ModifierManager;
import dev.aurelium.auraskills.bukkit.region.BukkitWorldManager;
import dev.aurelium.auraskills.bukkit.region.RegionBlockListener;
import dev.aurelium.auraskills.bukkit.region.RegionManager;
import dev.aurelium.auraskills.bukkit.requirement.RequirementListener;
import dev.aurelium.auraskills.bukkit.requirement.RequirementManager;
import dev.aurelium.auraskills.bukkit.reward.BukkitRewardManager;
import dev.aurelium.auraskills.bukkit.scheduler.BukkitScheduler;
import dev.aurelium.auraskills.bukkit.skills.agility.AgilityAbilities;
import dev.aurelium.auraskills.bukkit.stat.BukkitStatManager;
import dev.aurelium.auraskills.bukkit.trait.BukkitTraitManager;
import dev.aurelium.auraskills.bukkit.ui.BukkitUiProvider;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.bukkit.user.BukkitUserManager;
import dev.aurelium.auraskills.bukkit.util.armor.ArmorListener;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.ability.AbilityRegistry;
import dev.aurelium.auraskills.common.api.ApiAuraSkills;
import dev.aurelium.auraskills.common.api.ApiRegistrationUtil;
import dev.aurelium.auraskills.common.config.ConfigurateLoader;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.event.EventHandler;
import dev.aurelium.auraskills.common.hooks.HookManager;
import dev.aurelium.auraskills.common.leaderboard.LeaderboardManager;
import dev.aurelium.auraskills.common.level.XpRequirements;
import dev.aurelium.auraskills.common.mana.ManaAbilityRegistry;
import dev.aurelium.auraskills.common.menu.MenuHelper;
import dev.aurelium.auraskills.common.message.MessageKey;
import dev.aurelium.auraskills.common.message.MessageProvider;
import dev.aurelium.auraskills.common.message.PlatformLogger;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.migration.MigrationManager;
import dev.aurelium.auraskills.common.reward.RewardManager;
import dev.aurelium.auraskills.common.scheduler.Scheduler;
import dev.aurelium.auraskills.common.skill.SkillLoader;
import dev.aurelium.auraskills.common.skill.SkillManager;
import dev.aurelium.auraskills.common.skill.SkillRegistry;
import dev.aurelium.auraskills.common.stat.StatLoader;
import dev.aurelium.auraskills.common.stat.StatManager;
import dev.aurelium.auraskills.common.stat.StatRegistry;
import dev.aurelium.auraskills.common.storage.StorageProvider;
import dev.aurelium.auraskills.common.storage.backup.BackupProvider;
import dev.aurelium.auraskills.common.storage.file.FileStorageProvider;
import dev.aurelium.auraskills.common.storage.sql.DatabaseCredentials;
import dev.aurelium.auraskills.common.storage.sql.SqlStorageProvider;
import dev.aurelium.auraskills.common.storage.sql.pool.ConnectionPool;
import dev.aurelium.auraskills.common.storage.sql.pool.MySqlConnectionPool;
import dev.aurelium.auraskills.common.trait.TraitRegistry;
import dev.aurelium.auraskills.common.user.User;
import fr.minuskube.inv.InventoryManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.Locale;

public class AuraSkills extends JavaPlugin implements AuraSkillsPlugin {

    private final AuraSkillsApi api;
    private final SkillManager skillManager;
    private final BukkitAbilityManager abilityManager;
    private final BukkitManaAbilityManager manaAbilityManager;
    private final StatManager statManager;
    private final BukkitTraitManager traitManager;
    private final SkillRegistry skillRegistry;
    private final StatRegistry statRegistry;
    private final TraitRegistry traitRegistry;
    private final AbilityRegistry abilityRegistry;
    private final ManaAbilityRegistry manaAbilityRegistry;
    private final BukkitItemRegistry itemRegistry;
    private final PlatformLogger logger;
    private final MessageProvider messageProvider;
    private BukkitConfigProvider configProvider;
    private BukkitLevelManager levelManager;
    private BukkitUserManager userManager;
    private XpRequirements xpRequirements;
    private HookManager hookManager;
    private LeaderboardManager leaderboardManager;
    private BukkitUiProvider uiProvider;
    private RewardManager rewardManager;
    private Scheduler scheduler;
    private StorageProvider storageProvider;
    private Slate slate;
    private MenuFileManager menuFileManager;
    private PaperCommandManager commandManager;
    private BukkitAudiences audiences;
    private RegionManager regionManager;
    private BukkitWorldManager worldManager;
    private LootTableManager lootTableManager;
    private ModifierManager modifierManager;
    private RequirementManager requirementManager;
    private BackupProvider backupProvider;
    private InventoryManager inventoryManager;
    private MenuHelper menuHelper;
    private EventHandler eventHandler;
    private ItemManager itemManager;
    private boolean nbtApiEnabled;

    public AuraSkills() {
        this.api = new ApiAuraSkills(this);
        ApiRegistrationUtil.register(api);
        logger = new BukkitLogger(this);
        // Load messages
        messageProvider = new MessageProvider(this);
        messageProvider.loadMessages();
        // Init managers
        skillManager = new SkillManager(this);
        abilityManager = new BukkitAbilityManager(this);
        manaAbilityManager = new BukkitManaAbilityManager(this);
        statManager = new BukkitStatManager(this);
        traitManager = new BukkitTraitManager(this);

        // Init registries
        skillRegistry = new SkillRegistry(this);
        statRegistry = new StatRegistry(this);
        traitRegistry = new TraitRegistry(this);
        abilityRegistry = new AbilityRegistry(this);
        manaAbilityRegistry = new ManaAbilityRegistry(this);
        itemRegistry = new BukkitItemRegistry(this);
    }

    @Override
    public void onEnable() {
        audiences = BukkitAudiences.create(this);
        eventHandler = new BukkitEventHandler(this);
        hookManager = new HookManager();
        generateConfigs(); // Generate default config files if missing
        // Handle migration
        MigrationManager migrationManager = new MigrationManager(this);
        migrationManager.attemptMigration();
        // Load config.yml file
        configProvider = new BukkitConfigProvider(this);
        configProvider.loadOptions();
        // Load blocked/disabled worlds lists
        worldManager = new BukkitWorldManager(this);
        worldManager.loadWorlds(getConfig());
        // Create scheduler
        scheduler = new BukkitScheduler(this);
        regionManager = new RegionManager(this);
        userManager = new BukkitUserManager(this);
        backupProvider = new BackupProvider(this);
        xpRequirements = new XpRequirements(this);
        leaderboardManager = new LeaderboardManager(this);
        uiProvider = new BukkitUiProvider(this);
        modifierManager = new ModifierManager(this);
        inventoryManager = new InventoryManager(this);
        inventoryManager.init();
        rewardManager = new BukkitRewardManager(this); // Loaded later
        lootTableManager = new LootTableManager(this); // Loaded later
        commandManager = new CommandRegistrar(this).registerCommands();
        levelManager = new BukkitLevelManager(this);
        itemManager = new ItemManagerImpl(this);
        initStorageProvider();
        leaderboardManager.updateLeaderboards(); // Schedules async task
        registerPriorityEvents();
        AureliumAPI.setPlugin(this); // Initialize legacy API

        // Stuff to be run on the first tick
        scheduler.executeSync(() -> {
            loadSkills(); // Load skills, stats, abilities, etc from configs
            levelManager.registerLevelers(); // Requires skills loaded
            levelManager.loadXpRequirements(); // Requires skills loaded
            uiProvider.getBossBarManager().loadOptions(); // Requires skills registered
            requirementManager = new RequirementManager(this); // Requires skills registered
            rewardManager.loadRewards(); // Requires skills loaded
            lootTableManager.loadLootTables(); // Requires skills registered
            // Register default content
            abilityManager.registerAbilityImplementations();
            manaAbilityManager.registerProviders();
            traitManager.registerTraitImplementations();
            registerEvents();
            registerAndLoadMenus();
            // Tell threads that the plugin has finished loading
            AuraSkills plugin = getInstance();
            synchronized (plugin) {
                plugin.notifyAll();
            }
        });

        // Check if NBT API is supported for the version
        if (MinecraftVersion.getVersion() == MinecraftVersion.UNKNOWN) {
            getLogger().warning("NBT API is not yet supported for your Minecraft version, item modifier, requirement, and some other functionality is disabled!");
            nbtApiEnabled = false;
        } else {
            nbtApiEnabled = true;
        }
    }

    @Override
    public void onDisable() {
        scheduler.shutdown();
        // Save users
        for (User user : userManager.getUserMap().values()) {
            try {
                storageProvider.save(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        userManager.getUserMap().clear();
        // Shut down connection pool
        if (storageProvider instanceof SqlStorageProvider sqlStorageProvider) {
            sqlStorageProvider.getPool().disable();
        }
        // Remove fleeting
        var agilityAbilities = getAbilityManager().getAbilityImpl(AgilityAbilities.class);
        for (Player player : Bukkit.getOnlinePlayers()) {
            agilityAbilities.removeFleetingQuit(player);
        }
    }

    public void generateConfigs() {
        ConfigurateLoader loader = new ConfigurateLoader(this, null);
        loader.generateUserFile("config.yml");
        loader.generateUserFile("skills.yml");
        loader.generateUserFile("stats.yml");
        loader.generateUserFile("traits.yml");
        loader.generateUserFile("abilities.yml");
        loader.generateUserFile("mana_abilities.yml");
        loader.generateUserFile("xp_requirements.yml");
        for (Skill skill : Skills.values()) {
            String sources = "sources/" + skill.name().toLowerCase(Locale.ROOT) + ".yml";
            loader.generateUserFile(sources);
        }
    }

    public void loadSkills() {
        SkillLoader skillLoader = new SkillLoader(this);
        skillLoader.loadSkills();
        StatLoader statLoader = new StatLoader(this);
        statLoader.loadStats();
    }

    private void registerAndLoadMenus() {
        slate = new Slate(this);
        menuHelper = new SlateMenuHelper(slate);
        new MenuRegistrar(this).register();
        menuFileManager = new MenuFileManager(this);
        menuFileManager.generateDefaultFiles();
        menuFileManager.loadMenus();
    }

    private void initStorageProvider() {
        // MySQL storage
        if (configBoolean(Option.MYSQL_ENABLED)) {
            DatabaseCredentials credentials = new DatabaseCredentials(
                    configString(Option.MYSQL_HOST),
                    configInt(Option.MYSQL_PORT),
                    configString(Option.MYSQL_DATABASE),
                    configString(Option.MYSQL_USERNAME),
                    configString(Option.MYSQL_PASSWORD),
                    configBoolean(Option.MYSQL_SSL)
            );
            ConnectionPool connectionPool = new MySqlConnectionPool(credentials);
            connectionPool.enable();
            storageProvider = new SqlStorageProvider(this, connectionPool);
        } else { // File storage
            storageProvider = new FileStorageProvider(this, getDataFolder().getPath() + "/userdata");
        }
    }

    private void registerPriorityEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinQuit(this), this);
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new DamageListener(this), this);
        pm.registerEvents(new BlockLootHandler(this), this);
        pm.registerEvents(new FishingLootHandler(this), this);
        pm.registerEvents(new RequirementListener(this), this);
        pm.registerEvents(new ItemListener(this), this);
        pm.registerEvents(new ArmorListener(configStringList(Option.MODIFIER_ARMOR_EQUIP_BLOCKED_MATERIALS)), this);
        pm.registerEvents(new ArmorModifierListener(this), this);
        pm.registerEvents(new RegionBlockListener(this), this);
        pm.registerEvents(new PlayerDeath(this), this);
    }

    public BukkitAudiences getAudiences() {
        return audiences;
    }

    public Slate getSlate() {
        return slate;
    }

    public MenuFileManager getMenuFileManager() {
        return menuFileManager;
    }

    public MenuManager getMenuManager() {
        return slate.getMenuManager();
    }

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }

    public LootTableManager getLootTableManager() {
        return lootTableManager;
    }

    public ModifierManager getModifierManager() {
        return modifierManager;
    }

    public RequirementManager getRequirementManager() {
        return requirementManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public int getResourceId() {
        return 81069;
    }

    @Override
    public AuraSkillsApi getApi() {
        return api;
    }

    @Override
    public MessageProvider getMessageProvider() {
        return messageProvider;
    }

    @Override
    public BukkitConfigProvider config() {
        return configProvider;
    }

    @Override
    public BukkitAbilityManager getAbilityManager() {
        return abilityManager;
    }

    @Override
    public BukkitManaAbilityManager getManaAbilityManager() {
        return manaAbilityManager;
    }

    @Override
    public StatManager getStatManager() {
        return statManager;
    }

    @Override
    public BukkitItemRegistry getItemRegistry() {
        return itemRegistry;
    }

    @Override
    public BukkitLevelManager getLevelManager() {
        return levelManager;
    }

    @Override
    public BukkitUserManager getUserManager() {
        return userManager;
    }

    @NotNull
    public User getUser(Player player) {
        return userManager.getUser(player);
    }

    @Override
    public XpRequirements getXpRequirements() {
        return xpRequirements;
    }

    @Override
    public PlatformLogger logger() {
        return logger;
    }

    @Override
    public SkillManager getSkillManager() {
        return skillManager;
    }

    @Override
    public SkillRegistry getSkillRegistry() {
        return skillRegistry;
    }

    @Override
    public StatRegistry getStatRegistry() {
        return statRegistry;
    }

    @Override
    public TraitRegistry getTraitRegistry() {
        return traitRegistry;
    }

    @Override
    public BukkitTraitManager getTraitManager() {
        return traitManager;
    }

    @Override
    public AbilityRegistry getAbilityRegistry() {
        return abilityRegistry;
    }

    @Override
    public ManaAbilityRegistry getManaAbilityRegistry() {
        return manaAbilityRegistry;
    }

    @Override
    public HookManager getHookManager() {
        return hookManager;
    }

    @Override
    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }

    @Override
    public BukkitUiProvider getUiProvider() {
        return uiProvider;
    }

    @Override
    public RewardManager getRewardManager() {
        return rewardManager;
    }

    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    @Override
    public StorageProvider getStorageProvider() {
        return storageProvider;
    }

    @Override
    public BackupProvider getBackupProvider() {
        return backupProvider;
    }

    @Override
    public BukkitWorldManager getWorldManager() {
        return worldManager;
    }

    @Override
    public MenuHelper getMenuHelper() {
        return menuHelper;
    }

    @Override
    public EventHandler getEventHandler() {
        return eventHandler;
    }

    @Override
    public <T> T getItemManager(Class<T> itemManagerClass) {
        if (itemManagerClass.equals(ItemManager.class)) {
            return itemManagerClass.cast(itemManager);
        }
        throw new IllegalArgumentException("Invalid itemManagerClass");
    }

    @Override
    public String getMsg(MessageKey key, Locale locale) {
        return messageProvider.get(key, locale);
    }

    @Override
    public String getPrefix(Locale locale) {
        return messageProvider.get(CommandMessage.PREFIX, locale);
    }

    @Override
    public void runConsoleCommand(String command) {
        getServer().dispatchCommand(getServer().getConsoleSender(), command);
    }

    @Override
    public void runPlayerCommand(User user, String command) {
        getServer().dispatchCommand(((BukkitUser) user).getPlayer(), command);
    }

    @Override
    public InputStream getResource(@NotNull String path) {
        return super.getResource(path);
    }

    @Override
    public void saveResource(@NotNull String path, boolean replace) {
        super.saveResource(path, replace);
    }

    @Override
    public File getPluginFolder() {
        return this.getDataFolder();
    }

    public Locale getLocale(CommandSender sender) {
        if (sender instanceof Player player) {
            return getUser(player).getLocale();
        } else {
            return messageProvider.getDefaultLanguage();
        }
    }

    public Locale getLocale(CommandIssuer issuer) {
        if (issuer.isPlayer()) {
            User user = userManager.getUser(issuer.getUniqueId());
            return user != null ? user.getLocale() : messageProvider.getDefaultLanguage();
        } else {
            return messageProvider.getDefaultLanguage();
        }
    }

    public boolean isNbtApiDisabled() {
        return !nbtApiEnabled;
    }

    private AuraSkills getInstance() {
        return this;
    }

}
