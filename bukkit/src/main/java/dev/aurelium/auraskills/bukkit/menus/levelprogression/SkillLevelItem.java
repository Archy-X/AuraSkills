package dev.aurelium.auraskills.bukkit.menus.levelprogression;

import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.common.AbstractItem;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.hooks.EconomyHook;
import dev.aurelium.auraskills.common.mana.ManaAbilityManager;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.reward.SkillReward;
import dev.aurelium.auraskills.common.reward.type.MoneyReward;
import dev.aurelium.auraskills.common.util.math.NumberUtil;
import dev.aurelium.auraskills.common.util.math.RomanNumber;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class SkillLevelItem extends AbstractItem implements TemplateItemProvider<Integer> {

    private final List<Integer> track;
    
    public SkillLevelItem(AuraSkills plugin) {
        super(plugin);
        this.track = new ArrayList<>();
        track.add(9); track.add(18); track.add(27); track.add(36); track.add(37);
        track.add(38); track.add(29); track.add(20); track.add(11); track.add(12);
        track.add(13); track.add(22); track.add(31); track.add(40); track.add(41);
        track.add(42); track.add(33); track.add(24); track.add(15); track.add(16);
        track.add(17); track.add(26); track.add(35); track.add(44);
    }

    @Override
    public Class<Integer> getContext() {
        return Integer.class;
    }

    @Override
    public SlotPos getSlotPos(Player player, ActiveMenu activeMenu, Integer context) {
        int index = context - 2;
        int pos = track.get(index);
        return SlotPos.of(pos / 9, pos % 9);
    }

    @Override
    public ItemStack onItemModify(ItemStack baseItem, Player player, ActiveMenu activeMenu, Integer num) {
        // Functionality for showing the level as the amount on the item
        int page = activeMenu.getCurrentPage();
        int level = num + page * (int) activeMenu.getProperty("items_per_page");
        // Don't show if level is more than max skill level
        Skill skill = (Skill) activeMenu.getProperty("skill");
        if (level > skill.getMaxLevel()) {
            return null;
        }
        // Amount matching level functionality
        if (activeMenu.getOption(Boolean.class, "use_level_as_amount", true)) {
            if (level <= 64) {
                baseItem.setAmount(level);
            } else {
                int overMaxStackAmount = activeMenu.getOption(Integer.class, "overMaxStackAmount", 1);
                baseItem.setAmount(overMaxStackAmount);
            }
        }
        return baseItem;
    }

    protected String getRewardsLore(Skill skill, int level, Player player, Locale locale) {
        ImmutableList<SkillReward> rewards = plugin.getRewardManager().getRewardTable(skill).getRewards(level);
        StringBuilder message = new StringBuilder();
        double totalMoney = 0;
        for (SkillReward reward : rewards) {
            message.append(reward.getMenuMessage(plugin.getUser(player), locale, skill, level));
            if (reward instanceof MoneyReward) {
                totalMoney += ((MoneyReward) reward).getAmount();
            }
        }
        // Legacy money rewards
        if (plugin.getHookManager().isRegistered(EconomyHook.class)) {
            if (plugin.configBoolean(Option.SKILL_MONEY_REWARDS_ENABLED)) {
                double base = plugin.configDouble(Option.SKILL_MONEY_REWARDS_BASE);
                double multiplier = plugin.configDouble(Option.SKILL_MONEY_REWARDS_MULTIPLIER);
                totalMoney += base + (multiplier * level * level);
            }
        }
        if (totalMoney > 0) {
            message.append(TextUtil.replace(plugin.getMsg(MenuMessage.MONEY_REWARD, locale), "{amount}", NumberUtil.format2(totalMoney)));
        }
        return TextUtil.replace(plugin.getMsg(MenuMessage.REWARDS, locale),"{rewards}", message.toString());
    }

    protected String getAbilityLore(Skill skill, int level, Locale locale) {
        StringBuilder abilityLore = new StringBuilder();
        if (skill.getAbilities().size() == 5) {
            for (Ability ability : plugin.getAbilityManager().getAbilities(skill, level)) {
                if (ability.isEnabled()) {
                    if (level == ability.getUnlock()) {
                        abilityLore.append(TextUtil.replace(plugin.getMsg(MenuMessage.ABILITY_UNLOCK, locale)
                                , "{ability}", ability.getDisplayName(locale)
                                , "{desc}", TextUtil.replace(ability.getDescription(locale)
                                        , "{value_2}", NumberUtil.format1(ability.getSecondaryValue(1))
                                        , "{value}", NumberUtil.format1(ability.getValue(1)))));
                    } else {
                        int abilityLevel = ((level - ability.getUnlock()) / ability.getLevelUp()) + 1;
                        if (abilityLevel <= ability.getMaxLevel() || ability.getMaxLevel() == 0) { // Check max level
                            abilityLore.append(TextUtil.replace(plugin.getMsg(MenuMessage.ABILITY_LEVEL, locale)
                                    , "{ability}", ability.getDisplayName(locale)
                                    , "{level}", RomanNumber.toRoman(abilityLevel, plugin)
                                    , "{desc}", TextUtil.replace(ability.getDescription(locale)
                                            , "{value_2}", NumberUtil.format1(ability.getSecondaryValue(abilityLevel))
                                            , "{value}", NumberUtil.format1(ability.getValue(abilityLevel)))));
                        }
                    }
                }
            }
        }
        return abilityLore.toString();
    }

    protected String getManaAbilityLore(Skill skill, int level, Locale locale) {
        ManaAbilityManager manager = plugin.getManaAbilityManager();
        ManaAbility manaAbility = manager.getManaAbilityAtLevel(skill, level);
        StringBuilder manaAbilityLore = new StringBuilder();
        if (manaAbility != null) {
            if (manaAbility.isEnabled()) {
                if (level == manaAbility.getUnlock()) {
                    manaAbilityLore.append(TextUtil.replace(plugin.getMsg(MenuMessage.MANA_ABILITY_UNLOCK, locale)
                            , "{mana_ability}", manaAbility.getDisplayName(locale)
                            , "{desc}", TextUtil.replace(manaAbility.getDescription(locale)
                                    , "{value}", NumberUtil.format1(manaAbility.getDisplayValue(1))
                                    , "{duration}", NumberUtil.format1(getDuration(manaAbility, 1))
                                    , "{haste_level}", String.valueOf(manager.getManaAbility(ManaAbilities.SPEED_MINE).config().getInt("haste_level", 10)))));
                }
                else {
                    int manaAbilityLevel = ((level - manaAbility.getUnlock()) / manaAbility.getLevelUp()) + 1;
                    if (manaAbilityLevel <= manaAbility.getMaxLevel() || manaAbility.getMaxLevel() == 0) {
                        manaAbilityLore.append(TextUtil.replace(plugin.getMsg(MenuMessage.MANA_ABILITY_LEVEL, locale)
                                , "{mana_ability}", manaAbility.getDisplayName(locale)
                                , "{level}", RomanNumber.toRoman(manaAbilityLevel, plugin)
                                , "{desc}", TextUtil.replace(manaAbility.getDescription(locale)
                                        , "{value}", NumberUtil.format1(manaAbility.getDisplayValue(manaAbilityLevel))
                                        , "{duration}", NumberUtil.format1(getDuration(manaAbility, manaAbilityLevel))
                                        , "{haste_level}", String.valueOf(ManaAbilities.SPEED_MINE.optionInt("haste_level", 10)))));
                    }
                }
            }
        }
        return manaAbilityLore.toString();
    }

    private double getDuration(ManaAbility manaAbility, int level) {
        if (manaAbility == ManaAbilities.LIGHTNING_BLADE) {
            double baseDuration = ManaAbilities.LIGHTNING_BLADE.optionDouble("base_duration");
            double durationPerLevel = ManaAbilities.LIGHTNING_BLADE.optionDouble("duration_per_level");
            return baseDuration + (durationPerLevel * (level - 1));
        } else {
            return manaAbility.getValue(level);
        }
    }

    protected int getItemsPerPage(ActiveMenu activeMenu) {
        Object property = activeMenu.getProperty("items_per_page");
        int itemsPerPage;
        if (property instanceof Integer) {
            itemsPerPage = (Integer) property;
        } else {
            itemsPerPage = 24;
        }
        return itemsPerPage;
    }

    protected int getLevel(ActiveMenu activeMenu, int position) {
        int itemsPerPage = getItemsPerPage(activeMenu);
        int currentPage = activeMenu.getCurrentPage();
        return currentPage * itemsPerPage + position;
    }

}
