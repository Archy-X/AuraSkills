package dev.aurelium.auraskills.test;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.util.file.FileUtil;
import org.junit.jupiter.api.Test;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigOptionTest {

    @Test
    public void testOptionMatchesConfigFile() throws IOException {
        ConfigurationNode config = FileUtil.loadEmbeddedYamlFile("config.yml", AuraSkillsPlugin.class.getClassLoader(), false);
        
        for (Option option : Option.values()) {
            NodePath path = FileUtil.toPath(option.getPath());
            ConfigurationNode node = config.node(path);
            // Ensure option exist
            assertFalse(node.virtual(), "Failed at " + option);
            // Ensure type matches
            switch (option.getType()) {
                case INT -> assertNotNull(node.get(Integer.class), "Failed at " + option);
                case DOUBLE -> assertNotNull(node.get(Double.class), "Failed at " + option);
                case BOOLEAN -> assertNotNull(node.get(Boolean.class), "Failed at " + option);
                case STRING, COLOR -> {
                    assertNotNull(node.getString(), "Failed at " + option);
                    assertInstanceOf(String.class, node.raw(), "Failed at " + option);
                }
                case LIST -> assertNotNull(node.getList(String.class), "Failed at " + option);
            }
        }
    }

}
