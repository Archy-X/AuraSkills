package dev.auramc.auraskills.api.event;

import dev.auramc.auraskills.api.AuraSkillsApi;

public class AuraSkillsEvent {

    private final AuraSkillsApi api;

    public AuraSkillsEvent(AuraSkillsApi api) {
        this.api = api;
    }

    public AuraSkillsApi getApi() {
        return api;
    }

    public Class<? extends AuraSkillsEvent> getEventType() {
        return getClass();
    }

}
