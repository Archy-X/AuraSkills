package dev.aurelium.skills.common.stat;

import dev.aurelium.skills.api.stat.Stat;
import dev.aurelium.skills.common.data.PlayerData;

public interface StatManager {

    void reloadStat(PlayerData playerData, Stat stat);

}
