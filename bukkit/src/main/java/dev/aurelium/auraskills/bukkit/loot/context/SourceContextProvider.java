package dev.aurelium.auraskills.bukkit.loot.context;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.source.SourceTag;
import dev.aurelium.auraskills.common.util.data.DataUtil;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SourceContextProvider extends ContextProvider {

    private final AuraSkills plugin;

    public SourceContextProvider(AuraSkills plugin) {
        super("sources");
        this.plugin = plugin;
    }

    @Override
    @Nullable
    public Set<LootContext> parseContext(Map<?, ?> parentMap) {
        Set<LootContext> contexts = new HashSet<>();
        if (!parentMap.containsKey("sources")) {
            return contexts;
        }
        List<String> sourcesList = DataUtil.getStringList(parentMap, "sources");
        for (String name : sourcesList) {
            NamespacedId sourceId = NamespacedId.fromDefault(name);
            XpSource source = plugin.getSkillManager().getSourceById(sourceId);
            if (source != null) {
                contexts.add(new SourceContext(source));
            } else {
                SourceTag tag = SourceTag.valueOf(name.toUpperCase(Locale.ROOT));
                List<XpSource> sourceList = plugin.getSkillManager().getSourcesWithTag(tag);
                for (XpSource tagSource : sourceList) {
                    contexts.add(new SourceContext(tagSource));
                }
            }
        }
        return contexts;
    }
}
