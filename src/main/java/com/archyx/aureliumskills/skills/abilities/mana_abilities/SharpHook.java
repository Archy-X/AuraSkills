package com.archyx.aureliumskills.skills.abilities.mana_abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.levelers.SorceryLeveler;
import com.archyx.aureliumskills.util.LoreUtil;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Locale;

public class SharpHook implements ManaAbility {

    private final SorceryLeveler sorceryLeveler;

    public SharpHook(AureliumSkills plugin) {
        this.sorceryLeveler = plugin.getSorceryLeveler();
    }

    @Override
    public void activate(Player player) {
        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
        if (playerSkill != null) {
            Locale locale = Lang.getLanguage(player);
            //Consume mana
            int manaConsumed = MAbility.SHARP_HOOK.getManaCost(playerSkill.getManaAbilityLevel(MAbility.SHARP_HOOK));
            AureliumSkills.manaManager.setMana(player.getUniqueId(), AureliumSkills.manaManager.getMana(player.getUniqueId()) - manaConsumed);
            // Level Sorcery
            sorceryLeveler.level(player, manaConsumed);
            player.sendMessage(AureliumSkills.getPrefix(locale) + LoreUtil.replace(Lang.getMessage(ManaAbilityMessage.SHARP_HOOK_USE, locale), "{mana}", String.valueOf(manaConsumed)));
            if (OptionL.getBoolean(Option.SHARP_HOOK_ENABLE_SOUND)) {
                if (XMaterial.isNewVersion()) {
                    player.playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_RETRIEVE, 1f, 1.5f);
                } else {
                    player.playSound(player.getLocation(), "entity.bobber.retrieve", 1f, 1.5f);
                }
            }
        }
    }

    @Override
    public void update(Player player) {

    }

    @Override
    public void stop(Player player) {
        PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
        if (skill != null) {
            AureliumSkills.manaAbilityManager.setCooldown(player.getUniqueId(), MAbility.SHARP_HOOK, (int) (MAbility.SHARP_HOOK.getCooldown(skill.getManaAbilityLevel(MAbility.SHARP_HOOK)) * 20));
        }
    }
}
