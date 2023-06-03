package dev.auramc.auraskills.common.source.serializer;

import dev.auramc.auraskills.api.source.type.BlockXpSource;
import dev.auramc.auraskills.common.source.type.BlockSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class BlockSourceSerializer extends SourceSerializer<BlockSource> {

    @Override
    public BlockSource deserialize(Type type, ConfigurationNode source) throws SerializationException {
        String[] blocks = requiredPluralizedArray("block", source, String.class);
        BlockXpSource.BlockTriggers[] triggers = requiredPluralizedArray("trigger", source, BlockXpSource.BlockTriggers.class);
        boolean checkReplace = source.node("check_replace").getBoolean(true);
        BlockXpSource.BlockXpSourceState[] states = pluralizedArray("state", source, BlockXpSource.BlockXpSourceState.class);
        String stateMultiplier = source.node("state_multiplier").getString();

        return new BlockSource(getId(source), getXp(source), blocks, triggers, checkReplace, states, stateMultiplier);
    }

    public static class BlockSourceStateSerializer extends SourceSerializer<BlockXpSource.BlockXpSourceState> {

        @Override
        public BlockXpSource.BlockXpSourceState deserialize(Type type, ConfigurationNode source) {
            Map<String, Object> stateMap = new HashMap<>();
            // Add all keys and values in the section to the map
            for (Map.Entry<Object, ? extends ConfigurationNode> entry : source.childrenMap().entrySet()) {
                String key = entry.getKey().toString();
                Object value = entry.getValue().raw();
                stateMap.put(key, value);
            }
            return new BlockSource.BlockSourceState(stateMap);
        }
    }

}
