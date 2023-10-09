package dev.aurelium.auraskills.bukkit.menus.levelprogression;

import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.common.AbstractItem;
import dev.aurelium.auraskills.common.reward.SkillReward;
import dev.aurelium.auraskills.common.reward.type.MoneyReward;
import dev.aurelium.auraskills.common.util.math.NumberUtil;
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

    protected String getRewardEntries(Skill skill, int level, Player player, Locale locale, ActiveMenu activeMenu) {
        ImmutableList<SkillReward> rewards = plugin.getRewardManager().getRewardTable(skill).getRewards(level);
        StringBuilder message = new StringBuilder();
        double totalMoney = 0;
        for (SkillReward reward : rewards) {
            message.append(reward.getMenuMessage(plugin.getUser(player), locale, skill, level));
            if (reward instanceof MoneyReward) {
                totalMoney += ((MoneyReward) reward).getAmount();
            }
        }
        if (totalMoney > 0) {
            message.append(TextUtil.replace(activeMenu.getFormat("money_reward_entry"), "{amount}", NumberUtil.format2(totalMoney)));
        }
        return message.toString();
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
