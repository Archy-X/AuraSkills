package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.event.user.UserLoadEvent;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.util.AttributeCompat;
import dev.aurelium.auraskills.bukkit.util.VersionUtils;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.EquipmentSlotGroup;

import java.util.UUID;

public class LuckTrait extends TraitImpl {

    private final UUID LEGACY_MODIFIER_ID = UUID.fromString("fd1c6253-b865-454f-9203-002e3676a9da");
    private final String MODIFIER_KEY = "luck_trait";

    LuckTrait(AuraSkills plugin) {
        super(plugin, Traits.LUCK);
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        AttributeInstance attribute = player.getAttribute(AttributeCompat.LUCK);
        if (attribute == null) {
            return 0.0;
        }
        double current = attribute.getValue();
        // Subtract skills modifier
        for (AttributeModifier am : attribute.getModifiers()) {
            if (isLuckTraitModifier(am)) {
                current -= am.getAmount();
            }
        }
        return current;
    }

    @EventHandler
    public void onJoin(UserLoadEvent event) {
        setLuck(event.getPlayer());
    }

    @Override
    public void reload(Player player, Trait trait) {
        setLuck(player);
    }

    @EventHandler
    public void worldChange(PlayerChangedWorldEvent event) {
        setLuck(event.getPlayer());
    }

    @SuppressWarnings("removal")
    private void setLuck(Player player) {
        AttributeInstance attribute = player.getAttribute(AttributeCompat.LUCK);
        if (attribute == null) {
            return;
        }
        boolean hasModifier = false;
        // Removes existing modifiers of the same name
        for (AttributeModifier am : attribute.getModifiers()) {
            if (isLuckTraitModifier(am)) {
                attribute.removeModifier(am);
                hasModifier = true;
            }
        }
        if (!Traits.LUCK.isEnabled()) return;
        if (!hasModifier) {
            attribute.setBaseValue(0.0);
        }
        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
            return;
        }
        User user = plugin.getUser(player);
        double luck = user.getBonusTraitLevel(Traits.LUCK);
        if (luck < 0.01) {
            return;
        }
        if (VersionUtils.isAtLeastVersion(21)) {
            NamespacedKey key = new NamespacedKey(plugin, MODIFIER_KEY);
            attribute.addModifier(new AttributeModifier(key, luck, Operation.ADD_NUMBER, EquipmentSlotGroup.ANY));
        } else {
            attribute.addModifier(new AttributeModifier(LEGACY_MODIFIER_ID, "AureliumSkills-Luck", luck, AttributeModifier.Operation.ADD_NUMBER));
        }
    }

    private boolean isLuckTraitModifier(AttributeModifier modifier) {
        final String legacyModifierName = "AureliumSkills-Luck";
        if (modifier.getName().equals(legacyModifierName)) {
            return true;
        }
        if (VersionUtils.isAtLeastVersion(21)) {
            final String attributeNamespace = "auraskills";
            String namespace = modifier.getKey().getNamespace();
            String key = modifier.getKey().getKey();
            if (key.equals(LEGACY_MODIFIER_ID.toString())) {
                return true;
            } else if (namespace.equals(attributeNamespace) && key.equals(MODIFIER_KEY)) {
                return true;
            } else {
                try {
                    UUID.fromString(key); // Check if the key is a UUID, ignore if exception is thrown
                    // Modifier is a high chance of a legacy migrated to 1.21 with unknown UUID
                    if (namespace.equals("minecraft") && modifier.getAmount() == 0 && modifier.getOperation().equals(Operation.ADD_NUMBER)) {
                        return true;
                    }
                } catch (IllegalArgumentException ignored) { }
            }
        }
        return false;
    }

}
