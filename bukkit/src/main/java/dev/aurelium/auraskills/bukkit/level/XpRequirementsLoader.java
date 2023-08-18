package dev.aurelium.auraskills.bukkit.level;

import com.udojava.evalex.Expression;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.ConfigurateLoader;
import dev.aurelium.auraskills.common.level.XpRequirements;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class XpRequirementsLoader {

    private final AuraSkills plugin;
    private final XpRequirements requirements;

    public XpRequirementsLoader(AuraSkills plugin, XpRequirements requirements) {
        this.plugin = plugin;
        this.requirements = requirements;
    }

    public void load() {
        ConfigurateLoader loader = new ConfigurateLoader(plugin, TypeSerializerCollection.builder().build());
        String FILE_NAME = "xp_requirements.yml";
        try {
            loader.generateUserFile(FILE_NAME);

            ConfigurationNode config = loader.loadUserFile(FILE_NAME);

            // Load default section
            ConfigurationNode defaultConfig = config.node("default");
            if (!defaultConfig.virtual()) {
                requirements.setDefaultXpRequirements(loadSection(defaultConfig));
                plugin.logger().info("Loaded default section in " + FILE_NAME);
            }

            // Load skill sections
            int numLoaded = 0;
            for (Skill skill : plugin.getSkillManager().getSkillValues()) {
                requirements.removeSkillXpRequirements(skill); // Remove to account for deleted sections

                ConfigurationNode skillNode = config.node("skills").node(skill.name().toLowerCase());
                // Use NamespacedId as name if regular name fails
                if (skillNode.virtual()) {
                    skillNode = config.node("skills").node(skill.getId().toString());
                }
                // Only load if present
                if (!skillNode.virtual()) {
                    requirements.setSkillXpRequirements(skill, loadSection(skillNode));
                    numLoaded++;
                }
            }
            if (numLoaded > 0) {
                plugin.logger().info("Loaded " + numLoaded + " skill sections in " + FILE_NAME);
            }
        } catch (IOException e) {
            plugin.logger().warn("Failed to load " + FILE_NAME + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Integer> loadSection(ConfigurationNode config) throws SerializationException {
        List<Integer> list = new ArrayList<>();
        int highestMaxLevel = plugin.config().getHighestMaxLevel();

        if (!config.node("values").virtual()) {
            // Direct list of values
            List<String> values = config.node("values").getList(String.class);
            Objects.requireNonNull(values);

            for (int i = 0; i < highestMaxLevel; i++) {
                if (i < values.size()) {
                    list.add(Integer.parseInt(values.get(i)));
                } else {
                    // Use the last defined value for the rest of the levels
                    list.add(Integer.parseInt(values.get(values.size() - 1)));
                }
            }
        } else {
            // Expression based
            Expression expression = getXpExpression(config);
            // Add xp requirement for each level
            for (int i = 0; i < highestMaxLevel; i++) {
                expression.setVariable("level", BigDecimal.valueOf(i + 2));
               list.add((int) Math.round(expression.eval().doubleValue()));
            }
        }

        return list;
    }

    private Expression getXpExpression(ConfigurationNode config) {
        String expressionString = config.node("expression").getString();
        Objects.requireNonNull(expressionString);

        Expression expression = new Expression(expressionString);
        // Set variables
        for (Object variableObj : config.childrenMap().keySet()) {
            String variable = (String) variableObj;

            if (variable.equals("expression")) continue;

            double variableValue = config.node(variable).getDouble();
            expression.setVariable(variable, BigDecimal.valueOf(variableValue));
        }
        return expression;
    }

}
