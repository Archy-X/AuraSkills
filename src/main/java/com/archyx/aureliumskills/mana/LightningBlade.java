package com.archyx.aureliumskills.mana;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.skills.sorcery.SorceryLeveler;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.Locale;

public class LightningBlade implements ManaAbility {

    private final AureliumSkills plugin;
    private final SorceryLeveler sorceryLeveler;

    public LightningBlade(AureliumSkills plugin) {
        this.plugin = plugin;
        this.sorceryLeveler = plugin.getSorceryLeveler();
    }

    @Override
    public AureliumSkills getPlugin() {
        return plugin;
    }

    @Override
    public MAbility getManaAbility() {
        return MAbility.LIGHTNING_BLADE;
    }

    @Override
    public void activate(Player player) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData == null) return;

        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (attribute == null) return;

        // Remove existing modifier if exists
        for (AttributeModifier modifier : attribute.getModifiers()) {
            if (modifier.getName().equals("AureliumSkills-LightningBlade")) {
                attribute.removeModifier(modifier);
            }
        }
        // Increase attack speed attribute
        double currentValue = attribute.getValue();
        double addedValue = plugin.getManaAbilityManager().getValue(MAbility.LIGHTNING_BLADE, playerData) / 100 * currentValue;
        attribute.addModifier(new AttributeModifier("AureliumSkills-LightningBlade", addedValue, AttributeModifier.Operation.ADD_NUMBER));
        //Consume mana
        double manaConsumed = plugin.getManaAbilityManager().getManaCost(MAbility.LIGHTNING_BLADE, playerData);
        playerData.setMana(playerData.getMana() - manaConsumed);
        sorceryLeveler.level(player, manaConsumed); // Level sorcery
        // Play sound and send message
        if (XMaterial.isNewVersion()) {
            player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 0.5f, 1);
        } else {
            player.playSound(player.getLocation(), "entity.illusion_illager.prepare_mirror", 0.5f, 1);
        }
        Locale locale = playerData.getLocale();
        plugin.getAbilityManager().sendMessage(player, Lang.getMessage(ManaAbilityMessage.LIGHTNING_BLADE_START, locale)
                .replace("{mana}", NumberUtil.format0(manaConsumed)));
    }

    @Override
    public void stop(Player player) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (attribute == null) return;
        // Remove modifier if exists
        for (AttributeModifier modifier : attribute.getModifiers()) {
            if (modifier.getName().equals("AureliumSkills-LightningBlade")) {
                attribute.removeModifier(modifier);
            }
        }
        plugin.getAbilityManager().sendMessage(player, Lang.getMessage(ManaAbilityMessage.LIGHTNING_BLADE_END, plugin.getLang().getLocale(player)));
    }

}
