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
 * Listener that rewards players with Skill Tokens AND SkillCoins when they level up skills
 * 
 * Rewards are shown in the level lore and in chat messages
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
        
        // Calculate SkillCoins reward based on level
        int coinsReward = calculateCoinsReward(level);
        
        // Award tokens
        plugin.getSkillCoinsEconomy().addBalance(player.getUniqueId(), CurrencyType.TOKENS, tokenReward);
        
        // Award coins only if coinsReward > 0 (scaled payout on multiples of 5)
        if (coinsReward > 0) {
            plugin.getSkillCoinsEconomy().addBalance(player.getUniqueId(), CurrencyType.COINS, coinsReward);
        }
        
        // Send notification message
        player.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "  ✦ " + ChatColor.YELLOW + ChatColor.BOLD + "LEVEL UP REWARDS" + ChatColor.GREEN + " ✦");
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "  " + event.getSkill().getDisplayName(event.getUser().getLocale()) + 
                " Level " + ChatColor.WHITE + level);
        player.sendMessage("");
        if (coinsReward > 0) {
            player.sendMessage(ChatColor.GOLD + "  ⬥ " + ChatColor.YELLOW + coinsReward + " ⛃");
        }
        player.sendMessage(ChatColor.AQUA + "  ⬥ " + ChatColor.WHITE + tokenReward + " 🎟");
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "  Use 🎟 to purchase skill levels at " + ChatColor.YELLOW + "/shop");
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
    
    /**
     * Calculate SkillCoins reward based on level
     * Scales with level for progression but only pays out on multiples of 5
     */
    private int calculateCoinsReward(int level) {
        // Only give coins on multiples of 5 (scaled option)
        if (level % 5 != 0) {
            return 0;
        }
        // Base reward of 10 coins, scaling up with level
        int baseReward = 10;
        
        if (level <= 10) {
            return baseReward + (level * 2); // 12-30 coins
        } else if (level <= 25) {
            return baseReward + 20 + ((level - 10) * 3); // 33-65 coins
        } else if (level <= 50) {
            return baseReward + 65 + ((level - 25) * 4); // 79-165 coins
        } else if (level <= 75) {
            return baseReward + 165 + ((level - 50) * 5); // 180-290 coins
        } else if (level <= 90) {
            return baseReward + 290 + ((level - 75) * 7); // 307-395 coins
        } else {
            return baseReward + 395 + ((level - 90) * 10); // 405+ coins
        }
    }
    
    /**
     * Get the SkillCoins reward for a specific level
     * Can be called from other classes to display in lore
     */
    public static int getCoinsRewardForLevel(int level) {
        int baseReward = 10;
        
        if (level <= 10) {
            return baseReward + (level * 2);
        } else if (level <= 25) {
            return baseReward + 20 + ((level - 10) * 3);
        } else if (level <= 50) {
            return baseReward + 65 + ((level - 25) * 4);
        } else if (level <= 75) {
            return baseReward + 165 + ((level - 50) * 5);
        } else if (level <= 90) {
            return baseReward + 290 + ((level - 75) * 7);
        } else {
            return baseReward + 395 + ((level - 90) * 10);
        }
    }
    
    /**
     * Get the Token reward for a specific level
     * Can be called from other classes to display in lore
     */
    public static int getTokenRewardForLevel(int level) {
        if (level <= 10) {
            return 1;
        } else if (level <= 25) {
            return 2;
        } else if (level <= 50) {
            return 3;
        } else if (level <= 75) {
            return 5;
        } else if (level <= 90) {
            return 7;
        } else {
            return 10;
        }
    }
}
