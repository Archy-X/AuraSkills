package dev.aurelium.auraskills.common.source.serializer;

import com.google.common.collect.ImmutableMap;
import dev.aurelium.auraskills.api.source.type.BlockXpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.type.BlockSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class BlockSourceSerializer extends SourceSerializer<BlockSource> {

    public BlockSourceSerializer(AuraSkillsPlugin plugin, String sourceName) {
        super(plugin, sourceName);
    }

    @Override
    public BlockSource deserialize(Type type, ConfigurationNode source) throws SerializationException {
        String[] blocks = requiredPluralizedArray("block", source, String.class);
        BlockXpSource.BlockTriggers[] triggers = requiredPluralizedArray("trigger", source, BlockXpSource.BlockTriggers.class);
        boolean checkReplace = source.node("check_replace").getBoolean(true);
        BlockXpSource.BlockXpSourceState[] states = pluralizedArray("state", source, BlockXpSource.BlockXpSourceState.class);
        String stateMultiplier = source.node("state_multiplier").getString("");
        BlockXpSource.SupportBlockType supportBlockType = source.node("support_block").get(BlockXpSource.SupportBlockType.class, BlockXpSource.SupportBlockType.NONE);

        boolean trunk = source.node("trunk").getBoolean(false);
        boolean leaf = source.node("leaf").getBoolean(false);

        return new BlockSource(plugin, getId(), getXp(source), getDisplayName(source), blocks, triggers, checkReplace, states, stateMultiplier, supportBlockType, trunk, leaf);
    }

    public static class BlockSourceStateSerializer extends SourceSerializer<BlockXpSource.BlockXpSourceState> {

        public BlockSourceStateSerializer(AuraSkillsPlugin plugin, String sourceName) {
            super(plugin, sourceName);
        }

        @Override
        public BlockXpSource.BlockXpSourceState deserialize(Type type, ConfigurationNode source) {
            Map<String, Object> stateMap = new HashMap<>();
            // Add all keys and values in the section to the map
            for (Map.Entry<Object, ? extends ConfigurationNode> entry : source.childrenMap().entrySet()) {
                String key = entry.getKey().toString();
                Object value = entry.getValue().raw();
                stateMap.put(key, value);
            }
            return new BlockSource.BlockSourceState(ImmutableMap.copyOf(stateMap));
        }
    }

}
