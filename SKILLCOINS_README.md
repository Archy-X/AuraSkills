# SkillCoins Shop System

## Overview
A complete shop system integrated into AuraSkills that allows players to buy and sell items using **SkillCoins** and **SkillTokens**.

## Features

### Currency System
- **SkillCoins**: Primary currency earned through gameplay
- **SkillTokens**: Premium/special currency (future expansion)
- File-based storage in `plugins/AuraSkills/skillcoins/`
- Automatic save/load on player join/quit

### Shop System
- 4 Main Categories:
  - **Combat**: Weapons, armor, combat supplies
  - **Enchantments**: Enchanted books at all levels
  - **Resources**: Ores, minerals, building materials
  - **Tools**: Pickaxes, axes, utility items

### GUI Features
- **Main Shop Menu**: Browse all categories
- **Section Menus**: View items with pagination
- **Transaction Menu**: Buy/sell with quantity adjustment
  - +1 / +10 buttons
  - -1 / -10 buttons
  - Confirm/Cancel options
  - Real-time balance checking

## Commands

### Player Commands
- `/shop` - Open the SkillCoins shop
- `/skillcoins balance` or `/sc bal` - Check your balance

### Admin Commands
- `/skillcoins give <player> <coins|tokens> <amount>` - Give currency
- `/skillcoins take <player> <coins|tokens> <amount>` - Remove currency
- `/skillcoins set <player> <coins|tokens> <amount>` - Set balance
- `/skillcoins check <player>` - Check another player's balance

## Permissions
- `auraskills.command.shop` - Access the shop
- `auraskills.command.skillcoins.balance` - Check own balance
- `auraskills.command.skillcoins.give` - Give currency (admin)
- `auraskills.command.skillcoins.take` - Take currency (admin)
- `auraskills.command.skillcoins.set` - Set balance (admin)
- `auraskills.command.skillcoins.check` - Check other balances (admin)

## Configuration

### Shop Files Location
```
plugins/AuraSkills/SkillCoinsShop/
├── sections/
│   ├── Combat.yml
│   ├── Enchantments.yml
│   ├── Resources.yml
│   └── Tools.yml
└── shops/
    ├── Combat.yml
    ├── Enchantments.yml
    ├── Resources.yml
    └── Tools.yml
```

### Section File Format (sections/*.yml)
```yaml
enable: true
slot: 11
title: ''
hidden: false
sub-section: false
display-item: false
fill-item:
  material: AIR
nav-bar:
  mode: INHERIT
item:
  material: DIAMOND_SWORD
  displayname: '&c&lCombat'
  name: '&c&lCombat'
```

### Shop File Format (shops/*.yml)
```yaml
pages:
  page1:
    items:
      '1':
        material: DIAMOND_SWORD
        buy: 250      # Buy price (-1 to disable)
        sell: 80      # Sell price (-1 to disable)
      '2':
        material: ENCHANTED_BOOK
        enchantments:
          - SHARPNESS:5
        buy: 350
        sell: 105
```

## Technical Details

### Architecture
- **Common Module**: Core economy logic, storage interfaces
- **Bukkit Module**: GUI implementation, command handling, shop loading

### Storage
- Player balances stored in YAML format
- Auto-save on balance changes
- Async save operations to prevent lag
- Data loaded on join, unloaded on quit

### Code Structure
```
common/src/main/java/dev/aurelium/auraskills/common/skillcoins/
├── CurrencyType.java              # Currency enum (COINS, TOKENS)
├── EconomyProvider.java           # Economy API interface
├── SkillCoinsEconomy.java         # In-memory economy implementation
├── SkillCoinsStorage.java         # Storage interface
└── FileSkillCoinsStorage.java     # File-based storage

bukkit/src/main/java/dev/aurelium/auraskills/bukkit/skillcoins/
├── command/
│   ├── ShopCommand.java           # /shop command
│   └── SkillCoinsCommand.java     # /skillcoins command
├── menu/
│   ├── ShopMainMenu.java          # Main shop GUI
│   ├── ShopSectionMenu.java       # Category browser
│   └── TransactionMenu.java       # Buy/sell confirmation
└── shop/
    ├── ShopItem.java              # Item data model
    ├── ShopSection.java           # Section data model
    └── ShopLoader.java            # YAML configuration loader
```

## Pricing Balance

### Progression Tiers
- **Basic** (1-10 coins): Common materials, basic tools
- **Intermediate** (10-100 coins): Iron/Gold tier, common enchants
- **Advanced** (100-500 coins): Diamond tier, rare enchants (Fortune III)
- **Elite** (500-1000 coins): Netherite gear, Mending, Totem
- **Legendary** (1000+ coins): Nether Star, Dragon Egg, Beacon

### Sell Prices
- Set at 30% of buy price by default
- Encourages earning through gameplay rather than grinding

## Usage Examples

### For Players
1. Earn SkillCoins through gameplay
2. Run `/shop` to open the shop menu
3. Click a category (Combat, Enchantments, etc.)
4. Left-click an item to buy, right-click to sell
5. Adjust quantity with +/- buttons
6. Click CONFIRM to complete transaction

### For Admins
```
# Give 1000 coins to player
/skillcoins give Steve coins 1000

# Check a player's balance
/skillcoins check Steve

# Set a player's tokens
/skillcoins set Steve tokens 50
```

## Integration with AuraSkills

The SkillCoins system is fully integrated into AuraSkills:
- Initialized on plugin enable
- Saved on plugin disable
- Player data loaded/unloaded with AuraSkills user data
- Uses AuraSkills scheduler for async operations
- Follows AuraSkills module structure (common + bukkit)

## Future Enhancements
- SQL storage support for large servers
- Vault integration for multi-economy support
- PlaceholderAPI placeholders (%auraskills_coins%, %auraskills_tokens%)
- Shop item custom display names and lore
- Transaction history logging
- Admin shop GUI for easy item management
- Dynamic pricing based on supply/demand
- Player-to-player trades
- SkillToken rewards for achievements
