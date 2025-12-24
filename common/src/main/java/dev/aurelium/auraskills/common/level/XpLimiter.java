package dev.aurelium.auraskills.common.level;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.user.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class XpLimiter {

    private final AuraSkillsPlugin plugin;
    private final String globalKey = "GLOBAL";
    private final Map<UUID, Map<String, XpLimiterRecord>> tracker = new HashMap<>();
    private final Map<UUID, Set<String>> limitedSkills = new HashMap<>();

    private long resetTime = System.currentTimeMillis();

    public XpLimiter(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public String globalKey() {
        return this.globalKey;
    }

    public boolean isSkillLimited(UUID uuid, Skill skill) {
        boolean setForSkill = this.limitedSkills.getOrDefault(uuid, Collections.emptySet()).contains(skill.name().toUpperCase());
        boolean setGlobally = this.limitedSkills.getOrDefault(uuid, Collections.emptySet()).contains(this.globalKey);
        return setForSkill || setGlobally;
    }

    public double getMultiplier(User user, Skill skill) {
        double multiplier = 1.0;
        if (plugin.configBoolean(Option.LEVELER_LIMITER_USE_XP_MULTIPLIER)) {
            multiplier += user.getPermissionMultiplier(skill);
        } else {
            multiplier += user.getPermissionLimitMultiplier(skill);
        }
        return multiplier;
    }

    public long getResetTime() {
        return this.resetTime;
    }

    public Map<String, XpLimiterRecord> trackerGetByUuid(UUID uuid) {
        return this.tracker.get(uuid);
    }

    public void putSkillRecord(UUID uuid, String key, Map<String, XpLimiterRecord> xpTracker, double cap, long now) {
        xpTracker.put(key, new XpLimiterRecord(cap, now));
        this.tracker.put(uuid, xpTracker);
    }

    public void resetLimitedByKey(UUID uuid, String key) {
        if (this.limitedSkills.containsKey(uuid)) {
            this.limitedSkills.get(uuid).remove(key);
        }
    }

    public void clearTracked() {
        this.tracker.clear();
        this.limitedSkills.clear();
        this.resetTime = System.currentTimeMillis();
    }

    public void limitSkill(UUID uuid, String key) {
        this.limitedSkills.computeIfAbsent(uuid, k -> new HashSet<>()).add(key);
    }

    public boolean canSendMessage(UUID uuid, String key) {
        // Make sure the message is only send once.
        if (this.limitedSkills.containsKey(uuid)) {
            if (this.limitedSkills.get(uuid).contains(key)) return false;
            // Add the key if it wasn't in the set.
            this.limitedSkills.computeIfAbsent(uuid, k -> new HashSet<>()).add(key);
        } else {
            // Add the user and key if it wasn't tracked.
            this.limitedSkills.put(uuid, new HashSet<>(Set.of(key)));
        }
        return true;
    }

}
