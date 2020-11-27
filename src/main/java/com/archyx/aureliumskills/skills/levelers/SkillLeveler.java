package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.*;
import com.archyx.aureliumskills.skills.abilities.Ability;
import com.cryptomorin.xseries.XMaterial;
import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;

public class SkillLeveler {

    public final AureliumSkills plugin;
    private final SourceManager sourceManager;
    private BukkitAPIHelper bukkitAPIHelper;
    private Ability ability;

    public SkillLeveler(AureliumSkills plugin) {
        this.plugin = plugin;
        this.sourceManager = plugin.getSourceManager();
        if (AureliumSkills.mythicMobsEnabled) {
            bukkitAPIHelper = new BukkitAPIHelper();
        }
    }

    public SkillLeveler(AureliumSkills plugin, Ability ability) {
        this.plugin = plugin;
        this.ability = ability;
        this.sourceManager = plugin.getSourceManager();
        if (AureliumSkills.mythicMobsEnabled) {
            bukkitAPIHelper = new BukkitAPIHelper();
        }
    }

    public double getXp(Source source) {
        return sourceManager.getXp(source);
    }

    public double getXp(Player player, Source source) {
        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
        if (playerSkill != null) {
            double output = getXp(source);
            if (ability != null) {
                if (AureliumSkills.abilityOptionManager.isEnabled(ability)) {
                    double modifier = 1;
                    modifier += ability.getValue(playerSkill.getAbilityLevel(ability)) / 100;
                    output *= modifier;
                }
            }
            return output;
        }
        return 0.0;
    }

    public double getXp(Player player, double input) {
        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
        if (playerSkill != null) {
            double output = input;
            if (ability != null) {
                if (AureliumSkills.abilityOptionManager.isEnabled(ability)) {
                    double modifier = 1;
                    modifier += ability.getValue(playerSkill.getAbilityLevel(ability)) / 100;
                    output *= modifier;
                }
            }
            return output;
        }
        return 0.0;
    }

    public double getXp(Player player, double input, Ability ability) {
        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
        if (playerSkill != null) {
            double output = input;
            if (ability != null) {
                if (AureliumSkills.abilityOptionManager.isEnabled(ability)) {
                    double modifier = 1;
                    modifier += ability.getValue(playerSkill.getAbilityLevel(ability)) / 100;
                    output *= modifier;
                }
            }
            return output;
        }
        return 0.0;
    }

    public void checkCustomBlocks(Player player, Block block, Skill skill) {
        // Check custom blocks
        Map<XMaterial, Double> customBlocks = sourceManager.getCustomBlocks(skill);
        if (customBlocks != null) {
            for (Map.Entry<XMaterial, Double> entry : customBlocks.entrySet()) {
                if (XMaterial.isNewVersion()) {
                    if (entry.getKey().parseMaterial() == block.getType()) {
                        if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && block.hasMetadata("skillsPlaced")) {
                            return;
                        }
                        Leveler.addXp(player, skill, getXp(player, entry.getValue()));
                        break;
                    }
                }
                else {
                    if (entry.getKey().parseMaterial() == block.getType() && block.getData() == entry.getKey().getData()) {
                        if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && block.hasMetadata("skillsPlaced")) {
                            return;
                        }
                        Leveler.addXp(player, skill, getXp(player, entry.getValue()));
                        break;
                    }
                }
            }
        }
    }

    public boolean isMythicMob(Entity entity) {
        if (AureliumSkills.mythicMobsEnabled) {
            if (bukkitAPIHelper != null) {
                if (bukkitAPIHelper.isMythicMob(entity)) {
                    return sourceManager.getCustomMobSet().contains(bukkitAPIHelper.getMythicMobInstance(entity).getType().getInternalName());
                }
            }
        }
        return false;
    }

}
