package dev.aurelium.auraskills.bukkit.requirement;

import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.ConfigurateLoader;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class RequirementManager implements Listener {

    private Set<GlobalRequirement> globalRequirements;
    private final Map<UUID, Integer> errorMessageTimer;
    private final AuraSkills plugin;

    public RequirementManager(AuraSkills plugin) {
        errorMessageTimer = new HashMap<>();
        this.plugin = plugin;
        load();
        tickTimer();
    }

    public void load() {
        ConfigurateLoader loader = new ConfigurateLoader(plugin, TypeSerializerCollection.builder().build());
        try {
            ConfigurationNode config = loader.loadUserFile("config.yml");

            this.globalRequirements = new HashSet<>();
            int loaded = 0;
            for (ModifierType type : ModifierType.values()) {
                List<String> list = config.node("requirement", type.name().toLowerCase(Locale.ROOT), "global").getList(String.class, new ArrayList<>());
                for (String text : list) {
                    String[] splitText = text.split(" ");
                    try {
                        Material material = Material.valueOf(splitText[0].toUpperCase(Locale.ROOT));
                        Map<Skill, Integer> requirements = new HashMap<>();
                        for (int i = 1; i < splitText.length; i++) {
                            String requirementText = splitText[i];
                            try {
                                Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(requirementText.split(":")[0].toLowerCase(Locale.ROOT)));
                                if (skill != null) {
                                    int level = Integer.parseInt(requirementText.split(":")[1]);
                                    requirements.put(skill, level);
                                }
                            } catch (Exception e) {
                                plugin.logger().warn("Error parsing global skill " + type.name().toLowerCase(Locale.ROOT) + " requirement skill level pair with text " + requirementText);
                            }
                        }
                        GlobalRequirement globalRequirement = new GlobalRequirement(type, material, requirements);
                        globalRequirements.add(globalRequirement);
                        loaded++;
                    } catch (IllegalArgumentException e) {
                        plugin.logger().warn("Error loading global requirement with text " + text + ", is your material valid?");
                        e.printStackTrace();
                    }
                }
            }
            if (loaded > 0) {
                plugin.logger().info("Loaded " + loaded + " global requirement" + (loaded != 1 ? "s" : ""));
            }
        } catch (IOException e) {
            plugin.logger().warn("Error loading global requirements: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Set<GlobalRequirement> getGlobalRequirements() {
        return globalRequirements;
    }

    public Set<GlobalRequirement> getGlobalRequirementsType(ModifierType type) {
        Set<GlobalRequirement> matched = new HashSet<>();
        for (GlobalRequirement requirement : globalRequirements) {
            if (requirement.getType() == type) {
                matched.add(requirement);
            }
        }
        return matched;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        errorMessageTimer.remove(event.getPlayer().getUniqueId());
    }

    public void tickTimer() {
        var task = new TaskRunnable() {
            @Override
            public void run() {
                for (UUID id : errorMessageTimer.keySet()) {
                    int timer = errorMessageTimer.get(id);
                    if (timer != 0) {
                        errorMessageTimer.put(id, errorMessageTimer.get(id) - 1);
                    }
                }
            }
        };
        plugin.getScheduler().timerSync(task, 0, 5 * 50, TimeUnit.MILLISECONDS);
    }

    public Map<UUID, Integer> getErrorMessageTimer() {
        return errorMessageTimer;
    }

}
