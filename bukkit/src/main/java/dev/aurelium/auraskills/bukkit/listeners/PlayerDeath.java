package dev.aurelium.auraskills.bukkit.listeners;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {

    private final AuraSkills plugin;

    public PlayerDeath(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (plugin.configBoolean(Option.ON_DEATH_RESET_SKILLS) || plugin.configBoolean(Option.ON_DEATH_RESET_XP)) {
            Player player = event.getEntity();
            User user = plugin.getUser(player);
            for (Skill skill : plugin.getSkillRegistry().getValues()) {
                if (plugin.configBoolean(Option.ON_DEATH_RESET_SKILLS)) {
                    user.resetSkill(skill);
                } else if (plugin.configBoolean(Option.ON_DEATH_RESET_XP)) {
                    user.setSkillXp(skill, 0);
                }
            }
        }
    }

}
