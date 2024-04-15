package dev.aurelium.auraskills.bukkit.hooks.mythicmobs;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.user.User;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;

@MythicMechanic(name = "takeMana")
public class TakeManaMechanic implements ITargetedEntitySkill {

    private final AuraSkills plugin;
    private final double manaToTake;

    public TakeManaMechanic(AuraSkills plugin, MythicMechanicLoadEvent loader) {
        this.plugin = plugin;
        this.manaToTake = loader.getConfig().getDouble(new String[]{"mana", "m"}, 0);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (!data.getCaster().getEntity().isPlayer()) return SkillResult.CONDITION_FAILED;

        User user = plugin.getUser(BukkitAdapter.adapt(data.getCaster().getEntity().asPlayer()));

        if (user.getMana() < manaToTake) return SkillResult.CONDITION_FAILED;

        user.setMana(user.getMana() - manaToTake);

        return SkillResult.SUCCESS;
    }
}
