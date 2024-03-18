package dev.aurelium.auraskills.common.ui;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.hooks.PlaceholderHook;
import dev.aurelium.auraskills.common.message.type.ActionBarMessage;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class ActionBarManager {

    protected final AuraSkillsPlugin plugin;
    private final UiProvider uiProvider;

    private final HashSet<UUID> isPaused = new HashSet<>();
    private final HashSet<UUID> isGainingXp = new HashSet<>();
    private final HashMap<UUID, Integer> timer = new HashMap<>();
    private final HashMap<UUID, Integer> currentAction = new HashMap<>();

    public ActionBarManager(AuraSkillsPlugin plugin, UiProvider uiProvider) {
        this.plugin = plugin;
        this.uiProvider = uiProvider;
        startTimerCountdown();
        startUpdatingIdleActionBar();
    }

    public void startTimerCountdown() {
        var task = new TaskRunnable() {
            @Override
            public void run() {
                if (!plugin.configBoolean(Option.ACTION_BAR_ENABLED)) {
                    return;
                }
    
                for (User user : plugin.getUserManager().getOnlineUsers()) {
                    UUID uuid = user.getUuid();
                    Integer time = timer.get(uuid);
                    if (time != null) {
                        if (time > 0) {
                            timer.put(uuid, time - 1);
                        }
                    } else {
                        timer.put(uuid, 0);
                    }
                }
            }
        };
        plugin.getScheduler().timerSync(task, 0, 2 * 50L, TimeUnit.MILLISECONDS);
    }
    
    public void startUpdatingIdleActionBar() {
        var task = new TaskRunnable() {
            @Override
            public void run() {
                if (!plugin.configBoolean(Option.ACTION_BAR_IDLE)) {
                    return;
                }
                for (User user : plugin.getUserManager().getOnlineUsers()) {
                    UUID uuid = user.getUuid();
                    // Check player setting
                    if (!user.isActionBarEnabled(ActionBarType.IDLE)) {
                        continue;
                    }
                    // Check disabled worlds
                    if (plugin.getWorldManager().isDisabledWorld(getWorldName(user))) {
                        continue;
                    }

                    if (!currentAction.containsKey(uuid)) {
                        currentAction.put(uuid, 0);
                    }
                    if (isGainingXp.contains(uuid) || isPaused.contains(uuid)) {
                        continue;
                    }

                    String message = TextUtil.replace(plugin.getMsg(ActionBarMessage.IDLE, user.getLocale())
                            , "{hp}", getHp(user)
                            , "{max_hp}", getMaxHp(user)
                            , "{mana}", getMana(user)
                            , "{max_mana}", getMaxMana(user));
                    message = replacePlaceholderApi(user, message);

                    uiProvider.sendActionBar(user, message);
                }
            }
        };
        plugin.getScheduler().timerSync(task, 0, plugin.configInt(Option.ACTION_BAR_UPDATE_PERIOD) * 50L, TimeUnit.MILLISECONDS);
    }

    public void sendXpActionBar(User user, Skill skill, double currentXp, double levelXp, double xpGained, int level, boolean maxed) {
        ActionBarType sendType = maxed ? ActionBarType.MAXED : ActionBarType.XP;
        // Return if the type is disabled in config
        if (!plugin.configBoolean(sendType.getOption())) {
            return;
        }

        // Return if player has it disabled
        if (!user.isActionBarEnabled(sendType)) {
            return;
        }

        UUID uuid = user.getUuid();
        if (isPaused.contains(uuid)) {
            return;
        }
        // Set timer and is gaining xp
        isGainingXp.add(uuid);
        timer.put(uuid, 20);

        // Increment action number
        int thisAction = currentAction.getOrDefault(uuid, 0) + 1;
        currentAction.put(uuid, thisAction);
        // Schedule timer task to update action bar
        plugin.getScheduler().timerSync(
            new TaskRunnable() {
                @Override
                public void run() {
                    if (!isGainingXp.contains(uuid)) {
                        cancel();
                        return;
                    }
                    // Cancel if a later action bar is sent
                    Integer actionNum = currentAction.get(uuid);
                    if (actionNum == null) {
                        cancel();
                        return;
                    }
                    if (thisAction != actionNum) {
                        cancel();
                        return;
                    }

                    String message = getXpActionBarMessage(user, skill, currentXp, levelXp, xpGained, level, maxed);
                    uiProvider.sendActionBar(user, message);
                }
            }, 0, plugin.configInt(Option.ACTION_BAR_UPDATE_PERIOD) * 50L, TimeUnit.MILLISECONDS);
        // Schedule task to stop updating action bar
        plugin.getScheduler().scheduleSync(() -> {
            Integer timerNum = timer.get(uuid);
            if (timerNum != null) {
                if (timerNum.equals(0)) {
                    isGainingXp.remove(uuid);
                }
            }
        }, 41 * 50L, TimeUnit.MILLISECONDS);
    }

    public void resetActionBars() {
        isGainingXp.clear();
        timer.clear();
        currentAction.clear();
        isPaused.clear();
    }

    public void resetActionBar(User user) {
        UUID uuid = user.getUuid();
        isGainingXp.remove(uuid);
        timer.remove(uuid);
        currentAction.remove(uuid);
        isPaused.remove(uuid);
    }

    public void setPaused(User user, int time, TimeUnit timeUnit) {
        UUID uuid = user.getUuid();
        isPaused.add(uuid);
        Integer action = currentAction.get(uuid);
        if (action != null) {
            currentAction.put(uuid, action + 1);
        } else {
            currentAction.put(uuid, 0);
        }
        int thisAction = this.currentAction.get(uuid);
        plugin.getScheduler().scheduleSync(() -> {
            Integer actionBarCurrentAction = currentAction.get(uuid);
            if (actionBarCurrentAction != null) {
                if (thisAction == actionBarCurrentAction) {
                    isPaused.remove(uuid);
                }
            }
        }, time, timeUnit);
    }

    public void sendAbilityActionBar(User user, String message) {
        if (!user.isActionBarEnabled(ActionBarType.ABILITY)) return;

        String actionBarText = TextUtil.replace(plugin.getMsg(ActionBarMessage.ABILITY, user.getLocale()),
                "{hp}", getHp(user),
                "{max_hp}", getMaxHp(user),
                "{mana}", getMana(user),
                "{max_mana}", getMaxMana(user),
                "{message}", message);
        plugin.getUiProvider().sendActionBar(user, actionBarText);
        setPaused(user, 15 * 50, TimeUnit.MILLISECONDS);
    }

    private String getXpActionBarMessage(User user, Skill skill, double currentXp, double levelXp, double xpGained, int level, boolean maxed) {
        ActionBarMessage messageKey = maxed ? ActionBarMessage.MAXED : ActionBarMessage.XP;
        Locale locale = user.getLocale();

        String message = plugin.getMsg(messageKey, locale);
        // Replace built in placeholders
        message = TextUtil.replace(message,
                "{hp}", getHp(user),
                "{max_hp}", getMaxHp(user),
                "{xp_gained}", xpGained > 0 ? "+" + NumberUtil.format1(xpGained) : NumberUtil.format1(xpGained),
                "{skill}", skill.getDisplayName(locale),
                "{current_xp}", NumberUtil.format1(currentXp),
                "{level_xp}", NumberUtil.format1(levelXp),
                "{skill_level}", String.valueOf(level),
                "{mana}", getMana(user),
                "{max_mana}", getMaxMana(user));
        // Replace PlaceholderAPI placeholders
        message = replacePlaceholderApi(user, message);

        return message;
    }

    @NotNull
    public abstract String getHp(User user);

    @NotNull
    public abstract String getMaxHp(User user);

    @NotNull
    public abstract String getWorldName(User user);

    private String getMana(User user) {
        return String.valueOf((int) Math.floor(user.getMana()));
    }

    private String getMaxMana(User user) {
        return String.valueOf((int) Math.floor(user.getMaxMana()));
    }

    private String replacePlaceholderApi(User user, String message) {
        if (plugin.getHookManager().isRegistered(PlaceholderHook.class)) {
            return plugin.getHookManager().getHook(PlaceholderHook.class).setPlaceholders(user, message);
        }
        return message;
    }

}
