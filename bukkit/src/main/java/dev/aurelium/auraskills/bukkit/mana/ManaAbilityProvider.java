package dev.aurelium.auraskills.bukkit.mana;

import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.event.mana.ManaAbilityActivateEvent;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.mana.ManaAbilityData;
import dev.aurelium.auraskills.common.message.type.ManaAbilityMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public abstract class ManaAbilityProvider implements Listener {

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

    protected boolean checkActivation(Player player) {
        User user = plugin.getUser(player);

        ManaAbilityData data = user.getManaAbilityData(manaAbility);

        // Return if not ready or already activated
        if (!isReady(user) || data.isActivated()) {
            return false;
        }

        int duration = getDuration(user);
        double manaCost = getManaCost(user);

        // Check that player has enough mana
        if (!hasEnoughMana(player, user, manaCost)) {
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
            plugin.getScheduler().scheduleSync(() -> stop(player, user, data), duration * 50L, TimeUnit.MILLISECONDS);
        } else {
            stop(player, user, data);
        }
        return true;
    }

    protected void stop(Player player, User user, ManaAbilityData data) {
        data.setActivated(false);
        data.setReady(false);

        onStop(player, user); // Run mana ability specific stop behavior

        data.setCooldown(getCooldownTicks(user));

        if (stopMessage != null) {
            plugin.getAbilityManager().sendMessage(player, plugin.getMsg(stopMessage, user.getLocale()));
        }
    }

    protected boolean isDisabled() {
        return !manaAbility.getSkill().isEnabled() || !manaAbility.isEnabled();
    }

    protected boolean failsChecks(Player player) {
        if (plugin.getUser(player).getManaAbilityLevel(manaAbility) <= 0) {
            return true;
        }
        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
            return true;
        }
        if (!player.hasPermission("auraskills.skill." + manaAbility.getSkill().name().toLowerCase(Locale.ROOT))) {
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

    protected int getDuration(User user) {
        return (int) Math.round(manaAbility.getValue(user.getManaAbilityLevel(manaAbility)) * 20);
    }

    protected void consumeMana(Player player, User user, double manaConsumed) {
        user.setMana(user.getMana() - manaConsumed);

        plugin.getAbilityManager().sendMessage(player, TextUtil.replace(plugin.getMsg(activateMessage, user.getLocale())
                ,"{mana}", NumberUtil.format0(manaConsumed)));
    }

    public boolean hasEnoughMana(Player player, User user, double manaCost) {
        Locale locale = user.getLocale();
        if (user.getMana() >= manaCost) {
            return true;
        } else {
            plugin.getAbilityManager().sendMessage(player, TextUtil.replace(plugin.getMsg(ManaAbilityMessage.NOT_ENOUGH_MANA, locale)
                    ,"{mana}", NumberUtil.format0(manaCost)
                    , "{current_mana}", String.valueOf(Math.round(user.getMana()))
                    , "{max_mana}", String.valueOf(Math.round(user.getMaxMana()))));
            return false;
        }
    }

    public double getManaCost(User user) {
        return manaAbility.getManaCost(user.getManaAbilityLevel(manaAbility));
    }

    private int getCooldownTicks(User user) {
        return (int) manaAbility.getCooldown(user.getManaAbilityLevel(manaAbility)) * 20;
    }

}
