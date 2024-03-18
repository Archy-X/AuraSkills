package dev.aurelium.auraskills.common.ui;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.user.User;

public interface UiProvider {

    ActionBarManager getActionBarManager();

    void sendActionBar(User user, String message);

    void sendXpBossBar(User user, Skill skill, double currentXp, double levelXp, double xpGained, int level, boolean maxed);

    void sendTitle(User user, String title, String subtitle, int fadeIn, int stay, int fadeOut);

}
