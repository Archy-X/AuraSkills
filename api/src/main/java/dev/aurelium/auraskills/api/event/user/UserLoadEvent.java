package dev.aurelium.auraskills.api.event.user;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.event.AuraSkillsEvent;
import dev.aurelium.auraskills.api.user.SkillsUser;

public class UserLoadEvent extends AuraSkillsEvent {

    private final SkillsUser player;

    public UserLoadEvent(AuraSkillsApi api, SkillsUser player) {
        super(api);
        this.player = player;
    }

    public SkillsUser getUser() {
        return player;
    }

}
