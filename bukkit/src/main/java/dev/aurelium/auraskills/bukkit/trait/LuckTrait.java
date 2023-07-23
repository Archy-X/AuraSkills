package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.event.AuraSkillsEventHandler;
import dev.aurelium.auraskills.api.event.data.PlayerDataLoadEvent;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class LuckTrait extends TraitImpl {

    LuckTrait(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_LUCK);
        if (attribute == null) {
            return 0.0;
        }
        double current = attribute.getValue();
        // Subtract skills modifier
        for (AttributeModifier am : attribute.getModifiers()) {
            if (am.getName().equals("AureliumSkills-Luck")) {
                current -= am.getAmount();
            }
        }
        return current;
    }

    @AuraSkillsEventHandler
    public void onJoin(PlayerDataLoadEvent event) {
        setLuck(BukkitUser.getPlayer(event.getPlayer()));
    }

    public void reload(Player player) {
        if (player != null) {
            setLuck(player);
        }
    }

    @EventHandler
    public void worldChange(PlayerChangedWorldEvent event) {
        setLuck(event.getPlayer());
    }

    private void setLuck(Player player) {
        if (!Traits.LUCK.isEnabled()) return;
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_LUCK);
        if (attribute == null) {
            return;
        }
        boolean hasModifier = false;
        //Removes existing modifiers of the same name
        for (AttributeModifier am : attribute.getModifiers()) {
            if (am.getName().equals("AureliumSkills-Luck")) {
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
        User user = plugin.getUser(player);
        double luck = user.getBonusTraitLevel(Traits.LUCK);
        attribute.addModifier(new AttributeModifier("AureliumSkills-Luck", luck, AttributeModifier.Operation.ADD_NUMBER));
    }

}
