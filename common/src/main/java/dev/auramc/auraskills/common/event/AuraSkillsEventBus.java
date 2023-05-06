package dev.auramc.auraskills.common.event;

import dev.auramc.auraskills.api.event.AuraSkillsEvent;
import dev.auramc.auraskills.common.AuraSkillsPlugin;
import net.kyori.event.SimpleEventBus;

public class AuraSkillsEventBus extends SimpleEventBus<AuraSkillsEvent> {

    private final AuraSkillsPlugin plugin;

    public AuraSkillsEventBus(AuraSkillsPlugin plugin) {
        super(AuraSkillsEvent.class);
        this.plugin = plugin;
    }

    public AuraSkillsPlugin getPlugin() {
        return plugin;
    }

}
