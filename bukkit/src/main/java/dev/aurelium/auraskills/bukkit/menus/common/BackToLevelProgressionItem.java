package dev.aurelium.auraskills.bukkit.menus.common;

import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.levelprogression.LevelProgressionOpener;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BackToLevelProgressionItem extends BackItem {

    public BackToLevelProgressionItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, ItemStack item, SlotPos pos, ActiveMenu activeMenu) {
        Skill skill = (Skill) activeMenu.getProperty("skill");
        new LevelProgressionOpener(plugin).open(player, skill);
    }

}
