package com.archyx.aureliumskills.menus.common;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.menus.levelprogression.LevelProgressionOpener;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.slate.menu.ActiveMenu;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BackToLevelProgressionItem extends BackItem {

    public BackToLevelProgressionItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, ItemStack item, SlotPos pos, ActiveMenu activeMenu) {
        Skill skill = (Skill) activeMenu.getProperty("skill");
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null) {
            new LevelProgressionOpener(plugin).open(player, playerData, skill);
        }
    }

}
