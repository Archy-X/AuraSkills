package dev.aurelium.auraskills.api.user;

import java.util.UUID;

public interface UserManager {

    SkillsUser getUser(UUID playerId);

}
