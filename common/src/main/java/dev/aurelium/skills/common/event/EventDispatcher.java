package dev.aurelium.skills.common.event;

import dev.aurelium.skills.api.event.AureliumSkillsEvent;

public class EventDispatcher {

    private final AureliumSkillsEventManager eventManager;

    public EventDispatcher(AureliumSkillsEventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void callEvent(AureliumSkillsEvent event) {
        eventManager.getEventBus().post(event);
    }

}
