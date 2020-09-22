package com.archyx.aureliumskills.api;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.levelers.Leveler;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class AureliumAPI {

    /**
     * Gets the current mana of a player
     * @return the current mana of a player
     */
    public static int getMana(Player player) {
        return AureliumSkills.manaManager.getMana(player.getUniqueId());
    }

    /**
     * Gets the max mana of a player
     * @return the max mana of a player
     */
    public static int getMaxMana(Player player) {
        return AureliumSkills.manaManager.getMaxMana(player.getUniqueId());
    }

    /**
     * Sets a player's mana to an amount
     */
    public static void setMana(Player player, int amount) {
        AureliumSkills.manaManager.setMana(player.getUniqueId(), amount);
    }

    /**
     * Adds Skill XP to a player for a certain skill, and includes multiplier permissions
     */
    public static void addXp(Player player, Skill skill, double amount) {
        Leveler.addXp(player, skill, amount);
    }

    /**
     * Adds Skill XP to a player for a certain skill, without multipliers
     */
    public static void addXpRaw(Player player, Skill skill, double amount) {
        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
        if (playerSkill != null) {
            playerSkill.addXp(skill, amount);
            Leveler.checkLevelUp(player, skill);
            Leveler.sendActionBarMessage(player, skill, amount);
        }
    }

    /**
     * Adds Skill XP to an offline player for a certain skill
     */
    public static boolean addXpOffline(OfflinePlayer player, Skill skill, double amount) {
        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
        if (playerSkill != null) {
            playerSkill.addXp(skill, amount);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Gets the skill level of a player
     * @return the skill level of a player, or 1 if player does not have a skills profile
     */
    public static int getSkillLevel(Player player, Skill skill) {
        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
        if (playerSkill != null) {
            return playerSkill.getSkillLevel(skill);
        }
        else {
            return 1;
        }
    }

    public static double getXp(Player player, Skill skill) {
        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
        if (playerSkill != null) {
            return playerSkill.getXp(skill);
        }
        else {
            return 1;
        }
    }

}
