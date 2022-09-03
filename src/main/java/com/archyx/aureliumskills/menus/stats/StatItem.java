package com.archyx.aureliumskills.menus.stats;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.menus.common.AbstractItem;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.stats.Stats;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class StatItem extends AbstractItem implements TemplateItemProvider<Stat> {

    public StatItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public @NotNull Class<Stat> getContext() {
        return Stat.class;
    }

    @Override
    public @NotNull Set<@NotNull Stat> getDefinedContexts(@NotNull Player player, @NotNull ActiveMenu activeMenu) {
        return new HashSet<>(plugin.getStatRegistry().getStats());
    }

    @Override
    public @NotNull String onPlaceholderReplace(@NotNull String placeholder, @NotNull Player player, @NotNull ActiveMenu activeMenu, @NotNull PlaceholderType type, @NotNull Stat stat) {
        @Nullable Locale locale = plugin.getLang().getLocale(player);
        @Nullable PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData == null)
            return placeholder;
        @Nullable String m = placeholder;
        switch (placeholder) {
            case "color":
                m = stat.getColor(locale);
                break;
            case "stat":
                m = stat.getDisplayName(locale);
                break;
            case "stat_desc":
                m = stat.getDescription(locale);
                break;
            case "skills":
                m = getSkillsLeveledBy(stat, locale);
                break;
            case "your_level":
                m = TextUtil.replace(Lang.getMessage(MenuMessage.YOUR_LEVEL, locale),
                        "{color}", stat.getColor(locale),
                        "{level}", NumberUtil.format1(playerData.getStatLevel(stat)));
                break;
            case "descriptors":
                @Nullable String name = stat.name();
                assert (null != name);
                switch (name.toLowerCase(Locale.ROOT)) {
                    case "strength":
                        m = getStrengthDescriptors(playerData, locale);
                        break;
                    case "health":
                        m = getHealthDescriptors(playerData, locale);
                        break;
                    case "regeneration":
                        m = getRegenerationDescriptors(playerData, locale);
                        break;
                    case "luck":
                        m = getLuckDescriptors(playerData, locale);
                        break;
                    case "wisdom":
                        m = getWisdomDescriptors(playerData, locale);
                        break;
                    case "toughness":
                        m = getToughnessDescriptors(playerData, locale);
                        break;
                    default:
                        m = "";
                        break;
                }
        }
        assert (null != m);
        return m;
    }

    private @NotNull String getSkillsLeveledBy(@NotNull Stat stat, @Nullable Locale locale) {
        List<Skill> skillsLeveledBy = plugin.getRewardManager().getSkillsLeveledBy(stat);
        StringBuilder skillList = new StringBuilder();
        for (Skill skill : skillsLeveledBy) {
            @Nullable String displayName = skill.getDisplayName(locale);
            assert (null != displayName);
            skillList.append(displayName).append(", ");
        }
        if (skillList.length() > 1) {
            skillList.delete(skillList.length() - 2, skillList.length());
        }
        if (skillsLeveledBy.size() > 0) {
            @Nullable String m = TextUtil.replace(Lang.getMessage(MenuMessage.SKILLS, locale),
                    "{skills}", skillList.toString());
            assert (null != m);
            return m;
        }
        return "";
    }

    private @NotNull String getStrengthDescriptors(@NotNull PlayerData playerData, @Nullable Locale locale) {
        double strengthLevel = playerData.getStatLevel(Stats.STRENGTH);
        double attackDamage = strengthLevel * OptionL.getDouble(Option.STRENGTH_MODIFIER);
        if (OptionL.getBoolean(Option.STRENGTH_DISPLAY_DAMAGE_WITH_HEALTH_SCALING) && !OptionL.getBoolean(Option.STRENGTH_USE_PERCENT)) {
            attackDamage *= OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
        }
        @Nullable String m = TextUtil.replace(Lang.getMessage(MenuMessage.ATTACK_DAMAGE, locale),"{value}", NumberUtil.format2(attackDamage));
        assert (null != m);
        return m;
    }

    private @NotNull String getHealthDescriptors(@NotNull PlayerData playerData, @Nullable Locale locale) {
        double modifier = playerData.getStatLevel(Stats.HEALTH) * OptionL.getDouble(Option.HEALTH_MODIFIER);
        double scaledHealth = modifier * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
        @Nullable String m = TextUtil.replace(Lang.getMessage(MenuMessage.HP, locale),"{value}", NumberUtil.format2(scaledHealth));
        assert (null != m);
        return m;
    }

    private @NotNull String getRegenerationDescriptors(@NotNull PlayerData playerData, @Nullable Locale locale) {
        double regenLevel = playerData.getStatLevel(Stats.REGENERATION);
        double saturatedRegen = regenLevel * OptionL.getDouble(Option.REGENERATION_SATURATED_MODIFIER) * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
        double hungerFullRegen = regenLevel *  OptionL.getDouble(Option.REGENERATION_HUNGER_FULL_MODIFIER) * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
        double almostFullRegen = regenLevel *  OptionL.getDouble(Option.REGENERATION_HUNGER_ALMOST_FULL_MODIFIER) * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
        double manaRegen = playerData.getManaRegen();
        @Nullable String m = TextUtil.replace(Lang.getMessage(MenuMessage.SATURATED_REGEN, locale),"{value}", NumberUtil.format2(saturatedRegen))
                + "\n" + TextUtil.replace(Lang.getMessage(MenuMessage.FULL_HUNGER_REGEN, locale),"{value}", NumberUtil.format2(hungerFullRegen))
                + "\n" + TextUtil.replace(Lang.getMessage(MenuMessage.ALMOST_FULL_HUNGER_REGEN, locale),"{value}", NumberUtil.format2(almostFullRegen))
                + "\n" + TextUtil.replace(Lang.getMessage(MenuMessage.MANA_REGEN, locale),"{value}", String.valueOf(Math.round(manaRegen)));
        assert (null != m);
        return m;
    }

    private @NotNull String getLuckDescriptors(@NotNull PlayerData playerData, @Nullable Locale locale) {
        double luckLevel = playerData.getStatLevel(Stats.LUCK);
        double luck = luckLevel * OptionL.getDouble(Option.LUCK_MODIFIER);
        double doubleDropChance = luckLevel * OptionL.getDouble(Option.LUCK_DOUBLE_DROP_MODIFIER) * 100;
        if (doubleDropChance > OptionL.getDouble(Option.LUCK_DOUBLE_DROP_PERCENT_MAX)) {
            doubleDropChance = OptionL.getDouble(Option.LUCK_DOUBLE_DROP_PERCENT_MAX);
        }
        @Nullable String m = TextUtil.replace(Lang.getMessage(MenuMessage.LUCK, locale),"{value}", NumberUtil.format2(luck))
                + "\n" + TextUtil.replace(Lang.getMessage(MenuMessage.DOUBLE_DROP_CHANCE, locale),"{value}", NumberUtil.format2(doubleDropChance));
        assert (null != m);
        return m;
    }

    private @NotNull String getWisdomDescriptors(@NotNull PlayerData playerData, @Nullable Locale locale) {
        double wisdomLevel = playerData.getStatLevel(Stats.WISDOM);
        double xpModifier = wisdomLevel * OptionL.getDouble(Option.WISDOM_EXPERIENCE_MODIFIER) * 100;
        double anvilCostReduction = (-1.0 * Math.pow(1.025, -1.0 * wisdomLevel * OptionL.getDouble(Option.WISDOM_ANVIL_COST_MODIFIER)) + 1) * 100;
        double maxMana = playerData.getMaxMana();
        @Nullable String m = TextUtil.replace(Lang.getMessage(MenuMessage.XP_GAIN, locale),"{value}", NumberUtil.format2(xpModifier))
                + "\n" + TextUtil.replace(Lang.getMessage(MenuMessage.ANVIL_COST_REDUCTION, locale),"{value}", NumberUtil.format1(anvilCostReduction)) + " "
                + "\n" + TextUtil.replace(Lang.getMessage(MenuMessage.MAX_MANA, locale), "{value}", NumberUtil.format1(maxMana));
        assert (null != m);
        return m;
    }

    private @NotNull String getToughnessDescriptors(@NotNull PlayerData playerData, @Nullable Locale locale) {
        double toughness = playerData.getStatLevel(Stats.TOUGHNESS) * OptionL.getDouble(Option.TOUGHNESS_NEW_MODIFIER);
        double damageReduction = (-1.0 * Math.pow(1.01, -1.0 * toughness) + 1) * 100;
        @Nullable String m = TextUtil.replace(Lang.getMessage(MenuMessage.INCOMING_DAMAGE, locale),"{value}", NumberUtil.format2(damageReduction));
        assert (null != m);
        return m;
    }

}
