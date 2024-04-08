package dev.aurelium.auraskills.bukkit.menus.contexts;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.slate.context.ContextProvider;
import org.jetbrains.annotations.Nullable;

public class XpSourceContext implements ContextProvider<XpSource> {

    private final AuraSkills plugin;

    public XpSourceContext(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public Class<XpSource> getType() {
        return XpSource.class;
    }

    @Override
    @Nullable
    public XpSource parse(String menuName, String input) {
        String[] split = input.split(" ");
        // Parse Skill from first part of input
        Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(split[0]));
        if (skill == null || !skill.isEnabled()) {
            return null;
        }
        // Parse Source from second part of input
        NamespacedId sourceId = NamespacedId.fromDefault(split[1]);
        // Find source from skill that matches id
        return skill.getSources().stream().filter(source -> source.getId().equals(sourceId)).findFirst().orElse(null);
    }
}
