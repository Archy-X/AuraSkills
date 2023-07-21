package dev.aurelium.auraskills.bukkit.source;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.type.JumpingXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.source.SourceType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class JumpingLeveler extends SourceLeveler {

    private final Set<UUID> prevPlayersOnGround = new HashSet<>();

    public JumpingLeveler(AuraSkills plugin) {
        super(plugin, SourceType.JUMPING);
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onJump(PlayerMoveEvent event) {
        if (disabled()) return;
        Player player = event.getPlayer();

        handleJump(player, event);

        if (player.isOnGround()) {
            prevPlayersOnGround.add(player.getUniqueId());
        } else {
            prevPlayersOnGround.remove(player.getUniqueId());
        }
    }

    @SuppressWarnings("deprecation")
    private void handleJump(Player player, PlayerMoveEvent event) {
        if (player.getVelocity().getY() <= 0) {
            return;
        }

        double jumpVelocity = 0.42F;
        if (player.hasPotionEffect(PotionEffectType.JUMP)) {
            PotionEffect effect = player.getPotionEffect(PotionEffectType.JUMP);
            if (effect != null) {
                jumpVelocity += ((float) (effect.getAmplifier() + 1) * 0.1F);
            }
        }
        if (player.getLocation().getBlock().getType() == Material.LADDER || !prevPlayersOnGround.contains(player.getUniqueId())) {
            return;
        }
        if (player.isOnGround() || Double.compare(player.getVelocity().getY(), jumpVelocity) != 0) {
            return;
        }
        var sourcePair = plugin.getSkillManager().getSingleSourceOfType(JumpingXpSource.class);
        if (sourcePair == null) return;

        JumpingXpSource source = sourcePair.first();
        Skill skill = sourcePair.second();

        if (player.hasMetadata("skillsJumps")) {
            player.setMetadata("skillsJumps", new FixedMetadataValue(plugin, player.getMetadata("skillsJumps").get(0).asInt() + 1));
            if (player.getMetadata("skillsJumps").get(0).asInt() >= source.getInterval()) {

                if (failsChecks(event, player, player.getLocation(), skill)) return;

                plugin.getLevelManager().addXp(plugin.getUser(player), skill, source.getXp());

                player.removeMetadata("skillsJumps", plugin);
            }
        } else {
            player.setMetadata("skillsJumps", new FixedMetadataValue(plugin, 1));
        }
    }

}
