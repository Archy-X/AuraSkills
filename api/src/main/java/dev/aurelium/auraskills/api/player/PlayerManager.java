package dev.aurelium.auraskills.api.player;

import java.util.UUID;

public interface PlayerManager {

    SkillsPlayer getPlayer(UUID playerId);

}
