package dev.auramc.auraskills.common.event;

import dev.auramc.auraskills.api.event.AuraSkillsEvent;

public class EventDispatcher {

    private final AuraSkillsEventManager eventManager;

    public EventDispatcher(AuraSkillsEventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void callEvent(AuraSkillsEvent event) {
        eventManager.getEventBus().post(event);
    }

}
