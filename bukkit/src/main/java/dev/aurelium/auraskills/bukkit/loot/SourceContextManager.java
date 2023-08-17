package dev.aurelium.auraskills.bukkit.loot;

import com.archyx.lootmanager.loot.context.ContextManager;
import com.archyx.lootmanager.loot.context.LootContext;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.source.SourceTag;
import dev.aurelium.auraskills.common.util.data.DataUtil;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SourceContextManager extends ContextManager {

    private final AuraSkills plugin;

    public SourceContextManager(AuraSkills plugin) {
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
                contexts.add(new SourceContextWrapper(source));
            } else {
                SourceTag tag = SourceTag.valueOf(name.toUpperCase(Locale.ROOT));
                List<XpSource> sourceList = plugin.getSkillManager().getSourcesWithTag(tag);
                for (XpSource tagSource : sourceList) {
                    contexts.add(new SourceContextWrapper(tagSource));
                }
            }
        }
        return contexts;
    }
}
