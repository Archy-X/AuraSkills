package dev.aurelium.auraskills.common.ui;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.hooks.PlaceholderHook;
import dev.aurelium.auraskills.common.message.type.ActionBarMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import dev.aurelium.auraskills.common.util.math.NumberUtil;
import dev.aurelium.auraskills.common.util.text.TextUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class ActionBarManager {

    private final AuraSkillsPlugin plugin;
    private final UiProvider uiProvider;

    private final HashSet<UUID> isPaused = new HashSet<>();
    private final HashSet<UUID> isGainingXp = new HashSet<>();
    private final HashMap<UUID, Integer> timer = new HashMap<>();
    private final HashMap<UUID, Integer> currentAction = new HashMap<>();

    public ActionBarManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.uiProvider = plugin.getUiProvider();
    }

    public void scheduleTimerCountdown() {
        plugin.getScheduler().timerSync(
            new TaskRunnable() {
                @Override
                public void run() {
                    if (plugin.configBoolean(Option.ACTION_BAR_ENABLED)) {
                        return;
                    }

                    for (UUID uuid : plugin.getUserManager().getOnlineUuids()) {
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
            }, 0, 2, TimeUnit.MILLISECONDS);
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
            }, 0, plugin.configInt(Option.ACTION_BAR_UPDATE_PERIOD), TimeUnit.MILLISECONDS);
        // Schedule task to stop updating action bar
        plugin.getScheduler().scheduleSync(() -> {
            Integer timerNum = timer.get(uuid);
            if (timerNum != null) {
                if (timerNum.equals(0)) {
                    isGainingXp.remove(uuid);
                }
            }
        }, 41, TimeUnit.MILLISECONDS);
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

    private String getXpActionBarMessage(User user, Skill skill, double currentXp, double levelXp, double xpGained, int level, boolean maxed) {
        ActionBarMessage messageKey = maxed ? ActionBarMessage.MAXED : ActionBarMessage.XP;
        if (xpGained < 0) {
            messageKey = maxed ? ActionBarMessage.MAXED_REMOVED : ActionBarMessage.XP_REMOVED;
        }
        Locale locale = user.getLocale();

        String message = plugin.getMsg(messageKey, locale);
        // Replace built in placeholders
        message = TextUtil.replace(message,
                "{hp}", getHp(user),
                "{max_hp}", getMaxHp(user),
                "{xp_gained}", NumberUtil.format1(xpGained),
                "{xp_removed}", NumberUtil.format1(xpGained),
                "{skill}", skill.getDisplayName(locale),
                "{current_xp}", NumberUtil.format1(currentXp),
                "{level_xp}", NumberUtil.format1(levelXp),
                "{skill_level}", String.valueOf(level),
                "{mana}", getMana(user),
                "{max_mana}", getMaxMana(user));
        // Replace PlaceholderAPI placeholders
        if (plugin.getHookManager().isRegistered(PlaceholderHook.class)) {
            message = plugin.getHookManager().getHook(PlaceholderHook.class).setPlaceholders(user, message);
        }

        return message;
    }

    public abstract String getHp(User user);

    public abstract String getMaxHp(User user);

    private String getMana(User user) {
        return String.valueOf(Math.round(user.getMana()));
    }

    private String getMaxMana(User user) {
        return String.valueOf(Math.round(user.getMaxMana()));
    }

}
