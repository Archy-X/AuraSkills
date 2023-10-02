package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.math.NumberUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AttackSpeedTrait extends TraitImpl {

    private final UUID MODIFIER_ID = UUID.fromString("2fc64528-614b-11ee-8c99-0242ac120002");

    AttackSpeedTrait(AuraSkills plugin) {
        super(plugin, Traits.ATTACK_SPEED);
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        return 0;
    }

    @Override
    public String getMenuDisplay(double value, Trait trait) {
        return "+" + NumberUtil.format1(value) + "%";
    }

    @Override
    protected void reload(Player player, Trait trait) {
        User user = plugin.getUser(player);
        setAttackSpeed(player, user);
    }

    private void setAttackSpeed(Player player, User user) {
        if (!Traits.ATTACK_SPEED.isEnabled()) return;
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (attribute == null) {
            return;
        }
        boolean hasModifier = false;
        //Removes existing modifiers of the same name
        for (AttributeModifier am : attribute.getModifiers()) {
            if (am.getName().equals("auraskills_attack_speed")) {
                attribute.removeModifier(am);
                hasModifier = true;
            }
        }
        if (!hasModifier) {
            attribute.setBaseValue(0.0);
        }
        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
            return;
        }
        double attackSpeed = user.getEffectiveTraitLevel(Traits.ATTACK_SPEED) / 100;
        attribute.addModifier(new AttributeModifier(MODIFIER_ID, "auraskills_attack_speed", attackSpeed, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
    }

}
