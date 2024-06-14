package dev.aurelium.auraskills.bukkit.requirement;

import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Requirements {

    private final AuraSkills plugin;

    public Requirements(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public Map<Skill, Integer> getLegacyRequirements(ModifierType type, ReadWriteNBT nbtItem) {
        ReadWriteNBT legacyCompound = ItemUtils.getLegacyRequirementsTypeCompound(nbtItem, type);
        Map<Skill, Integer> requirements = new HashMap<>();

        for (String key : legacyCompound.getKeys()) {
            Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(key.toLowerCase(Locale.ROOT)));
            if (skill != null) {
                int value = legacyCompound.getInteger(key);
                requirements.put(skill, value);
            }
        }
        return requirements;
    }

}
