package com.archyx.aureliumskills.skills.abilities.mana_abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.levelers.SorceryLeveler;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Locale;

public class SpeedMine implements ManaAbility {

    private final SorceryLeveler sorceryLeveler;

    public SpeedMine(AureliumSkills plugin) {
        this.sorceryLeveler = plugin.getSorceryLeveler();
    }

    @Override
    public void activate(Player player) {
        if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            Locale locale = Lang.getLanguage(player);
            //Apply haste
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, (int) (MAbility.SPEED_MINE.getValue(playerSkill.getManaAbilityLevel(MAbility.SPEED_MINE)) * 20), 9, false, false), true);
            //Play sound
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            //Consume mana
            int manaConsumed = MAbility.TREECAPITATOR.getManaCost(playerSkill.getManaAbilityLevel(MAbility.SPEED_MINE));
            AureliumSkills.manaManager.setMana(player.getUniqueId(), AureliumSkills.manaManager.getMana(player.getUniqueId()) - manaConsumed);
            // Level Sorcery
            sorceryLeveler.level(player, manaConsumed);
            player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(ManaAbilityMessage.SPEED_MINE_START, locale).replace("{mana}", String.valueOf(manaConsumed)));
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
            AureliumSkills.manaAbilityManager.setCooldown(player.getUniqueId(), MAbility.SPEED_MINE, MAbility.SPEED_MINE.getCooldown(skill.getManaAbilityLevel(MAbility.SPEED_MINE)));
            player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(ManaAbilityMessage.SPEED_MINE_END, locale));
        }
    }
}
