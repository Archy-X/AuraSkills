package dev.aurelium.auraskills.bukkit.menus.levelprogression;

import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.common.AbstractItem;
import dev.aurelium.auraskills.common.util.data.DataUtil;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class SkillLevelItem extends AbstractItem implements TemplateItemProvider<Integer> {

    private final List<Integer> track;
    protected int START_LEVEL = 1;
    protected int ITEMS_PER_PAGE = 24;
    
    public SkillLevelItem(AuraSkills plugin) {
        super(plugin);
        this.track = getDefaultTrack();
    }

    public static List<Integer> getDefaultTrack() {
        List<Integer> track = new ArrayList<>();
        track.add(9); track.add(18); track.add(27); track.add(36); track.add(37);
        track.add(38); track.add(29); track.add(20); track.add(11); track.add(12);
        track.add(13); track.add(22); track.add(31); track.add(40); track.add(41);
        track.add(42); track.add(33); track.add(24); track.add(15); track.add(16);
        track.add(17); track.add(26); track.add(35); track.add(44);
        return track;
    }

    @Override
    public void onInitialize(Player player, ActiveMenu activeMenu, Integer context) {
        this.START_LEVEL = activeMenu.getOption(Integer.class, "start_level", 1);
        this.ITEMS_PER_PAGE = activeMenu.getOption(Integer.class, "items_per_page", 24);
        Object trackObj = activeMenu.getOption("track");
        if (trackObj != null) {
            this.track.clear();
            this.track.addAll(DataUtil.castIntegerList(trackObj));
        }
    }

    @Override
    public Class<Integer> getContext() {
        return Integer.class;
    }

    @Override
    public SlotPos getSlotPos(Player player, ActiveMenu activeMenu, Integer level) {
        int page = activeMenu.getCurrentPage();
        int index = level - START_LEVEL - page * ITEMS_PER_PAGE;
        if (index < track.size()) {
            int pos = track.get(index);
            return SlotPos.of(pos / 9, pos % 9);
        } else {
            return SlotPos.of(1, 1);
        }
    }

    @Override
    public ItemStack onItemModify(ItemStack baseItem, Player player, ActiveMenu activeMenu, Integer level) {
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

}
