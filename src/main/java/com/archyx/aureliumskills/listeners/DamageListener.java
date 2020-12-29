package com.archyx.aureliumskills.listeners;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.*;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.stats.PlayerStat;
import com.archyx.aureliumskills.stats.Strength;
import com.archyx.aureliumskills.stats.Toughness;
import com.archyx.aureliumskills.util.DamageType;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageListener implements Listener {

    private final Strength strength;
    private final Critical critical;
    private final AureliumSkills plugin;
    private final ExcavationAbilities excavationAbilities;
    private final FarmingAbilities farmingAbilities;
    private final MiningAbilities miningAbilities;
    private final ForagingAbilities foragingAbilities;
    private final ArcheryAbilities archeryAbilities;
    private final FightingAbilities fightingAbilities;
    private final DefenseAbilities defenseAbilities;

    public DamageListener(AureliumSkills plugin) {
        strength = new Strength();
        this.plugin = plugin;
        this.critical = new Critical(plugin);
        this.excavationAbilities = new ExcavationAbilities(plugin);
        this.farmingAbilities = new FarmingAbilities(plugin);
        this.miningAbilities = new MiningAbilities(plugin);
        this.foragingAbilities = new ForagingAbilities(plugin);
        this.archeryAbilities = new ArcheryAbilities(plugin);
        this.fightingAbilities = new FightingAbilities(plugin);
        this.defenseAbilities = new DefenseAbilities(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent event) {

        //Check if not cancelled
        if (event.isCancelled()) {
            return;
        }

        //Gets the player who dealt damage
        Player player = getDamager(event.getDamager());
        if (player != null) {

            //Check disabled world
            if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
                return;
            }
            //Gets player skill
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
            if (playerSkill == null || playerStat == null) {
                return;
            }

            DamageType damageType = getDamageType(event, player);

            //Applies strength
            strength.strength(event, playerStat, damageType);

            //Applies master abilities
            switch (damageType) {
                case SWORD:
                    fightingAbilities.swordMaster(event, player, playerSkill);
                    break;
                case BOW:
                    archeryAbilities.bowMaster(event, player, playerSkill);
                    break;
                case AXE:
                    foragingAbilities.axeMaster(event, player, playerSkill);
                    break;
                case PICKAXE:
                    miningAbilities.pickMaster(event, player, playerSkill);
                    break;
                case HOE:
                    farmingAbilities.scytheMaster(event, player, playerSkill);
                    break;
                case SHOVEL:
                    excavationAbilities.spadeMaster(event, player, playerSkill);
                    break;
            }

            //First strike
            if (damageType == DamageType.SWORD) {
                fightingAbilities.firstStrike(event, playerSkill, player);
            }

            //Critical
            if (OptionL.criticalEnabled(damageType)) {
                critical.applyCrit(event, player, playerSkill);
            }

            // Charged shot
            if (damageType == DamageType.BOW) {
                archeryAbilities.applyChargedShot(event);
            }
        }
        //Handles being damaged
        if (event.getEntity() instanceof Player) {
            onDamaged(event, (Player) event.getEntity());
        }
    }

    private void onDamaged(EntityDamageByEntityEvent event, Player player) {
        //Check disabled world
        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
            return;
        }
        //Gets player skill
        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
        PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
        if (playerSkill == null || playerStat == null) {
            return;
        }
        //Handles toughness
        Toughness.onDamage(event, playerStat);

        //Handles mob master
        defenseAbilities.mobMaster(event, playerSkill);

        //Handles shielding
        defenseAbilities.shielding(event, playerSkill, player);
    }

    @SuppressWarnings("deprecation")
    private DamageType getDamageType(EntityDamageByEntityEvent event, Player player) {
        if (event.getDamager() instanceof Arrow || event.getDamager() instanceof SpectralArrow || event.getDamager() instanceof TippedArrow) {
            return DamageType.BOW;
        }
        Material material = player.getInventory().getItemInMainHand().getType();
        if (material.name().contains("SWORD")) {
            return DamageType.SWORD;
        }
        else if (material.name().contains("_AXE")) {
            return DamageType.AXE;
        }
        else if (material.name().contains("PICKAXE")) {
            return DamageType.PICKAXE;
        }
        else if (material.name().contains("SHOVEL") || material.name().contains("SPADE")) {
            return DamageType.SHOVEL;
        }
        else if (material.name().contains("HOE")) {
            return DamageType.HOE;
        }
        else if (material.equals(Material.AIR)) {
            return DamageType.HAND;
        }
        else if (XMaterial.isNewVersion()) {
            if (event.getDamager() instanceof Trident) {
                return DamageType.BOW;
            }
        }
        return DamageType.OTHER;
    }

    private Player getDamager(Entity entity) {
        Player player = null;
        if (entity instanceof Player) {
            player = (Player) entity;
        }
        else if (entity instanceof Arrow) {
            Arrow arrow = (Arrow) entity;
            if (arrow.getShooter() instanceof Player) {
                player = (Player) arrow.getShooter();
            }
        }
        return player;
    }

}
