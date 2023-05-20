package dev.auramc.auraskills.api.event.data;

import dev.auramc.auraskills.api.AuraSkillsApi;
import dev.auramc.auraskills.api.event.AuraSkillsEvent;
import dev.auramc.auraskills.api.player.SkillsPlayer;

public class PlayerDataLoadEvent extends AuraSkillsEvent {

    private final SkillsPlayer player;

    public PlayerDataLoadEvent(AuraSkillsApi api, SkillsPlayer player) {
        super(api);
        this.player = player;
    }

    public SkillsPlayer getPlayer() {
        return player;
    }

}
