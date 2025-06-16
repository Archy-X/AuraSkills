package dev.aurelium.auraskills.bukkit.requirement;

import dev.aurelium.auraskills.api.config.ConfigNode;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.api.user.SkillsUser;
import org.bukkit.Bukkit;

import java.util.List;

public class RequirementCheck {
    public static boolean failed(List<ConfigNode> requirements, SkillsUser user) {
        if (requirements == null) {
            return false;
        }

        for (ConfigNode config : requirements) {
            if (config.hasChild("type")) {
                if (checkType(config, user) == false) {
                    return true;
                }
            }
        }
        return false;
    }

    protected static boolean checkType(ConfigNode config, SkillsUser user) {
        boolean passes = true;
        if (config.hasChild("type")) {
            switch (config.node("type").getString()) {
                case "skill_level":
                    passes = checkSkillLevel(config, user);
                    break;
                case "stat_level":
                    passes = checkStatLevel(config, user);
                    break;
                case "permission":
                    passes = checkPermission(config, user);
                    break;
                case "world":
                    passes = checkWorld(config, user);
                    break;
            }
        }

        return passes;
    }

    protected static boolean checkSkillLevel(ConfigNode config, SkillsUser user) {
        Skill skill = Skills.valueOf(config.node("skill").getString().toUpperCase());
        int level = user.getSkillLevel(skill);
        if (level >= config.node("level").getInt()) {
            return true;
        }
        return false;
    }

    protected static boolean checkStatLevel(ConfigNode config, SkillsUser user) {
        Stat stat = Stats.valueOf(config.node("stat").getString().toUpperCase());
        double level = user.getStatLevel(stat);
        if (level >= config.node("level").getInt()) {
            return true;
        }
        return false;
    }

    protected static boolean checkPermission(ConfigNode config, SkillsUser user) {
        if (Bukkit.getPlayer(user.getUuid()).hasPermission(config.node("permission").getString())) {
            return true;
        }
        return false;
    }

    protected static boolean checkWorld(ConfigNode config, SkillsUser user) {
        String playerWorld = Bukkit.getPlayer(user.getUuid()).getLocation().getWorld().getName();
        ConfigNode world = config.node("world");

        if (world.isList()) {
            if (world.getList(String.class).contains(playerWorld)) {
                return true;
            }
        } else {
            if (world.getString().equals(playerWorld)) {
                return true;
            }
        }
        return false;
    }

}
