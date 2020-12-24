package com.archyx.aureliumskills.util;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SourceManager;
import com.archyx.aureliumskills.skills.levelers.Leveler;
import com.archyx.aureliumskills.skills.levelers.SkillLeveler;
import com.cryptomorin.xseries.XMaterial;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Map;

public class MythicMobsSupport extends SkillLeveler implements Listener {

    private final SourceManager sourceManager;

    public MythicMobsSupport(AureliumSkills plugin) {
        super(plugin, Skill.FIGHTING);
        this.sourceManager = plugin.getSourceManager();
    }

    @EventHandler
    public void onDeath(MythicMobDeathEvent event) {
        if (event.getKiller() instanceof Player) {
            Player player = (Player) event.getKiller();
            Entity entity = event.getEntity();
            if (player == null) {
                return;
            }
            if (blockXpGainLocation(entity.getLocation())) return;
            //Check creative mode disable
            if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
                if (player.getGameMode().equals(GameMode.CREATIVE)) {
                    return;
                }
            }
            if (entity.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) entity.getLastDamageCause();
                boolean archery = false;
                if (ee.getDamager() instanceof Arrow || ee.getDamager() instanceof TippedArrow || ee.getDamager() instanceof SpectralArrow) {
                    archery = true;
                }
                if (XMaterial.isNewVersion()) {
                    if (ee.getDamager() instanceof Trident) {
                        archery = true;
                    }
                }
                if (VersionUtils.isAboveVersion(14)) {
                    if (ee.getDamager() instanceof AbstractArrow) {
                        archery = true;
                    }
                }
                // Archery
                if (archery) {
                    if (!player.hasPermission("aureliumskills.archery")) {
                        return;
                    }
                    Map<String, Double> customMobs = sourceManager.getCustomMobs(Skill.ARCHERY);
                    if (customMobs != null) {
                        for (Map.Entry<String, Double> entry : customMobs.entrySet()) {
                            if (event.getMobType().getInternalName().equals(entry.getKey())) {
                                Leveler.addXp(player, Skill.ARCHERY, getXp(player, entry.getValue(), Ability.ARCHER));
                                break;
                            }
                        }
                    }
                }
                // Fighting
                else {
                    if (!player.hasPermission("aureliumskills.fighting")) {
                        return;
                    }
                    Map<String, Double> customMobs = sourceManager.getCustomMobs(Skill.FIGHTING);
                    if (customMobs != null) {
                        for (Map.Entry<String, Double> entry : customMobs.entrySet()) {
                            if (event.getMobType().getInternalName().equals(entry.getKey())) {
                                Leveler.addXp(player, Skill.FIGHTING, getXp(player, entry.getValue(), Ability.FIGHTER));
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

}
