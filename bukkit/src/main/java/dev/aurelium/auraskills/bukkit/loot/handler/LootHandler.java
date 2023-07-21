package dev.aurelium.auraskills.bukkit.loot.handler;

import com.archyx.lootmanager.loot.Loot;
import com.archyx.lootmanager.loot.LootPool;
import com.archyx.lootmanager.loot.context.LootContext;
import com.archyx.lootmanager.loot.type.CommandLoot;
import com.archyx.lootmanager.loot.type.ItemLoot;
import com.archyx.lootmanager.util.CommandExecutor;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.loot.SourceContextWrapper;
import dev.aurelium.auraskills.common.hooks.PlaceholderHook;
import dev.aurelium.auraskills.common.message.MessageKey;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class LootHandler {

    protected final AuraSkills plugin;
    private final Random random = new Random();

    public LootHandler(AuraSkills plugin) {
        this.plugin = plugin;
    }

    protected void giveCommandLoot(Player player, CommandLoot loot, XpSource source, Skill skill) {
        // Apply placeholders to command
        String finalCommand = TextUtil.replace(loot.getCommand(), "{player}", player.getName());
        User user = plugin.getUser(player);
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
        attemptSendMessage(player, loot);
        giveXp(player, loot, source, skill);
    }

    protected void giveBlockItemLoot(Player player, ItemLoot loot, BlockBreakEvent event, @Nullable XpSource source, Skill skill) {
        Block block = event.getBlock();
        ItemStack drop = loot.getItem().clone();
        drop.setAmount(generateAmount(loot.getMinAmount(), loot.getMaxAmount()));
        Location location = block.getLocation().add(0.5, 0.5, 0.5);
        // TODO Add PlayerLootDropEvent
        block.getWorld().dropItem(location, drop);
        attemptSendMessage(player, loot);
        giveXp(player, loot, source, skill);
    }

    protected void giveFishingItemLoot(Player player, ItemLoot loot, PlayerFishEvent event, @Nullable XpSource source, Skill skill) {
        if (!(event.getCaught() instanceof Item itemEntity)) return;

        int amount = generateAmount(loot.getMinAmount(), loot.getMaxAmount());
        if (amount == 0) return;

        ItemStack drop = loot.getItem().clone();
        drop.setAmount(amount);
        // TODO Add PlayerLootDropEvent
        itemEntity.setItemStack(drop);
        attemptSendMessage(player, loot);
        giveXp(player, loot, source, skill);
    }

    @Nullable
    protected Loot selectLoot(LootPool pool, @Nullable XpSource source) {
        List<Loot> lootList = new ArrayList<>();
        // Add applicable loot to list for selection
        for (Loot loot : pool.getLoot()) {
            Set<LootContext> sourcesContext = loot.getContexts().get("sources");
            if (sourcesContext != null && source != null) {
                for (LootContext context : sourcesContext) { // Go through LootContext and cast to Source
                    if (context instanceof SourceContextWrapper wrapper) {
                        if (wrapper.getSource().equals(source)) { // Check if source matches one of the contexts
                            lootList.add(loot);
                            break;
                        }
                    }
                }
            } else {
                lootList.add(loot);
            }
        }
        // Loot selected based on weight
        int totalWeight = 0;
        for (Loot loot : lootList) {
            totalWeight += loot.getWeight();
        }
        if (totalWeight == 0) { // Don't attempt selection if no loot entries are applicable
            return null;
        }
        int selected = random.nextInt(totalWeight);
        int currentWeight = 0;
        Loot selectedLoot = null;
        for (Loot loot : lootList) {
            if (selected >= currentWeight && selected < currentWeight + loot.getWeight()) {
                selectedLoot = loot;
                break;
            }
            currentWeight += loot.getWeight();
        }
        return selectedLoot;
    }

    private void giveXp(Player player, Loot loot, @Nullable XpSource source, Skill skill) {
        User user = plugin.getUser(player);
        double xp = loot.getOption("xp", Double.class, -1.0);
        if (xp == -1.0 && source != null) { // Xp not specified
            plugin.getLevelManager().addXp(user, skill, source.getXp());
        } else if (xp > 0) { // Xp explicitly specified
            plugin.getLevelManager().addXp(user, skill, xp);
        }
    }

    private int generateAmount(int minAmount, int maxAmount) {
        return new Random().nextInt(maxAmount - minAmount + 1) + minAmount;
    }

    private void attemptSendMessage(Player player, Loot loot) {
        String message = loot.getMessage();
        if (message == null || message.equals("")) {
            return;
        }
        User user = plugin.getUser(player);

        Locale locale = user.getLocale();
        // Try to get message as message key
        MessageKey messageKey = MessageKey.of(message);
        String finalMessage = plugin.getMsg(messageKey, locale);
        // Replace placeholders
        if (plugin.getHookManager().isRegistered(PlaceholderHook.class)) {
            finalMessage = plugin.getHookManager().getHook(PlaceholderHook.class).setPlaceholders(user, finalMessage);
        }
        player.sendMessage(finalMessage);
    }

    protected double getCommonChance(LootPool pool, User user) {
        double chancePerLuck = pool.getOption("chance_per_luck", Double.class, 0.0) / 100;
        return pool.getBaseChance() + chancePerLuck * user.getStatLevel(Stats.LUCK);
    }

    protected double getAbilityModifiedChance(double chance, Ability ability, User user) {
        // Check option to scale base chance
        if (ability.optionBoolean("scale_base_chance", false)) {
            chance *= 1 + (ability.getValue(user.getAbilityLevel(ability)) / 100);
        } else { // Otherwise add to base chance
            chance += (ability.getValue(user.getAbilityLevel(ability)) / 100);
        }
        return chance;
    }

}
