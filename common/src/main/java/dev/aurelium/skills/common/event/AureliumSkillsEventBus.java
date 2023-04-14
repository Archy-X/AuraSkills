package dev.aurelium.skills.common.event;

import dev.aurelium.skills.api.event.AureliumSkillsEvent;
import dev.aurelium.skills.common.AureliumSkillsPlugin;
import net.kyori.event.SimpleEventBus;

public class AureliumSkillsEventBus extends SimpleEventBus<AureliumSkillsEvent> {

    private final AureliumSkillsPlugin plugin;

    public AureliumSkillsEventBus(AureliumSkillsPlugin plugin) {
        super(AureliumSkillsEvent.class);
        this.plugin = plugin;
    }

    public AureliumSkillsPlugin getPlugin() {
        return plugin;
    }

}
