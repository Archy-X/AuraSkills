# GitHub Copilot Instructions for AuraSkills-Coins Project

## Project Overview
This is **AuraSkills** with the **SkillCoins** economy system integration. It's a Minecraft plugin that adds RPG-style skills and progression, enhanced with a custom shop system using skill-based currency.

## Technology Stack
- **Language**: Java (Minecraft Bukkit/Spigot API)
- **Build System**: Gradle (Kotlin DSL)
- **Target**: Minecraft 1.20+ servers
- **Dependencies**: AuraSkills API, Vault (optional), PlaceholderAPI

## Code Structure

### Module Organization
```
AuraSkills-Coins/
├── api/                    # Core API interfaces
├── api-bukkit/             # Bukkit-specific API
├── bukkit/                 # Main plugin implementation
│   ├── src/main/resources/
│   │   └── SkillCoinsShop/ # ⚠️ DEFAULT shop configs (bundled in JAR)
│   │       ├── sections/   # Section definitions (Combat, Enchantments, etc.)
│   │       └── shops/      # Shop item lists (prices, items, enchants)
│   └── skillcoins/         # SkillCoins economy system
│       ├── command/        # Commands (/shop, /skillcoins)
│       ├── menu/           # GUI menus (shop, transactions)
│       ├── shop/           # Shop system (items, sections, loader)
│       └── listener/       # Event listeners
└── common/                 # Shared utilities
```

### Important Packages
- `dev.aurelium.auraskills.bukkit.skillcoins` - Main SkillCoins implementation
- `dev.aurelium.auraskills.bukkit.skillcoins.menu` - All GUI menus
- `dev.aurelium.auraskills.bukkit.skillcoins.shop` - Shop item and section management
- `dev.aurelium.auraskills.common.skillcoins` - Currency types and economy provider

## Coding Standards

### Java Style
- **Package naming**: All lowercase (e.g., `skillcoins.menu`)
- **Class naming**: PascalCase (e.g., `ShopMainMenu`)
- **Methods**: camelCase (e.g., `openShopMenu`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `ITEMS_PER_PAGE`)

### GUI Menus
- Always use **54 slots (6 rows)** for main menus
- Use **slot 0** for player head/info
- Use **slot 8** for balance/stats (top right)
- Use **slot 53** for close/back button (bottom right)
- Use **BLACK_STAINED_GLASS_PANE** with empty name for borders
- Center items symmetrically
  - Main shop menu: 2 rows (slots 11-14 for items, slots 20-25 for more items) + row 5 (slots 38-39 for token services)
  - Section menus: Use centered grids based on content
  - Token Exchange menu: Custom layout
    - Slot 13: Token display (center top) - EMERALD showing total
    - Slots 20-24: Quantity controls (-10, -1, display[EMERALD], +1, +10)
    - Slot 31: Quick Select button (NETHER_STAR) - opens preset submenu
    - Slot 45: Balance display (bottom left)
    - Slot 49: Confirm button (center bottom)
    - Slot 53: Back button (bottom right)
  - Quick Select Amount submenu (27 slots):
    - Slots 10-14, 16: Preset buttons (1, 10, 50, 100, 500, 1000 tokens)
    - Slot 22: Back button

### Color Scheme
Use hex colors via `ChatColor.of()`:
- **Primary**: `#00FFFF` (cyan) - Main branding
- **Success**: `#55FF55` (green) - Success messages, buy actions
- **Warning**: `#FFFF00` (yellow) - Warnings, attention
- **Error**: `#FF5555` (red) - Errors, deny actions
- **Info**: `#808080` (gray) - Secondary text
- **Currency - Coins**: `#FFD700` (gold)
- **Currency - Tokens**: `#00FFFF` (cyan)

### Item Display Format
```java
List<String> lore = new ArrayList<>();
lore.add("");  // Always start with blank line
lore.add(ChatColor.of("#55FF55") + "● Buy: " + ChatColor.of("#FFFFFF") + price);
lore.add("");
lore.add(ChatColor.of("#FFFF00") + "▸ Click to open!");
```

### Menu Navigation Pattern
```java
@EventHandler
public void onClick(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player)) return;
    String title = event.getView().getTitle();
    if (!title.contains("Expected Title")) return;
    
    event.setCancelled(true);  // Always cancel to prevent item pickup
    
    Player player = (Player) event.getWhoClicked();
    ItemStack clicked = event.getCurrentItem();
    
    if (clicked == null || clicked.getType() == Material.AIR) return;
    if (clicked.getType() == Material.BLACK_STAINED_GLASS_PANE) return;  // Ignore borders
    
    int slot = event.getSlot();
    // Handle clicks...
}
```

## Currency System

### Currency Types
```java
public enum CurrencyType {
    COINS,   // SkillCoins - earned from leveling skills
    TOKENS   // SkillTokens - premium currency for special purchases
}
```

### Economy Provider Usage
```java
EconomyProvider economy = plugin.getEconomyProvider();
double balance = economy.getBalance(uuid, CurrencyType.COINS);
economy.addBalance(uuid, CurrencyType.COINS, amount);
economy.subtractBalance(uuid, CurrencyType.TOKENS, amount);
```

## Shop System

### Shop Item Types
```java
public enum ShopItemType {
    REGULAR,         // Normal items (buy/sell)
    SKILL_LEVEL,     // Purchase skill levels with tokens
    TOKEN_EXCHANGE   // Exchange coins for tokens
}
```

### Shop Configuration
- Section files: `SkillCoinsShop/sections/*.yml`
- Shop files: `SkillCoinsShop/shops/*.yml`
- Auto-generated on first run
- **DO NOT** manually copy config files in deploy script - let plugin generate them

## Menu Hierarchy
```
ShopMainMenu (Main hub)
    ↓
    ├─→ ShopSectionMenu (Category view with pagination)
    │       ↓
    │   TransactionMenu (Buy/sell with quantity adjustment)
    │
    └─→ TokenExchangeMenu (Custom token purchase interface)
            - Quantity controls (+/-1, +/-10)
            - Preset buttons (1, 10, 50, 100, 500)
            - Live cost calculation
            - Instant purchase
```

## Deployment

### Build Command
```bash
./gradlew clean build -x test
```

### Deploy Script
Located at `deploy.sh` in workspace root:
- Stops container using `docker inspect` (reliable detection)
- Removes old plugin folder completely
- Copies only JAR file (configs auto-generate)
- Starts container with verification
- Monitors server startup logs

### Container Detection
Always use `docker inspect` for reliable state checking:
```bash
is_running() {
    docker inspect -f '{{.State.Running}}' "$CONTAINER_ID" 2>/dev/null | grep -q "true"
}
```

## Common Patterns

### Creating Menu Items
```java
ItemStack item = new ItemStack(Material.DIAMOND);
ItemMeta meta = item.getItemMeta();
if (meta != null) {
    meta.setDisplayName(ChatColor.of("#00FFFF") + "Item Name");
    List<String> lore = new ArrayList<>();
    lore.add("");
    lore.add(ChatColor.of("#808080") + "Description");
    meta.setLore(lore);
    item.setItemMeta(meta);
}
inv.setItem(slot, item);
```

### Number Formatting
```java
private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
String formatted = MONEY_FORMAT.format(amount);  // e.g., "1,234.56"
```

### Sound Feedback
```java
player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);  // Success
player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);     // Error
player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);        // Click
```

## Testing Checklist
When modifying menus:
- [ ] All buttons respond to clicks
- [ ] Back/close buttons work correctly
- [ ] Navigation flows properly between menus
- [ ] Colors are consistent with theme
- [ ] Layout is symmetric and centered
- [ ] Lore text is clear and helpful
- [ ] Sounds provide appropriate feedback
- [ ] Border glass panes are unclickable

## Performance Considerations
- Use `HashMap` for player-specific data (e.g., page numbers)
- Clear player data in `InventoryCloseEvent`
- Batch operations when possible
- Cache frequently accessed config values
- Use `ItemStack.isSimilar()` for item matching, not `.equals()`

## Documentation
- Add JavaDoc for public methods
- Explain complex logic with inline comments
- Document menu slot layouts in class comments

## Shop Configuration Files
⚠️ **CRITICAL**: Shop configs are bundled INSIDE the JAR from resources!

**Correct Location (edit these):**
```
bukkit/src/main/resources/SkillCoinsShop/
├── sections/   # Section definitions (Combat.yml, etc.)
└── shops/      # Shop item lists (Combat.yml, etc.)
```

**What Happens:**
1. Files in `bukkit/src/main/resources/` get bundled into the JAR during build
2. On first server load, plugin extracts them to `plugins/AuraSkills/SkillCoinsShop/`
3. Server uses the extracted files (users can customize them)
4. If you need to change defaults, edit files in `bukkit/src/main/resources/`, NOT the workspace root

## DO NOT
- ❌ Edit config files in workspace root `SkillCoinsShop/` folder (they're not used!)
- ❌ Use deprecated Bukkit methods (use modern alternatives)
- ❌ Create menus smaller than 54 slots for main hubs
- ❌ Forget to cancel InventoryClickEvents
- ❌ Use `setType()` on ItemStacks in inventories
- ❌ Hardcode colors (always use hex with ChatColor.of())

## DO
- ✅ Always null-check `getCurrentItem()` and `getItemMeta()`
- ✅ Use `is_running()` function for container checks
- ✅ Provide visual feedback for all actions
- ✅ Center items in menus for aesthetic appeal
- ✅ Include helpful lore text on all clickable items
- ✅ Test navigation flow after menu changes
- ✅ Use consistent icon symbols (⚔, ✦, ❖, etc.)

## Git Workflow
- Branch: `master`
- Commit style: Descriptive (e.g., "Fix shop menu layout and button interactions")
- Test locally before pushing
- Build must succeed before deployment

---

**Remember**: This plugin prioritizes user experience with beautiful, intuitive menus and clear visual feedback. Every change should maintain or improve the polished aesthetic and smooth navigation flow.
