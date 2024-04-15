package dev.aurelium.auraskills.bukkit.hooks.mythicmobs;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.user.User;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import org.bukkit.entity.Player;

@MythicMechanic(name = "giveSkillXP")
public class GiveSkillXpMechanic implements ITargetedEntitySkill {

    private final AuraSkills plugin;
    private final PlaceholderDouble xp;
    private final Skill skill;

    public GiveSkillXpMechanic(AuraSkills plugin, MythicMechanicLoadEvent loader) {
        this.plugin = plugin;
        this.xp = loader.getConfig().getPlaceholderDouble("xp", 0.0D);
        this.skill = plugin.getSkillRegistry().get(NamespacedId.fromDefault(loader.getConfig().getString(new String[]{"skill", "s"})));
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (!target.isPlayer()) return SkillResult.INVALID_TARGET;
        if (skill == null) return SkillResult.INVALID_CONFIG;

        Player player = BukkitAdapter.adapt(target.asPlayer());
        User user = plugin.getUser(player);

        plugin.getScheduler().executeSync(() -> plugin.getLevelManager().addXp(user, skill, null, xp.get(data)));

        return SkillResult.SUCCESS;
    }
}
