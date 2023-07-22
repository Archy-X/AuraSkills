package dev.aurelium.auraskills.bukkit.menus.stats;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.common.AbstractItem;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.math.NumberUtil;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class StatItem extends AbstractItem implements TemplateItemProvider<Stat> {

    public StatItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public Class<Stat> getContext() {
        return Stat.class;
    }

    @Override
    public Set<Stat> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        return new HashSet<>(plugin.getStatManager().getStatValues());
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, Stat stat) {
        Locale locale = plugin.getUser(player).getLocale();
        User user = plugin.getUser(player);
        switch (placeholder) {
            case "color":
                return stat.getColor(locale);
            case "stat":
                return stat.getDisplayName(locale);
            case "stat_desc":
                return stat.getDescription(locale);
            case "skills":
                return getSkillsLeveledBy(stat, locale);
            case "your_level":
                return TextUtil.replace(plugin.getMsg(MenuMessage.YOUR_LEVEL, locale),
                        "{color}", stat.getColor(locale),
                        "{level}", NumberUtil.format1(user.getStatLevel(stat)));
            case "descriptors":
                switch (stat.name().toLowerCase(Locale.ROOT)) {
                    case "strength":
                        return getStrengthDescriptors(user, locale);
                    case "health":
                        return getHealthDescriptors(user, locale);
                    case "regeneration":
                        return getRegenerationDescriptors(user, locale);
                    case "luck":
                        return getLuckDescriptors(user, locale);
                    case "wisdom":
                        return getWisdomDescriptors(user, locale);
                    case "toughness":
                        return getToughnessDescriptors(user, locale);
                    default:
                        return "";
                }
        }
        return placeholder;
    }

    private String getSkillsLeveledBy(Stat stat, Locale locale) {
        List<Skill> skillsLeveledBy = plugin.getRewardManager().getSkillsLeveledBy(stat);
        StringBuilder skillList = new StringBuilder();
        for (Skill skill : skillsLeveledBy) {
            skillList.append(skill.getDisplayName(locale)).append(", ");
        }
        if (skillList.length() > 1) {
            skillList.delete(skillList.length() - 2, skillList.length());
        }
        if (skillsLeveledBy.size() > 0) {
            return TextUtil.replace(plugin.getMsg(MenuMessage.SKILLS, locale),
                    "{skills}", skillList.toString());
        } else {
            return "";
        }
    }

    private String getStrengthDescriptors(User user, Locale locale) {
        double strengthLevel = user.getStatLevel(Stats.STRENGTH);
        double attackDamage = strengthLevel * plugin.configDouble(Option.STRENGTH_MODIFIER);
        if (plugin.configBoolean(Option.STRENGTH_DISPLAY_DAMAGE_WITH_HEALTH_SCALING) && !plugin.configBoolean(Option.STRENGTH_USE_PERCENT)) {
            attackDamage *= plugin.configDouble(Option.HEALTH_HP_INDICATOR_SCALING);
        }
        return TextUtil.replace(plugin.getMsg(MenuMessage.ATTACK_DAMAGE, locale),"{value}", NumberUtil.format2(attackDamage));
    }

    private String getHealthDescriptors(User user, Locale locale) {
        double modifier = user.getStatLevel(Stats.HEALTH) * plugin.configDouble(Option.HEALTH_MODIFIER);
        double scaledHealth = modifier * plugin.configDouble(Option.HEALTH_HP_INDICATOR_SCALING);
        return TextUtil.replace(plugin.getMsg(MenuMessage.HP, locale),"{value}", NumberUtil.format2(scaledHealth));
    }

    private String getRegenerationDescriptors(User user, Locale locale) {
        double regenLevel = user.getStatLevel(Stats.REGENERATION);
        double saturatedRegen = regenLevel * plugin.configDouble(Option.REGENERATION_SATURATED_MODIFIER) * plugin.configDouble(Option.HEALTH_HP_INDICATOR_SCALING);
        double hungerFullRegen = regenLevel *  plugin.configDouble(Option.REGENERATION_HUNGER_FULL_MODIFIER) * plugin.configDouble(Option.HEALTH_HP_INDICATOR_SCALING);
        double almostFullRegen = regenLevel *  plugin.configDouble(Option.REGENERATION_HUNGER_ALMOST_FULL_MODIFIER) * plugin.configDouble(Option.HEALTH_HP_INDICATOR_SCALING);
        double manaRegen = user.getTraitLevel(Traits.MANA_REGENERATION);
        return TextUtil.replace(plugin.getMsg(MenuMessage.SATURATED_REGEN, locale),"{value}", NumberUtil.format2(saturatedRegen))
                + "\n" + TextUtil.replace(plugin.getMsg(MenuMessage.FULL_HUNGER_REGEN, locale),"{value}", NumberUtil.format2(hungerFullRegen))
                + "\n" + TextUtil.replace(plugin.getMsg(MenuMessage.ALMOST_FULL_HUNGER_REGEN, locale),"{value}", NumberUtil.format2(almostFullRegen))
                + "\n" + TextUtil.replace(plugin.getMsg(MenuMessage.MANA_REGEN, locale),"{value}", String.valueOf(Math.round(manaRegen)));
    }

    private String getLuckDescriptors(User user, Locale locale) {
        double luckLevel = user.getStatLevel(Stats.LUCK);
        double luck = luckLevel * plugin.configDouble(Option.LUCK_MODIFIER);
        double doubleDropChance = luckLevel * plugin.configDouble(Option.LUCK_DOUBLE_DROP_MODIFIER) * 100;
        if (doubleDropChance > plugin.configDouble(Option.LUCK_DOUBLE_DROP_PERCENT_MAX)) {
            doubleDropChance = plugin.configDouble(Option.LUCK_DOUBLE_DROP_PERCENT_MAX);
        }
        return TextUtil.replace(plugin.getMsg(MenuMessage.LUCK, locale),"{value}", NumberUtil.format2(luck))
                + "\n" + TextUtil.replace(plugin.getMsg(MenuMessage.DOUBLE_DROP_CHANCE, locale),"{value}", NumberUtil.format2(doubleDropChance));
    }

    private String getWisdomDescriptors(User user, Locale locale) {
        double wisdomLevel = user.getStatLevel(Stats.WISDOM);
        double xpModifier = wisdomLevel * plugin.configDouble(Option.WISDOM_EXPERIENCE_MODIFIER) * 100;
        double anvilCostReduction = (-1.0 * Math.pow(1.025, -1.0 * wisdomLevel * plugin.configDouble(Option.WISDOM_ANVIL_COST_MODIFIER)) + 1) * 100;
        double maxMana = user.getMaxMana();
        return TextUtil.replace(plugin.getMsg(MenuMessage.XP_GAIN, locale),"{value}", NumberUtil.format2(xpModifier))
                + "\n" + TextUtil.replace(plugin.getMsg(MenuMessage.ANVIL_COST_REDUCTION, locale),"{value}", NumberUtil.format1(anvilCostReduction)) + " "
                + "\n" + TextUtil.replace(plugin.getMsg(MenuMessage.MAX_MANA, locale), "{value}", NumberUtil.format1(maxMana));
    }

    private String getToughnessDescriptors(User user, Locale locale) {
        double toughness = user.getStatLevel(Stats.TOUGHNESS) * plugin.configDouble(Option.TOUGHNESS_NEW_MODIFIER);
        double damageReduction = (-1.0 * Math.pow(1.01, -1.0 * toughness) + 1) * 100;
        return TextUtil.replace(plugin.getMsg(MenuMessage.INCOMING_DAMAGE, locale),"{value}", NumberUtil.format2(damageReduction));
    }

}
