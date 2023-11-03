package dev.aurelium.auraskills.bukkit.event;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.event.skill.SkillLevelUpEvent;
import dev.aurelium.auraskills.api.event.skill.XpGainEvent;
import dev.aurelium.auraskills.api.event.user.UserLoadEvent;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.event.EventHandler;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.Pair;
import org.bukkit.Bukkit;

public class BukkitEventHandler implements EventHandler {

    private final AuraSkills plugin;

    public BukkitEventHandler(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public void callUserLoadEvent(User user) {
        UserLoadEvent event = new UserLoadEvent(((BukkitUser) user).getPlayer(), user.toApi());
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public void callSkillLevelUpEvent(User user, Skill skill, int level) {
        SkillLevelUpEvent event = new SkillLevelUpEvent(((BukkitUser) user).getPlayer(), user.toApi(), skill, level);
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public Pair<Boolean, Double> callXpGainEvent(User user, Skill skill, double amount) {
        XpGainEvent event = new XpGainEvent(((BukkitUser) user).getPlayer(), user.toApi(), skill, amount);
        Bukkit.getPluginManager().callEvent(event);
        return new Pair<>(event.isCancelled(), event.getAmount());
    }

}
