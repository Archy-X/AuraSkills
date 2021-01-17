package com.archyx.aureliumskills.mana;

import com.archyx.aureliumskills.AureliumSkills;
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

public class SharpHook implements ManaAbility {

    private final SorceryLeveler sorceryLeveler;
    private final AureliumSkills plugin;

    public SharpHook(AureliumSkills plugin) {
        this.plugin = plugin;
        this.sorceryLeveler = plugin.getSorceryLeveler();
    }

    @Override
    public AureliumSkills getPlugin() {
        return plugin;
    }

    @Override
    public MAbility getManaAbility() {
        return MAbility.SHARP_HOOK;
    }

    @Override
    public void activate(Player player) {
        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
        if (playerSkill != null) {
            Locale locale = Lang.getLanguage(player);
            //Consume mana
            double manaConsumed = plugin.getManaAbilityManager().getManaCost(MAbility.SHARP_HOOK, playerSkill);
            plugin.getManaManager().setMana(player.getUniqueId(), plugin.getManaManager().getMana(player.getUniqueId()) - manaConsumed);
            // Level Sorcery
            sorceryLeveler.level(player, manaConsumed);
            plugin.getAbilityManager().sendMessage(player, LoreUtil.replace(Lang.getMessage(ManaAbilityMessage.SHARP_HOOK_USE, locale), "{mana}", NumberUtil.format0(manaConsumed)));
            if (plugin.getManaAbilityManager().getOptionAsBooleanElseTrue(MAbility.SHARP_HOOK, "enable_sound")) {
                if (XMaterial.isNewVersion()) {
                    player.playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_RETRIEVE, 1f, 1.5f);
                } else {
                    player.playSound(player.getLocation(), "entity.bobber.retrieve", 1f, 1.5f);
                }
            }
        }
    }

}
