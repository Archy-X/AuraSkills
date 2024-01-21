package dev.aurelium.auraskills.test.env;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.ability.AbilityManager;
import dev.aurelium.auraskills.common.ability.AbilityRegistry;
import dev.aurelium.auraskills.common.config.ConfigProvider;
import dev.aurelium.auraskills.common.event.EventHandler;
import dev.aurelium.auraskills.common.hooks.HookManager;
import dev.aurelium.auraskills.common.item.ItemRegistry;
import dev.aurelium.auraskills.common.leaderboard.LeaderboardManager;
import dev.aurelium.auraskills.common.level.LevelManager;
import dev.aurelium.auraskills.common.level.XpRequirements;
import dev.aurelium.auraskills.common.mana.ManaAbilityManager;
import dev.aurelium.auraskills.common.mana.ManaAbilityRegistry;
import dev.aurelium.auraskills.common.menu.MenuHelper;
import dev.aurelium.auraskills.common.message.MessageKey;
import dev.aurelium.auraskills.common.message.MessageProvider;
import dev.aurelium.auraskills.common.message.PlatformLogger;
import dev.aurelium.auraskills.common.region.WorldManager;
import dev.aurelium.auraskills.common.reward.RewardManager;
import dev.aurelium.auraskills.common.scheduler.Scheduler;
import dev.aurelium.auraskills.common.skill.SkillManager;
import dev.aurelium.auraskills.common.skill.SkillRegistry;
import dev.aurelium.auraskills.common.source.SourceTypeRegistry;
import dev.aurelium.auraskills.common.stat.StatManager;
import dev.aurelium.auraskills.common.stat.StatRegistry;
import dev.aurelium.auraskills.common.storage.StorageProvider;
import dev.aurelium.auraskills.common.storage.backup.BackupProvider;
import dev.aurelium.auraskills.common.trait.TraitManager;
import dev.aurelium.auraskills.common.trait.TraitRegistry;
import dev.aurelium.auraskills.common.ui.UiProvider;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.user.UserManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class TestAuraSkills implements AuraSkillsPlugin {

    private final File testDir;
    private final TestPlatformLogger logger = new TestPlatformLogger();

    public TestAuraSkills(File testDir) {
        this.testDir = testDir;
        try {
            Path envPath = Paths.get("src/test/resources/env");
            // Copy the contents of the env directory to the testDir
            Files.walk(envPath).forEach(source -> {
                try {
                    if (!envPath.equals(source)) {
                        Path relativePath = envPath.relativize(source);
                        Path target = testDir.toPath().resolve(relativePath);
                        System.out.println("Copying from " + source + " to " + target);
                        Files.copy(source, target);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AuraSkillsApi getApi() {
        return null;
    }

    @Override
    public MessageProvider getMessageProvider() {
        return null;
    }

    @Override
    public ConfigProvider config() {
        return null;
    }

    @Override
    public AbilityManager getAbilityManager() {
        return null;
    }

    @Override
    public ManaAbilityManager getManaAbilityManager() {
        return null;
    }

    @Override
    public StatManager getStatManager() {
        return null;
    }

    @Override
    public ItemRegistry getItemRegistry() {
        return null;
    }

    @Override
    public LevelManager getLevelManager() {
        return null;
    }

    @Override
    public UserManager getUserManager() {
        return null;
    }

    @Override
    public XpRequirements getXpRequirements() {
        return null;
    }

    @Override
    public PlatformLogger logger() {
        return logger;
    }

    @Override
    public SkillManager getSkillManager() {
        return null;
    }

    @Override
    public SkillRegistry getSkillRegistry() {
        return null;
    }

    @Override
    public StatRegistry getStatRegistry() {
        return null;
    }

    @Override
    public TraitRegistry getTraitRegistry() {
        return null;
    }

    @Override
    public TraitManager getTraitManager() {
        return null;
    }

    @Override
    public AbilityRegistry getAbilityRegistry() {
        return null;
    }

    @Override
    public ManaAbilityRegistry getManaAbilityRegistry() {
        return null;
    }

    @Override
    public SourceTypeRegistry getSourceTypeRegistry() {
        return null;
    }

    @Override
    public HookManager getHookManager() {
        return null;
    }

    @Override
    public LeaderboardManager getLeaderboardManager() {
        return null;
    }

    @Override
    public UiProvider getUiProvider() {
        return null;
    }

    @Override
    public RewardManager getRewardManager() {
        return null;
    }

    @Override
    public Scheduler getScheduler() {
        return null;
    }

    @Override
    public StorageProvider getStorageProvider() {
        return null;
    }

    @Override
    public BackupProvider getBackupProvider() {
        return null;
    }

    @Override
    public WorldManager getWorldManager() {
        return null;
    }

    @Override
    public MenuHelper getMenuHelper() {
        return null;
    }

    @Override
    public EventHandler getEventHandler() {
        return null;
    }

    @Override
    public String getMsg(MessageKey key, Locale locale) {
        return null;
    }

    @Override
    public String getPrefix(Locale locale) {
        return null;
    }

    @Override
    public void runConsoleCommand(String command) {

    }

    @Override
    public void runPlayerCommand(User user, String command) {

    }

    @Override
    public InputStream getResource(String path) {
        try {
            File res = new File("src/test/resources/env/AuraSkills", path);
            if (res.exists()) {
                return new FileInputStream(res);
            }
        } catch (FileNotFoundException e) {
            return null;
        }
        return null;
    }

    @Override
    public void saveResource(String path, boolean replace) {
        if (path != null && !path.isEmpty()) {
            path = path.replace('\\', '/');
            InputStream in = this.getResource(path);
            if (in == null) {
                throw new IllegalArgumentException("The embedded resource '" + path + "' cannot be found");
            } else {
                File outFile = new File(getPluginFolder(), path);
                int lastIndex = path.lastIndexOf(47);
                File outDir = new File(getPluginFolder(), path.substring(0, Math.max(lastIndex, 0)));
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }

                try {
                    if (outFile.exists() && !replace) {
                        this.logger.warn("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
                    } else {
                        OutputStream out = new FileOutputStream(outFile);
                        byte[] buf = new byte[1024];

                        int len;
                        while((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        out.close();
                        in.close();
                    }
                } catch (IOException var10) {
                    this.logger.severe("Could not save " + outFile.getName() + " to " + outFile, var10);
                }

            }
        } else {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
    }

    @Override
    public File getPluginFolder() {
        return new File(testDir, "AuraSkills");
    }
}
