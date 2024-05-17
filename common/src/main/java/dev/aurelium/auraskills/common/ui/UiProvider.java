package dev.aurelium.auraskills.common.ui;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.user.User;

import java.text.NumberFormat;

public interface UiProvider {

    ActionBarManager getActionBarManager();

    // Allows ActionBarManager to access boss bar format options
    NumberFormat getFormat(FormatType type);

    void sendActionBar(User user, String message);

    void sendXpBossBar(User user, Skill skill, double currentXp, double levelXp, double xpGained, int level, boolean maxed, double income);

    void sendTitle(User user, String title, String subtitle, int fadeIn, int stay, int fadeOut);

    enum FormatType {

        XP,
        PERCENT,
        MONEY

    }

}
