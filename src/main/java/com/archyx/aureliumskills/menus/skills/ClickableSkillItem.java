package com.archyx.aureliumskills.menus.skills;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.menus.common.AbstractSkillItem;
import com.archyx.aureliumskills.skills.Skill;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ClickableSkillItem extends AbstractSkillItem {

    public ClickableSkillItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, ItemStack item, SlotPos pos, Skill skill) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData == null) {
            return;
        }

        if (player.hasPermission("aureliumskills." + skill.toString().toLowerCase(Locale.ENGLISH))) {
            int page = getPage(skill, playerData);
            Map<String, Object> properties = new HashMap<>();
            properties.put("skill", skill);
            plugin.getSlate().getMenuManager().openMenu(player, "level_progression", properties, page);
        }
    }

    private int getPage(Skill skill, PlayerData playerData) {
        int page = (playerData.getSkillLevel(skill) - 2) / 24;
        int maxLevelPage = (OptionL.getMaxLevel(skill) - 2) / 24;
        if (page > maxLevelPage) {
            page = maxLevelPage;
        }
        return page;
    }

}
