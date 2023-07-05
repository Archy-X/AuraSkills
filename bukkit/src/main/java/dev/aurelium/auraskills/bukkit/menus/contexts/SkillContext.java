package dev.aurelium.auraskills.bukkit.menus.contexts;

import com.archyx.slate.context.ContextProvider;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.jetbrains.annotations.Nullable;

public class SkillContext implements ContextProvider<Skill> {

    private final AuraSkills plugin;

    public SkillContext(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Nullable
    @Override
    public Skill parse(String menuName, String input) {
        Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromStringOrDefault(input));
        if (skill != null && skill.isEnabled()) {
            return skill;
        } else {
            return null;
        }
    }
}
