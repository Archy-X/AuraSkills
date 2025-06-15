package dev.aurelium.auraskills.bukkit.loot.handler;

import dev.aurelium.auraskills.api.event.loot.LootDropEvent;
import dev.aurelium.auraskills.api.event.loot.LootDropEvent.Cause;
import dev.aurelium.auraskills.api.loot.*;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.hooks.WorldGuardFlags.FlagKey;
import dev.aurelium.auraskills.bukkit.hooks.WorldGuardHook;
import dev.aurelium.auraskills.bukkit.loot.context.MobContext;
import dev.aurelium.auraskills.bukkit.loot.type.EntityLoot;
import dev.aurelium.auraskills.bukkit.loot.type.ItemLoot;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.common.commands.CommandExecutor;
import dev.aurelium.auraskills.common.hooks.PlaceholderHook;
import dev.aurelium.auraskills.common.loot.AbstractLootHandler;
import dev.aurelium.auraskills.common.loot.CommandLoot;
import dev.aurelium.auraskills.common.loot.SourceContext;
import dev.aurelium.auraskills.common.message.MessageKey;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import dev.aurelium.slate.text.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static dev.aurelium.auraskills.bukkit.ref.BukkitItemRef.unwrap;

public abstract class LootHandler extends AbstractLootHandler {

    protected final AuraSkills plugin;
    private final TextFormatter tf = new TextFormatter();

    public LootHandler(AuraSkills plugin) {
        this.plugin = plugin;
    }

    protected void giveCommandLoot(Player player, CommandLoot loot, @Nullable XpSource source, Skill skill) {
        // Apply placeholders to command
        User user = plugin.getUser(player);
        for (String command : loot.getCommands()) {
            String finalCommand = TextUtil.replace(command, "{player}", player.getName());
            if (plugin.getHookManager().isRegistered(PlaceholderHook.class)) {
                finalCommand = plugin.getHookManager().getHook(PlaceholderHook.class).setPlaceholders(user, finalCommand);
            }
            // Execute command
            CommandExecutor executor = loot.getExecutor();
            if (executor == CommandExecutor.CONSOLE) {
                Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), finalCommand);
            } else if (executor == CommandExecutor.PLAYER) {
                Bukkit.dispatchCommand(player, finalCommand);
            }
        }
        attemptSendMessage(player, loot);
        giveXp(player, loot, source, skill);
    }

    protected void giveBlockItemLoot(Player player, ItemLoot loot, BlockBreakEvent breakEvent, Skill skill, LootDropCause cause, LootTable table) {
        Block block = breakEvent.getBlock();
        ItemStack drop = generateDamaged(unwrap(loot.getItem().supplyItem(plugin, table)), loot.getMinDamage(), loot.getMaxDamage());
        drop.setAmount(generateAmount(loot.getMinAmount(), loot.getMaxAmount()));
        Location location = block.getLocation().add(0.5, 0.5, 0.5);

        giveDropItemLoot(player, location, cause, drop);

        attemptSendMessage(player, loot);
        giveXp(player, loot, null, skill);
    }

    protected void giveMobItemLoot(Player player, ItemLoot loot, Location location, Skill skill, LootDropCause cause, LootTable table) {
        ItemStack drop = generateDamaged(unwrap(loot.getItem().supplyItem(plugin, table)), loot.getMinDamage(), loot.getMaxDamage());
        drop.setAmount(generateAmount(loot.getMinAmount(), loot.getMaxAmount()));

        giveDropItemLoot(player, location, cause, drop);

        attemptSendMessage(player, loot);
        giveXp(player, loot, null, skill);
    }

    private void giveDropItemLoot(Player player, Location location, LootDropCause cause, ItemStack drop) {
        boolean toInventory = plugin.getLootManager().toInventory(player.getInventory().getItemInMainHand());

        LootDropEvent dropEvent = new LootDropEvent(player, plugin.getUser(player).toApi(), drop, location, (Cause) cause, toInventory);
        Bukkit.getPluginManager().callEvent(dropEvent);

        if (dropEvent.isCancelled()) return;

        ItemUtils.giveBlockLoot(player, dropEvent);
    }

    protected void giveFishingItemLoot(Player player, ItemLoot loot, PlayerFishEvent event, @Nullable XpSource source, Skill skill, LootDropCause cause, LootTable table) {
        if (!(event.getCaught() instanceof Item itemEntity)) return;

        int amount = generateAmount(loot.getMinAmount(), loot.getMaxAmount());
        if (amount == 0) return;

        ItemStack drop = generateDamaged(unwrap(loot.getItem().supplyItem(plugin, table)), loot.getMinDamage(), loot.getMaxDamage());
        drop.setAmount(amount);

        LootDropEvent dropEvent = new LootDropEvent(player, plugin.getUser(player).toApi(), drop, event.getHook().getLocation(), (Cause) cause, false);
        Bukkit.getPluginManager().callEvent(dropEvent);

        if (dropEvent.isCancelled()) return;

        itemEntity.setItemStack(dropEvent.getItem());
        attemptSendMessage(player, loot);
        giveXp(player, loot, source, skill);
    }

    protected void giveFishingEntityLoot(Player player, EntityLoot loot, PlayerFishEvent event, @Nullable XpSource source, Skill skill, LootDropEvent.Cause cause) {
        if (!(event.getCaught() instanceof Item itemEntity)) return;

        Location location = event.getHook().getLocation();
        Entity entity = loot.getEntity().spawnEntity(plugin, event.getHook().getLocation());

        if (entity == null) return;

        LootDropEvent dropEvent = new LootDropEvent(player, plugin.getUser(player).toApi(), entity, event.getHook().getLocation(), cause);
        Bukkit.getPluginManager().callEvent(dropEvent);

        if (dropEvent.isCancelled()) {
            loot.getEntity().removeEntity(entity);
            return;
        }

        itemEntity.setItemStack(new ItemStack(Material.AIR));

        Float hVelocity = loot.getEntity().getEntityProperties().horizontalVelocity();
        if (hVelocity == null) hVelocity = 1.2f;

        Float vVelocity = loot.getEntity().getEntityProperties().verticalVelocity();
        if (vVelocity == null) vVelocity = 1.3f;

        Vector vector = player.getLocation().subtract(location).toVector().multiply(hVelocity - 1);
        vector.setY((vector.getY() + 0.2) * vVelocity);
        entity.setVelocity(vector);

        attemptSendMessage(player, loot);
        giveXp(player, loot, source, skill);
    }

    @Nullable
    protected Loot selectLoot(LootPool pool, @NotNull LootContext providedContext) {
        return pool.rollLoot(loot -> {
            if (providedContext instanceof SourceContext(XpSource providedSource)) {
                Set<LootContext> lootContexts = loot.getValues().getContexts().get("sources");
                // Make sure the loot defines a sources context and the provided context exists
                if (lootContexts != null && providedSource != null) {
                    boolean matched = false;
                    for (LootContext context : lootContexts) { // Go through LootContext and cast to Source
                        if (context instanceof SourceContext(XpSource configuredSource)) {
                            if (configuredSource.equals(providedSource)) { // Check if source matches one of the contexts
                                matched = true;
                                break;
                            }
                        }
                    }
                    return matched;
                }
            } else if (providedContext instanceof MobContext(EntityType providedType)) {
                Set<LootContext> lootContexts = loot.getValues().getContexts().get("mobs");
                if (lootContexts != null && providedType != null) {
                    boolean matched = false;
                    for (LootContext context : lootContexts) {
                        if (context instanceof MobContext(EntityType configuredType)) {
                            if (configuredType.equals(providedType)) {
                                matched = true;
                            }
                        }
                    }
                    return matched;
                }
            }
            return true;
        }).orElse(null);
    }

    private void giveXp(Player player, Loot loot, @Nullable XpSource source, Skill skill) {
        if (plugin.getHookManager().isRegistered(WorldGuardHook.class)) {
            // Check generic xp-gain and skill-specific flags
            if (plugin.getHookManager().getHook(WorldGuardHook.class).isBlocked(player.getLocation(), player, skill)) {
                return;
            }
        }

        User user = plugin.getUser(player);
        Object xpObj = loot.getValues().getOptions().get("xp");

        double xp;
        if (xpObj instanceof Integer) {
            xp = (int) xpObj;
        } else if (xpObj instanceof Double) {
            xp = (double) xpObj;
        } else {
            xp = -1.0;
        }
        if (xp == -1.0 && source != null) { // Xp not specified
            plugin.getLevelManager().addXp(user, skill, source, source.getXp());
        } else if (xp > 0) { // Xp explicitly specified
            plugin.getLevelManager().addXp(user, skill, source, xp);
        }
    }

    private ItemStack generateDamaged(ItemStack drop, double minDamage, double maxDamage) {
        if (minDamage >= 0.0 && minDamage <= 1.0 &&
                maxDamage >= 0.0 && maxDamage <= 1.0 &&
                minDamage <= maxDamage) {

            // Check if the item is damageable.
            if (drop == null) {
                return drop;
            }

            ItemMeta meta = drop.getItemMeta();
            if (meta instanceof Damageable damageable) {
                int damage = 0; // Default to 0 damage
                short durability = drop.getType().getMaxDurability();
                int minDamageValue = (int) (durability * minDamage); // E.g. 1561 * 0.0 = 0 -> resulting in an undamaged item.
                int maxDamageValue = (int) (durability * maxDamage); // E.g. 1561 * 0.5 = 780 -> resulting in a max 50% damaged item.

                if (minDamage == maxDamage) {
                    damage = maxDamageValue;
                } else {
                    damage = ThreadLocalRandom.current().nextInt(minDamageValue, maxDamageValue);
                }

                damageable.setDamage(damage);
                drop.setItemMeta(meta);
            }
        }

        return drop;
    }

    private void attemptSendMessage(Player player, Loot loot) {
        String message = loot.getValues().getMessage();
        if (message == null || message.isEmpty()) {
            return;
        }
        User user = plugin.getUser(player);

        Locale locale = user.getLocale();
        // Try to get message as message key
        MessageKey messageKey = MessageKey.of(message);
        String keyedMessage = plugin.getMessageProvider().getOrNull(messageKey, locale);
        if (keyedMessage != null) {
            message = keyedMessage;
        }
        // Replace placeholders
        if (plugin.getHookManager().isRegistered(PlaceholderHook.class)) {
            message = plugin.getHookManager().getHook(PlaceholderHook.class).setPlaceholders(user, message);
        }
        user.sendMessage(tf.toComponent(message));
    }

    protected boolean failsChecks(Player player, Location location, boolean disableInCreative) {
        if (disableInCreative && player.getGameMode() == GameMode.CREATIVE) { // Only drop loot in survival mode
            return true;
        }

        if (plugin.getWorldManager().isInDisabledWorld(location)) return true;

        if (plugin.getHookManager().isRegistered(WorldGuardHook.class)) {
            return plugin.getHookManager().getHook(WorldGuardHook.class).isBlocked(location, player, FlagKey.CUSTOM_LOOT);
        }
        return false;
    }

}
