package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.event.user.UserLoadEvent;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.UUID;

public class MovementSpeedTrait extends TraitImpl {

    private final UUID MODIFIER_ID = UUID.fromString("3f805765-46e6-494e-877b-37ef82ed9e22");

    MovementSpeedTrait(AuraSkills plugin) {
        super(plugin, Traits.MOVEMENT_SPEED);
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (attribute == null) {
            return 100;
        }
        double totalValue = attribute.getValue();
        // Subtract the trait value
        double baseValue = totalValue - getAttributeModifierValue(player, trait);
        return baseValue * 500;
    }

    @Override
    protected void reload(Player player, Trait trait) {
        double value = getAttributeModifierValue(player, trait);
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (attribute == null) return;
        // Remove existing modifier
        for (AttributeModifier modifier : attribute.getModifiers()) {
            if (modifier.getUniqueId().equals(MODIFIER_ID)) {
                attribute.removeModifier(modifier);
            }
        }
        if (!trait.isEnabled()) return;
        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) return;
        if (attribute.getValue() > trait.optionDouble("max") * 0.002) return;

        var mod = new AttributeModifier(MODIFIER_ID, "auraskills_movement_speed", value, AttributeModifier.Operation.ADD_NUMBER);
        attribute.addModifier(mod);
    }

    @EventHandler
    public void onLoad(UserLoadEvent event) {
        reload(event.getPlayer(), getTraits()[0]);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void worldChange(PlayerChangedWorldEvent event) {
        reload(event.getPlayer(), getTraits()[0]);
    }

    private double getAttributeModifierValue(Player player, Trait trait) {
        User user = plugin.getUser(player);
        return user.getBonusTraitLevel(trait) * 0.002;
    }

}
