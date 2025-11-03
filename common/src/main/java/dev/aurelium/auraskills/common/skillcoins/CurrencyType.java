package dev.aurelium.auraskills.common.skillcoins;

/**
 * Represents the different currency types in the SkillCoins system
 */
public enum CurrencyType {
    /**
     * Skill Coins - earned through gameplay and used to purchase items
     */
    COINS("SkillCoins"),
    
    /**
     * Skill Tokens - premium currency (optional future use)
     */
    TOKENS("SkillTokens");
    
    private final String displayName;
    
    CurrencyType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
