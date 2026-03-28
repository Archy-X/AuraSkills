package dev.aurelium.auraskills.bukkit.mana;

import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.source.type.BlockXpSource;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.mana.ManaAbilityData;
import dev.aurelium.auraskills.common.message.type.ManaAbilityMessage;
import dev.aurelium.auraskills.common.source.type.BlockSource;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class ReadiedManaAbility extends ManaAbilityProvider {

    private final String[] materials;
    private final Action[] actions;
    protected final Map<UUID, AbilityBlocksRegistry> abilityBlocksMap = new HashMap<>();

    public ReadiedManaAbility(AuraSkills plugin, ManaAbility manaAbility, ManaAbilityMessage activateMessage, @Nullable ManaAbilityMessage stopMessage, String[] materials, Action[] actions) {
        super(plugin, manaAbility, activateMessage, stopMessage);
        this.materials = materials;
        this.actions = actions;
    }

    @Override
    protected boolean isReady(User user) {
        ManaAbilityData data = user.getManaAbilityData(manaAbility);
        return data.isReady() && !data.isActivated();
    }

    protected boolean isHoldingMaterial(Player player) {
        return materialMatches(player.getInventory().getItemInMainHand().getType().toString(), player);
    }

    protected boolean isHoldingMaterial(Player player, ItemStack item) {
        if (item == null) return false;
        return materialMatches(item.getType().toString(), player);
    }

    protected boolean materialMatches(String checked, Player player) {
        for (String material : materials) {
            if (checked.contains(material)) {
                return true;
            }
        }
        return false;
    }

    // Stop if the tool isn't meeting the requirements.
    protected void stopBreak(Player player, ItemStack newItem) {
        if (isHoldingMaterial(player, newItem)) {
            return;
        }
        clearAbilityBlocksMaps(player);
    }

    protected void clearAbilityBlocksMaps(Player player) {
        // Apply damage, if applicable
        AbilityBlocksRegistry registry = abilityBlocksMap.remove(player.getUniqueId());
        if (registry != null) {
            for (AbilityBlocks abilityBlocks : registry.getValues()) {
                setHoldingMaterialDurability(player, abilityBlocks.getBlocksBroken(), manaAbility.optionDouble("durability_multiplier", 0));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        stopBreak(player, newItem);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemHeldSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = event.getOffHandItem();
        stopBreak(player, newItem);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemHeldDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getInventorySlots().contains(player.getInventory().getHeldItemSlot())) {
            ItemStack newItem = event.getNewItems().get(player.getInventory().getHeldItemSlot());
            stopBreak(player, newItem);
        }
    }

    protected void scheduleDropItemCheck(Player player, ItemStack item) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (isHoldingMaterial(player)) {
                return;
            }
            clearAbilityBlocksMaps(player);
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemHeldClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getClick() == ClickType.NUMBER_KEY) {
            ItemStack newItem = player.getInventory().getItem(event.getHotbarButton());
            stopBreak(player, newItem);
        }

        int heldSlot = player.getInventory().getHeldItemSlot();
        if (event.getClick() == ClickType.DROP || event.getClick() == ClickType.CONTROL_DROP) {
            if (event.getSlot() != heldSlot) return;
            ItemStack droppedItem = event.getCurrentItem();
            scheduleDropItemCheck(player, droppedItem);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemHeldDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        scheduleDropItemCheck(player, droppedItem);
    }

    protected void setHoldingMaterialDurability(Player player, int count, double multiplier) {
        setHoldingMaterialDurability(player, player.getInventory().getItemInMainHand(), count, multiplier);
    }

    protected void setHoldingMaterialDurability(Player player, ItemStack item, int count, double multiplier) {
        if (item == null || multiplier <= 0) return;

        int takenDamage = (int) (count * multiplier);
        if (takenDamage <= 0) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.isUnbreakable()) return;

        if (meta instanceof Damageable damageable) {
            int currentDamage = damageable.getDamage();
            int unbreakingLevel = item.getEnchantmentLevel(Enchantment.UNBREAKING);

            if (unbreakingLevel > 0) {
                takenDamage = (int) (takenDamage * (1.0 - (1.0 / (unbreakingLevel + 1))));
            }

            int newDamage = currentDamage + takenDamage;
            int maxDamage = item.getType().getMaxDurability();

            if (newDamage >= maxDamage) {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                player.getWorld().spawnParticle(Particle.ITEM, player.getLocation(), 30, 0.2, 0.5, 0.2, new ItemStack(item.getType()));
            } else {
                damageable.setDamage(newDamage);
                item.setItemMeta(meta);
            }
        }
    }

    protected int getHoldingMaterialDurability(Player player, int limit) {
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if (meta != null && !meta.isUnbreakable() && meta instanceof Damageable damageable) {
            int durability = item.getType().getMaxDurability() - damageable.getDamage();
            return Math.min(limit, durability);
        }

        return limit;
    }

    protected boolean isExcludedBlock(Block block) {
        return hasInteraction(block);
    }

    protected boolean hasInteraction(Block block) {
        Material mat = block.getType();
        return switch (mat) {
            case ENDER_CHEST, CRAFTING_TABLE, ENCHANTING_TABLE, BEACON, ANVIL, GRINDSTONE, CARTOGRAPHY_TABLE, LOOM, STONECUTTER, SMITHING_TABLE, LEVER, BAMBOO_BUTTON, BIRCH_BUTTON, ACACIA_BUTTON, CHERRY_BUTTON, CRIMSON_BUTTON, DARK_OAK_BUTTON, JUNGLE_BUTTON, MANGROVE_BUTTON, OAK_BUTTON, POLISHED_BLACKSTONE_BUTTON, SPRUCE_BUTTON, STONE_BUTTON, WARPED_BUTTON ->
                    true;
            default -> {
                BlockData data = block.getBlockData();
                if (block.getState() instanceof InventoryHolder) {
                    yield true;
                } else yield data instanceof Bed || data instanceof Sign || data instanceof Door ||
                        data instanceof Gate || data instanceof NoteBlock || data instanceof TrapDoor;
            }
        };
    }

    public String[] getMaterials() {
        return materials;
    }

    @EventHandler
    public void onReady(PlayerInteractEvent event) {
        if (isDisabled()) return;

        // Check action is valid
        boolean valid = false;
        for (Action action : actions) {
            if (event.getAction() == action) {
                valid = true;
                break;
            }
        }
        if (!valid) return;
        // Check block exclusions
        Block block = event.getClickedBlock();
        // Match sure material matches
        Player player = event.getPlayer();
        if (!isHoldingMaterial(player)) {
            return;
        }
        if (block != null) {
            if (isExcludedBlock(block)) return;
        }
        if (!isAllowReady(player, event)) {
            return;
        }
        User user = plugin.getUser(player);
        ManaAbilityData data = user.getManaAbilityData(manaAbility);

        Locale locale = user.getLocale();
        if (user.getManaAbilityLevel(manaAbility) <= 0) {
            return;
        }
        // Check if already activated
        if (data.isActivated()) {
            return;
        }
        // Checks if already ready
        if (data.isReady()) {
            return;
        }
        if (data.getCooldown() == 0) { // Ready
            data.setReady(true);
            plugin.getAbilityManager().sendMessage(player, plugin.getMsg(ManaAbilityMessage.valueOf(manaAbility.name() + "_RAISE"), locale));
            scheduleUnready(player, locale, data);
        } else { // Cannot ready, send cooldown error
            if (data.getErrorTimer() == 0) {
                plugin.getAbilityManager().sendMessage(player, plugin.getMsg(ManaAbilityMessage.NOT_READY, locale).replace("{cooldown}",
                        NumberUtil.format0((double) data.getCooldown() / 20)));
                data.setErrorTimer(2);
            }
        }
    }

    private void scheduleUnready(Player player, Locale locale, ManaAbilityData data) {
        int readyDuration = 80;
        plugin.getScheduler().scheduleSync(() -> {
            if (!data.isActivated()) {
                if (data.isReady()) {
                    data.setReady(false);
                    plugin.getAbilityManager().sendMessage(player, plugin.getMsg(ManaAbilityMessage.valueOf(manaAbility.name() + "_LOWER"), locale));
                }
            }
        }, readyDuration * 50, TimeUnit.MILLISECONDS);
    }

    private boolean isAllowReady(Player player, PlayerInteractEvent event) {
        // Check if requires sneak
        if (manaAbility.optionBoolean("require_sneak", false)) {
            if (!player.isSneaking()) return false;
        }
        // Check if the offhand item is being placed
        if (isBlockPlace(event, player, manaAbility)) {
            return false;
        }
        // Check disabled worlds
        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
            return false;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (shouldIgnoreItem(item)) {
            return false;
        }
        // Check permission
        User user = plugin.getUser(player);
        return user.hasSkillPermission(manaAbility.getSkill());
    }

    private boolean isBlockPlace(PlayerInteractEvent event, Player player, ManaAbility manaAbility) {
        if (manaAbility.optionBoolean("check_offhand", true)) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (player.isSneaking() && manaAbility.optionBoolean("sneak_offhand_bypass", true)) {
                    return false;
                }
                ItemStack item = player.getInventory().getItemInOffHand();
                if (item.getType() == Material.AIR) return false;
                return item.getType().isBlock();
            }
        }
        return false;
    }

    protected static class AbilityBlocks {

        private final UUID id;
        private final Block originalBlock;
        private int blocksBroken;
        private int maxBlocks;

        public AbilityBlocks(Block originalBlock, BlockXpSource source) {
            this.id = UUID.randomUUID();
            this.originalBlock = originalBlock;
            setMaxBlocks(source);
        }

        public UUID getId() {
            return id;
        }

        public Block getOriginalBlock() {
            return originalBlock;
        }

        public int getBlocksBroken() {
            return blocksBroken;
        }

        public void incrementBlocksBroken() {
            blocksBroken++;
        }

        public int getMaxBlocks() {
            return maxBlocks;
        }

        public void setMaxBlocks(int maxBlocks) {
            this.maxBlocks = maxBlocks;
        }

        private void setMaxBlocks(BlockXpSource source) {
            int maxBlocks = (source != null && source.getMaxBlocks() >= 1) ? source.getMaxBlocks() : BlockSource.DEFAULT_MAX_BLOCKS;
            double multiplier = ManaAbilities.TREECAPITATOR.optionDouble("max_blocks_multiplier", 1.0);
            this.maxBlocks = (int) (maxBlocks * multiplier);
        }

    }

    public static class AbilityBlocksRegistry {
        private final Map<UUID, AbilityBlocks> blocks = new HashMap<>();

        public AbilityBlocksRegistry(AbilityBlocks abilityBlocks) {
            put(abilityBlocks);
        }

        public void put(AbilityBlocks abilityBlocks) {
            blocks.put(abilityBlocks.getId(), abilityBlocks);
        }

        public Collection<AbilityBlocks> getValues() {
            return blocks.values();
        }

        public boolean hasValue(AbilityBlocks abilityBlocks) {
            return blocks.containsKey(abilityBlocks.getId());
        }

        public AbilityBlocks get(AbilityBlocks abilityBlocks) {
            return blocks.get(abilityBlocks.getId());
        }

        public AbilityBlocks remove(AbilityBlocks abilityBlocks) {
            return blocks.remove(abilityBlocks.getId());
        }

    }

}
