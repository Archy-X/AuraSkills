package dev.aurelium.auraskills.common.antiafk;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.ref.PlayerRef;
import dev.aurelium.auraskills.common.user.AntiAfkLog;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AntiAfkManager {

    private final AuraSkillsPlugin plugin;
    private final Map<CheckType, Check> checkMap = new HashMap<>();
    private Expression logThresholdExpression;

    public AntiAfkManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;

        if (!plugin.configBoolean(Option.ANTI_AFK_ENABLED)) return;

        loadLogThresholdExpression();
    }

    public abstract void registerCheckEvents(Check check);

    public abstract void unregisterCheckEvents(Check check);

    public abstract LogLocation getLogLocation(PlayerRef ref);

    protected abstract Constructor<?> getCheckConstructor(Class<? extends Check> checkClass) throws NoSuchMethodException;

    public Optional<Check> getCheck(CheckType type) {
        return Optional.ofNullable(checkMap.get(type));
    }

    public CheckData getCheckData(PlayerRef ref, CheckType type) {
        return plugin.getUser(ref).getCheckData(type);
    }

    public abstract CheckType[] getCheckTypes();

    public AuraSkillsPlugin getPlugin() {
        return plugin;
    }

    public Expression getLogThresholdExpression() {
        return logThresholdExpression;
    }

    public void reload() {
        loadLogThresholdExpression();
        // Unregister checks
        for (Check existing : checkMap.values()) {
            unregisterCheckEvents(existing);
        }
        checkMap.clear();
        // Register checks again to account for changed config values
        if (!plugin.configBoolean(Option.ANTI_AFK_ENABLED)) return;
        registerChecks();
    }

    public void logAndNotifyFail(PlayerRef ref, CheckType checkType, CheckData checkData) {
        User user = plugin.getUser(ref);

        String message = TextUtil.replace(plugin.getMsg(CommandMessage.ANTIAFK_FAILED, plugin.getDefaultLanguage()),
                "{player}", user.getUsername(),
                "{check}", checkType.name(),
                "{count}", String.valueOf(checkData.getCount()));

        LogLocation logLocation = getLogLocation(ref);

        // Log message
        var log = new AntiAfkLog(System.currentTimeMillis(), message, logLocation.coordinates(), logLocation.worldName());
        user.getSessionAntiAfkLogs().add(log);

        // Send to online players with notify permission
        for (User notified : plugin.getUserManager().getOnlineUsers()) {
            if (!notified.hasPermission("auraskills.antiafk.notify")) {
                continue;
            }

            notified.sendMessage(message);
        }
    }

    public void registerChecks() {
        if (!plugin.configBoolean(Option.ANTI_AFK_ENABLED)) return;

        for (CheckType type : getCheckTypes()) {
            constructCheck(type).ifPresent(check -> {
                registerCheckEvents(check);
                checkMap.put(type, check);
            });
        }
    }

    private Optional<Check> constructCheck(CheckType type) {
        Class<? extends Check> checkClass = type.getCheckClass();
        try {
            Constructor<?> constructor = getCheckConstructor(checkClass);
            Object checkObj = constructor.newInstance(type, this);
            if (checkObj instanceof Check check) {
                return Optional.of(check);
            }
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            plugin.logger().warn("Failed to register check of type " + type);
            e.printStackTrace();
        }
        return Optional.empty();
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

}
