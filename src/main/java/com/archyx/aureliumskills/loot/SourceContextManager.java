package com.archyx.aureliumskills.loot;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.source.Source;
import com.archyx.aureliumskills.source.SourceTag;
import com.archyx.lootmanager.loot.context.ContextManager;
import com.archyx.lootmanager.loot.context.LootContext;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SourceContextManager extends ContextManager {

    private final AureliumSkills plugin;

    public SourceContextManager(AureliumSkills plugin) {
        super("sources");
        this.plugin = plugin;
    }

    @Override
    @Nullable
    public Set<LootContext> parseContext(String name) {
        Source source = plugin.getSourceRegistry().valueOf(name);
        if (source != null) {
            Set<LootContext> contextSet = new HashSet<>();
            contextSet.add(source);
            return contextSet;
        } else {
            SourceTag tag = SourceTag.valueOf(name.toUpperCase(Locale.ROOT));
            List<Source> sources = plugin.getSourceManager().getTag(tag);
            return new HashSet<>(sources);
        }
    }
}
