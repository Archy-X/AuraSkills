package com.archyx.aureliumskills.menus.contexts;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.slate.context.ContextProvider;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

public class SkillContext implements ContextProvider<@NotNull Skill> {

    private final AureliumSkills plugin;

    public SkillContext(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    @Nullable
    @Override
    public Skill parse(String input) {
        assert (null != input);
        return plugin.getSkillRegistry().getSkill(input);
    }
}
