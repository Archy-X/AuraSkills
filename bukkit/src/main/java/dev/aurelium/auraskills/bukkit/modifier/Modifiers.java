package dev.aurelium.auraskills.bukkit.modifier;

import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Modifiers {
    
    private final AuraSkills plugin;

    public Modifiers(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public List<StatModifier> getLegacyModifiers(ModifierType type, ReadWriteNBT nbt) {
        if (plugin.isNbtApiDisabled()) return new ArrayList<>();
        List<StatModifier> modifiers = new ArrayList<>();
        ReadWriteNBT compound = ItemUtils.getLegacyModifiersTypeCompound(nbt, type);
        for (String key : compound.getKeys()) {
            Stat stat = plugin.getStatRegistry().getOrNull(NamespacedId.fromDefault(key.toLowerCase(Locale.ROOT)));
            if (stat != null) {
                double value = compound.getDouble(key);
                modifiers.add(new StatModifier(key, stat, value));
            }
        }
        return modifiers;
    }
    
}
