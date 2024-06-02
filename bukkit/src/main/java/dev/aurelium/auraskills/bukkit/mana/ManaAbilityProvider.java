package dev.aurelium.auraskills.bukkit.mana;

import dev.aurelium.auraskills.api.event.mana.ManaAbilityActivateEvent;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.util.VersionUtils;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.mana.ManaAbilityData;
import dev.aurelium.auraskills.common.message.type.ManaAbilityMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public abstract class ManaAbilityProvider implements Listener {

    public static final String IGNORE_INTERACT_KEY = "ignore_interact";
    protected final AuraSkills plugin;
    protected final ManaAbility manaAbility;
    private final ManaAbilityMessage activateMessage;
    @Nullable
    private final ManaAbilityMessage stopMessage;

    public ManaAbilityProvider(AuraSkills plugin, ManaAbility manaAbility, ManaAbilityMessage activateMessage, @Nullable ManaAbilityMessage stopMessage) {
        this.plugin = plugin;
        this.manaAbility = manaAbility;
        this.activateMessage = activateMessage;
        this.stopMessage = stopMessage;
    }

    public ManaAbility getManaAbility() {
        return manaAbility;
    }

    public ManaAbilityMessage getActivateMessage() {
        return activateMessage;
    }

    public abstract void onActivate(Player player, User user);

    public abstract void onStop(Player player, User user);

    public String replaceDescPlaceholders(String input, User user) {
        return input;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // Stop active mana ability when logging out
        stop(player);
    }

    protected boolean checkActivation(Player player) {
        User user = plugin.getUser(player);

        ManaAbilityData data = user.getManaAbilityData(manaAbility);

        // Return if not ready or already activated
        if (!isReady(user) || data.isActivated()) {
            return false;
        }

        int duration = getDuration(user); // In ticks
        double manaCost = getManaCost(user);

        // Check that player has enough mana
        if (insufficientMana(user, manaCost)) {
            return false;
        }

        ManaAbilityActivateEvent event = new ManaAbilityActivateEvent(player, user.toApi(), manaAbility, duration, manaCost);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        data.setActivated(true);

        onActivate(player, user); // Mana ability specific behavior is run
        consumeMana(player, user, event.getManaUsed());

        if (duration != 0) {
            //Schedules stop
            plugin.getScheduler().scheduleSync(() -> stop(player), duration * 50L, TimeUnit.MILLISECONDS);
        } else {
            stop(player);
        }
        return true;
    }

    protected void stop(Player player) {
        // Ignore if offline
        if (!plugin.getUserManager().hasUser(player.getUniqueId())) return;

        User user = plugin.getUser(player);
        ManaAbilityData data = user.getManaAbilityData(manaAbility);

        if (!data.isActivated()) return;
        data.setActivated(false);
        data.setReady(false);

        onStop(player, user); // Run mana ability specific stop behavior

        data.setCooldown(getCooldownTicks(user));

        if (stopMessage != null) {
            plugin.getAbilityManager().sendMessage(player, plugin.getMsg(stopMessage, user.getLocale()));
        }
    }

    protected boolean isDisabled() {
        return !manaAbility.isEnabled() || !manaAbility.getSkill().isEnabled();
    }

    protected boolean failsChecks(Player player) {
        if (plugin.getUser(player).getManaAbilityLevel(manaAbility) <= 0) {
            return true;
        }
        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
            return true;
        }
        if (!plugin.getUser(player).hasSkillPermission(manaAbility.getSkill())) {
            return true;
        }
        if (plugin.configBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
            return player.getGameMode().equals(GameMode.CREATIVE);
        }
        return false;
    }

    protected boolean isReady(User user) {
        return true;
    }

    protected boolean isActivated(User user) {
        return user.getManaAbilityData(manaAbility).isActivated();
    }

    protected double getValue(User user) {
        return manaAbility.getValue(user.getManaAbilityLevel(manaAbility));
    }

    // Duration in ticks
    protected int getDuration(User user) {
        return (int) Math.round(manaAbility.getValue(user.getManaAbilityLevel(manaAbility)) * 20);
    }

    protected void consumeMana(Player player, User user, double manaConsumed) {
        if (plugin.configBoolean(Option.MANA_ENABLED)) {
            user.setMana(user.getMana() - manaConsumed);
        } else {
            manaConsumed = 0.0;
        }

        plugin.getAbilityManager().sendMessage(player, TextUtil.replace(plugin.getMsg(activateMessage, user.getLocale())
                ,"{mana}", NumberUtil.format0(manaConsumed)));
    }

    public boolean insufficientMana(User user, double manaCost) {
        if (user.getMana() >= manaCost || !plugin.configBoolean(Option.MANA_ENABLED)) {
            return false;
        } else {
            plugin.getManaAbilityManager().sendNotEnoughManaMessage(user, manaCost);
            return true;
        }
    }

    public double getManaCost(User user) {
        return manaAbility.getManaCost(user.getManaAbilityLevel(manaAbility));
    }

    private int getCooldownTicks(User user) {
        return (int) manaAbility.getCooldown(user.getManaAbilityLevel(manaAbility)) * 20;
    }

    protected boolean shouldIgnoreItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        var container = meta.getPersistentDataContainer();

        NamespacedKey key = new NamespacedKey(plugin, IGNORE_INTERACT_KEY);

        if (VersionUtils.isAtLeastVersion(20)) {
            // Convert old format from 2.1.0-2.1.1
            if (container.has(key, PersistentDataType.BOOLEAN)) {
                container.remove(key);
                container.set(key, PersistentDataType.BYTE, (byte) 1);
            }
        }

        return container.has(new NamespacedKey(plugin, IGNORE_INTERACT_KEY), PersistentDataType.BYTE);
    }

}
