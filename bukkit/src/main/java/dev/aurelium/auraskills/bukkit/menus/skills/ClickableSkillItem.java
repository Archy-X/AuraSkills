package dev.aurelium.auraskills.bukkit.menus.skills;

import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.common.AbstractSkillItem;
import dev.aurelium.auraskills.bukkit.menus.levelprogression.LevelProgressionOpener;
import dev.aurelium.auraskills.common.user.User;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ClickableSkillItem extends AbstractSkillItem {

    public ClickableSkillItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, ItemStack item, SlotPos pos, ActiveMenu activeMenu, Skill skill) {
        User user = plugin.getUser(player);

        if (player.hasPermission("aureliumskills." + skill.toString().toLowerCase(Locale.ROOT))) {
            new LevelProgressionOpener(plugin).open(player, user, skill);
        }
    }

    @Override
    public Set<Skill> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        return new HashSet<>(plugin.getSkillManager().getEnabledSkills());
    }

    @Override
    public ItemStack onItemModify(ItemStack baseItem, Player player, ActiveMenu activeMenu, Skill skill) {
        if (skill.isEnabled()) {
            return baseItem;
        } else {
            return null; // Hide item if skill is disabled
        }
    }
}
