package com.archyx.aureliumskills.rewards;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.Stat;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RewardManager {

    private final AureliumSkills plugin;
    private final Map<Skill, RewardTable> rewardTables;

    public RewardManager(AureliumSkills plugin) {
        this.plugin = plugin;
        this.rewardTables = new HashMap<>();
    }

    public RewardTable getRewardTable(Skill skill) {
        return rewardTables.get(skill);
    }

    public void loadRewards() throws FileNotFoundException {
        this.rewardTables.clear();
        File rewardsDirectory = new File(plugin.getDataFolder() + "/rewards");
        // Load each file
        for (Skill skill : Skill.values()) {
            File rewardsFile = new File(rewardsDirectory + "/" + skill.toString().toLowerCase(Locale.ROOT) + ".json");
            JsonObject rewards = new Gson().fromJson(new InputStreamReader(new FileInputStream(rewardsFile)), JsonObject.class);

            JsonArray patterns = rewards.getAsJsonArray("patterns");
            for (JsonElement element : patterns) {
                JsonObject pattern = element.getAsJsonObject();

            }
        }
    }

    private Reward parseReward(JsonObject object) {
        String type = object.get("type").getAsString();
        switch (type) {
            case "stat":
                Stat stat = Stat.valueOf(object.get("name").getAsString().toUpperCase(Locale.ROOT));
                double value = object.get("value").getAsDouble();
                return new StatReward(plugin, stat, value);
            case "ability":
                Ability ability = Ability.valueOf(object.get("name").getAsString().toUpperCase(Locale.ROOT));
                return new AbilityReward(plugin, ability);
            case "command":

                break;
            case "permission":

                break;
        }
        return null;
    }

}
