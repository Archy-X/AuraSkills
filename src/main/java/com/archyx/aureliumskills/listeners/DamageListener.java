package com.archyx.aureliumskills.listeners;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.abilities.*;
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
import org.bukkit.plugin.Plugin;

public class DamageListener implements Listener {

    private final Strength strength;
    private final Critical critical;
    private final Plugin plugin;

    public DamageListener(Plugin plugin) {
        strength = new Strength();
        this.plugin = plugin;
        this.critical = new Critical(plugin);
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
            if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
                return;
            }
            //Checks for player skill
            if (!SkillLoader.playerSkills.containsKey(player.getUniqueId()) || !SkillLoader.playerStats.containsKey(player.getUniqueId())) {
                return;
            }
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
            DamageType damageType = getDamageType(event, player);

            //Applies strength
            strength.strength(event, playerStat, damageType);

            //Applies master abilities
            switch (damageType) {
                case SWORD:
                    FightingAbilities.swordMaster(event, player, playerSkill);
                    break;
                case BOW:
                    ArcheryAbilities.bowMaster(event, player, playerSkill);
                    break;
                case AXE:
                    ForagingAbilities.axeMaster(event, player, playerSkill);
                    break;
                case PICKAXE:
                    MiningAbilities.pickMaster(event, player, playerSkill);
                    break;
                case HOE:
                    FarmingAbilities.scytheMaster(event, player, playerSkill);
                    break;
                case SHOVEL:
                    ExcavationAbilities.spadeMaster(event, player, playerSkill);
                    break;
            }

            //First strike
            if (damageType == DamageType.SWORD) {
                FightingAbilities.firstStrike(event, playerSkill, player, plugin);
            }

            //Critical
            if (OptionL.criticalEnabled(damageType)) {
                critical.applyCrit(event, player, playerSkill);
            }

        }
        //Handles being damaged
        if (event.getEntity() instanceof Player) {
            onDamaged(event, (Player) event.getEntity());
        }
    }

    private void onDamaged(EntityDamageByEntityEvent event, Player player) {
        //Check disabled world
        if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
            return;
        }
        //Checks for player skill
        if (!SkillLoader.playerSkills.containsKey(player.getUniqueId()) || !SkillLoader.playerStats.containsKey(player.getUniqueId())) {
            return;
        }
        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
        PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());

        //Handles toughness
        Toughness.onDamage(event, playerStat);

        //Handles mob master
        DefenseAbilities.mobMaster(event, playerSkill);

        //Handles shielding
        DefenseAbilities.shielding(event, playerSkill, player);
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
