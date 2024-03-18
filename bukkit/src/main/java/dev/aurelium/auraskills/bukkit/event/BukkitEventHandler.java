package dev.aurelium.auraskills.bukkit.event;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.event.skill.SkillLevelUpEvent;
import dev.aurelium.auraskills.api.event.skill.XpGainEvent;
import dev.aurelium.auraskills.api.event.user.UserLoadEvent;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.event.EventHandler;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class BukkitEventHandler implements EventHandler {

    private final AuraSkills plugin;

    public BukkitEventHandler(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public void callUserLoadEvent(User user) {
        Player player = ((BukkitUser) user).getPlayer();
        if (player != null) {
            UserLoadEvent event = new UserLoadEvent(player, user.toApi());
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    @Override
    public void callSkillLevelUpEvent(User user, Skill skill, int level) {
        Player player = ((BukkitUser) user).getPlayer();
        if (player != null) {
            SkillLevelUpEvent event = new SkillLevelUpEvent(player, user.toApi(), skill, level);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    @Override
    public Pair<Boolean, Double> callXpGainEvent(User user, Skill skill, @Nullable XpSource source, double amount) {
        Player player = ((BukkitUser) user).getPlayer();
        if (player != null) {
            XpGainEvent event = new XpGainEvent(player, user.toApi(), skill, source, amount);
            Bukkit.getPluginManager().callEvent(event);
            return new Pair<>(event.isCancelled(), event.getAmount());
        } else {
            return new Pair<>(false, 0.0);
        }
    }

}
