package com.archyx.aureliumskills.requirement;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.skills.Skill;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class RequirementManager implements Listener {

    private Set<GlobalRequirement> globalItemRequirements;
    private Set<GlobalRequirement> globalArmorRequirements;
    private final Map<UUID, Integer> errorMessageTimer;
    private final AureliumSkills plugin;

    public RequirementManager(AureliumSkills plugin) {
        errorMessageTimer = new HashMap<>();
        this.plugin = plugin;
        tickTimer();
    }

    public void load() {
        FileConfiguration config = plugin.getConfig();
        this.globalItemRequirements = new HashSet<>();
        this.globalArmorRequirements = new HashSet<>();
        List<String> globalItems = config.getStringList("requirement.item.global");
        for (String text : globalItems) {
            String[] splitText = text.split(" ");
            Optional<XMaterial> potentialMaterial = XMaterial.matchXMaterial(splitText[0].toUpperCase());
            if (potentialMaterial.isPresent()) {
                XMaterial material = potentialMaterial.get();
                Map<Skill, Integer> requirements = new HashMap<>();
                for (int i = 1; i < splitText.length; i++) {
                    String requirementText = splitText[i];
                    try {
                        Skill skill = Skill.valueOf(requirementText.split(":")[0]);
                        int level = Integer.parseInt(requirementText.split(":")[1]);
                        requirements.put(skill, level);
                    }
                    catch (Exception e) {
                        Bukkit.getLogger().warning("[AureliumSkills] Error parsing global skill item requirement skill level pair with text " + requirementText);
                    }
                }
                GlobalRequirement globalRequirement = new GlobalRequirement(material, requirements);
                globalItemRequirements.add(globalRequirement);
            }
            else {
                Bukkit.getLogger().warning("[AureliumSkills] Error parsing global skill item requirement material with text " + splitText[0]);
            }
        }
        List<String> globalArmor = config.getStringList("requirement.armor.global");
        for (String text : globalArmor) {
            String[] splitText = text.split(" ");
            Optional<XMaterial> potentialMaterial = XMaterial.matchXMaterial(splitText[0].toUpperCase());
            if (potentialMaterial.isPresent()) {
                XMaterial material = potentialMaterial.get();
                Map<Skill, Integer> requirements = new HashMap<>();
                for (int i = 1; i < splitText.length; i++) {
                    String requirementText = splitText[i];
                    try {
                        Skill skill = Skill.valueOf(requirementText.split(":")[0]);
                        int level = Integer.parseInt(requirementText.split(":")[1]);
                        requirements.put(skill, level);
                    }
                    catch (Exception e) {
                        Bukkit.getLogger().warning("[AureliumSkills] Error parsing global skill armor requirement skill level pair with text " + requirementText);
                    }
                }
                GlobalRequirement globalRequirement = new GlobalRequirement(material, requirements);
                globalArmorRequirements.add(globalRequirement);
            }
            else {
                Bukkit.getLogger().warning("[AureliumSkills] Error parsing global skill armor requirement material with text " + splitText[0]);
            }
        }
    }

    public Set<GlobalRequirement> getGlobalItemRequirements() {
        return globalItemRequirements;
    }

    public Set<GlobalRequirement> getGlobalArmorRequirements() {
        return globalArmorRequirements;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        errorMessageTimer.remove(event.getPlayer().getUniqueId());
    }

    public void tickTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID id : errorMessageTimer.keySet()) {
                    int timer = errorMessageTimer.get(id);
                    if (timer != 0) {
                        errorMessageTimer.put(id, errorMessageTimer.get(id) - 1);
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 5);
    }

    public Map<UUID, Integer> getErrorMessageTimer() {
        return errorMessageTimer;
    }

}
