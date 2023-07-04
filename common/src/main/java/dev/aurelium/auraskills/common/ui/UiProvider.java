package dev.aurelium.auraskills.common.ui;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.player.User;

public interface UiProvider {

    void sendXpActionBar(User user, double currentXp, double levelXp, double xpGained, int level, boolean maxed);

    void sendXpBossBar(User user, Skill skill, double currentXp, double levelXp, double xpGained, int level, boolean maxed);

    void sendTitle(User user, String title, String subtitle, int fadeIn, int stay, int fadeOut);

}
