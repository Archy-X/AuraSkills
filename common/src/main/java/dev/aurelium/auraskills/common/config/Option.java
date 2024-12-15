package dev.aurelium.auraskills.common.config;


public enum Option {

    // Mysql Options
    SQL_ENABLED("sql.enabled", OptionType.BOOLEAN),
    SQL_HOST("sql.host", OptionType.STRING),
    SQL_PORT("sql.port", OptionType.INT),
    SQL_DATABASE("sql.database", OptionType.STRING),
    SQL_USERNAME("sql.username", OptionType.STRING),
    SQL_PASSWORD("sql.password", OptionType.STRING),
    SQL_LOAD_DELAY("sql.load_delay", OptionType.INT),
    SQL_ALWAYS_LOAD_ON_JOIN("sql.always_load_on_join", OptionType.BOOLEAN),
    SQL_SSL("sql.ssl", OptionType.BOOLEAN),
    SQL_POOL_MAXIMUM_POOL_SIZE("sql.pool.maximum_pool_size", OptionType.INT),
    SQL_POOL_MINIMUM_IDLE("sql.pool.minimum_idle", OptionType.INT),
    SQL_POOL_CONNECTION_TIMEOUT("sql.pool.connection_timeout", OptionType.INT),
    SQL_POOL_MAX_LIFETIME("sql.pool.max_lifetime", OptionType.INT),
    SQL_POOL_KEEPALIVE_TIME("sql.pool.keepalive_time", OptionType.INT),
    DEFAULT_LANGUAGE("default_language", OptionType.STRING),
    TRY_DETECT_CLIENT_LANGUAGE("try_detect_client_language", OptionType.BOOLEAN),
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
    ACTION_BAR_FORMAT_LAST("action_bar.format_last", OptionType.BOOLEAN),
    ACTION_BAR_UPDATE_ASYNC("action_bar.update_async", OptionType.BOOLEAN),
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
    BOSS_BAR_XP_FORMAT("boss_bar.xp_format", OptionType.STRING),
    BOSS_BAR_LEVEL_XP_FORMAT("boss_bar.level_xp_format", OptionType.STRING),
    BOSS_BAR_PERCENT_FORMAT("boss_bar.percent_format", OptionType.STRING),
    BOSS_BAR_MONEY_FORMAT("boss_bar.money_format", OptionType.STRING),
    BOSS_BAR_ANIMATE_PROGRESS("boss_bar.animate_progress", OptionType.BOOLEAN),
    // Jobs options
    JOBS_ENABLED("jobs.enabled", OptionType.BOOLEAN),
    JOBS_SELECTION_REQUIRE_SELECTION("jobs.selection.require_selection", OptionType.BOOLEAN),
    JOBS_SELECTION_DEFAULT_JOB_LIMIT("jobs.selection.default_job_limit", OptionType.INT),
    JOBS_SELECTION_DISABLE_UNSELECTED_XP("jobs.selection.disable_unselected_xp", OptionType.BOOLEAN),
    JOBS_SELECTION_COOLDOWN_SEC("jobs.selection.cooldown_sec", OptionType.INT),
    JOBS_INCOME_USE_XP("jobs.income.use_xp", OptionType.BOOLEAN),
    JOBS_INCOME_USE_EXPRESSION("jobs.income.use_expression", OptionType.BOOLEAN),
    JOBS_INCOME_DEFAULT_INCOME_PER_XP("jobs.income.default.income_per_xp", OptionType.DOUBLE),
    JOBS_INCOME_DEFAULT_EXPRESSION("jobs.income.default.expression", OptionType.STRING),
    JOBS_INCOME_USE_FINAL_XP("jobs.income.use_final_xp", OptionType.BOOLEAN),
    JOBS_INCOME_BATCHING_ENABLED("jobs.income.batching.enabled", OptionType.BOOLEAN),
    JOBS_INCOME_BATCHING_INTERVAL_MS("jobs.income.batching.interval_ms", OptionType.INT),
    JOBS_INCOME_BATCHING_DISPLAY_INDIVIDUAL("jobs.income.batching.display_individual", OptionType.BOOLEAN),
    // Anti-AFK options
    ANTI_AFK_ENABLED("anti_afk.enabled", OptionType.BOOLEAN),
    ANTI_AFK_LOGGING_ENABLED("anti_afk.logging_enabled", OptionType.BOOLEAN),
    ANTI_AFK_LOG_THRESHOLD("anti_afk.log_threshold", OptionType.STRING),
    ANTI_AFK_CHECKS_BLOCK_A_ENABLED("anti_afk.checks.block_a.enabled", OptionType.BOOLEAN),
    ANTI_AFK_CHECKS_BLOCK_A_MIN_COUNT("anti_afk.checks.block_a.min_count", OptionType.INT),
    ANTI_AFK_CHECKS_DAMAGE_A_ENABLED("anti_afk.checks.damage_a.enabled", OptionType.BOOLEAN),
    ANTI_AFK_CHECKS_DAMAGE_A_MAX_DISTANCE("anti_afk.checks.damage_a.max_distance", OptionType.DOUBLE),
    ANTI_AFK_CHECKS_DAMAGE_A_MIN_COUNT("anti_afk.checks.damage_a.min_count", OptionType.INT),
    ANTI_AFK_CHECKS_DAMAGE_B_ENABLED("anti_afk.checks.damage_b.enabled", OptionType.BOOLEAN),
    ANTI_AFK_CHECKS_DAMAGE_B_MIN_COUNT("anti_afk.checks.damage_b.min_count", OptionType.INT),
    ANTI_AFK_CHECKS_DAMAGE_C_ENABLED("anti_afk.checks.damage_c.enabled", OptionType.BOOLEAN),
    ANTI_AFK_CHECKS_DAMAGE_C_MIN_COUNT("anti_afk.checks.damage_c.min_count", OptionType.INT),
    ANTI_AFK_CHECKS_ENTITY_A_ENABLED("anti_afk.checks.entity_a.enabled", OptionType.BOOLEAN),
    ANTI_AFK_CHECKS_ENTITY_A_MAX_DISTANCE("anti_afk.checks.entity_a.max_distance", OptionType.DOUBLE),
    ANTI_AFK_CHECKS_ENTITY_A_MIN_COUNT("anti_afk.checks.entity_a.min_count", OptionType.INT),
    ANTI_AFK_CHECKS_ENTITY_B_ENABLED("anti_afk.checks.entity_b.enabled", OptionType.BOOLEAN),
    ANTI_AFK_CHECKS_ENTITY_B_MIN_COUNT("anti_afk.checks.entity_b.min_count", OptionType.INT),
    ANTI_AFK_CHECKS_ENTITY_C_ENABLED("anti_afk.checks.entity_c.enabled", OptionType.BOOLEAN),
    ANTI_AFK_CHECKS_ENTITY_C_MIN_COUNT("anti_afk.checks.entity_c.min_count", OptionType.INT),
    ANTI_AFK_CHECKS_FISHING_A_ENABLED("anti_afk.checks.fishing_a.enabled", OptionType.BOOLEAN),
    ANTI_AFK_CHECKS_FISHING_A_MIN_COUNT("anti_afk.checks.fishing_a.min_count", OptionType.INT),
    ENABLE_ROMAN_NUMERALS("enable_roman_numerals", OptionType.BOOLEAN),
    // Damage hologram options
    DAMAGE_HOLOGRAMS_ENABLED("damage_holograms.enabled", OptionType.BOOLEAN),
    DAMAGE_HOLOGRAMS_SCALING("damage_holograms.scaling", OptionType.BOOLEAN),
    DAMAGE_HOLOGRAMS_DECIMAL_MAX("damage_holograms.decimal.max_amount", OptionType.INT),
    DAMAGE_HOLOGRAMS_DECIMAL_LESS_THAN("damage_holograms.decimal.display_when_less_than", OptionType.INT),
    DAMAGE_HOLOGRAMS_OFFSET_X("damage_holograms.offset.x", OptionType.DOUBLE),
    DAMAGE_HOLOGRAMS_OFFSET_Y("damage_holograms.offset.y", OptionType.DOUBLE),
    DAMAGE_HOLOGRAMS_OFFSET_Z("damage_holograms.offset.z", OptionType.DOUBLE),
    DAMAGE_HOLOGRAMS_OFFSET_RANDOM_ENABLED("damage_holograms.offset.random.enabled", OptionType.BOOLEAN),
    DAMAGE_HOLOGRAMS_OFFSET_RANDOM_X_MIN("damage_holograms.offset.random.x_min", OptionType.DOUBLE),
    DAMAGE_HOLOGRAMS_OFFSET_RANDOM_X_MAX("damage_holograms.offset.random.x_max", OptionType.DOUBLE),
    DAMAGE_HOLOGRAMS_OFFSET_RANDOM_Y_MIN("damage_holograms.offset.random.y_min", OptionType.DOUBLE),
    DAMAGE_HOLOGRAMS_OFFSET_RANDOM_Y_MAX("damage_holograms.offset.random.y_max", OptionType.DOUBLE),
    DAMAGE_HOLOGRAMS_OFFSET_RANDOM_Z_MIN("damage_holograms.offset.random.z_min", OptionType.DOUBLE),
    DAMAGE_HOLOGRAMS_OFFSET_RANDOM_Z_MAX("damage_holograms.offset.random.z_max", OptionType.DOUBLE),
    DAMAGE_HOLOGRAMS_COLORS_DEFAULT("damage_holograms.colors.default", OptionType.STRING),
    DAMAGE_HOLOGRAMS_COLORS_CRITICAL_DIGITS("damage_holograms.colors.critical.digits", OptionType.LIST),
    START_LEVEL("start_level", OptionType.INT),
    DATA_VALIDATION_CORRECT_OVER_MAX_LEVEL("data_validation.correct_over_max_level", OptionType.BOOLEAN),
    LEADERBOARDS_UPDATE_PERIOD("leaderboards.update_period", OptionType.INT),
    LEADERBOARDS_UPDATE_DELAY("leaderboards.update_delay", OptionType.INT),
    ENABLE_SKILL_COMMANDS("enable_skill_commands", OptionType.BOOLEAN),
    CHECK_BLOCK_REPLACE_ENABLED("check_block_replace.enabled", OptionType.BOOLEAN),
    DISABLE_IN_CREATIVE_MODE("disable_in_creative_mode", OptionType.BOOLEAN),
    ON_DEATH_RESET_SKILLS("on_death.reset_skills", OptionType.BOOLEAN),
    ON_DEATH_RESET_XP("on_death.reset_xp", OptionType.BOOLEAN),
    AUTO_SAVE_ENABLED("auto_save.enabled", OptionType.BOOLEAN),
    AUTO_SAVE_INTERVAL_TICKS("auto_save.interval_ticks", OptionType.INT),
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
    MANA_ENABLED("mana.enabled", OptionType.BOOLEAN),
    MANA_COOLDOWN_TIMER_PERIOD("mana.cooldown_timer_period", OptionType.INT),
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
    REQUIREMENT_OVERRIDE_GLOBAL("requirement.override_global", OptionType.BOOLEAN),
    REQUIREMENT_ITEM_PREVENT_TOOL_USE("requirement.item.prevent_tool_use", OptionType.BOOLEAN),
    REQUIREMENT_ITEM_PREVENT_WEAPON_USE("requirement.item.prevent_weapon_use", OptionType.BOOLEAN),
    REQUIREMENT_ITEM_PREVENT_BLOCK_PLACE("requirement.item.prevent_block_place", OptionType.BOOLEAN),
    REQUIREMENT_ITEM_PREVENT_INTERACT("requirement.item.prevent_interact", OptionType.BOOLEAN),
    REQUIREMENT_ARMOR_PREVENT_ARMOR_EQUIP("requirement.armor.prevent_armor_equip", OptionType.BOOLEAN),
    // Damage options
    DAMAGE_CORRECT_LAST_DAMAGE("damage.correct_last_damage", OptionType.BOOLEAN),
    // Critical options
    CRITICAL_ENABLED_SWORD("critical.enabled.sword", OptionType.BOOLEAN),
    CRITICAL_ENABLED_BOW("critical.enabled.bow", OptionType.BOOLEAN),
    CRITICAL_ENABLED_AXE("critical.enabled.axe", OptionType.BOOLEAN),
    CRITICAL_ENABLED_PICKAXE("critical.enabled.pickaxe", OptionType.BOOLEAN),
    CRITICAL_ENABLED_SHOVEL("critical.enabled.shovel", OptionType.BOOLEAN),
    CRITICAL_ENABLED_HOE("critical.enabled.hoe", OptionType.BOOLEAN),
    CRITICAL_ENABLED_HAND("critical.enabled.hand", OptionType.BOOLEAN),
    CRITICAL_ENABLED_OTHER("critical.enabled.other", OptionType.BOOLEAN),
    // Source options
    SOURCE_GRINDSTONE_BLOCKED_ENCHANTS("source.grindstone.blocked_enchants", OptionType.LIST),
    SOURCE_STATISTIC_GAIN_PERIOD_TICKS("source.statistic.gain_period_ticks", OptionType.INT),
    SOURCE_ENTITY_GIVE_ALCHEMY_ON_POTION_COMBAT("source.entity.give_alchemy_on_potion_combat", OptionType.BOOLEAN),
    // Menu options
    MENUS_LORE_WRAPPING_WIDTH("menus.lore_wrapping_width", OptionType.INT),
    MENUS_PLACEHOLDER_API("menus.placeholder_api", OptionType.BOOLEAN),
    MENUS_STATS_SHOW_TRAIT_VALUES_DIRECTLY("menus.stats.show_trait_values_directly", OptionType.BOOLEAN),
    LOOT_UPDATE_LOOT_TABLES("loot.update_loot_tables", OptionType.BOOLEAN),
    LOOT_DIRECTLY_TO_INVENTORY("loot.directly_to_inventory", OptionType.BOOLEAN),
    CHECK_FOR_UPDATES("check_for_updates", OptionType.BOOLEAN),
    AUTOMATIC_BACKUPS_ENABLED("automatic_backups.enabled", OptionType.BOOLEAN),
    AUTOMATIC_BACKUPS_MINIMUM_INTERVAL_HOURS("automatic_backups.minimum_interval_hours", OptionType.DOUBLE),
    AUTOMATIC_BACKUPS_MAX_USERS("automatic_backups.max_users", OptionType.INT),
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
