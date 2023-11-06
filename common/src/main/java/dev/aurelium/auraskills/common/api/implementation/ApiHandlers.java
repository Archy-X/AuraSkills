package dev.aurelium.auraskills.common.api.implementation;

import dev.aurelium.auraskills.api.registry.Handlers;
import dev.aurelium.auraskills.api.trait.TraitHandler;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;

public class ApiHandlers implements Handlers {

    private final AuraSkillsPlugin plugin;

    public ApiHandlers(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerTraitHandler(TraitHandler traitHandler) {
        plugin.getTraitManager().registerTraitHandler(traitHandler);
    }
}
