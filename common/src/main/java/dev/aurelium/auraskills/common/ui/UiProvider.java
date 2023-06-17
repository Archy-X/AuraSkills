package dev.aurelium.auraskills.common.ui;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.data.PlayerData;

public interface UiProvider {

    void sendXpActionBar(PlayerData playerData, double currentXp, double levelXp, double xpGained, int level, boolean maxed);

    void sendXpBossBar(PlayerData playerData, Skill skill, double currentXp, double levelXp, double xpGained, int level, boolean maxed);

    void sendTitle(PlayerData playerData, String title, String subtitle, int fadeIn, int stay, int fadeOut);

}
