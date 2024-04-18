package dev.aurelium.auraskills.bukkit.hooks;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.mana.ReadiedManaAbility;
import dev.aurelium.auraskills.common.leaderboard.SkillValue;
import dev.aurelium.auraskills.common.ui.ActionBarType;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.math.BigNumber;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.common.util.math.RomanNumber;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class PlaceholderApiProvider extends PlaceholderExpansion {

    private final AuraSkills plugin;
    private final String identifier;
    private final String[] xpIdentifiers = new String[]{"xp_required_formatted_", "xp_required_", "xp_progress_int_", "xp_progress_1_", "xp_progress_", "xp_int_", "xp_formatted_", "xp_"};

    public PlaceholderApiProvider(AuraSkills plugin, String identifier) {
        this.plugin = plugin;
        this.identifier = identifier;
    }

    @Override
    public @NotNull String getIdentifier() {
        return identifier;
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        // Check placeholders that don't need a player first
        if (identifier.startsWith("lb_")) {
            return checkLeaderboardPlaceholders(identifier);
        }

        if (player == null) {
            return "";
        }

        //Gets total combined skill level
        if (identifier.equals("power")) {
            User user = plugin.getUser(player);
            return String.valueOf(user.getPowerLevel());
        }

        // Gets skill average
        if (identifier.equals("average")) {
            User user = plugin.getUser(player);
            return String.valueOf(user.getSkillAverage());
        }
        // Get skill average as integer
        if (identifier.equals("average_int")) {
            User user = plugin.getUser(player);
            return String.valueOf(Math.round(user.getSkillAverage()));
        }
        // Get skill average rounded to 1 decimal
        if (identifier.equals("average_1")) {
            User user = plugin.getUser(player);
            return NumberUtil.format1(user.getSkillAverage());
        }

        //Gets HP with scaling as an integer
        if (identifier.equals("hp")) {
            return String.valueOf(Math.round(player.getHealth() * Traits.HP.optionDouble("action_bar_scaling")));
        }

        //Gets HP with scaling with 1 decimal
        if (identifier.equals("hp_1")) {
            return NumberUtil.format1(player.getHealth() * Traits.HP.optionDouble("action_bar_scaling"));
        }

        //Gets max hp
        if (identifier.equals("hp_max")) {
            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (attribute != null) {
                return String.valueOf(Math.round(attribute.getValue() * Traits.HP.optionDouble("action_bar_scaling")));
            } else {
                return "";
            }
        }

        //Gets HP with scaling with 2 decimal
        if (identifier.equals("hp_2")) {
            return NumberUtil.format2(player.getHealth() * Traits.HP.optionDouble("action_bar_scaling"));
        }

        //Gets HP Percent as an integer
        if (identifier.equals("hp_percent")) {
            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (attribute != null) {
                return String.valueOf(Math.round(player.getHealth() / attribute.getValue()));
            } else {
                return "";
            }
        }

        //Gets mana
        if (identifier.equals("mana")) {
            User user = plugin.getUser(player);
            return String.valueOf(user.getMana());
        }

        // Gets mana rounded to an integer
        if (identifier.equals("mana_int")) {
            User user = plugin.getUser(player);
            return String.valueOf(Math.round(user.getMana()));
        }

        // Gets max mana
        if (identifier.equals("mana_max")) {
            User user = plugin.getUser(player);
            return String.valueOf(user.getMaxMana());
        }

        // Gets max mana rounded to an integer
        if (identifier.equals("mana_max_int")) {
            User user = plugin.getUser(player);
            return String.valueOf(Math.round(user.getMaxMana()));
        }

        //Gets stat values
        for (Stat stat : plugin.getStatRegistry().getValues()) {
            if (identifier.equals(stat.name().toLowerCase(Locale.ROOT))) {
                User user = plugin.getUser(player);
                return String.valueOf(user.getStatLevel(stat));
            } else if (identifier.equals(stat.name().toLowerCase(Locale.ROOT) + "_int")) {
                User user = plugin.getUser(player);
                return String.valueOf(Math.round(user.getStatLevel(stat)));
            }
        }

        //Gets skill levels
        for (Skill skill : plugin.getSkillRegistry().getValues()) {
            if (identifier.equals(skill.name().toLowerCase(Locale.ROOT))) {
                User user = plugin.getUser(player);
                return String.valueOf(user.getSkillLevel(skill));
            } else if (identifier.equals(skill.name().toLowerCase(Locale.ROOT) + "_roman")) {
                User user = plugin.getUser(player);
                return RomanNumber.toRoman(user.getSkillLevel(skill), plugin);
            }
        }

        // Gets ability levels
        for (Ability ability : plugin.getAbilityRegistry().getValues()) {
            if (identifier.equals(ability.name().toLowerCase(Locale.ROOT))) {
                User user = plugin.getUser(player);
                return String.valueOf(user.getAbilityLevel(ability));
            } else if (identifier.equals(ability.name().toLowerCase(Locale.ROOT) + "_value")) {
                User user = plugin.getUser(player);
                return String.valueOf(ability.getValue(user.getAbilityLevel(ability)));
            } else if (identifier.equals(ability.name().toLowerCase(Locale.ROOT) + "_value_2")) {
                if (ability.hasSecondaryValue()) {
                    User user = plugin.getUser(player);
                    return String.valueOf(ability.getSecondaryValue(user.getAbilityLevel(ability)));
                }
            }
        }

        // Gets mana ability levels
        for (ManaAbility manaAbility : plugin.getManaAbilityRegistry().getValues()) {
            if (identifier.equals(manaAbility.name().toLowerCase(Locale.ROOT))) {
                User user = plugin.getUser(player);
                return String.valueOf(user.getManaAbilityLevel(manaAbility));
            } else if (identifier.equals(manaAbility.name().toLowerCase(Locale.ROOT) + "_value")) {
                User user = plugin.getUser(player);
                return String.valueOf(manaAbility.getValue(user.getManaAbilityLevel(manaAbility)));
            } else if (identifier.equals(manaAbility.name().toLowerCase(Locale.ROOT) + "_active")) {
                User user = plugin.getUser(player);
                return String.valueOf(user.getManaAbilityData(manaAbility).isActivated());
            }
        }

        if (identifier.equals("rank")) {
            return String.valueOf(plugin.getLeaderboardManager().getPowerRank(player.getUniqueId()));
        }

        if (identifier.startsWith("rank_")) {
            String skillName = TextUtil.replace(identifier, "rank_", "");
            Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(skillName));
            if (skill != null) {
                return String.valueOf(plugin.getLeaderboardManager().getSkillRank(skill, player.getUniqueId()));
            }
        }

        for (String id : xpIdentifiers) {
            if (!identifier.startsWith(id)) {
                continue;
            }
            String skillName = TextUtil.replace(identifier, id, "");

            Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(skillName));
            if (skill == null) {
                continue;
            }
            User user = plugin.getUser(player);
            switch (id) {
                case "xp_required_formatted_":
                    return BigNumber.withSuffix(plugin.getXpRequirements().getXpRequired(skill, user.getSkillLevel(skill) + 1));
                case "xp_required_":
                    return String.valueOf(plugin.getXpRequirements().getXpRequired(skill, user.getSkillLevel(skill) + 1));
                case "xp_progress_int_":
                    return String.valueOf(Math.round(user.getSkillXp(skill) / (double) (plugin.getXpRequirements().getXpRequired(skill, user.getSkillLevel(skill) + 1)) * 100));
                case "xp_progress_1_":
                    return NumberUtil.format1(user.getSkillXp(skill) / (double) (plugin.getXpRequirements().getXpRequired(skill, user.getSkillLevel(skill) + 1)) * 100);
                case "xp_progress_":
                    return String.valueOf(user.getSkillXp(skill) / (double) (plugin.getXpRequirements().getXpRequired(skill, user.getSkillLevel(skill) + 1)) * 100);
                case "xp_int_":
                    return String.valueOf(Math.round(user.getSkillXp(skill)));
                case "xp_formatted_":
                    return BigNumber.withSuffix(Math.round(user.getSkillXp(skill)));
                case "xp_":
                    return String.valueOf(user.getSkillXp(skill));
            }
        }

        if (identifier.startsWith("multiplier")) {
            User user = plugin.getUser(player);
            if (identifier.equals("multiplier")) {
                return NumberUtil.format2(plugin.getLevelManager().getGenericMultiplier(user));
            }
            String skillName = TextUtil.replace(identifier, "multiplier_", "");
            Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(skillName));
            if (skill != null) {
                return NumberUtil.format2(plugin.getLevelManager().calculateMultiplier(user, skill));
            }
        }

        if (identifier.startsWith("multiplier_percent")) {
            User user = plugin.getUser(player);
            if (identifier.equals("multiplier_percent")) {
                return String.valueOf(Math.round((plugin.getLevelManager().getGenericMultiplier(user) - 1) * 100));
            }
            String skillName = TextUtil.replace(identifier, "multiplier_percent_", "");
            Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(skillName));
            if (skill != null) {
                return String.valueOf(Math.round((plugin.getLevelManager().calculateMultiplier(user, skill) - 1) * 100));
            }
        }

        // Get Actionbar Status
        if (identifier.startsWith("actionbar_status")) {
            if (identifier.equals("actionbar_status")) {
                User user = plugin.getUser(player);
                if (user.isActionBarEnabled(ActionBarType.IDLE)) {
                    return "true";
                } else {
                    return "false";
                }
            }
        }

        return null;
    }

    private String checkLeaderboardPlaceholders(String identifier) {
        String leaderboardType = TextUtil.replace(identifier, "lb_", "");
        if (leaderboardType.startsWith("power_")) {
            int place = NumberUtil.toInt(TextUtil.replace(leaderboardType, "power_", ""));
            if (place > 0) {
                List<SkillValue> list = plugin.getLeaderboardManager().getPowerLeaderboard(place, 1);
                if (!list.isEmpty()) {
                    SkillValue skillValue = list.get(0);
                    String name = Bukkit.getOfflinePlayer(skillValue.id()).getName();
                    return (name != null ? name : "?") + " - " + skillValue.level();
                } else return "";
            } else {
                if (identifier.endsWith("name")) {
                    int namePlace = NumberUtil.toInt(TextUtil.replace(leaderboardType, "power_", "", "_name", ""));
                    if (namePlace > 0) {
                        List<SkillValue> list = plugin.getLeaderboardManager().getPowerLeaderboard(namePlace, 1);
                        if (!list.isEmpty()) {
                            SkillValue skillValue = list.get(0);
                            String name = Bukkit.getOfflinePlayer(skillValue.id()).getName();
                            return name != null ? name : "?";
                        } else return "";
                    }
                } else if (identifier.endsWith("value")) {
                    int valuePlace = NumberUtil.toInt(TextUtil.replace(leaderboardType, "power_", "", "_value", ""));
                    if (valuePlace > 0) {
                        List<SkillValue> list = plugin.getLeaderboardManager().getPowerLeaderboard(valuePlace, 1);
                        if (!list.isEmpty()) {
                            SkillValue playerSkill = list.get(0);
                            return String.valueOf(playerSkill.level());
                        } else return "";
                    }
                }
            }
        } else {
            for (Skill skill : plugin.getSkillManager().getEnabledSkills()) {
                if (!leaderboardType.startsWith(skill.name().toLowerCase(Locale.ROOT) + "_")) {
                    continue;
                }
                int place = NumberUtil.toInt(TextUtil.replace(leaderboardType, skill.name().toLowerCase(Locale.ROOT) + "_", ""));
                if (place > 0) {
                    List<SkillValue> list = plugin.getLeaderboardManager().getLeaderboard(skill, place, 1);
                    if (!list.isEmpty()) {
                        SkillValue skillValue = list.get(0);
                        String name = Bukkit.getOfflinePlayer(skillValue.id()).getName();
                        return (name != null ? name : "?") + " - " + skillValue.level();
                    } else return "";
                } else {
                    if (identifier.endsWith("name")) {
                        int namePlace = NumberUtil.toInt(TextUtil.replace(leaderboardType, skill.name().toLowerCase(Locale.ROOT) + "_", "", "_name", ""));
                        if (namePlace > 0) {
                            List<SkillValue> list = plugin.getLeaderboardManager().getLeaderboard(skill, namePlace, 1);
                            if (!list.isEmpty()) {
                                SkillValue skillValue = list.get(0);
                                String name = Bukkit.getOfflinePlayer(skillValue.id()).getName();
                                return name != null ? name : "?";
                            } else return "";
                        }
                    } else if (identifier.endsWith("value")) {
                        int valuePlace = NumberUtil.toInt(TextUtil.replace(leaderboardType, skill.name().toLowerCase(Locale.ROOT) + "_", "", "_value", ""));
                        if (valuePlace > 0) {
                            List<SkillValue> list = plugin.getLeaderboardManager().getLeaderboard(skill, valuePlace, 1);
                            if (!list.isEmpty()) {
                                SkillValue skillValue = list.get(0);
                                return String.valueOf(skillValue.level());
                            } else return "";
                        }
                    }
                }
            }
        }
        return "";
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return List.of(
                "%auraskills_power%",
                "%auraskills_[skill]%",
                "%auraskills_[skill]_roman%",
                "%auraskills_[stat]%",
                "%auraskills_[stat]_int%",
                "%auraskills_[ability]%",
                "%auraskills_[ability]_active%",
                "%auraskills_[ability]_value%",
                "%auraskills_[ability]_value_2%",
                "%auraskills_average%",
                "%auraskills_average_int%",
                "%auraskills_average_1%",
                "%auraskills_hp%",
                "%auraskills_hp_1%",
                "%auraskills_hp_2%",
                "%auraskills_hp_max%",
                "%auraskills_hp_percent%",
                "%auraskills_mana%",
                "%auraskills_mana_int%",
                "%auraskills_mana_max%",
                "%auraskills_mana_max_int%",
                "%auraskills_lb_power_[place]%",
                "%auraskills_lb_power_[place]_name%",
                "%auraskills_lb_power_[place]_value%",
                "%auraskills_lb_[skill]_[place]_name%",
                "%auraskills_lb_[skill]_[place]_value%",
                "%auraskills_rank",
                "%auraskills_rank_[skill]",
                "%auraskills_xp_required_formatted_[skill]%",
                "%auraskills_xp_required_[skill]%",
                "%auraskills_xp_progress_int_[skill]%",
                "%auraskills_xp_progress_1_[skill]%",
                "%auraskills_xp_progress_[skill]%",
                "%auraskills_xp_int_[skill]%",
                "%auraskills_xp_formatted_[skill]%",
                "%auraskills_xp_[skill]%",
                "%auraskills_multiplier%",
                "%auraskills_multiplier_[skill]%",
                "%auraskills_multiplier_percent%",
                "%auraskills_multiplier_percent_[skill]%",
                "%auraskills_actionbar_status%"
        );
    }
}
