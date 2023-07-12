package dev.aurelium.auraskills.bukkit.ui;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.player.User;
import dev.aurelium.auraskills.common.ui.UiProvider;
import org.bukkit.entity.Player;

public class BukkitUiProvider implements UiProvider {

    @Override
    public void sendXpActionBar(User user, double currentXp, double levelXp, double xpGained, int level, boolean maxed) {

    }

    @Override
    public void sendXpBossBar(User user, Skill skill, double currentXp, double levelXp, double xpGained, int level, boolean maxed) {
        Player player = ((BukkitUser) user).getPlayer();
        player.sendMessage("You gained " + xpGained + " " + skill.toString() + " XP");
    }

    @Override
    public void sendTitle(User user, String title, String subtitle, int fadeIn, int stay, int fadeOut) {

    }
}
