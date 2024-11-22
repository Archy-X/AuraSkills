package dev.aurelium.auraskills.bukkit.loot.handler;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.event.loot.LootDropEvent;
import dev.aurelium.auraskills.api.loot.Loot;
import dev.aurelium.auraskills.api.loot.LootContext;
import dev.aurelium.auraskills.api.loot.LootPool;
import dev.aurelium.auraskills.api.loot.LootTable;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.hooks.WorldGuardFlags.FlagKey;
import dev.aurelium.auraskills.bukkit.hooks.WorldGuardHook;
import dev.aurelium.auraskills.bukkit.loot.context.MobContext;
import dev.aurelium.auraskills.bukkit.loot.context.SourceContext;
import dev.aurelium.auraskills.bukkit.loot.type.CommandLoot;
import dev.aurelium.auraskills.bukkit.loot.type.EntityLoot;
import dev.aurelium.auraskills.bukkit.loot.type.ItemLoot;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.common.commands.CommandExecutor;
import dev.aurelium.auraskills.common.hooks.PlaceholderHook;
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
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public abstract class LootHandler {

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

    protected void giveBlockItemLoot(Player player, ItemLoot loot, BlockBreakEvent breakEvent, Skill skill, LootDropEvent.Cause cause, LootTable table) {
        Block block = breakEvent.getBlock();
        ItemStack drop = loot.getItem().supplyItem(plugin, table);
        drop.setAmount(generateAmount(loot.getMinAmount(), loot.getMaxAmount()));
        Location location = block.getLocation().add(0.5, 0.5, 0.5);

        giveDropItemLoot(player, location, cause, drop);

        attemptSendMessage(player, loot);
        giveXp(player, loot, null, skill);
    }

    protected void giveMobItemLoot(Player player, ItemLoot loot, Location location, Skill skill, LootDropEvent.Cause cause, LootTable table) {
        ItemStack drop = loot.getItem().supplyItem(plugin, table);
        drop.setAmount(generateAmount(loot.getMinAmount(), loot.getMaxAmount()));

        giveDropItemLoot(player, location, cause, drop);

        attemptSendMessage(player, loot);
        giveXp(player, loot, null, skill);
    }

    private void giveDropItemLoot(Player player, Location location, LootDropEvent.Cause cause, ItemStack drop) {
        boolean toInventory = plugin.getLootTableManager().toInventory(player.getInventory().getItemInMainHand());

        LootDropEvent dropEvent = new LootDropEvent(player, plugin.getUser(player).toApi(), drop, location, cause, toInventory);
        Bukkit.getPluginManager().callEvent(dropEvent);

        if (dropEvent.isCancelled()) return;

        ItemUtils.giveBlockLoot(player, dropEvent);
    }

    protected void giveFishingItemLoot(Player player, ItemLoot loot, PlayerFishEvent event, @Nullable XpSource source, Skill skill, LootDropEvent.Cause cause, LootTable table) {
        if (!(event.getCaught() instanceof Item itemEntity)) return;

        int amount = generateAmount(loot.getMinAmount(), loot.getMaxAmount());
        if (amount == 0) return;

        ItemStack drop = loot.getItem().supplyItem(plugin, table);
        drop.setAmount(amount);

        LootDropEvent dropEvent = new LootDropEvent(player, plugin.getUser(player).toApi(), drop, event.getHook().getLocation(), cause, false);
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
            if (providedContext instanceof SourceContext sourceContext) {
                Set<LootContext> lootContexts = loot.getValues().getContexts().get("sources");
                // Make sure the loot defines a sources context and the provided context exists
                if (lootContexts != null && sourceContext.source() != null) {
                    boolean matched = false;
                    for (LootContext context : lootContexts) { // Go through LootContext and cast to Source
                        if (context instanceof SourceContext sourceLootContext) {
                            if (sourceLootContext.source().equals(sourceContext.source())) { // Check if source matches one of the contexts
                                matched = true;
                                break;
                            }
                        }
                    }
                    return matched;
                }
            } else if (providedContext instanceof MobContext mobContext) {
                Set<LootContext> lootContexts = loot.getValues().getContexts().get("mobs");
                if (lootContexts != null && mobContext.entityType() != null) {
                    boolean matched = false;
                    for (LootContext context : lootContexts) {
                        if (context instanceof MobContext mobLootContext) {
                            if (mobLootContext.entityType().equals(mobContext.entityType())) {
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

    private int generateAmount(int minAmount, int maxAmount) {
        return new Random().nextInt(maxAmount - minAmount + 1) + minAmount;
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

    public double getCommonChance(LootPool pool, User user) {
        double chancePerLuck = pool.getOption("chance_per_luck", Double.class, 0.0) / 100;
        return pool.getBaseChance() + chancePerLuck * user.getStatLevel(Stats.LUCK);
    }

    public double getAbilityModifiedChance(double chance, Ability ability, User user) {
        // Check option to scale base chance
        if (ability.optionBoolean("scale_base_chance", false)) {
            chance *= 1 + (ability.getValue(user.getAbilityLevel(ability)) / 100);
        } else { // Otherwise add to base chance
            chance += (ability.getValue(user.getAbilityLevel(ability)) / 100);
        }
        return chance;
    }

    protected boolean failsChecks(Player player, Location location) {
        if (player.getGameMode() == GameMode.CREATIVE) { // Only drop loot in survival mode
            return true;
        }

        if (plugin.getWorldManager().isInDisabledWorld(location)) return true;

        if (plugin.getHookManager().isRegistered(WorldGuardHook.class)) {
            return plugin.getHookManager().getHook(WorldGuardHook.class).isBlocked(location, player, FlagKey.CUSTOM_LOOT);
        }
        return false;
    }

    protected boolean isPoolUnobtainable(LootPool pool, XpSource source) {
        for (Loot loot : pool.getLoot()) {
            Set<LootContext> contexts = loot.getValues().getContexts().getOrDefault("sources", new HashSet<>());
            // Loot will be reachable if it has no contexts
            if (contexts.isEmpty()) {
                return false;
            }
            // Loot is reachable if at least one context matches the entity type
            for (LootContext context : contexts) {
                if (context instanceof SourceContext sourceContext) {
                    if (sourceContext.source().equals(source)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
