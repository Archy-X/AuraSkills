package dev.aurelium.auraskills.common.message.type;

import dev.aurelium.auraskills.common.message.MessageKey;

import java.util.Locale;

public enum MenuMessage implements MessageKey {

    // Common
    CLOSE(0),
    LEVEL(0),
    PROGRESS_TO_LEVEL(0),
    MAX_LEVEL(0),
    ABILITY_LEVELS(0),
    STATS_LEVELED(0),
    MANA_ABILITY(0),
    DURATION(0),
    MANA_COST(0),
    COOLDOWN(0),
    DAMAGE(0),
    MAX_MANA_COST(0),
    ATTACK_SPEED(0),
    BACK(0),
    BACK_CLICK(0),
    UNLOCKED(0),
    IN_PROGRESS(0),
    LOCKED(0),
    YOUR_LEVEL(0),
    NEXT_PAGE(0),
    NEXT_PAGE_CLICK(0),
    PREVIOUS_PAGE(0),
    PREVIOUS_PAGE_CLICK(0),
    // Skills Menu
    SKILLS_TITLE(1),
    YOUR_SKILLS(1),
    YOUR_SKILLS_DESC(1),
    YOUR_SKILLS_HOVER(1),
    YOUR_SKILLS_CLICK(1),
    SKILL_CLICK(1),
    STATS(1),
    STATS_DESC(1),
    STATS_CLICK(1),
    // Level Progression Menu
    LEVEL_PROGRESSION_TITLE(2),
    YOUR_RANKING(2),
    RANK_OUT_OF(2),
    RANK_PERCENT(2),
    REWARDS(2),
    ABILITY_UNLOCK(2),
    MANA_ABILITY_UNLOCK(2),
    PROGRESS(2),
    LEADERBOARD_CLICK(2),
    SOURCES(2),
    SOURCES_DESC(2),
    SOURCES_CLICK(2),
    ABILITIES(2),
    ABILITIES_DESC(2),
    MANA_ABILITIES_DESC(2),
    ABILITIES_CLICK(2),
    // Stats Menu
    STATS_TITLE(3),
    SKILLS(3),
    ADDED_DROP(3),
    ADDED_DROPS(3),
    CHANCE_DROP(3),
    CHANCE_DROPS(3),
    // Unclaimed items menu
    UNCLAIMED_ITEMS_TITLE(4),
    INVENTORY_FULL(4),
    CLICK_TO_CLAIM(4),
    // Leaderboard menu
    LEADERBOARD_TITLE(5),
    // Sources menu
    SOURCES_TITLE(6),
    SORTER(6),
    SORT_TYPE(6),
    SELECTED(6),
    DESCENDING(6),
    ASCENDING(6),
    ALPHABETICAL(6),
    REVERSE_ALPHABETICAL(6),
    SORT_CLICK(6),
    MULTIPLIED_XP(6),
    MULTIPLIED_DESC(6),
    // Abilities menu
    ABILITIES_TITLE(7),
    DESCRIPTION(7),
    UNLOCKED_AT(7),
    NEXT_UPGRADE_AT(7);

    private String path;
    
    MenuMessage(int section) {
        String key = this.name().toLowerCase(Locale.ROOT);
        if (section == 0) {
            this.path = "menus.common." + key;
        } else if (section == 1) {
            this.path = "menus.skills." + key;
        } else if (section == 2) {
            this.path = "menus.level_progression." + key;
        } else if (section == 3) {
            this.path = "menus.stats." + key;
        } else if (section == 4) {
            this.path = "menus.unclaimed_items." + key;
        } else if (section == 5) {
            this.path = "menus.leaderboard." + key;
        } else if (section == 6) {
            this.path = "menus.sources." + key;
        } else if (section == 7) {
            this.path = "menus.abilities." + key;
        }
    }

    @Override
    public String getPath() {
        return path;
    }
}
