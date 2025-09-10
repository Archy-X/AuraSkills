package dev.aurelium.auraskills.bukkit.level;

import dev.aurelium.auraskills.api.event.skill.XpGainEvent;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.message.type.LevelerMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class XpLimiter implements Listener {

    private final AuraSkills plugin;
    private final Map<UUID, Map<String, XpLimiterRecord>> tracker = new HashMap<>();

    public XpLimiter(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSkillXpGain(XpGainEvent event) {
        if (!plugin.configBoolean(Option.LEVELER_LIMITER_ENABLED)) return;

        String limitKey = "GLOBAL";
        int limitCap = plugin.configInt(Option.LEVELER_LIMITER_GLOBAL_CAP);
        int limitTime = plugin.configInt(Option.LEVELER_LIMITER_GLOBAL_TIME);

        boolean enabled = true;

        if (plugin.configBoolean(Option.LEVELER_LIMITER_PER_SKILL)) {
            Skill skill = event.getSkill();
            limitKey = skill.name().toUpperCase();
            Map<String, Object> skillLimit = skill.optionMap("limiter");

            if (skillLimit != null && !skillLimit.isEmpty()) {
                enabled = (boolean) skillLimit.getOrDefault("enabled", enabled);
                int skillCap = (int) skillLimit.getOrDefault("cap", limitCap);
                int skillTime = (int) skillLimit.getOrDefault("time", limitTime);

                if (skillCap > 0) limitCap = skillCap;
                if (skillTime > 0) limitTime = skillTime;
            }

            if (!enabled) return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        Map<String, XpLimiterRecord> xpTracker = tracker.get(uuid);
        // Return if new tracker for player.
        if (xpTracker == null) {
            Map<String, XpLimiterRecord> newXpRecord = new HashMap<>();
            putSkillRecord(uuid, limitKey, newXpRecord, event.getAmount(), now);
            return;
        }

        XpLimiterRecord skillRecord = xpTracker.get(limitKey);
        // Return if new skill for player.
        if (skillRecord == null) {
            putSkillRecord(uuid, limitKey, xpTracker, event.getAmount(), now);
            return;
        }

        // Reset if expired.
        long remainingTime = limitTime - ((now - skillRecord.timestamp()) / 1000);
        if (remainingTime <= 0) {
            putSkillRecord(uuid, limitKey, xpTracker, event.getAmount(), now);
            return;
        }

        // Cancel (or max it) if going over cap.
        double totalXp = skillRecord.xp() + event.getAmount();
        if (totalXp > limitCap) {
            // Either reward partially (if enabled) or cancel.
            double remainingXp = limitCap - skillRecord.xp();
            if (remainingXp > 0 && plugin.configBoolean(Option.LEVELER_LIMITER_PARTIAL_XP_GAIN)) {
                event.setAmount(remainingXp);
                putSkillRecord(uuid, limitKey, xpTracker, limitCap, now);
                return;
            } else {
                trySendMessage(player, event, remainingTime);
                event.setCancelled(true);
                return;
            }
        }

        // Update recorded with total received XP.
        putSkillRecord(uuid, limitKey, xpTracker, totalXp, skillRecord.timestamp());
    }

    protected void putSkillRecord(UUID uuid, String key, Map<String, XpLimiterRecord> xpTracker, double cap, long now) {
        xpTracker.put(key, new XpLimiterRecord(cap, now));
        tracker.put(uuid, xpTracker);
    }

    protected void trySendMessage(Player player, XpGainEvent event, long remainingTime) {
        plugin.getAbilityManager().sendMessage(player, plugin.getMsg(LevelerMessage.LIMIT_REACHED, event.getUser().getLocale()).replace("{time}",
                String.valueOf(remainingTime)));
    }

}
