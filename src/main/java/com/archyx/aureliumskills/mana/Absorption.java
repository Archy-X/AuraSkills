package com.archyx.aureliumskills.mana;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.DefenseAbilities;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.levelers.SorceryLeveler;
import com.archyx.aureliumskills.util.LoreUtil;
import com.archyx.aureliumskills.util.NumberUtil;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Locale;

public class Absorption implements ManaAbility {

    private final AureliumSkills plugin;
    private final DefenseAbilities defenseAbilities;
    private final SorceryLeveler sorceryLeveler;

    public Absorption(AureliumSkills plugin, DefenseAbilities defenseAbilities) {
        this.plugin = plugin;
        this.sorceryLeveler = plugin.getSorceryLeveler();
        this.defenseAbilities = defenseAbilities;
    }

    @Override
    public AureliumSkills getPlugin() {
        return plugin;
    }

    @Override
    public MAbility getManaAbility() {
        return MAbility.ABSORPTION;
    }

    @Override
    public void activate(Player player) {
        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
        if (playerSkill != null) {
            Locale locale = Lang.getLanguage(player);
            defenseAbilities.getAbsorptionActivated().add(player); // Register as absorption activated
            // Play sound
            if (XMaterial.isNewVersion()) {
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);
            } else {
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            }
            // Consume mana
            double manaConsumed = plugin.getManaAbilityManager().getManaCost(MAbility.ABSORPTION, playerSkill);
            plugin.getManaManager().setMana(player.getUniqueId(), plugin.getManaManager().getMana(player.getUniqueId()) - manaConsumed);
            // Level Sorcery
            sorceryLeveler.level(player, manaConsumed);
            player.sendMessage(AureliumSkills.getPrefix(locale) + LoreUtil.replace(Lang.getMessage(ManaAbilityMessage.ABSORPTION_START, locale)
                    ,"{mana}", NumberUtil.format0(manaConsumed)));
        }
    }

    @Override
    public void stop(Player player) {
        defenseAbilities.getAbsorptionActivated().remove(player);
        Locale locale = Lang.getLanguage(player);
        player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(ManaAbilityMessage.ABSORPTION_END, locale));
    }
}
