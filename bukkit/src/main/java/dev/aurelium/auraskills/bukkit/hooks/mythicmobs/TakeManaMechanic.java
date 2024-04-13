package dev.aurelium.auraskills.bukkit.hooks.mythicmobs;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;

@MythicMechanic(name = "takeMana")
public class TakeManaMechanic implements ITargetedEntitySkill {

    private final double manaToTake;

    public TakeManaMechanic(MythicMechanicLoadEvent loader) {
        this.manaToTake = loader.getConfig().getDouble(new String[] {"mana", "m"}, 0);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if(!data.getCaster().getEntity().isPlayer()) return SkillResult.CONDITION_FAILED;

        var user = AuraSkillsApi.get().getUser(data.getCaster().getEntity().getUniqueId());

        if(user == null) return SkillResult.CONDITION_FAILED;
        if(user.getMana() < manaToTake) return SkillResult.CONDITION_FAILED;

        user.setMana(user.getMana() - manaToTake);

        return SkillResult.SUCCESS;
    }
}
