package dev.aurelium.auraskills.bukkit.hooks.mythicmobs;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.conditions.ICasterCondition;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(name = "hasMana")
public class HasManaCondition implements ICasterCondition {
    private final double requiredMana;

    public HasManaCondition(MythicConditionLoadEvent loader) {
        this.requiredMana = loader.getConfig().getDouble(new String[] { "mana", "m" }, 0);
    }

    @Override
    public boolean check(SkillCaster caster) {
        if(!caster.getEntity().isPlayer()) return false;
        return AuraSkillsApi.get().getUser(caster.getEntity().getUniqueId()).getMana() >= requiredMana;
    }
}
