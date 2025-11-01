# PvP Equipment-Only Stats Feature

## Overview

This feature adds a new configuration option to make PvP (player vs player) combat more balanced by only applying
equipment-based stats during player combat. When enabled, two players with the same equipment will have equal stats in
PvP, regardless of their skill levels.

## How It Works

### Normal Mode (Feature Disabled - Default)

When a player attacks another entity or gets damaged:

- All stats are applied (from skills, equipment, modifiers, etc.)
- Players with higher skill levels have an advantage in all combat scenarios

### PvP Equipment-Only Mode (Feature Enabled)

When a player attacks another **player** or gets damaged by another **player**:

- Only equipment stats (from items and armor) are applied
- Skill-based stats and other non-equipment modifiers are ignored
- Both attack damage and damage reduction traits respect this setting

When a player attacks or gets damaged by **non-player entities** (mobs):

- All stats apply normally (skills, equipment, everything)
- PvE combat is unaffected by this setting

## Configuration

Add this to your `config.yml`:

```yaml
# PvP balance settings
pvp:
  # When enabled, only equipment stats (from items and armor) will be applied during player vs player combat.
  # This ensures that two players with the same equipment have equal stats in PvP, regardless of their skill levels.
  # Skills and other non-equipment modifiers will still apply in PvE combat.
  only_equipment_stats: false
```

Set to `true` to enable the feature.

## Technical Details

### Modified Files

1. **Option.java** - Added `PVP_ONLY_EQUIPMENT_STATS` configuration option
2. **DamageMeta.java** - Added `isPvP()` helper method to detect player vs player combat
3. **UserStats.java** - Added `getBonusTraitLevelEquipmentOnly()` method to calculate traits using only equipment
   modifiers
4. **User.java** - Exposed the equipment-only trait calculation method
5. **AttackDamageTrait.java** - Modified to use equipment-only stats in PvP when configured
6. **DamageReductionTrait.java** - Modified to use equipment-only stats in PvP when configured
7. **config.yml** - Added the new configuration section with documentation

### How Equipment Modifiers Are Identified

Equipment modifiers are identified by their naming convention:

- Stat modifiers from items/armor start with: `AuraSkills.Modifiers.`
- Trait modifiers from items/armor start with: `AuraSkills.TraitModifiers.`

The system filters modifiers by checking if their names start with these prefixes.

### Affected Stats

The following traits are modified during PvP when the feature is enabled:

1. **Attack Damage (Strength)** - Only equipment-based attack damage applies
2. **Damage Reduction** - Only equipment-based damage reduction applies
3. **Crit Chance** - Base value (from config) + equipment-based bonus only
4. **Crit Damage** - Only equipment-based critical hit damage multiplier applies

### Traits Not Affected (Technical Limitations)

The following traits continue to use all modifiers even in PvP mode due to technical constraints:

1. **HP (Max Health)** - Applied via Minecraft attributes continuously, cannot be dynamically changed per-combat
2. **Movement Speed** - Applied continuously to player's walk speed, not during combat events
3. **Max Mana** - Calculated outside of combat events
4. **Mana Regen** - Applied continuously over time, not during combat
5. **Hunger Regen / Saturation Regen** - Applied during health regeneration events, not direct combat

These traits affect the player's persistent state rather than combat calculations, making it impractical to toggle them
based on combat scenarios without implementing a complex combat tracking system.

## Use Cases

### Scenario 1: Balanced PvP Arena

You run a PvP server where you want equipment to be the primary factor in combat, not grinding skills:

- Enable `only_equipment_stats: true`
- Players with the same gear are on equal footing regardless of skill levels
- Encourages fair competitive play

### Scenario 2: Mixed Server (PvE and PvP)

You want skill progression to matter in PvE but balanced PvP:

- Enable `only_equipment_stats: true`
- Players work hard to level skills for better PvE performance
- PvP remains balanced and skill-independent

### Scenario 3: Traditional RPG Server

You want skill levels to matter everywhere:

- Keep `only_equipment_stats: false` (default)
- Higher skill level = more powerful in all scenarios
- Traditional RPG progression affects all combat

## Examples

### Example 1: Same Equipment, Different Skills

**Setup:**

- Player A: Level 50 Strength skill, Diamond Sword with +5 Attack
- Player B: Level 10 Strength skill, Diamond Sword with +5 Attack

**With feature disabled:** Player A deals more damage due to higher skill level
**With feature enabled:** Both players deal the same damage (only the sword's +5 Attack applies)

### Example 2: Different Equipment, Same Skills

**Setup:**

- Player A: Level 30 Strength skill, Diamond Sword with +5 Attack
- Player B: Level 30 Strength skill, Iron Sword with +2 Attack

**With feature disabled:** Player A deals more damage due to better equipment
**With feature enabled:** Player A still deals more damage due to better equipment

This shows that equipment still matters - the feature only removes the skill-based advantage.

## Testing

To test if the feature is working:

1. Enable the feature in `config.yml`:
   ```yaml
   pvp:
     only_equipment_stats: true
   ```

2. Reload the configuration: `/skills reload`

3. Create two test players with:
    - Same armor and weapons
    - Different skill levels

4. Have them fight each other - damage should be equal

5. Have them fight mobs - the player with higher skills should perform better

## Compatibility

- Works with all existing equipment modifiers
- Compatible with custom items that add stat modifiers
- Does not affect abilities or other game mechanics
- Only affects damage calculation in PvP scenarios

## Future Enhancements

Possible future improvements:

- Per-trait configuration (enable for some traits but not others)
- Whitelist/blacklist specific modifiers
- Different modes (equipment-only, skill-only, percentage-based)
- Arena-specific configuration

## Support

If you encounter any issues with this feature:

1. Check that the configuration is correct
2. Verify the feature is enabled with `/skills reload`
3. Test in a controlled environment with debug logging enabled
4. Report issues with reproduction steps and server version

