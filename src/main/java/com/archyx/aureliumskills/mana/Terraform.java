package com.archyx.aureliumskills.mana;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.skills.levelers.SorceryLeveler;
import com.archyx.aureliumskills.util.math.NumberUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Locale;

public class Terraform implements ManaAbility {

    private final AureliumSkills plugin;
    private final SorceryLeveler sorceryLeveler;

    public Terraform(AureliumSkills plugin) {
        this.plugin = plugin;
        this.sorceryLeveler = plugin.getSorceryLeveler();
    }

    @Override
    public AureliumSkills getPlugin() {
        return plugin;
    }

    @Override
    public MAbility getManaAbility() {
        return MAbility.TERRAFORM;
    }

    @Override
    public void activate(Player player) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null) {
            Locale locale = playerData.getLocale();
            //Play sound
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            //Consume mana
            double manaConsumed = plugin.getManaAbilityManager().getManaCost(MAbility.TERRAFORM, playerData);
            playerData.setMana(playerData.getMana() - manaConsumed);
            // Level Sorcery
            sorceryLeveler.level(player, manaConsumed);
            plugin.getAbilityManager().sendMessage(player, Lang.getMessage(ManaAbilityMessage.TERRAFORM_START, locale).replace("{mana}", NumberUtil.format0(manaConsumed)));
        }
    }

    @Override
    public void stop(Player player) {
        plugin.getAbilityManager().sendMessage(player, Lang.getMessage(ManaAbilityMessage.TERRAFORM_END, plugin.getLang().getLocale(player)));
    }
}
