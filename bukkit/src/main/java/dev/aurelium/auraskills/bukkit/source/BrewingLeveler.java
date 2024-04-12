package dev.aurelium.auraskills.bukkit.source;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.SkillSource;
import dev.aurelium.auraskills.api.source.type.BrewingXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.region.BukkitBlock;
import dev.aurelium.auraskills.common.region.BlockPosition;
import dev.aurelium.auraskills.common.source.SourceTypes;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BrewingLeveler extends SourceLeveler {

    private final Map<BlockPosition, BrewingStandData> brewingStands;

    public BrewingLeveler(AuraSkills plugin) {
        super(plugin, SourceTypes.BREWING);
        this.brewingStands = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBrew(BrewEvent event) {
        if (disabled()) return;
        ItemStack ingredient = event.getContents().getIngredient();
        if (ingredient == null) return;

        SkillSource<BrewingXpSource> skillSource = getSource(ingredient);
        if (skillSource == null) return;

        BrewingXpSource source = skillSource.source();
        Skill skill = skillSource.skill();

        if (source.getTrigger() == BrewingXpSource.BrewTriggers.TAKEOUT) {
            checkBrewedSlots(event);
        } else if (source.getTrigger() == BrewingXpSource.BrewTriggers.BREW) {
            if (!event.getBlock().hasMetadata("skillsBrewingStandOwner")) return;

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(event.getBlock().getMetadata("skillsBrewingStandOwner").get(0).asString()));
            if (!offlinePlayer.isOnline()) {
                return;
            }

            Player player = offlinePlayer.getPlayer();
            if (player == null) return;

            User user = plugin.getUserManager().getUser(player);

            if (failsChecks(event, player, event.getBlock().getLocation(), skill)) return;

            plugin.getLevelManager().addXp(user, skill, source, source.getXp());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTakePotionOut(InventoryClickEvent event) {
        if (disabled()) return;
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;
        if (inventory.getType() != InventoryType.BREWING && !(inventory instanceof BrewerInventory)) return;

        int slot = event.getSlot();
        if (slot > 2) return; // Slots 0-2 are result slots

        InventoryAction action = event.getAction();
        // Filter out other actions
        if (action != InventoryAction.PICKUP_ALL && action != InventoryAction.PICKUP_HALF && action != InventoryAction.PICKUP_SOME
                && action != InventoryAction.PICKUP_ONE && action != InventoryAction.MOVE_TO_OTHER_INVENTORY && action != InventoryAction.HOTBAR_SWAP
                && action != InventoryAction.HOTBAR_MOVE_AND_READD) {
            return;
        }
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        // Get the brewing stand data
        Location location = inventory.getLocation();
        if (location == null) return;
        BrewingStandData standData = brewingStands.get(BukkitBlock.from(location.getBlock()));
        if (standData == null) return;

        if (!(event.getWhoClicked() instanceof Player player)) return;

        BrewingSlot brewingSlot = standData.getSlot(slot);
        if (!brewingSlot.isBrewed()) return; // Check that the slot was brewed

        List<ItemStack> ingredients = brewingSlot.getIngredients();
        if (ingredients.isEmpty()) return;

        List<SkillSource<BrewingXpSource>> sources = getSources(ingredients);
        Map<Skill, Double> totalXpMap = new HashMap<>();

        brewingSlot.setBrewed(false); // Set data to false
        brewingSlot.resetIngredients();

        for (SkillSource<BrewingXpSource> skillSource : sources) {
            if (skillSource == null) return;

            BrewingXpSource source = skillSource.source();
            Skill skill = skillSource.skill();

            if (failsChecks(event, player, location, skill)) return;

            // Add the xp to the total for that skill
            totalXpMap.put(skill, totalXpMap.getOrDefault(skill, 0.0) + source.getXp());
        }

        User user = plugin.getUser(player);
        for (Skill skill : totalXpMap.keySet()) {
            // Add all xp from brews in that slot
            plugin.getLevelManager().addXp(user, skill, sources.get(0).source(), totalXpMap.getOrDefault(skill, 0.0));
        }
    }

    private void checkBrewedSlots(BrewEvent event) {
        BrewerInventory before = event.getContents();
        ItemStack ingredient = before.getIngredient();
        if (ingredient == null) return;
        ItemStack clonedIngredient = ingredient.clone();
        ItemStack[] beforeItems = Arrays.copyOf(before.getContents(), 3); // Items in result slots before
        plugin.getScheduler().scheduleSync(() -> {
            BlockState blockState = event.getBlock().getState();
            if (blockState instanceof BrewingStand brewingStand) {
                BrewerInventory after = brewingStand.getInventory();
                ItemStack[] afterItems = Arrays.copyOf(after.getContents(), 3); // Items in result slots after
                BlockPosition pos = BukkitBlock.from(event.getBlock());
                BrewingStandData standData = getBrewingStandData(clonedIngredient, beforeItems, afterItems, pos);
                brewingStands.put(pos, standData); // Register the stand data
            }
        }, 50, TimeUnit.MILLISECONDS);
    }

    @NotNull
    private BrewingStandData getBrewingStandData(ItemStack ingredient, ItemStack[] beforeItems, ItemStack[] afterItems, BlockPosition pos) {
        BrewingStandData standData = brewingStands.getOrDefault(pos, new BrewingStandData());
        // Set the items that changed as brewed
        for (int i = 0; i < 3; i++) {
            ItemStack beforeItem = beforeItems[i];
            ItemStack afterItem = afterItems[i];
            if (beforeItem != null && beforeItem.getType() != Material.AIR && afterItem != null && afterItem.getType() != Material.AIR) {
                if (!beforeItem.equals(afterItem)) {
                    BrewingSlot slot = standData.getSlot(i);
                    slot.setBrewed(true);
                    if (slot.getIngredients().size() < 5) { // Max 5 ingredients stacked to prevent auto brewing
                        slot.addIngredient(ingredient); // Track the ingredient used to brew
                    }
                }
            }
        }
        return standData;
    }

    // Marks brewing stand as owned by player when placed
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBrewingStandPlace(BlockPlaceEvent event) {
        if (disabled()) return;
        Block block = event.getBlock();
        if (block.getType() != Material.BREWING_STAND) {
            return;
        }
        block.setMetadata("skillsBrewingStandOwner", new FixedMetadataValue(plugin, event.getPlayer().getUniqueId()));
    }

    // Un-marks brewing stand as owned by player when broken
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBrewingStandBreak(BlockBreakEvent event) {
        if (disabled()) return;
        Block block = event.getBlock();
        if (block.getType().equals(Material.BREWING_STAND)) {
            return;
        }
        if (event.getBlock().hasMetadata("skillsBrewingStandOwner")) {
            event.getBlock().removeMetadata("skillsBrewingStandOwner", plugin);
        }
        brewingStands.remove(BukkitBlock.from(event.getBlock()));
    }

    // Marks brewing stand as owned by player when opened if unclaimed
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (disabled()) return;
        Inventory inventory = event.getInventory();
        if (!inventory.getType().equals(InventoryType.BREWING)) {
            return;
        }
        if (inventory.getHolder() == null) {
            return;
        }
        if (inventory.getLocation() == null) {
            return;
        }
        Block block = inventory.getLocation().getBlock();
        if (!block.hasMetadata("skillsBrewingStandOwner")) {
            block.setMetadata("skillsBrewingStandOwner", new FixedMetadataValue(plugin, event.getPlayer().getUniqueId()));
        }
    }

    @Nullable
    private SkillSource<BrewingXpSource> getSource(ItemStack item) {
        for (SkillSource<BrewingXpSource> entry : plugin.getSkillManager().getSourcesOfType(BrewingXpSource.class)) {
            if (plugin.getItemRegistry().passesFilter(item, entry.source().getIngredients())) {
                return entry;
            }
        }
        return null;
    }

    private List<SkillSource<BrewingXpSource>> getSources(List<ItemStack> items) {
        return items.stream().map(this::getSource).collect(Collectors.toList());
    }

    public static class BrewingStandData {

        private final Map<Integer, BrewingSlot> slots;

        public BrewingStandData() {
            this.slots = new HashMap<>();
        }

        public BrewingSlot getSlot(int slot) {
            return slots.computeIfAbsent(slot, s -> new BrewingSlot());
        }

    }

    public static class BrewingSlot {

        private boolean brewed;
        private final List<ItemStack> ingredients;

        public BrewingSlot() {
            this.brewed = false;
            this.ingredients = new ArrayList<>();
        }

        public boolean isBrewed() {
            return brewed;
        }

        public void setBrewed(boolean brewed) {
            this.brewed = brewed;
        }

        public List<ItemStack> getIngredients() {
            return ingredients;
        }

        public void addIngredient(ItemStack item) {
            this.ingredients.add(item);
        }

        public void resetIngredients() {
            this.ingredients.clear();
        }
    }

}
