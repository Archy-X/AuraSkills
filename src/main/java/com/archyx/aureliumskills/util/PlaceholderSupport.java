package com.archyx.aureliumskills.util;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.skills.PlayerSkillInstance;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.Stat;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PlaceholderSupport extends PlaceholderExpansion {

    private final AureliumSkills plugin;
    private final NumberFormat format1;
    private final NumberFormat format2;
    private final String[] xpIdentifiers = new String[] {"xp_required_formatted_", "xp_required_", "xp_progress_int_", "xp_progress_1_", "xp_progress_", "xp_int_", "xp_formatted_", "xp_"};

    public PlaceholderSupport(AureliumSkills plugin) {
        this.plugin = plugin;
        format1 = new DecimalFormat("#,###.#");
        format2 = new DecimalFormat("#,###.##");
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getIdentifier() {
        return "aureliumskills";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        //Gets total combined skill level
        if (identifier.equals("power")) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData != null) {
                return String.valueOf(playerData.getPowerLevel());
            }
        }

        //Gets HP with scaling as an integer
        if (identifier.equals("hp")) {
            return String.valueOf((int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)));
        }

        //Gets HP with scaling with 1 decimal
        if (identifier.equals("hp_1")) {
            return String.valueOf(format1.format(player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)));
        }

        //Gets max hp
        if (identifier.equals("hp_max")) {
            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (attribute != null) {
                return String.valueOf((int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)));
            }
            else {
                return "";
            }
        }

        //Gets HP with scaling with 2 decimal
        if (identifier.equals("hp_2")) {
            return String.valueOf(format2.format(player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)));
        }

        //Gets HP Percent as an integer
        if (identifier.equals("hp_percent")) {
            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (attribute != null) {
                return String.valueOf((int) (player.getHealth() / attribute.getValue()));
            }
            else {
                return "";
            }
        }

        //Gets mana
        if (identifier.equals("mana")) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData != null) {
                return String.valueOf(playerData.getMana());
            }
        }

        // Gets mana rounded to an integer
        if (identifier.equals("mana_int")) {
            return String.valueOf(Math.round(plugin.getManaManager().getMana(player.getUniqueId())));
        }

        //Gets max mana
        if (identifier.equals("mana_max")) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData != null) {
                return String.valueOf(playerData.getMaxMana());
            }
        }

        //Gets stat values
        for (Stat stat : Stat.values()) {
            if (identifier.equals(stat.name().toLowerCase(Locale.ENGLISH))) {
                PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
                if (playerData != null) {
                    return String.valueOf(playerData.getStatLevel(stat));
                }
            }
        }

        //Gets skill levels
        for (Skill skill : Skill.values()) {
            if (identifier.equals(skill.name().toLowerCase(Locale.ENGLISH))) {
                PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
                if (playerData != null) {
                    return String.valueOf(playerData.getSkillLevel(skill));
                }
            }
            else if (identifier.equals(skill.name().toLowerCase(Locale.ENGLISH) + "_roman")) {
                PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
                if (playerData != null) {
                    return RomanNumber.toRoman(playerData.getSkillLevel(skill));
                }
            }
        }

        if (identifier.startsWith("lb_")) {
            String leaderboardType = LoreUtil.replace(identifier, "lb_", "");
            if (leaderboardType.startsWith("power_")) {
                int place = NumberUtils.toInt(LoreUtil.replace(leaderboardType, "power_", ""));
                if (place > 0) {
                    List<PlayerSkillInstance> list = plugin.getLeaderboard().readPowerLeaderboard(place, 1);
                    if (list.size() > 0) {
                        PlayerSkillInstance playerSkill = list.get(0);
                        return Bukkit.getOfflinePlayer(playerSkill.getPlayerId()).getName() + " - " + playerSkill.getPowerLevel();
                    }
                    else return "";
                }
                else {
                    if (identifier.endsWith("name")) {
                        int namePlace = NumberUtils.toInt(LoreUtil.replace(leaderboardType, "power_", "", "_name", ""));
                        if (namePlace > 0) {
                            List<PlayerSkillInstance> list = plugin.getLeaderboard().readPowerLeaderboard(namePlace, 1);
                            if (list.size() > 0) {
                                PlayerSkillInstance playerSkill = list.get(0);
                                return Bukkit.getOfflinePlayer(playerSkill.getPlayerId()).getName();
                            }
                            else return "";
                        }
                    }
                    else if (identifier.endsWith("value")) {
                        int valuePlace = NumberUtils.toInt(LoreUtil.replace(leaderboardType, "power_", "", "_value", ""));
                        if (valuePlace > 0) {
                            List<PlayerSkillInstance> list = plugin.getLeaderboard().readPowerLeaderboard(valuePlace, 1);
                            if (list.size() > 0) {
                                PlayerSkillInstance playerSkill = list.get(0);
                                return String.valueOf(playerSkill.getPowerLevel());
                            }
                            else return "";
                        }
                    }
                }
            }
            else {
                for (Skill skill : Skill.values()) {
                    if (leaderboardType.startsWith(skill.name().toLowerCase(Locale.ENGLISH) + "_")) {
                        int place = NumberUtils.toInt(LoreUtil.replace(leaderboardType, skill.name().toLowerCase(Locale.ENGLISH) + "_", ""));
                        if (place > 0) {
                            List<PlayerSkillInstance> list = plugin.getLeaderboard().readSkillLeaderboard(skill, 1, 1);
                            if (list.size() > 0) {
                                PlayerSkillInstance playerSkill = list.get(0);
                                return Bukkit.getOfflinePlayer(playerSkill.getPlayerId()).getName() + " - " + playerSkill.getSkillLevel(skill);
                            }
                            else return "";
                        }
                        else {
                            if (identifier.endsWith("name")) {
                                int namePlace = NumberUtils.toInt(LoreUtil.replace(leaderboardType, skill.name().toLowerCase(Locale.ENGLISH) + "_", "", "_name", ""));
                                if (namePlace > 0) {
                                    List<PlayerSkillInstance> list = plugin.getLeaderboard().readSkillLeaderboard(skill, namePlace, 1);
                                    if (list.size() > 0) {
                                        PlayerSkillInstance playerSkill = list.get(0);
                                        return Bukkit.getOfflinePlayer(playerSkill.getPlayerId()).getName();
                                    }
                                    else return "";
                                }
                            }
                            else if (identifier.endsWith("value")) {
                                int valuePlace = NumberUtils.toInt(LoreUtil.replace(leaderboardType, skill.name().toLowerCase(Locale.ENGLISH) + "_", "", "_value", ""));
                                if (valuePlace > 0) {
                                    List<PlayerSkillInstance> list = plugin.getLeaderboard().readSkillLeaderboard(skill, valuePlace, 1);
                                    if (list.size() > 0) {
                                        PlayerSkillInstance playerSkill = list.get(0);
                                        return String.valueOf(playerSkill.getSkillLevel(skill));
                                    }
                                    else return "";
                                }
                            }
                        }
                    }
                }
            }
        }

        if (identifier.equals("rank")) {
            return String.valueOf(plugin.getLeaderboard().getPowerRank(player.getUniqueId()));
        }

        if (identifier.startsWith("rank_")) {
            String skillName = LoreUtil.replace(identifier, "rank_", "");
            try {
                Skill skill = Skill.valueOf(skillName.toUpperCase());
                return String.valueOf(plugin.getLeaderboard().getSkillRank(skill, player.getUniqueId()));
            }
            catch (Exception e) {
                return "";
            }
        }

        for (String id : xpIdentifiers) {
            if (identifier.startsWith(id)) {
                String skillName = LoreUtil.replace(identifier, id, "");
                try {
                    Skill skill = Skill.valueOf(skillName.toUpperCase());
                    PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
                    if (playerData != null) {
                        switch (id) {
                            case "xp_required_formatted_":
                                return BigNumber.withSuffix(plugin.getLeveler().getXpRequired(playerData.getSkillLevel(skill) + 1));
                            case "xp_required_":
                                return String.valueOf(plugin.getLeveler().getXpRequired(playerData.getSkillLevel(skill) + 1));
                            case "xp_progress_int_":
                                return String.valueOf(Math.round(playerData.getSkillXp(skill) / (double) (plugin.getLeveler().getXpRequired(playerData.getSkillLevel(skill) + 1)) * 100));
                            case "xp_progress_1_":
                                return NumberUtil.format1(playerData.getSkillXp(skill) / (double) (plugin.getLeveler().getXpRequired(playerData.getSkillLevel(skill) + 1)) * 100);
                            case "xp_progress_":
                                return String.valueOf(playerData.getSkillXp(skill) / (double) (plugin.getLeveler().getXpRequired(playerData.getSkillLevel(skill) + 1)) * 100);
                            case "xp_int_":
                                return String.valueOf(Math.round(playerData.getSkillXp(skill)));
                            case "xp_formatted_":
                                return BigNumber.withSuffix(Math.round(playerData.getSkillXp(skill)));
                            case "xp_":
                                return String.valueOf(playerData.getSkillXp(skill));
                        }
                    }
                } catch (Exception e) { return ""; }
            }
        }

        if (identifier.equals("multiplier")) {
            return NumberUtil.format2(plugin.getLeveler().getMultiplier(player));
        }

        if (identifier.equals("multiplier_percent")) {
            return String.valueOf(Math.round((plugin.getLeveler().getMultiplier(player) - 1) * 100));
        }

        return null;
    }

}
