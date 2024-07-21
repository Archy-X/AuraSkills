package dev.aurelium.auraskills.common.source.parser;

import com.google.common.collect.ImmutableMap;
import dev.aurelium.auraskills.api.config.ConfigNode;
import dev.aurelium.auraskills.api.source.BaseContext;
import dev.aurelium.auraskills.api.source.UtilityParser;
import dev.aurelium.auraskills.api.source.type.BlockXpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.ConfigurateSourceContext;
import dev.aurelium.auraskills.common.source.type.BlockSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.HashMap;
import java.util.Map;

public class BlockSourceParser extends SourceParser<BlockSource> {

    public BlockSourceParser(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public BlockSource parse(ConfigurationNode source, ConfigurateSourceContext context) throws SerializationException {
        String[] blocks = context.requiredPluralizedArray("block", source, String.class);
        BlockXpSource.BlockTriggers[] triggers = context.requiredPluralizedArray("trigger", source, BlockXpSource.BlockTriggers.class);
        boolean checkReplace = source.node("check_replace").getBoolean(true);
        BlockXpSource.BlockXpSourceState[] states = context.pluralizedArray("state", source, BlockXpSource.BlockXpSourceState.class);
        BlockXpSource.BlockXpSourceState[] afterStates = context.pluralizedArray("after_state", source, BlockXpSource.BlockXpSourceState.class);
        String stateMultiplier = source.node("state_multiplier").getString("");
        BlockXpSource.SupportBlockType supportBlockType = source.node("support_block").get(BlockXpSource.SupportBlockType.class, BlockXpSource.SupportBlockType.NONE);

        boolean trunk = source.node("trunk").getBoolean(false);
        boolean leaf = source.node("leaf").getBoolean(false);

        return new BlockSource(plugin, context.parseValues(source), blocks, triggers, checkReplace, states, afterStates, stateMultiplier, supportBlockType, trunk, leaf);
    }

    public static class BlockSourceStateParser implements UtilityParser<BlockXpSource.BlockXpSourceState> {

        @Override
        public BlockXpSource.BlockXpSourceState parse(ConfigNode source, BaseContext context) {
            Map<String, Object> stateMap = new HashMap<>();
            // Add all keys and values in the section to the map
            for (Map.Entry<Object, ? extends ConfigNode> entry : source.childrenMap().entrySet()) {
                String key = entry.getKey().toString();
                Object value = entry.getValue().raw();
                stateMap.put(key, value);
            }
            return new BlockSource.BlockSourceState(ImmutableMap.copyOf(stateMap));
        }
    }

}
