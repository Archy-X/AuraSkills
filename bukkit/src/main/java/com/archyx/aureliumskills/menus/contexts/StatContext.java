package com.archyx.aureliumskills.menus.contexts;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.slate.context.ContextProvider;
import org.jetbrains.annotations.Nullable;

public class StatContext implements ContextProvider<Stat> {

    private final AureliumSkills plugin;

    public StatContext(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    @Nullable
    @Override
    public Stat parse(String menuName, String input) {
        return plugin.getStatRegistry().getStat(input);
    }

}
