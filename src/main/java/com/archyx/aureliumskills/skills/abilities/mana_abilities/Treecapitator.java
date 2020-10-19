package com.archyx.aureliumskills.skills.abilities.mana_abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.SkillLoader;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Locale;

public class Treecapitator implements ManaAbility {

    @Override
    public void activate(Player player) {
        if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
            Locale locale = Lang.getLanguage(player);
            PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            //Consume mana
            int manaConsumed = MAbility.TREECAPITATOR.getManaCost(skill.getManaAbilityLevel(MAbility.TREECAPITATOR));
            AureliumSkills.manaManager.setMana(player.getUniqueId(), AureliumSkills.manaManager.getMana(player.getUniqueId()) - manaConsumed);
            player.sendMessage(AureliumSkills.tag + ChatColor.GOLD + Lang.getMessage(ManaAbilityMessage.TREECAPITATOR_START, locale).replace("{mana}", String.valueOf(manaConsumed)));
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
            AureliumSkills.manaAbilityManager.setCooldown(player.getUniqueId(), MAbility.TREECAPITATOR, MAbility.TREECAPITATOR.getCooldown(skill.getManaAbilityLevel(MAbility.TREECAPITATOR)));
            player.sendMessage(AureliumSkills.tag + ChatColor.GOLD + Lang.getMessage(ManaAbilityMessage.TREECAPITATOR_END, locale));
        }
    }
}
