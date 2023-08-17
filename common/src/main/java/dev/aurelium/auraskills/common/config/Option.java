package dev.aurelium.auraskills.common.config;


public enum Option {

    // Mysql Options
    MYSQL_ENABLED("mysql.enabled", OptionType.BOOLEAN),
    MYSQL_HOST("mysql.host", OptionType.STRING),
    MYSQL_PORT("mysql.port", OptionType.INT),
    MYSQL_DATABASE("mysql.database", OptionType.STRING),
    MYSQL_USERNAME("mysql.username", OptionType.STRING),
    MYSQL_PASSWORD("mysql.password", OptionType.STRING),
    MYSQL_LOAD_DELAY("mysql.load_delay", OptionType.INT),
    MYSQL_ALWAYS_LOAD_ON_JOIN("mysql.always_load_on_join", OptionType.BOOLEAN),
    MYSQL_SSL("mysql.ssl", OptionType.BOOLEAN),
    TRY_DETECT_CLIENT_LANGUAGE("try_detect_client_language", OptionType.BOOLEAN),
    // Hologram Hook
    // Holographic Display hook
    HOLOGRAPHIC_DISPLAYS_ENABLED("hooks.HolographicDisplays.enabled", OptionType.BOOLEAN),
    //Decent Holograms hook
    DECENT_HOLOGRAMS_ENABLED("hooks.DecentHolograms.enabled", OptionType.BOOLEAN),
    // Action bar options
    ACTION_BAR_ENABLED("action_bar.enabled", OptionType.BOOLEAN),
    ACTION_BAR_IDLE("action_bar.idle", OptionType.BOOLEAN),
    ACTION_BAR_ABILITY("action_bar.ability", OptionType.BOOLEAN),
    ACTION_BAR_XP("action_bar.xp", OptionType.BOOLEAN),
    ACTION_BAR_MAXED("action_bar.maxed", OptionType.BOOLEAN),
    ACTION_BAR_UPDATE_PERIOD("action_bar.update_period", OptionType.INT),
    ACTION_BAR_ROUND_XP("action_bar.round_xp", OptionType.BOOLEAN),
    ACTION_BAR_PLACEHOLDER_API("action_bar.placeholder_api", OptionType.BOOLEAN),
    ACTION_BAR_USE_SUFFIX("action_bar.use_suffix", OptionType.BOOLEAN),
    // Boss bar options
    BOSS_BAR_ENABLED("boss_bar.enabled", OptionType.BOOLEAN),
    BOSS_BAR_MODE("boss_bar.mode", OptionType.STRING),
    BOSS_BAR_STAY_TIME("boss_bar.stay_time", OptionType.INT),
    BOSS_BAR_UPDATE_EVERY("boss_bar.update_every", OptionType.INT),
    BOSS_BAR_ROUND_XP("boss_bar.round_xp", OptionType.BOOLEAN),
    BOSS_BAR_DISPLAY_MAXED("boss_bar.display_maxed", OptionType.BOOLEAN),
    BOSS_BAR_PLACEHOLDER_API("boss_bar.placeholder_api", OptionType.BOOLEAN),
    BOSS_BAR_USE_SUFFIX("boss_bar.use_suffix", OptionType.BOOLEAN),
    BOSS_BAR_FORMAT("boss_bar.format", OptionType.LIST),
    BASE_MANA("base_mana", OptionType.DOUBLE),
    ENABLE_ROMAN_NUMERALS("enable_roman_numerals", OptionType.BOOLEAN),
    // Damage hologram options
    DAMAGE_HOLOGRAMS("damage_holograms", OptionType.BOOLEAN),
    DAMAGE_HOLOGRAMS_SCALING("damage_holograms_scaling", OptionType.BOOLEAN),
    DAMAGE_HOLOGRAMS_DECIMAL_MAX("damage_holograms_decimal.decimal_max_amount", OptionType.INT),
    DAMAGE_HOLOGRAMS_DECIMAL_LESS_THAN("damage_holograms_decimal.display_when_less_than", OptionType.INT),
    DAMAGE_HOLOGRAMS_OFFSET_X("damage_holograms_offset.x", OptionType.DOUBLE),
    DAMAGE_HOLOGRAMS_OFFSET_Y("damage_holograms_offset.y", OptionType.DOUBLE),
    DAMAGE_HOLOGRAMS_OFFSET_Z("damage_holograms_offset.z", OptionType.DOUBLE),
    DAMAGE_HOLOGRAMS_OFFSET_RANDOM_ENABLED("damage_holograms_offset.random.enabled", OptionType.BOOLEAN),
    DAMAGE_HOLOGRAMS_OFFSET_RANDOM_X_MIN("damage_holograms_offset.random.x_min", OptionType.DOUBLE),
    DAMAGE_HOLOGRAMS_OFFSET_RANDOM_X_MAX("damage_holograms_offset.random.x_max", OptionType.DOUBLE),
    DAMAGE_HOLOGRAMS_OFFSET_RANDOM_Y_MIN("damage_holograms_offset.random.y_min", OptionType.DOUBLE),
    DAMAGE_HOLOGRAMS_OFFSET_RANDOM_Y_MAX("damage_holograms_offset.random.y_max", OptionType.DOUBLE),
    DAMAGE_HOLOGRAMS_OFFSET_RANDOM_Z_MIN("damage_holograms_offset.random.z_min", OptionType.DOUBLE),
    DAMAGE_HOLOGRAMS_OFFSET_RANDOM_Z_MAX("damage_holograms_offset.random.z_max", OptionType.DOUBLE),
    LEADERBOARDS_UPDATE_PERIOD("leaderboards.update_period", OptionType.INT),
    LEADERBOARDS_UPDATE_DELAY("leaderboards.update_delay", OptionType.INT),
    ENABLE_SKILL_COMMANDS("enable_skill_commands", OptionType.BOOLEAN),
    CHECK_BLOCK_REPLACE("check_block_replace", OptionType.BOOLEAN),
    DISABLE_IN_CREATIVE_MODE("disable_in_creative_mode", OptionType.BOOLEAN),
    RESET_SKILLS_ON_DEATH("reset_skills_on_death", OptionType.BOOLEAN),
    AUTO_SAVE_ENABLED("auto_save.enabled", OptionType.BOOLEAN),
    AUTO_SAVE_INTERVAL_TICKS("auto_save.interval_ticks", OptionType.INT),
    SKILL_MONEY_REWARDS_ENABLED("skill_money_rewards.enabled", OptionType.BOOLEAN),
    SKILL_MONEY_REWARDS_BASE("skill_money_rewards.base", OptionType.DOUBLE),
    SKILL_MONEY_REWARDS_MULTIPLIER("skill_money_rewards.multiplier", OptionType.DOUBLE),
    // Leveler options
    LEVELER_TITLE_ENABLED("leveler.title.enabled", OptionType.BOOLEAN),
    LEVELER_TITLE_FADE_IN("leveler.title.fade_in", OptionType.INT),
    LEVELER_TITLE_STAY("leveler.title.stay", OptionType.INT),
    LEVELER_TITLE_FADE_OUT("leveler.title.fade_out", OptionType.INT),
    LEVELER_SOUND_ENABLED("leveler.sound.enabled", OptionType.BOOLEAN),
    LEVELER_SOUND_TYPE("leveler.sound.type", OptionType.STRING),
    LEVELER_SOUND_CATEGORY("leveler.sound.category", OptionType.STRING),
    LEVELER_SOUND_VOLUME("leveler.sound.volume", OptionType.DOUBLE),
    LEVELER_SOUND_PITCH("leveler.sound.pitch", OptionType.DOUBLE),
    LEVELER_DOUBLE_CHECK_DELAY("leveler.double_check_delay", OptionType.INT),
    // Modifier options
    MODIFIER_ARMOR_EQUIP_BLOCKED_MATERIALS("modifier.armor.equip_blocked_materials", OptionType.LIST),
    MODIFIER_ARMOR_TIMER_ENABLED("modifier.armor.timer.enabled", OptionType.BOOLEAN),
    MODIFIER_ARMOR_TIMER_CHECK_PERIOD("modifier.armor.timer.check_period", OptionType.INT),
    MODIFIER_ITEM_CHECK_PERIOD("modifier.item.check_period", OptionType.INT),
    MODIFIER_ITEM_ENABLE_OFF_HAND("modifier.item.enable_off_hand", OptionType.BOOLEAN),
    MODIFIER_MULTIPLIER_ENABLED("modifier.multiplier.enabled", OptionType.BOOLEAN),
    MODIFIER_AUTO_CONVERT_FROM_LEGACY("modifier.auto_convert_from_legacy", OptionType.BOOLEAN),
    // Requirement options
    REQUIREMENT_ENABLED("requirement.enabled", OptionType.BOOLEAN),
    REQUIREMENT_ITEM_PREVENT_TOOL_USE("requirement.item.prevent_tool_use", OptionType.BOOLEAN),
    REQUIREMENT_ITEM_PREVENT_WEAPON_USE("requirement.item.prevent_weapon_use", OptionType.BOOLEAN),
    REQUIREMENT_ITEM_PREVENT_BLOCK_PLACE("requirement.item.prevent_block_place", OptionType.BOOLEAN),
    REQUIREMENT_ITEM_PREVENT_INTERACT("requirement.item.prevent_interact", OptionType.BOOLEAN),
    REQUIREMENT_ARMOR_PREVENT_ARMOR_EQUIP("requirement.armor.prevent_armor_equip", OptionType.BOOLEAN),
    // Critical options
    CRITICAL_BASE_MULTIPLIER("critical.base_multiplier", OptionType.DOUBLE),
    CRITICAL_ENABLED_SWORD("critical.enabled.sword", OptionType.BOOLEAN),
    CRITICAL_ENABLED_BOW("critical.enabled.bow", OptionType.BOOLEAN),
    CRITICAL_ENABLED_AXE("critical.enabled.axe", OptionType.BOOLEAN),
    CRITICAL_ENABLED_PICKAXE("critical.enabled.pickaxe", OptionType.BOOLEAN),
    CRITICAL_ENABLED_SHOVEL("critical.enabled.shovel", OptionType.BOOLEAN),
    CRITICAL_ENABLED_HOE("critical.enabled.hoe", OptionType.BOOLEAN),
    CRITICAL_ENABLED_HAND("critical.enabled.hand", OptionType.BOOLEAN),
    CRITICAL_ENABLED_OTHER("critical.enabled.other", OptionType.BOOLEAN),
    // Menu options
    MENUS_PLACEHOLDER_API("menus.placeholder_api", OptionType.BOOLEAN),
    CHECK_FOR_UPDATES("check_for_updates", OptionType.BOOLEAN),
    AUTOMATIC_BACKUPS_ENABLED("automatic_backups.enabled", OptionType.BOOLEAN),
    AUTOMATIC_BACKUPS_MINIMUM_INTERVAL_HOURS("automatic_backups.minimum_interval_hours", OptionType.DOUBLE),
    SAVE_BLANK_PROFILES("save_blank_profiles", OptionType.BOOLEAN);

    private final String path;
    private final OptionType type;

    Option(String path, OptionType type) {
        this.path = path;
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public OptionType getType() {
        return type;
    }

}
