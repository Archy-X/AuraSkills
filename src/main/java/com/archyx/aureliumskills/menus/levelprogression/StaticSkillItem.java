package com.archyx.aureliumskills.menus.levelprogression;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.menus.common.AbstractSkillItem;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class StaticSkillItem extends AbstractSkillItem {

    public StaticSkillItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public @NotNull Set<@NotNull Skill> getDefinedContexts(@NotNull Player player, @NotNull ActiveMenu activeMenu) {
        Skill skill = (Skill) activeMenu.getProperty("skill");
        assert (null != skill);
        Set<@NotNull Skill> skills = new HashSet<>();
        skills.add(skill);
        return skills;
    }

}
