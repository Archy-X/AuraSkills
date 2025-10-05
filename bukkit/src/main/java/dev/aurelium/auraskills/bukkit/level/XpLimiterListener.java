package dev.aurelium.auraskills.bukkit.level;

import dev.aurelium.auraskills.api.event.skill.XpGainEvent;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.level.XpLimiter;
import dev.aurelium.auraskills.common.level.XpLimiterRecord;
import dev.aurelium.auraskills.common.message.type.LevelerMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class XpLimiterListener implements Listener {

    private final AuraSkills plugin;

    public XpLimiterListener(AuraSkills plugin) {
        this.plugin = plugin;
        this.startXpLimiterTask();
    }

    private void startXpLimiterTask() {
        if (!plugin.configBoolean(Option.LEVELER_LIMITER_FIXED_INTERVAL)) return;

        int intervalTicks = plugin.configInt(Option.LEVELER_LIMITER_GLOBAL_TIME) * 20;
        Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            plugin.getXpLimiter().clearTracked();
        }, intervalTicks, intervalTicks);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSkillXpGain(XpGainEvent event) {
        if (!plugin.configBoolean(Option.LEVELER_LIMITER_ENABLED)) return;

        XpLimiter limiter = plugin.getXpLimiter();
        String limitKey = limiter.globalKey();
        Skill skill = event.getSkill();
        int limitCap = plugin.configInt(Option.LEVELER_LIMITER_GLOBAL_CAP);
        int limitTime = plugin.configInt(Option.LEVELER_LIMITER_GLOBAL_TIME);
        boolean fixedInterval = plugin.configBoolean(Option.LEVELER_LIMITER_FIXED_INTERVAL);

        boolean enabled = true;

        if (plugin.configBoolean(Option.LEVELER_LIMITER_PER_SKILL)) {
            limitKey = skill.name().toUpperCase();
            Map<String, Object> skillLimit = skill.optionMap("limiter");

            if (skillLimit != null && !skillLimit.isEmpty()) {
                enabled = (boolean) skillLimit.getOrDefault("enabled", enabled);
                int skillCap = (int) skillLimit.getOrDefault("cap", limitCap);
                int skillTime = (int) skillLimit.getOrDefault("time", limitTime);

                if (skillCap > 0) limitCap = skillCap;
                if (skillTime > 0 && !fixedInterval) limitTime = skillTime;
            }

            if (!enabled) return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        limitCap = (int) (limitCap * limiter.getMultiplier(plugin.getUser(player), skill));

        Map<String, XpLimiterRecord> xpTracker = limiter.trackerGetByUuid(uuid);
        // Return if new tracker for player.
        if (xpTracker == null) {
            Map<String, XpLimiterRecord> newXpRecord = new HashMap<>();
            limiter.putSkillRecord(uuid, limitKey, newXpRecord, event.getAmount(), now);
            return;
        }

        XpLimiterRecord skillRecord = xpTracker.get(limitKey);
        // Return if new skill for player.
        if (skillRecord == null) {
            limiter.putSkillRecord(uuid, limitKey, xpTracker, event.getAmount(), now);
            return;
        }

        long remainingTime = 0;
        if (fixedInterval) {
            long remainingMS = (limitTime * 1000L) - (now - limiter.getResetTime());
            remainingTime = Math.max(remainingMS / 1000L, 0);
        } else {
            // Reset if expired.
            remainingTime = limitTime - ((now - skillRecord.timestamp()) / 1000);
            if (remainingTime <= 0) {
                limiter.putSkillRecord(uuid, limitKey, xpTracker, event.getAmount(), now);
                limiter.resetLimitedByKey(uuid, limitKey);
                return;
            }
        }

        // Cancel (or max it) if going over cap.
        double totalXp = skillRecord.xp() + event.getAmount();
        if (totalXp > limitCap) {
            // Either reward partially (if enabled) or cancel.
            double remainingXp = limitCap - skillRecord.xp();
            if (remainingXp > 0 && plugin.configBoolean(Option.LEVELER_LIMITER_PARTIAL_XP_GAIN)) {
                event.setAmount(remainingXp);
                limiter.putSkillRecord(uuid, limitKey, xpTracker, limitCap, now);
            } else {
                event.setCancelled(true);
                this.sendMessage(event, player, uuid, limitKey, remainingTime);
            }

            return;
        }

        // Update recorded with total received XP.
        limiter.putSkillRecord(uuid, limitKey, xpTracker, totalXp, skillRecord.timestamp());
    }

    protected void sendMessage(XpGainEvent event, Player player, UUID uuid, String limitKey, long remainingTime) {
        if (!plugin.getXpLimiter().canSendMessage(uuid, limitKey)) return;

        String message = plugin.getMsg(LevelerMessage.LIMIT_REACHED, event.getUser().getLocale());
        plugin.getAbilityManager().sendMessage(player, message.replace("{time}", String.valueOf(remainingTime)));
    }

}
