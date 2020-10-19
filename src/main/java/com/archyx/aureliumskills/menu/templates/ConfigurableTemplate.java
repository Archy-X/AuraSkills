package com.archyx.aureliumskills.menu.templates;

import org.bukkit.configuration.ConfigurationSection;

public interface ConfigurableTemplate {

    TemplateType getType();

    void load(ConfigurationSection config);

}
