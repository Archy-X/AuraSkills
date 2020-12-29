package com.archyx.aureliumskills.modifier;

import com.archyx.aureliumskills.requirement.RequirementManager;
import com.archyx.aureliumskills.requirement.Requirements;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.stats.PlayerStat;
import com.archyx.aureliumskills.util.ArmorEquipEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class ArmorModifierListener implements Listener {

    private final Modifiers modifiers;
    private final Requirements requirements;

    public ArmorModifierListener(RequirementManager requirementManager) {
        this.modifiers = new Modifiers();
        this.requirements = new Requirements(requirementManager);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (SkillLoader.playerStats.containsKey(player.getUniqueId())) {
            PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
            for (ItemStack armor : player.getInventory().getArmorContents()) {
                if (armor != null) {
                    if (!armor.getType().equals(Material.AIR)) {
                        if (requirements.meetsRequirements(ModifierType.ARMOR, armor, player)) {
                            for (StatModifier modifier : modifiers.getModifiers(ModifierType.ARMOR, armor)) {
                                playerStat.addModifier(modifier, false);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
        if (playerStat != null) {
            // Equip
            if (event.getNewArmorPiece() != null && event.getNewArmorPiece().getType() != Material.AIR) {
                ItemStack item = event.getNewArmorPiece();
                if (requirements.meetsRequirements(ModifierType.ARMOR, item, player)) {
                    for (StatModifier modifier : modifiers.getModifiers(ModifierType.ARMOR, item)) {
                        playerStat.addModifier(modifier);
                    }
                }
            }
            // Un-equip
            if (event.getOldArmorPiece() != null && event.getOldArmorPiece().getType() != Material.AIR) {
                ItemStack item = event.getOldArmorPiece();
                for (StatModifier modifier : modifiers.getModifiers(ModifierType.ARMOR, item)) {
                    playerStat.removeModifier(modifier.getName());
                }
            }
        }
    }

}
