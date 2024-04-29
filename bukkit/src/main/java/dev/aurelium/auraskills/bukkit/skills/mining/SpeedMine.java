package dev.aurelium.auraskills.bukkit.skills.mining;

import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.source.type.BlockXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.mana.ReadiedManaAbility;
import dev.aurelium.auraskills.bukkit.source.BlockLeveler;
import dev.aurelium.auraskills.bukkit.util.CompatUtil;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.message.type.ManaAbilityMessage;
import dev.aurelium.auraskills.common.source.SourceTag;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;

public class SpeedMine extends ReadiedManaAbility {

    public SpeedMine(AuraSkills plugin) {
        super(plugin, ManaAbilities.SPEED_MINE, ManaAbilityMessage.SPEED_MINE_START, ManaAbilityMessage.SPEED_MINE_END,
                new String[] {"PICKAXE"}, new Action[] {Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR});
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onActivate(Player player, User user) {
        int amplifier = manaAbility.optionInt("haste_level", 10) - 1;
        player.addPotionEffect(new PotionEffect(CompatUtil.haste(), (int) (getValue(user) * 20),
                amplifier, false, false), true);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    }

    @Override
    public void onStop(Player player, User playerData) {

    }

    @Override
    public String replaceDescPlaceholders(String input, User user) {
        return TextUtil.replace(input, "{haste_level}", String.valueOf(manaAbility.optionInt("haste_level", 10)));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void activationListener(BlockBreakEvent event) {
        if (isDisabled()) return;
        if (event.isCancelled()) return;

        Block block = event.getBlock();
        if (plugin.configBoolean(Option.CHECK_BLOCK_REPLACE_ENABLED) && plugin.getRegionManager().isPlacedBlock(block)) {
            return;
        }
        Player player = event.getPlayer();

        if (failsChecks(player)) return;

        var skillSource = plugin.getLevelManager().getLeveler(BlockLeveler.class).getSource(block, BlockXpSource.BlockTriggers.BREAK);
        if (skillSource == null) return;

        BlockXpSource source = skillSource.source();
        if (source == null) return;

        if (plugin.getSkillManager().hasTag(source, SourceTag.SPEED_MINE_APPLICABLE) && isHoldingMaterial(player)) {
            checkActivation(player);
        }
    }
}
