package com.archyx.aureliumskills.skills.abilities.mana_abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.SkillLoader;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Locale;

public class Replenish implements ManaAbility {

    @Override
    public void activate(Player player) {
        if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
            Locale locale = Lang.getLanguage(player);
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            int manaConsumed = MAbility.TREECAPITATOR.getManaCost(playerSkill.getManaAbilityLevel(MAbility.REPLENISH));
            AureliumSkills.manaManager.setMana(player.getUniqueId(), AureliumSkills.manaManager.getMana(player.getUniqueId()) - manaConsumed);
            player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(ManaAbilityMessage.REPLENISH_START, locale).replace("{mana}", String.valueOf(manaConsumed)));
        }
    }

    @Override
    public void update(Player player) {

    }

    @Override
    public void stop(Player player) {
        if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
            Locale locale = Lang.getLanguage(player);
            PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
            AureliumSkills.manaAbilityManager.setCooldown(player.getUniqueId(), MAbility.REPLENISH, MAbility.REPLENISH.getCooldown(skill.getManaAbilityLevel(MAbility.REPLENISH)));
            player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(ManaAbilityMessage.REPLENISH_END, locale));
        }
    }
}
