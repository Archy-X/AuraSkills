package dev.aurelium.auraskills.common.api;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.config.MainConfig;
import dev.aurelium.auraskills.api.loot.LootManager;
import dev.aurelium.auraskills.api.message.MessageManager;
import dev.aurelium.auraskills.api.registry.GlobalRegistry;
import dev.aurelium.auraskills.api.registry.Handlers;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.registry.NamespacedRegistry;
import dev.aurelium.auraskills.api.skill.XpRequirements;
import dev.aurelium.auraskills.api.source.SourceManager;
import dev.aurelium.auraskills.api.user.SkillsUser;
import dev.aurelium.auraskills.api.user.UserManager;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.api.implementation.*;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ApiAuraSkills implements AuraSkillsApi {

    private final AuraSkillsPlugin plugin;
    private final UserManager userManager;
    private final MessageManager messageManager;
    private final XpRequirements xpRequirements;
    private final Map<String, NamespacedRegistry> namespacedRegistryMap;
    private final Handlers handlers;
    private final MainConfig mainConfig;
    private final GlobalRegistry globalRegistry;
    private final SourceManager sourceManager;

    public ApiAuraSkills(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.userManager = new ApiUserManager(plugin);
        this.messageManager = new ApiMessageManager(plugin);
        this.xpRequirements = new ApiXpRequirements(plugin);
        this.namespacedRegistryMap = new HashMap<>();
        this.handlers = new ApiHandlers(plugin);
        this.mainConfig = new ApiMainConfig(plugin);
        this.globalRegistry = new ApiGlobalRegistry(plugin);
        this.sourceManager = new ApiSourceManager(plugin);
    }

    public AuraSkillsPlugin getPlugin() {
        return plugin;
    }

    @Override
    public SkillsUser getUser(UUID playerId) {
        return userManager.getUser(playerId);
    }

    @Override
    public UserManager getUserManager() {
        return userManager;
    }

    @Override
    public MessageManager getMessageManager() {
        return messageManager;
    }

    @Override
    public XpRequirements getXpRequirements() {
        return xpRequirements;
    }

    @Override
    public GlobalRegistry getGlobalRegistry() {
        return globalRegistry;
    }

    @Override
    public NamespacedRegistry useRegistry(String namespace, File contentDirectory) {
        namespace = namespace.toLowerCase(Locale.ROOT);
        if (namespace.equals(NamespacedId.AURASKILLS)) {
            throw new IllegalArgumentException("Cannot get a namespaced registry for auraskills, use the name of your plugin!");
        }
        final String finalNamespace = namespace;
        return namespacedRegistryMap.computeIfAbsent(namespace, s -> {
            plugin.getSkillManager().addContentDirectory(contentDirectory);
            return new ApiNamespacedRegistry(plugin, finalNamespace, contentDirectory);
        });
    }

    @Nullable
    public NamespacedRegistry getNamespacedRegistry(String namespace) {
        return namespacedRegistryMap.get(namespace);
    }

    @Override
    public Handlers getHandlers() {
        return handlers;
    }

    @Override
    public MainConfig getMainConfig() {
        return mainConfig;
    }

    @Override
    public SourceManager getSourceManager() {
        return sourceManager;
    }

    @Override
    public LootManager getLootManager() {
        return plugin.getApiProvider().getLootManager();
    }

    public Map<String, NamespacedRegistry> getNamespacedRegistryMap() {
        return namespacedRegistryMap;
    }

}
