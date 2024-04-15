package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.damage.DamageMeta;
import dev.aurelium.auraskills.api.event.damage.DamageEvent;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.api.damage.DamageModifier;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.api.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Locale;

public class AttackDamageTrait extends TraitImpl {

    AttackDamageTrait(AuraSkills plugin) {
        super(plugin, Traits.ATTACK_DAMAGE);
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        return 0;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void damageListener(DamageEvent event) {
        // LOW to make sure it runs before ability modifiers
        DamageMeta meta = event.getDamageMeta();
        Player player = meta.getAttackerAsPlayer();

        if (player != null) {
            User user = plugin.getUser(player);
            Trait trait = Traits.ATTACK_DAMAGE;
            if (!trait.isEnabled()) return;

            if (meta.getDamageType() == DamageType.HAND && !trait.optionBoolean("hand_damage")) {
                return;
            }

            if (meta.getDamageType() == DamageType.BOW && !trait.optionBoolean("bow_damage")) {
                return;
            }

            meta.addAttackModifier(applyStrength(user));
        }
    }

    @Override
    public String getMenuDisplay(double value, Trait trait, Locale locale) {
        if (Traits.ATTACK_DAMAGE.optionBoolean("use_percent")) {
            return "+" + NumberUtil.format1(value) + "%";
        } else {
            return "+" + NumberUtil.format1(value);
        }
    }

    private DamageModifier applyStrength(User user) {
        double value = user.getBonusTraitLevel(Traits.ATTACK_DAMAGE);
        if (Traits.ATTACK_DAMAGE.optionBoolean("use_percent")) {
            return new DamageModifier(value / 100, DamageModifier.Operation.MULTIPLY);
        } else {
            return new DamageModifier(value, DamageModifier.Operation.ADD_BASE);
        }
    }

}
