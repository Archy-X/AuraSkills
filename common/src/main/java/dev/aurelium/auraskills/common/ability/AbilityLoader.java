package dev.aurelium.auraskills.common.ability;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.ConfigurateLoader;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AbilityLoader {

    private static final String FILE_NAME = "abilities.yml";
    private final AuraSkillsPlugin plugin;
    private final ConfigurateLoader configurateLoader;

    private ConfigurationNode root;

    public AbilityLoader(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        TypeSerializerCollection abilitySerializers = TypeSerializerCollection.builder().build();
        this.configurateLoader = new ConfigurateLoader(plugin, abilitySerializers);
    }

    public void init() {
        try {
            configurateLoader.updateUserFile(FILE_NAME); // Update and save file
            ConfigurationNode embedded = configurateLoader.loadEmbeddedFile(FILE_NAME);
            ConfigurationNode defined = plugin.getAbilityRegistry().getDefinedConfig();
            ConfigurationNode user = configurateLoader.loadUserFile(FILE_NAME);

            this.root = configurateLoader.loadContentAndMerge(defined, FILE_NAME, embedded, user);
        } catch (IOException e) {
            plugin.logger().warn("Error loading " + FILE_NAME + " file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public LoadedAbility loadAbility(Ability ability, Skill skill) throws SerializationException {
        ConfigurationNode abilityNode = root.node("abilities", ability.getId().toString());

        // Add all values in ability to a map
        Map<String, Object> configMap = new HashMap<>();
        for (Object keyObj : abilityNode.childrenMap().keySet()) {
            String key = (String) keyObj;
            Object value = abilityNode.node(key).raw();
            value = handleTransformations(key, value);
            configMap.put(key, value);
        }

        AbilityConfig abilityConfig = new AbilityConfig(configMap);
        return new LoadedAbility(ability, skill, abilityConfig);
    }

    private Object handleTransformations(String key, Object value) {
        if (key.equals("unlock") && value instanceof String valueExpr) {
            valueExpr = TextUtil.replace(valueExpr, "{start}", String.valueOf(plugin.config().getStartLevel()));
            Expression expression = new Expression(valueExpr);
            try {
                return expression.evaluate().getNumberValue().intValue();
            } catch (EvaluationException | ParseException e) {
                plugin.logger().warn("Failed to parse ability unlock expression " + valueExpr);
                e.printStackTrace();
            }
        }
        return value;
    }

}
