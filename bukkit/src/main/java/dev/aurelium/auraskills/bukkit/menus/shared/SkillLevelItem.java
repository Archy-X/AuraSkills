package dev.aurelium.auraskills.bukkit.menus.shared;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.util.data.DataUtil;
import dev.aurelium.slate.info.TemplateInfo;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.menu.ActiveMenu;
import dev.aurelium.slate.menu.LoadedMenu;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SkillLevelItem {

    private final AuraSkills plugin;
    private final List<Integer> track;

    public SkillLevelItem(AuraSkills plugin) {
        this.plugin = plugin;
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

    public void loadTrack() {
        LoadedMenu menu = plugin.getSlate().getLoadedMenu("level_progression");
        if (menu != null) {
            Object trackObj = menu.options().get("track");
            if (trackObj != null) {
                this.track.clear();
                this.track.addAll(DataUtil.castIntegerList(trackObj));
            }
        }
    }

    public SlotPos slotPos(TemplateInfo<Integer> info) {
        int page = info.menu().getCurrentPage();
        int index = info.value() - startLevel(info.menu()) - page * itemsPerPage(info.menu());
        if (index < track.size()) {
            int pos = track.get(index);
            return SlotPos.of(pos / 9, pos % 9);
        } else {
            return SlotPos.of(1, 1);
        }
    }

    public ItemStack modify(TemplateInfo<Integer> info) {
        var skill = (Skill) info.menu().getProperty("skill");
        int level = info.value();
        if (level > skill.getMaxLevel()) {
            return null;
        }
        // Amount matching level functionality
        if (info.menu().getOption(Boolean.class, "use_level_as_amount", true)) {
            if (level <= 64) {
                info.item().setAmount(level);
            } else {
                int overAmount = info.menu().getOption(Integer.class, "overMaxStackAmount", 1);
                info.item().setAmount(overAmount);
            }
        }
        return info.item();
    }

    public int itemsPerPage(ActiveMenu activeMenu) {
        return activeMenu.getOption(Integer.class, "items_per_page", 24);
    }

    public int startLevel(ActiveMenu activeMenu) {
        return activeMenu.getOption(Integer.class, "start_level", 1);
    }

}
