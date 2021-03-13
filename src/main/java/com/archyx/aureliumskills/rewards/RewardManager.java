package com.archyx.aureliumskills.rewards;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.rewards.CommandReward.CommandExecutor;
import com.archyx.aureliumskills.rewards.exception.InvalidTypeException;
import com.archyx.aureliumskills.rewards.exception.RequiredKeyException;
import com.archyx.aureliumskills.rewards.exception.RewardException;
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

    public void loadRewards() throws FileNotFoundException, RewardException {
        this.rewardTables.clear();
        File rewardsDirectory = new File(plugin.getDataFolder() + "/rewards");
        // Load each file
        for (Skill skill : Skill.values()) {
            File rewardsFile = new File(rewardsDirectory + "/" + skill.toString().toLowerCase(Locale.ROOT) + ".json");
            JsonObject rewards = new Gson().fromJson(new InputStreamReader(new FileInputStream(rewardsFile)), JsonObject.class);

            RewardTable rewardTable = new RewardTable();
            // Load patterns section
            JsonArray patterns = getArrayOrNull(rewards, "patterns");
            if (patterns != null) {
                for (int i = 0; i < patterns.size(); i++) {
                    JsonElement element = patterns.get(i);
                    JsonObject object = element.getAsJsonObject();
                    try {
                        Reward reward = parseReward(object);
                        // Parse pattern
                        JsonObject pattern = getElement(object, "pattern").getAsJsonObject();
                        int start = 2;
                        if (pattern.has("start")) {
                            start = getElement(pattern, "start").getAsInt();
                        }
                        int interval = getElement(pattern, "interval").getAsInt();
                        int maxLevel = OptionL.getMaxLevel(skill);
                        int stop = maxLevel;
                        if (pattern.has("stop")) {
                            int potentialStop = getElement(pattern, "stop").getAsInt();
                            if (potentialStop < maxLevel) {
                                stop = potentialStop;
                            }
                        }
                        // Add to reward table
                        for (int j = start; j <= stop; j += interval) {
                            rewardTable.addReward(reward, j);
                        }
                    } catch (IllegalArgumentException e) {
                        throw new RewardException(rewardsFile.getName(), "patterns", i, e.getMessage());
                    }
                }
            }

        }
    }

    private Reward parseReward(JsonObject object) {
        // Get type of reward
        JsonElement typeElement = object.get("type");
        if (typeElement == null) {
            throw new InvalidTypeException("Reward is missing a type");
        }
        String type = typeElement.getAsString();

        // Parse each type
        switch (type) {
            case "stat":
                Stat stat = Stat.valueOf(getElement(object, "stat").getAsString().toUpperCase(Locale.ROOT));
                double statValue = getElement(object, "value").getAsDouble();
                return new StatReward(plugin, stat, statValue);

            case "ability":
                Ability ability = Ability.valueOf(getElement(object, "ability").getAsString().toUpperCase(Locale.ROOT));
                return new AbilityReward(plugin, ability);

            case "command":
                CommandExecutor executor = CommandExecutor.valueOf(getElement(object, "executor").getAsString().toUpperCase(Locale.ROOT));
                String command = getElement(object, "command").getAsString();
                return new CommandReward(plugin, executor, command);

            case "permission":
                String permission = getElement(object, "permission").getAsString();
                if (object.has("value")) {
                    boolean permissionValue = getElement(object, "value").getAsBoolean();
                    return new PermissionReward(plugin, permission, permissionValue);
                } else {
                    return new PermissionReward(plugin, permission);
                }
        }
        return null;
    }

    private JsonElement getElement(JsonObject object, String key) {
        // Check if not null
        JsonElement element = object.get(key);
        if (element != null) {
            return element;
        } else {
            throw new RequiredKeyException("Reward requires entry with key " + key);
        }
    }

    private JsonArray getArrayOrNull(JsonObject object, String key) {
        JsonElement element = object.get(key);
        if (element != null) {
            return element.getAsJsonArray();
        }
        return null;
    }

}
