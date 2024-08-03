package dev.aurelium.auraskills.bukkit.antiafk;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.region.BlockPosition;
import dev.aurelium.auraskills.common.user.AntiAfkLog;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class AntiAfkManager {

    private final AuraSkills plugin;
    private final Map<CheckType, Check> checkMap = new HashMap<>();
    private Expression logThresholdExpression;

    public AntiAfkManager(AuraSkills plugin) {
        this.plugin = plugin;

        if (!plugin.configBoolean(Option.ANTI_AFK_ENABLED)) return;

        loadLogThresholdExpression();
        registerChecks();
    }

    public void reload() {
        loadLogThresholdExpression();
        // Unregister checks
        for (Check existing : checkMap.values()) {
            HandlerList.unregisterAll(existing);
        }
        checkMap.clear();
        // Register checks again to account for changed config values
        if (!plugin.configBoolean(Option.ANTI_AFK_ENABLED)) return;
        registerChecks();
    }

    private void loadLogThresholdExpression() {
        this.logThresholdExpression = new Expression(plugin.configString(Option.ANTI_AFK_LOG_THRESHOLD));
        try {
            this.logThresholdExpression.validate();
        } catch (ParseException e) {
            plugin.logger().warn("Failed to parse anti_afk.log_threshold expression: " + e.getMessage());
            e.printStackTrace();
        }
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

    public Expression getLogThresholdExpression() {
        return logThresholdExpression;
    }

    public void logAndNotifyFail(Player player, CheckType type, CheckData checkData) {
        String message = TextUtil.replace(plugin.getMsg(CommandMessage.ANTIAFK_FAILED, plugin.getDefaultLanguage()),
                "{player}", player.getName(),
                "{check}", type.name(),
                "{count}", String.valueOf(checkData.getCount()));

        Location loc = player.getLocation();
        BlockPosition coords = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        @Nullable World world = loc.getWorld();
        String worldName = world != null ? world.getName() : "";

        // Log message
        User user = plugin.getUser(player);
        var log = new AntiAfkLog(System.currentTimeMillis(), message, coords, worldName);
        user.getSessionAntiAfkLogs().add(log);

        // Send to online players with notify permission
        for (Player notified : Bukkit.getOnlinePlayers()) {
            if (!notified.hasPermission("auraskills.antiafk.notify")) {
                continue;
            }

            notified.sendMessage(message);
        }
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
