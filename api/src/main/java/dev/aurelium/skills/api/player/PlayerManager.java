package dev.aurelium.skills.api.player;

import java.util.UUID;

public interface PlayerManager {

    SkillsPlayer getPlayer(UUID playerId);

}
