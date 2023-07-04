package dev.aurelium.auraskills.bukkit;

import co.aikar.commands.PaperCommandManager;
import com.archyx.slate.Slate;
import com.archyx.slate.menu.MenuManager;
import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.bukkit.commands.SkillsRootCommand;
import dev.aurelium.auraskills.bukkit.config.BukkitConfigProvider;
import dev.aurelium.auraskills.bukkit.item.BukkitItemRegistry;
import dev.aurelium.auraskills.bukkit.listeners.PlayerJoinQuit;
import dev.aurelium.auraskills.bukkit.logging.BukkitLogger;
import dev.aurelium.auraskills.bukkit.menus.MenuFileManager;
import dev.aurelium.auraskills.bukkit.menus.MenuRegistrar;
import dev.aurelium.auraskills.bukkit.reward.BukkitRewardManager;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.bukkit.user.BukkitUserManager;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.ability.AbilityManager;
import dev.aurelium.auraskills.common.ability.AbilityRegistry;
import dev.aurelium.auraskills.common.api.ApiAuraSkills;
import dev.aurelium.auraskills.common.api.ApiRegistrationUtil;
import dev.aurelium.auraskills.common.config.ConfigProvider;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.event.AuraSkillsEventManager;
import dev.aurelium.auraskills.common.hooks.HookManager;
import dev.aurelium.auraskills.common.leaderboard.LeaderboardManager;
import dev.aurelium.auraskills.common.leveler.Leveler;
import dev.aurelium.auraskills.common.leveler.XpRequirements;
import dev.aurelium.auraskills.common.mana.ManaAbilityManager;
import dev.aurelium.auraskills.common.mana.ManaAbilityRegistry;
import dev.aurelium.auraskills.common.message.MessageKey;
import dev.aurelium.auraskills.common.message.MessageProvider;
import dev.aurelium.auraskills.common.message.PlatformLogger;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.player.User;
import dev.aurelium.auraskills.common.reward.RewardManager;
import dev.aurelium.auraskills.common.scheduler.Scheduler;
import dev.aurelium.auraskills.common.skill.SkillManager;
import dev.aurelium.auraskills.common.skill.SkillRegistry;
import dev.aurelium.auraskills.common.stat.StatManager;
import dev.aurelium.auraskills.common.stat.StatRegistry;
import dev.aurelium.auraskills.common.storage.StorageProvider;
import dev.aurelium.auraskills.common.storage.file.FileStorageProvider;
import dev.aurelium.auraskills.common.storage.sql.DatabaseCredentials;
import dev.aurelium.auraskills.common.storage.sql.SqlStorageProvider;
import dev.aurelium.auraskills.common.storage.sql.pool.ConnectionPool;
import dev.aurelium.auraskills.common.storage.sql.pool.MySqlConnectionPool;
import dev.aurelium.auraskills.common.trait.TraitManager;
import dev.aurelium.auraskills.common.trait.TraitRegistry;
import dev.aurelium.auraskills.common.ui.UiProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
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
    private AbilityManager abilityManager;
    private ManaAbilityManager manaAbilityManager;
    private StatManager statManager;
    private TraitManager traitManager;
    private SkillRegistry skillRegistry;
    private StatRegistry statRegistry;
    private TraitRegistry traitRegistry;
    private AbilityRegistry abilityRegistry;
    private ManaAbilityRegistry manaAbilityRegistry;
    private BukkitItemRegistry itemRegistry;
    private Leveler leveler;
    private BukkitUserManager userManager;
    private XpRequirements xpRequirements;
    private AuraSkillsEventManager eventManager;
    private PlatformLogger logger;
    private HookManager hookManager;
    private LeaderboardManager leaderboardManager;
    private UiProvider uiProvider;
    private RewardManager rewardManager;
    private Scheduler scheduler;
    private StorageProvider storageProvider;
    private Slate slate;
    private MenuFileManager menuFileManager;
    private PaperCommandManager commandManager;
    private BukkitAudiences audiences;

    @Override
    public void onEnable() {
        registerApi();
        logger = new BukkitLogger(this);
        audiences = BukkitAudiences.create(this);
        // Load config.yml file
        configProvider = new BukkitConfigProvider(this);
        configProvider.loadOptions();
        // Loads messages
        messageProvider = new MessageProvider(this);

        // Init managers
        skillManager = new SkillManager(this);
        abilityManager = new AbilityManager(this);
        manaAbilityManager = new ManaAbilityManager(this);
        // TODO StatManager impl
        traitManager = new TraitManager(this);

        // Init registries
        skillRegistry = new SkillRegistry(this);
        statRegistry = new StatRegistry(this);
        traitRegistry = new TraitRegistry(this);
        abilityRegistry = new AbilityRegistry(this);
        manaAbilityRegistry = new ManaAbilityRegistry(this);
        itemRegistry = new BukkitItemRegistry(this);

        // TODO Leveler impl
        userManager = new BukkitUserManager(this);
        xpRequirements = new XpRequirements(this);
        eventManager = new AuraSkillsEventManager(this);
        hookManager = new HookManager();
        leaderboardManager = new LeaderboardManager(this);
        // TODO UiProvider impl

        // Load rewards
        rewardManager = new BukkitRewardManager(this);
        rewardManager.loadRewards();

        // TODO Scheduler impl
        initStorageProvider();

        registerEvents();
        registerCommands();
        registerAndLoadMenus();
    }

    @Override
    public void onDisable() {
        // Shut down connection pool
        if (storageProvider instanceof SqlStorageProvider sqlStorageProvider) {
            sqlStorageProvider.getPool().disable();
        }
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

    private void registerCommands() {
        commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");
        commandManager.usePerIssuerLocale(true, false);
        commandManager.getCommandReplacements().addReplacement("skills_alias", "skills|sk|skill");
        commandManager.registerCommand(new SkillsRootCommand(this));
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinQuit(this), this);
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

    @Override
    public AuraSkillsApi getApi() {
        return api;
    }

    @Override
    public MessageProvider getMessageProvider() {
        return messageProvider;
    }

    @Override
    public ConfigProvider config() {
        return configProvider;
    }

    @Override
    public AbilityManager getAbilityManager() {
        return abilityManager;
    }

    @Override
    public ManaAbilityManager getManaAbilityManager() {
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
    public Leveler getLeveler() {
        return leveler;
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
    public TraitManager getTraitManager() {
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
    public UiProvider getUiProvider() {
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
}
