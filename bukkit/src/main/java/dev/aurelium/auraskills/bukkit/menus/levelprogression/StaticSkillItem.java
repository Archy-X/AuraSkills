package dev.aurelium.auraskills.bukkit.menus.levelprogression;

import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.common.AbstractSkillItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class StaticSkillItem extends AbstractSkillItem {

    public StaticSkillItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public Set<Skill> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        Object property = activeMenu.getProperty("skill");
        Skill skill = (Skill) property;
        Set<Skill> skills = new HashSet<>();
        skills.add(skill);
        return skills;
    }

    @Override
    public ItemStack onItemModify(ItemStack baseItem, Player player, ActiveMenu activeMenu, Skill skill) {
        return modifyItem(skill, baseItem);
    }

}
