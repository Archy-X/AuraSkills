package dev.aurelium.auraskills.bukkit.modifier;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.api.skill.Multiplier;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Multipliers {

    private final AuraSkills plugin;

    public Multipliers(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public List<Multiplier> getLegacyMultipliers(ModifierType type, NBTItem nbtItem) {
        NBTCompound legacyCompound = ItemUtils.getLegacyMultipliersTypeCompound(nbtItem, type);
        List<Multiplier> multipliers = new ArrayList<>();

        for (String key : legacyCompound.getKeys()) {
            double value = legacyCompound.getDouble(key);
            if (key.equals("Global")) {
                multipliers.add(new Multiplier(key, null, value));
            } else {
                Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(key.toLowerCase(Locale.ROOT)));
                if (skill != null) {
                    multipliers.add(new Multiplier(key, skill, value));
                }
            }
        }
        return multipliers;
    }

}
