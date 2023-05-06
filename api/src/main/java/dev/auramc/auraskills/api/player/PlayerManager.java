package dev.auramc.auraskills.api.player;

import java.util.UUID;

public interface PlayerManager {

    SkillsPlayer getPlayer(UUID playerId);

}
