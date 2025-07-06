package dev.aurelium.auraskills.common.config;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class ConfigProviderTest {

    private ConfigProvider configProvider;

    protected abstract ConfigProvider getConfigProvider();

    @BeforeEach
    public void setUpConfigProvider() {
        configProvider = getConfigProvider();
    }

    @Test
    void testLoadOptions() {
        configProvider.loadOptions();

        for (Option option : Option.values()) {
            assertTrue(configProvider.options.containsKey(option));
        }
    }

    @Test
    void testGetMaxLevel() {
        for (Skill skill : Skills.values()) {
            if (configProvider.isEnabled(skill)) {
                assertEquals(100, configProvider.getMaxLevel(skill));
            } else {
                assertEquals(0, configProvider.getMaxLevel(skill));
            }
        }
    }

}
