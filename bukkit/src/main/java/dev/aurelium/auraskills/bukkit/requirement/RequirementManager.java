package dev.aurelium.auraskills.bukkit.requirement;

import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.requirement.blocks.*;
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
    private List<BlockRequirement> blockRequirements;
    private final Map<UUID, Integer> errorMessageTimer;
    private final AuraSkills plugin;

    public RequirementManager(AuraSkills plugin) {
        errorMessageTimer = new HashMap<>();
        this.plugin = plugin;
        load();
        loadBlocks();
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

    public void loadBlocks() {
        ConfigurateLoader loader = new ConfigurateLoader(plugin, TypeSerializerCollection.builder().build());
        try {
            ConfigurationNode config = loader.loadUserFile("config.yml");

            this.blockRequirements = new ArrayList<>();
            List<? extends ConfigurationNode> blockNodes = config.node("requirement", "blocks", "list").childrenList();
            for (ConfigurationNode blockNode : blockNodes) {
                Material material = Material.valueOf(blockNode.node("material").getString("").toUpperCase(Locale.ROOT));
                boolean allowPlace = blockNode.node("allow_place").getBoolean(false);
                boolean allowBreak = blockNode.node("allow_break").getBoolean(false);
                boolean allowHarvest = blockNode.node("allow_harvest").getBoolean(false);

                List<? extends ConfigurationNode> requirementNodes = blockNode.node("requirements").childrenList();
                List<RequirementNode> nodes = new ArrayList<>();

                for (ConfigurationNode requirementNode : requirementNodes) {
                    String type = requirementNode.node("type").getString("");
                    String message = requirementNode.node("message").getString("");

                    switch (type) {
                        case "skill_level" -> {
                            Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(requirementNode.node("skill").getString("").toLowerCase(Locale.ROOT)));
                            int level = requirementNode.node("level").getInt();
                            nodes.add(new SkillNode(plugin, skill, level, message));
                        }
                        case "permission" -> {
                            String permission = requirementNode.node("permission").getString();
                            nodes.add(new PermissionNode(plugin, permission, message));
                        }
                        case "excluded_world" -> {
                            String[] worlds = requirementNode.node("worlds").getList(String.class, new ArrayList<>()).toArray(new String[0]);
                            nodes.add(new ExcludedWorldNode(plugin, worlds, message));
                        }
                        case "stat" -> {
                            Stat stat = plugin.getStatManager().getEnabledStats().stream()
                                    .filter(s -> s.getId().equals(NamespacedId.fromDefault(requirementNode.node("stat").getString("").toLowerCase(Locale.ROOT))))
                                    .findFirst()
                                    .orElse(null);
                            int value = requirementNode.node("value").getInt();
                            nodes.add(new StatNode(plugin, stat, value, message));
                        }
                        default -> plugin.logger().warn("Unknown requirement type: " + type);
                    }
                }

                BlockRequirement blockRequirement = new BlockRequirement(material, allowPlace, allowBreak, allowHarvest, nodes);
                blockRequirements.add(blockRequirement);
            }
            if (!blockRequirements.isEmpty()) {
                plugin.logger().info("Loaded " + blockRequirements.size() + " block requirement" + (blockRequirements.size() != 1 ? "s" : ""));
            }
        } catch (IOException e) {
            plugin.logger().warn("Error loading block requirements: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Set<GlobalRequirement> getGlobalRequirements() {
        return globalRequirements;
    }

    public List<BlockRequirement> getBlocks() {
        return blockRequirements;
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
