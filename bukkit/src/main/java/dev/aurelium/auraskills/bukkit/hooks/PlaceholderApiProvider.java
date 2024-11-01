package dev.aurelium.auraskills.bukkit.hooks;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.bukkit.BukkitTraitHandler;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.shared.SkillItem;
import dev.aurelium.auraskills.bukkit.util.AttributeCompat;
import dev.aurelium.auraskills.common.leaderboard.SkillValue;
import dev.aurelium.auraskills.common.ui.ActionBarType;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.math.BigNumber;
import dev.aurelium.auraskills.common.util.math.RomanNumber;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class PlaceholderApiProvider extends PlaceholderExpansion {

    private final AuraSkills plugin;
    private final String identifier;
    private final String[] xpIdentifiers = new String[]{"xp_required_formatted_", "xp_required_", "xp_progress_int_", "xp_progress_1_", "xp_progress_", "xp_int_", "xp_formatted_", "xp_bar_", "xp_"};

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
            AttributeInstance attribute = player.getAttribute(AttributeCompat.MAX_HEALTH);
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
            AttributeInstance attribute = player.getAttribute(AttributeCompat.MAX_HEALTH);
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

        if (identifier.startsWith("mability_")) {
            NamespacedId id = NamespacedId.fromDefault(
                    identifier.replace("mability_", "")
                            .replace("_active", "")
                            .replace("_value_int", "")
                            .replace("_value", "")
                            .replace("_roman", ""));
            ManaAbility manaAbility = plugin.getManaAbilityRegistry().getOrNull(id);

            if (manaAbility == null) return null;

            User user = plugin.getUser(player);

            if (manaAbility.isEnabled()) {
                if (identifier.endsWith("value")) {
                    return String.valueOf(manaAbility.getValue(user.getManaAbilityLevel(manaAbility)));
                } else if (identifier.endsWith("value_int")) {
                    return String.valueOf(Math.round(manaAbility.getValue(user.getManaAbilityLevel(manaAbility))));
                } else if (identifier.endsWith("active")) {
                    return String.valueOf(user.getManaAbilityData(manaAbility).isActivated());
                } else if (identifier.endsWith("roman")) {
                    return RomanNumber.toRomanAlways(user.getManaAbilityLevel(manaAbility));
                } else if (identifier.endsWith(manaAbility.name().toLowerCase(Locale.ROOT))) {
                    return String.valueOf(user.getManaAbilityLevel(manaAbility));
                }
            }
        }

        if (identifier.startsWith("trait_")) {
            String traitName = getTraitName(identifier);

            NamespacedId id = NamespacedId.fromDefault(traitName);
            Trait trait = plugin.getTraitRegistry().getOrNull(id);

            if (trait == null || !trait.isEnabled()) return null;

            User user = plugin.getUser(player);

            if (identifier.endsWith(traitName)) {
                return NumberUtil.format2(user.getEffectiveTraitLevel(trait));
            } else if (identifier.endsWith("bonus")) {
                return NumberUtil.format2(user.getBonusTraitLevel(trait));
            } else if (identifier.endsWith("menu")) {
                BukkitTraitHandler handler = plugin.getTraitManager().getTraitImpl(trait);
                if (handler != null) {
                    return handler.getMenuDisplay(user.getEffectiveTraitLevel(trait), trait, user.getLocale());
                }
            }
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

        // Gets skill levels
        for (Skill skill : plugin.getSkillRegistry().getValues()) {
            if (identifier.equals(skill.name().toLowerCase(Locale.ROOT))) {
                User user = plugin.getUser(player);
                return String.valueOf(user.getSkillLevel(skill));
            } else if (identifier.equals(skill.name().toLowerCase(Locale.ROOT) + "_roman")) {
                User user = plugin.getUser(player);
                return RomanNumber.toRomanAlways(user.getSkillLevel(skill));
            }
        }

        // Gets ability levels
        for (Ability ability : plugin.getAbilityRegistry().getValues()) {
            if (identifier.equals(ability.name().toLowerCase(Locale.ROOT))) {
                User user = plugin.getUser(player);
                return String.valueOf(user.getAbilityLevel(ability));
            } else if (identifier.equals(ability.name().toLowerCase(Locale.ROOT) + "_roman")) {
                User user = plugin.getUser(player);
                return RomanNumber.toRomanAlways(user.getAbilityLevel(ability));
            } else if (identifier.equals(ability.name().toLowerCase(Locale.ROOT) + "_value")) {
                User user = plugin.getUser(player);
                return String.valueOf(ability.getValue(user.getAbilityLevel(ability)));
            } else if (identifier.equals(ability.name().toLowerCase(Locale.ROOT) + "_value_int")) {
                User user = plugin.getUser(player);
                return String.valueOf(Math.round(ability.getValue(user.getAbilityLevel(ability))));
            } else if (identifier.equals(ability.name().toLowerCase(Locale.ROOT) + "_value_2")) {
                if (ability.hasSecondaryValue()) {
                    User user = plugin.getUser(player);
                    return String.valueOf(ability.getSecondaryValue(user.getAbilityLevel(ability)));
                }
            } else if (identifier.equals(ability.name().toLowerCase(Locale.ROOT) + "_value_2_int")) {
                if (ability.hasSecondaryValue()) {
                    User user = plugin.getUser(player);
                    return String.valueOf(Math.round(ability.getSecondaryValue(user.getAbilityLevel(ability))));
                }
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
                case "xp_bar_":
                    int xpRequired = plugin.getXpRequirements().getXpRequired(skill, user.getSkillLevel(skill) + 1);
                    return plugin.getMessageProvider().applyFormatting(SkillItem.getBar(plugin, user.getSkillXp(skill), xpRequired));
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

        if (identifier.startsWith("jobs_")) {
            User user = plugin.getUser(player);
            switch (identifier) {
                case "jobs_list":
                    return String.join(",", user.getJobs().stream().map(s -> s.getId().getKey()).toList());
                case "jobs_list_formatted":
                    return String.join(ChatColor.RESET + ", ", user.getJobs().stream()
                            .map(s -> s.getDisplayName(plugin.getDefaultLanguage()))
                            .toList());
                case "jobs_count":
                    return String.valueOf(user.getJobs().size());
                case "jobs_limit":
                    return String.valueOf(user.getJobLimit());
            }
            final String activePrefix = "jobs_active_";
            if (identifier.startsWith(activePrefix)) {
                String skillName = identifier.substring(activePrefix.length());
                Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(skillName));
                if (skill != null) {
                    return user.getJobs().contains(skill) ? "true" : "false";
                }
            }
        }

        return null;
    }

    private static @NotNull String getTraitName(String identifier) {
        String traitName = identifier;
        if (traitName.startsWith("trait_")) {
            traitName = traitName.substring(6); // Remove the first 6 characters ("trait_")
        }

        if (traitName.endsWith("_menu")) {
            traitName = traitName.substring(0, traitName.length() - 5); // Remove the last 5 characters ("_menu")
        }

        if (traitName.endsWith("_bonus") && !traitName.equals("experience_bonus")) {
            traitName = traitName.substring(0, traitName.length() - 6); // Remove the last 6 characters ("_bonus")
        }
        return traitName;
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
                "%auraskills_[ability]_roman%",
                "%auraskills_[ability]_value%",
                "%auraskills_[ability]_value_int%",
                "%auraskills_[ability]_value_2%",
                "%auraskills_[ability]_value_2_int%",
                "%auraskills_mability_[ability]%",
                "%auraskills_mability_[ability]_roman%",
                "%auraskills_mability_[ability]_active%",
                "%auraskills_mability_[ability]_value%",
                "%auraskills_mability_[ability]_value_int%",
                "%auraskills_trait_[trait]",
                "%auraskills_trait_[trait]_bonus",
                "%auraskills_trait_[trait]_menu",
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
                "%auraskills_xp_bar_[skill]",
                "%auraskills_xp_[skill]%",
                "%auraskills_multiplier%",
                "%auraskills_multiplier_[skill]%",
                "%auraskills_multiplier_percent%",
                "%auraskills_multiplier_percent_[skill]%",
                "%auraskills_actionbar_status%",
                "%auraskills_jobs_list%",
                "%auraskills_jobs_list_formatted%",
                "%auraskills_jobs_count%",
                "%auraskills_jobs_limit%",
                "%auraskills_jobs_active_[skill]%"
        );
    }
}
