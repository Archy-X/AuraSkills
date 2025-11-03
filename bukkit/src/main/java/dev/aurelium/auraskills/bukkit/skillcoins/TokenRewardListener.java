package dev.aurelium.auraskills.bukkit.skillcoins;

import dev.aurelium.auraskills.api.event.skill.SkillLevelUpEvent;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.skillcoins.CurrencyType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Listener that rewards players with Skill Tokens when they level up skills
 */
public class TokenRewardListener implements Listener {

    private final AuraSkills plugin;

    public TokenRewardListener(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSkillLevelUp(SkillLevelUpEvent event) {
        Player player = event.getPlayer();
        int level = event.getLevel();
        
        if (plugin.getSkillCoinsEconomy() == null) {
            return;
        }

        // Calculate token reward based on level (scaled rewards)
        int tokenReward = calculateTokenReward(level);
        
        // Award tokens
        plugin.getSkillCoinsEconomy().addBalance(player.getUniqueId(), CurrencyType.TOKENS, tokenReward);
        
        // Send notification message
        player.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "  ✦ " + ChatColor.YELLOW + ChatColor.BOLD + "SKILL TOKEN REWARD" + ChatColor.GREEN + " ✦");
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "  You earned " + ChatColor.AQUA + ChatColor.BOLD + tokenReward + " Skill Token" + (tokenReward > 1 ? "s" : ""));
        player.sendMessage(ChatColor.GRAY + "  for reaching " + ChatColor.WHITE + event.getSkill().getDisplayName(event.getUser().getLocale()) + " Level " + level + ChatColor.GRAY + "!");
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "  Use tokens to purchase skill levels at " + ChatColor.YELLOW + "/shop");
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        // Play sound
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
    }

    /**
     * Calculate token reward based on level
     * Higher levels = more tokens
     */
    private int calculateTokenReward(int level) {
        if (level <= 10) {
            return 1; // Levels 1-10: 1 token
        } else if (level <= 25) {
            return 2; // Levels 11-25: 2 tokens
        } else if (level <= 50) {
            return 3; // Levels 26-50: 3 tokens
        } else if (level <= 75) {
            return 5; // Levels 51-75: 5 tokens
        } else if (level <= 90) {
            return 7; // Levels 76-90: 7 tokens
        } else {
            return 10; // Levels 91+: 10 tokens
        }
    }
}
