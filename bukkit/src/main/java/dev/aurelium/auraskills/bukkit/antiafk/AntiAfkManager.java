package dev.aurelium.auraskills.bukkit.antiafk;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.config.Option;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class AntiAfkManager {

    private final AuraSkills plugin;
    private final Map<CheckType, Check> checkMap = new HashMap<>();

    public AntiAfkManager(AuraSkills plugin) {
        this.plugin = plugin;
        init();
    }

    private void init() {
        if (!plugin.configBoolean(Option.ANTI_AFK_ENABLED)) return;

        registerChecks();
    }

    private void registerChecks() {
        for (CheckType type : CheckType.values()) {
            registerCheck(type);
        }
    }

    public AuraSkills getPlugin() {
        return plugin;
    }

    @Nullable
    public Check getCheck(CheckType type) {
        return checkMap.get(type);
    }

    public CheckData getCheckData(Player player, CheckType type)  {
        return ((BukkitUser) plugin.getUser(player)).getCheckData(type);
    }

    public void logFail(Player player, CheckType type) {
        plugin.logger().info(player.getName() + " failed check " + type);
    }

    private void registerCheck(CheckType type) {
        Class<? extends Check> checkClass = type.getCheckClass();
        try {
            Constructor<?> constructor = checkClass.getDeclaredConstructor(CheckType.class, AntiAfkManager.class);
            Object checkObj = constructor.newInstance(type, this);
            if (checkObj instanceof Check check) {
                plugin.getServer().getPluginManager().registerEvents(check, plugin);
                checkMap.put(type, check);
            }
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            plugin.logger().warn("Failed to register check of type " + type);
            e.printStackTrace();
        }
    }
}
