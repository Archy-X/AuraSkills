package com.archyx.aureliumskills.menus.contexts;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.source.Source;
import com.archyx.slate.context.ContextProvider;
import org.jetbrains.annotations.Nullable;

public class SourceContext implements ContextProvider<Source> {

    private final AureliumSkills plugin;

    public SourceContext(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    @Nullable
    public Source parse(String menuName, String input) {
        return plugin.getSourceRegistry().valueOf(input);
    }
}
