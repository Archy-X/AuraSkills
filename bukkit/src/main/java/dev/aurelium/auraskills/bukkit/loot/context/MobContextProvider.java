package dev.aurelium.auraskills.bukkit.loot.context;

import dev.aurelium.auraskills.common.util.data.DataUtil;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MobContextProvider extends ContextProvider {

    public MobContextProvider() {
        super("mobs");
    }

    @Override
    public @Nullable Set<LootContext> parseContext(Map<?, ?> parentMap) {
        Set<LootContext> contexts = new HashSet<>();
        if (!parentMap.containsKey("mobs")) {
            return contexts;
        }
        List<String> mobList = DataUtil.getStringList(parentMap, "mobs");
        for (String name : mobList) {
            EntityType entityType = EntityType.valueOf(name.toUpperCase(Locale.ROOT));
            contexts.add(new MobContext(entityType));
        }
        return contexts;
    }
}
