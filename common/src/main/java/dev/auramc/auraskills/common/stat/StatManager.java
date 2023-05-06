package dev.auramc.auraskills.common.stat;

import dev.auramc.auraskills.api.stat.Stat;
import dev.auramc.auraskills.common.data.PlayerData;

/**
 * Interface with methods to manage player stats.
 */
public interface StatManager {

    void reloadStat(PlayerData playerData, Stat stat);

}
