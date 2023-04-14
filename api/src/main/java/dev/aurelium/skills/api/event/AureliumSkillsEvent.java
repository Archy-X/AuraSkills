package dev.aurelium.skills.api.event;

import dev.aurelium.skills.api.AureliumSkillsApi;

public interface AureliumSkillsEvent {

    AureliumSkillsApi getApi();

    Class<? extends AureliumSkillsEvent> getEventType();

}
