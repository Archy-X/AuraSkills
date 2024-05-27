package dev.aurelium.auraskills.common.config;

import dev.aurelium.auraskills.api.skill.Skill;

import java.util.List;

/**
 * Represents a provider for the plugin's main configuration.
 */
public interface ConfigProvider {

    boolean getBoolean(Option option);

    int getInt(Option option);

    double getDouble(Option option);

    String getString(Option option);

    List<String> getStringList(Option option);

    boolean isEnabled(Skill skill);

    int getMaxLevel(Skill skill);

    int getHighestMaxLevel();

    int getStartLevel();

    boolean jobSelectionEnabled();

}
