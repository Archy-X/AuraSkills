package dev.aurelium.auraskills.bukkit.skills.farming;

import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.mana.ReadiedManaAbility;
import dev.aurelium.auraskills.bukkit.util.CompatUtil;
import dev.aurelium.auraskills.common.message.type.ManaAbilityMessage;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.concurrent.TimeUnit;

public class Replenish extends ReadiedManaAbility {

    public Replenish(AuraSkills plugin) {
        super(plugin, ManaAbilities.REPLENISH, ManaAbilityMessage.REPLENISH_START, ManaAbilityMessage.REPLENISH_END, new String[] {"HOE"},
                new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK});
    }

    @Override
    public void onActivate(Player player, User user) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    }

    @Override
    public void onStop(Player player, User user) {

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void activationListener(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        if (!canBeReplenished(event.getBlock().getType())) return;

        Player player = event.getPlayer();
        User user = plugin.getUser(player);

        if (isActivated(user)) {
            applyReplenish(event);
        } else if (isHoldingMaterial(player) && checkActivation(player)) {
            applyReplenish(event);
        }
    }

    public void applyReplenish(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (isFullyGrown(block)) {
            replantCrop(block);
        } else if (manaAbility.optionBoolean("prevent_unripe_break", true)) {
            // Cancel the block break if the option for preventing unripe crop breaks is true
            event.setCancelled(true);
        }
    }

    private boolean canBeReplenished(Material mat) {
        return mat == Material.WHEAT || mat == Material.CARROTS || mat == Material.POTATOES || mat == Material.BEETROOTS || mat == Material.NETHER_WART;
    }

    private boolean isFullyGrown(Block block) {
        if (block.getBlockData() instanceof Ageable crop) {
            return crop.getMaximumAge() == crop.getAge();
        }
        return false;
    }

    private void replantCrop(Block block) {
        Material material = block.getType();
        plugin.getScheduler().scheduleSync(() -> {
            if (material != Material.NETHER_WART) {
                if (block.getRelative(BlockFace.DOWN).getType() == Material.FARMLAND) {
                    block.setType(material);
                    attemptSpawnParticle(block);
                }
            } else {
                if (block.getRelative(BlockFace.DOWN).getType() == Material.SOUL_SAND) {
                    block.setType(material);
                    attemptSpawnParticle(block);
                }
            }
        }, manaAbility.optionInt("replant_delay", 4) * 50L, TimeUnit.MILLISECONDS);
    }

    private void attemptSpawnParticle(Block block) {
        if (manaAbility.optionBoolean("show_particles", true)) {
            block.getWorld().spawnParticle(CompatUtil.villagerParticle(), block.getLocation().add(0.5, 0.2, 0.5), 8, 0.25, 0, 0.25);
        }
    }

    @Override
    protected boolean isExcludedBlock(Block block) {
        return super.isExcludedBlock(block) ||
                block.getType() == Material.DIRT ||
                block.getType() == Material.GRASS_BLOCK ||
                block.getType() == Material.COARSE_DIRT ||
                block.getType() == Material.DIRT_PATH ||
                block.getType() == Material.FARMLAND;
    }

}
