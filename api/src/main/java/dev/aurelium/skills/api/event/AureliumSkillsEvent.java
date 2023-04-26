package dev.aurelium.skills.api.event;

import dev.aurelium.skills.api.AureliumSkillsApi;

public class AureliumSkillsEvent {

    private final AureliumSkillsApi api;

    public AureliumSkillsEvent(AureliumSkillsApi api) {
        this.api = api;
    }

    public AureliumSkillsApi getApi() {
        return api;
    }

    public Class<? extends AureliumSkillsEvent> getEventType() {
        return getClass();
    }

}
