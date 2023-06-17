package dev.aurelium.auraskills.api.event.data;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.event.AuraSkillsEvent;
import dev.aurelium.auraskills.api.player.SkillsPlayer;

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
