package com.archyx.aureliumskills.api;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.Stat;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AureliumAPI {

    private static AureliumSkills plugin;

    public static void setPlugin(AureliumSkills plugin) {
        AureliumAPI.plugin = plugin;
    }

    /**
     * Gets the current mana of a player
     * @return the current mana of a player
     */
    public static double getMana(Player player) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null) {
            return playerData.getMana();
        }
        return 0.0;
    }

    @Deprecated
    public static double getMana(UUID playerId) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(playerId);
        if (playerData != null) {
            return playerData.getMana();
        }
        return 0.0;
    }

    /**
     * Gets the max mana of a player
     * @return the max mana of a player
     */
    public static double getMaxMana(Player player) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null) {
            return playerData.getMaxMana();
        } else {
            return OptionL.getDouble(Option.BASE_MANA);
        }
    }

    @Deprecated
    public static double getMaxMana(UUID playerId) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(playerId);
        if (playerData != null) {
            return playerData.getMaxMana();
        } else {
            return OptionL.getDouble(Option.BASE_MANA);
        }
    }

    /**
     * Sets a player's mana to an amount
     */
    public static void setMana(Player player, double amount) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null) {
            playerData.setMana(amount);
        }
    }

    @Deprecated
    public static void setMana(UUID playerId, double amount) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(playerId);
        if (playerData != null) {
            playerData.setMana(amount);
        }
    }

    /**
     * Adds Skill XP to a player for a certain skill, and includes multiplier permissions
     */
    public static void addXp(Player player, Skill skill, double amount) {
        plugin.getLeveler().addXp(player, skill, amount);
    }

    /**
     * Adds Skill XP to a player for a certain skill, without multipliers
     */
    public static void addXpRaw(Player player, Skill skill, double amount) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null) {
            playerData.addSkillXp(skill, amount);
            plugin.getLeveler().checkLevelUp(player, skill);
        }
    }

    /**
     * Adds Skill XP to an offline player for a certain skill
     * No longer works in beta, offline players are not stored in memory anymore.
     */
    @Deprecated
    public static boolean addXpOffline(OfflinePlayer player, Skill skill, double amount) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData != null) {
            playerData.addSkillXp(skill, amount);
            return true;
        }
        else {
            return false;
        }
    }

    @Deprecated
    public static boolean addXpOffline(UUID playerId, Skill skill, double amount) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(playerId);
        if (playerData != null) {
            playerData.addSkillXp(skill, amount);
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
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null) {
            return playerData.getSkillLevel(skill);
        }
        else {
            return 1;
        }
    }

    @Deprecated
    public static int getSkillLevel(UUID playerId, Skill skill) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(playerId);
        if (playerData != null) {
            return playerData.getSkillLevel(skill);
        }
        else {
            return 1;
        }
    }

    /**
     * Gets the skill xp of a player
     * @param player The player to get from
     * @param skill The skill to get
     * @return The current skill xp
     */
    public static double getXp(Player player, Skill skill) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null) {
            return playerData.getSkillXp(skill);
        }
        else {
            return 1;
        }
    }

    @Deprecated
    public static double getXp(UUID playerId, Skill skill) {
        PlayerData playerSkill = plugin.getPlayerManager().getPlayerData(playerId);
        if (playerSkill != null) {
            return playerSkill.getSkillXp(skill);
        }
        else {
            return 1;
        }
    }

    /**
     * Gets the stat level of a player
     * @param player The player to get from
     * @param stat The stat to get
     * @return The stat level
     */
    public static double getStatLevel(Player player, Stat stat) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null) {
            return playerData.getStatLevel(stat);
        }
        else {
            return 0;
        }
    }

    @Deprecated
    public static double getStatLevel(UUID playerId, Stat stat) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(playerId);
        if (playerData != null) {
            return playerData.getStatLevel(stat);
        }
        else {
            return 0;
        }
    }

    /**
     * Gets the base stat level of a player, without modifiers
     * @param player The player to get from
     * @param stat The stat to get
     * @return The stat level without modifiers
     */
    @Deprecated
    public static double getBaseStatLevel(Player player, Stat stat) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null) {
            return playerData.getStatLevel(stat);
        }
        else {
            return 0;
        }
    }

    @Deprecated
    public static double getBaseStatLevel(UUID playerId, Stat stat) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(playerId);
        if (playerData != null) {
            return playerData.getStatLevel(stat);
        }
        else {
            return 0;
        }
    }

    /**
     * Adds a stat modifier to a player
     * @param player The player to add to
     * @param name The name of the stat modifier
     * @param stat The stat to add to
     * @param value The value of the modifier
     * @return true if a modifier was added, false if the player does not have a skills profile
     */
    public static boolean addStatModifier(Player player, String name, Stat stat, double value) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null) {
            playerData.addStatModifier(new StatModifier(name, stat, value));
            return true;
        }
        return false;
    }

    @Deprecated
    public static boolean addStatModifier(UUID playerId, String name, Stat stat, double value) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(playerId);
        if (playerData != null) {
            playerData.addStatModifier(new StatModifier(name, stat, value));
            return true;
        }
        return false;
    }

    /**
     * Removes a stat modifier from a player
     * @param player The player to remove from
     * @param name The name of the stat modifier
     * @return true if the operation was successful, false if the stat modifier was not found or the player does not have a skills profile
     */
    public static boolean removeStatModifier(Player player, String name) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null) {
            playerData.removeStatModifier(name);
            return true;
        }
        return false;
    }

    @Deprecated
    public static boolean removeStatModifier(UUID playerId, String name) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(playerId);
        if (playerData != null) {
            playerData.removeStatModifier(name);
            return true;
        }
        return false;
    }


}
