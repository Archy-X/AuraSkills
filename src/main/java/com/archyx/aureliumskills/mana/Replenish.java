package com.archyx.aureliumskills.mana;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.levelers.SorceryLeveler;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Locale;

public class Replenish implements ManaAbility {

    private final SorceryLeveler sorceryLeveler;

    public Replenish(AureliumSkills plugin) {
        this.sorceryLeveler = plugin.getSorceryLeveler();
    }


    @Override
    public void activate(Player player) {
        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
        if (playerSkill != null) {
            Locale locale = Lang.getLanguage(player);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            int manaConsumed = MAbility.TREECAPITATOR.getManaCost(playerSkill.getManaAbilityLevel(MAbility.REPLENISH));
            AureliumSkills.manaManager.setMana(player.getUniqueId(), AureliumSkills.manaManager.getMana(player.getUniqueId()) - manaConsumed);
            // Level Sorcery
            sorceryLeveler.level(player, manaConsumed);
            player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(ManaAbilityMessage.REPLENISH_START, locale).replace("{mana}", String.valueOf(manaConsumed)));
        }
    }

    @Override
    public void update(Player player) {

    }

    @Override
    public void stop(Player player) {
        PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
        if (skill != null) {
            Locale locale = Lang.getLanguage(player);
            AureliumSkills.manaAbilityManager.setCooldown(player.getUniqueId(), MAbility.REPLENISH, (int) (MAbility.REPLENISH.getCooldown(skill.getManaAbilityLevel(MAbility.REPLENISH)) * 20));
            player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(ManaAbilityMessage.REPLENISH_END, locale));
        }
    }
}
