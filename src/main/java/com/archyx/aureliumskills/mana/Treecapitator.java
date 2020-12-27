package com.archyx.aureliumskills.mana;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.levelers.SorceryLeveler;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Locale;

public class Treecapitator implements ManaAbility {

    private final AureliumSkills plugin;
    private final SorceryLeveler sorceryLeveler;

    public Treecapitator(AureliumSkills plugin) {
        this.plugin = plugin;
        this.sorceryLeveler = plugin.getSorceryLeveler();
    }

    @Override
    public void activate(Player player) {
        if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
            Locale locale = Lang.getLanguage(player);
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                //Consume mana
                double manaConsumed = plugin.getManaAbilityManager().getManaCost(MAbility.TREECAPITATOR, playerSkill);
                plugin.getManaManager().setMana(player.getUniqueId(), plugin.getManaManager().getMana(player.getUniqueId()) - manaConsumed);
                // Level Sorcery
                sorceryLeveler.level(player, manaConsumed);
                player.sendMessage(AureliumSkills.getPrefix(locale) + ChatColor.GOLD + Lang.getMessage(ManaAbilityMessage.TREECAPITATOR_START, locale).replace("{mana}", String.valueOf(manaConsumed)));
            }
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
            plugin.getManaAbilityManager().setPlayerCooldown(player.getUniqueId(), MAbility.TREECAPITATOR, (int) (plugin.getManaAbilityManager().getCooldown(MAbility.TREECAPITATOR, skill.getManaAbilityLevel(MAbility.TREECAPITATOR)) * 20));
            player.sendMessage(AureliumSkills.getPrefix(locale) + ChatColor.GOLD + Lang.getMessage(ManaAbilityMessage.TREECAPITATOR_END, locale));
        }
    }
}
