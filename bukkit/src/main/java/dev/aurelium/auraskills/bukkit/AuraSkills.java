package dev.aurelium.auraskills.bukkit;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.PaperCommandManager;
import com.archyx.slate.Slate;
import com.archyx.slate.menu.MenuManager;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.bukkit.ability.BukkitAbilityManager;
import dev.aurelium.auraskills.bukkit.commands.CommandRegistrar;
import dev.aurelium.auraskills.bukkit.config.BukkitConfigProvider;
import dev.aurelium.auraskills.bukkit.item.BukkitItemRegistry;
import dev.aurelium.auraskills.bukkit.level.BukkitLevelManager;
import dev.aurelium.auraskills.bukkit.listeners.DamageListener;
import dev.aurelium.auraskills.bukkit.listeners.PlayerJoinQuit;
import dev.aurelium.auraskills.bukkit.logging.BukkitLogger;
import dev.aurelium.auraskills.bukkit.loot.LootTableManager;
import dev.aurelium.auraskills.bukkit.loot.handler.BlockLootHandler;
import dev.aurelium.auraskills.bukkit.loot.handler.FishingLootHandler;
import dev.aurelium.auraskills.bukkit.mana.BukkitManaAbilityManager;
import dev.aurelium.auraskills.bukkit.menus.MenuFileManager;
import dev.aurelium.auraskills.bukkit.menus.MenuRegistrar;
import dev.aurelium.auraskills.bukkit.modifier.ArmorModifierListener;
import dev.aurelium.auraskills.bukkit.modifier.ItemListener;
import dev.aurelium.auraskills.bukkit.modifier.ModifierManager;
import dev.aurelium.auraskills.bukkit.region.BukkitWorldManager;
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
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.event.AuraSkillsEventManager;
import dev.aurelium.auraskills.common.hooks.HookManager;
import dev.aurelium.auraskills.common.leaderboard.LeaderboardManager;
import dev.aurelium.auraskills.common.level.XpRequirements;
import dev.aurelium.auraskills.common.mana.ManaAbilityRegistry;
import dev.aurelium.auraskills.common.message.MessageKey;
import dev.aurelium.auraskills.common.message.MessageProvider;
import dev.aurelium.auraskills.common.message.PlatformLogger;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
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

    private AuraSkillsApi api;
    private BukkitConfigProvider configProvider;
    private MessageProvider messageProvider;
    private SkillManager skillManager;
    private BukkitAbilityManager abilityManager;
    private BukkitManaAbilityManager manaAbilityManager;
    private StatManager statManager;
    private BukkitTraitManager traitManager;
    private SkillRegistry skillRegistry;
    private StatRegistry statRegistry;
    private TraitRegistry traitRegistry;
    private AbilityRegistry abilityRegistry;
    private ManaAbilityRegistry manaAbilityRegistry;
    private BukkitItemRegistry itemRegistry;
    private BukkitLevelManager levelManager;
    private BukkitUserManager userManager;
    private XpRequirements xpRequirements;
    private AuraSkillsEventManager eventManager;
    private PlatformLogger logger;
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
    private boolean nbtApiEnabled;

    @Override
    public void onEnable() {
        registerApi();
        logger = new BukkitLogger(this);
        audiences = BukkitAudiences.create(this);
        eventManager = new AuraSkillsEventManager(this);
        // Load config.yml file
        configProvider = new BukkitConfigProvider(this);
        configProvider.loadOptions();
        // Load messages
        messageProvider = new MessageProvider(this);
        messageProvider.loadMessages();
        // Load blocked/disabled worlds lists
        worldManager = new BukkitWorldManager(this);
        worldManager.loadWorlds(getConfig());
        // Create scheduler
        scheduler = new BukkitScheduler(this);

        // Init managers
        skillManager = new SkillManager(this);
        abilityManager = new BukkitAbilityManager(this);
        manaAbilityManager = new BukkitManaAbilityManager(this);
        statManager = new BukkitStatManager(this);
        traitManager = new BukkitTraitManager(this);
        regionManager = new RegionManager(this);

        // Init registries
        skillRegistry = new SkillRegistry(this);
        statRegistry = new StatRegistry(this);
        traitRegistry = new TraitRegistry(this);
        abilityRegistry = new AbilityRegistry(this);
        manaAbilityRegistry = new ManaAbilityRegistry(this);
        itemRegistry = new BukkitItemRegistry(this);

        // Load skills, stats
        loadSkills();

        // Register default traits
        traitManager.registerTraitImplementations();

        xpRequirements = new XpRequirements(this);
        userManager = new BukkitUserManager(this);
        backupProvider = new BackupProvider(this);
        hookManager = new HookManager();
        levelManager = new BukkitLevelManager(this);
        leaderboardManager = new LeaderboardManager(this);
        uiProvider = new BukkitUiProvider(this);
        uiProvider.getBossBarManager().loadOptions();
        modifierManager = new ModifierManager(this);
        requirementManager = new RequirementManager(this);

        // Load rewards
        rewardManager = new BukkitRewardManager(this);
        rewardManager.loadRewards();

        // Loads loot
        lootTableManager = new LootTableManager(this);
        lootTableManager.loadLootTables();

        initStorageProvider();

        levelManager.registerLevelers(); // Register levelers for skills
        registerEvents();
        commandManager = new CommandRegistrar(this).registerCommands();
        registerAndLoadMenus();

        // Update leaderboards
        leaderboardManager.updateLeaderboards();

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
        // Save users
        for (User user : userManager.getUserMap().values()) {
            try {
                storageProvider.save(user);
            } catch (Exception e) {
                logger().warn("Failed to save user " + user.getUsername());
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

    public void loadSkills() {
        SkillLoader skillLoader = new SkillLoader(this);
        skillLoader.loadSkills();
        StatLoader statLoader = new StatLoader(this);
        statLoader.loadStats();
    }

    private void registerApi() {
        this.api = new ApiAuraSkills(this);
        ApiRegistrationUtil.register(api);
    }

    private void registerAndLoadMenus() {
        slate = new Slate(this);
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

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinQuit(this), this);
        pm.registerEvents(new DamageListener(this), this);
        pm.registerEvents(new BlockLootHandler(this), this);
        pm.registerEvents(new FishingLootHandler(this), this);
        pm.registerEvents(new RequirementListener(this), this);
        pm.registerEvents(new ItemListener(this), this);
        pm.registerEvents(new ArmorListener(configStringList(Option.MODIFIER_ARMOR_EQUIP_BLOCKED_MATERIALS)), this);
        pm.registerEvents(new ArmorModifierListener(this), this);
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
    public AuraSkillsEventManager getEventManager() {
        return eventManager;
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

    public boolean isNbtApiEnabled() {
        return nbtApiEnabled;
    }
}
