package com.archyx.aureliumskills.mana;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.levelers.SorceryLeveler;
import com.archyx.aureliumskills.util.LoreUtil;
import com.archyx.aureliumskills.util.NumberUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Locale;

public class Replenish implements ManaAbility {

    private final AureliumSkills plugin;
    private final SorceryLeveler sorceryLeveler;

    public Replenish(AureliumSkills plugin) {
        this.plugin = plugin;
        this.sorceryLeveler = plugin.getSorceryLeveler();
    }

    @Override
    public AureliumSkills getPlugin() {
        return plugin;
    }

    @Override
    public MAbility getManaAbility() {
        return MAbility.REPLENISH;
    }

    @Override
    public void activate(Player player) {
        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
        if (playerSkill != null) {
            Locale locale = Lang.getLanguage(player);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            double manaConsumed = plugin.getManaAbilityManager().getManaCost(MAbility.TREECAPITATOR, playerSkill);
            plugin.getManaManager().setMana(player.getUniqueId(), plugin.getManaManager().getMana(player.getUniqueId()) - manaConsumed);
            // Level Sorcery
            sorceryLeveler.level(player, manaConsumed);
            plugin.getAbilityManager().sendMessage(player, LoreUtil.replace(Lang.getMessage(ManaAbilityMessage.REPLENISH_START, locale)
                    ,"{mana}", NumberUtil.format0(manaConsumed)));
        }
    }

    @Override
    public void stop(Player player) {
        plugin.getAbilityManager().sendMessage(player, Lang.getMessage(ManaAbilityMessage.REPLENISH_END, Lang.getLanguage(player)));
    }
}
