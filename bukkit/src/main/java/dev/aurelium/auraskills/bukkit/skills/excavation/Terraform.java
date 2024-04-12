package dev.aurelium.auraskills.bukkit.skills.excavation;

import dev.aurelium.auraskills.api.event.mana.TerraformBlockBreakEvent;
import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.source.type.BlockXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.hooks.TownyHook;
import dev.aurelium.auraskills.bukkit.mana.ReadiedManaAbility;
import dev.aurelium.auraskills.bukkit.source.BlockLeveler;
import dev.aurelium.auraskills.common.message.type.ManaAbilityMessage;
import dev.aurelium.auraskills.common.source.SourceTag;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.LinkedList;

public class Terraform extends ReadiedManaAbility {

    public Terraform(AuraSkills plugin) {
        super(plugin, ManaAbilities.TERRAFORM, ManaAbilityMessage.TERRAFORM_START, ManaAbilityMessage.TERRAFORM_END,
                new String[]{"SHOVEL"}, new Action[]{Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR});
    }

    @Override
    public void onActivate(Player player, User user) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    }

    @Override
    public void onStop(Player player, User user) {

    }

    @Override
    public String replaceDescPlaceholders(String input, User user) {
        return TextUtil.replace(input, "{radius}", String.valueOf(4));
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (isDisabled()) return;
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (failsChecks(player)) return;

        if (block.hasMetadata("block-ignore")) { // Compatibility fix
            return;
        }
        // AdvancedEnchantments compatibility fix
        if (block.hasMetadata("blockbreakevent-ignore")) {
            return;
        }
        if (!block.hasMetadata("AureliumSkills-Terraform") && event.getClass() == BlockBreakEvent.class) {
            applyTerraform(player, plugin.getUser(player), block);
        }
    }

    private void applyTerraform(Player player, User user, Block block) {
        // Check if block is applicable to ability
        var skillSource = plugin.getLevelManager().getLeveler(BlockLeveler.class).getSource(block, BlockXpSource.BlockTriggers.BREAK);
        if (skillSource == null) return;

        XpSource source = skillSource.source();

        if (!plugin.getSkillManager().hasTag(source, SourceTag.TERRAFORM_APPLICABLE)) return;
        // Apply if activated
        if (isActivated(user)) {
            terraformBreak(player, block);
            return;
        }
        // Checks if ability is ready
        if (isHoldingMaterial(player) && checkActivation(player)) {
            terraformBreak(player, block);
        }
    }

    private void terraformBreak(Player player, Block block) {
        Material material = block.getType();
        BlockFace[] faces = new BlockFace[] {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
        LinkedList<Block> toCheck = new LinkedList<>();
        toCheck.add(block);
        int count = 0;
        int maxCount = manaAbility.optionInt("max_blocks", 61);
        while ((block = toCheck.poll()) != null && count < maxCount) {
            if (block.getType() == material) {
                block.setMetadata("AureliumSkills-Terraform", new FixedMetadataValue(plugin, true));
                breakBlock(player, block);
                for (BlockFace face : faces) {
                    toCheck.add(block.getRelative(face));
                }
                count++;
            }
        }
    }

    private void breakBlock(Player player, Block block) {
        if (plugin.getHookManager().isRegistered(TownyHook.class) && !plugin.getHookManager().getHook(TownyHook.class).canBreak(player, block)) {
            block.removeMetadata("AureliumSkills-Terraform", plugin);
            return;
        }
        TerraformBlockBreakEvent event = new TerraformBlockBreakEvent(block, player);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            block.breakNaturally(player.getInventory().getItemInMainHand());
        }
        block.removeMetadata("AureliumSkills-Terraform", plugin);
    }

}
