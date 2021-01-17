package com.archyx.aureliumskills.mana;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.util.LoreUtil;
import com.archyx.aureliumskills.util.NumberUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Locale;

public class ChargedShot implements ManaAbility {

    private final AureliumSkills plugin;
    private final Entity projectile;
    private final float force;

    public ChargedShot(AureliumSkills plugin, Entity projectile, float force) {
        this.plugin = plugin;
        this.projectile = projectile;
        this.force = force;
    }

    @Override
    public AureliumSkills getPlugin() {
        return plugin;
    }

    @Override
    public MAbility getManaAbility() {
        return MAbility.CHARGED_SHOT;
    }

    @Override
    public void activate(Player player) {
        Locale locale = Lang.getLanguage(player);
        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
        if (playerSkill == null) return;
        double manaConsumed = plugin.getManaAbilityManager().getManaCost(MAbility.CHARGED_SHOT, playerSkill) * force;
        if (manaConsumed > plugin.getManaManager().getMana(player.getUniqueId())) {
            manaConsumed = plugin.getManaManager().getMana(player.getUniqueId());
        }
        plugin.getManaManager().setMana(player.getUniqueId(), plugin.getManaManager().getMana(player.getUniqueId()) - manaConsumed);
        double damagePercent = manaConsumed * plugin.getManaAbilityManager().getValue(MAbility.CHARGED_SHOT, playerSkill);
        if (plugin.getManaAbilityManager().getOptionAsBooleanElseTrue(MAbility.CHARGED_SHOT, "enable_sound")) {
            player.playSound(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 0.5f, 1);
        }
        // Level Sorcery
        plugin.getSorceryLeveler().level(player, manaConsumed);
        projectile.setMetadata("ChargedShotMultiplier", new FixedMetadataValue(plugin, 1 + damagePercent / 100));
        if (plugin.getManaAbilityManager().getOptionAsBooleanElseTrue(MAbility.CHARGED_SHOT, "enable_message")) {
            plugin.getAbilityManager().sendMessage(player, LoreUtil.replace(Lang.getMessage(ManaAbilityMessage.CHARGED_SHOT_SHOOT, locale)
                    , "{mana}", NumberUtil.format0(manaConsumed)
                    , "{percent}", NumberUtil.format0(damagePercent)));
        }
    }

    @Override
    public void stop(Player player) {
        plugin.getManaAbilityManager().setPlayerCooldown(player, MAbility.CHARGED_SHOT);
    }
}
