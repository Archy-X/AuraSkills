package dev.aurelium.auraskills.bukkit.jobs;

import dev.aurelium.auraskills.api.event.skill.XpGainEvent;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class JobsListener implements Listener {

    private final AuraSkills plugin;

    public JobsListener(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void blockXpGain(XpGainEvent event) {
        if (!plugin.config().jobSelectionEnabled()) return;
        if (!plugin.configBoolean(Option.JOBS_SELECTION_DISABLE_UNSELECTED_XP)) return;

        Skill skill = event.getSkill();
        User user = plugin.getUser(event.getPlayer());

        if (!user.getJobs().contains(skill)) {
            event.setCancelled(true);
        }
    }

}
