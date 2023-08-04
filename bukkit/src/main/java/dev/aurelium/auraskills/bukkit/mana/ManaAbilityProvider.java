package dev.aurelium.auraskills.bukkit.mana;

import dev.aurelium.auraskills.api.event.mana.ManaAbilityActivateEvent;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.mana.ManaAbilityData;
import dev.aurelium.auraskills.common.message.type.ManaAbilityMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.math.NumberUtil;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public abstract class ManaAbilityProvider {

    private final AuraSkills plugin;
    private final ManaAbility manaAbility;
    private final ManaAbilityMessage activateMessage;
    @Nullable
    private final ManaAbilityMessage stopMessage;

    ManaAbilityProvider(AuraSkills plugin, ManaAbility manaAbility, ManaAbilityMessage activateMessage, @Nullable ManaAbilityMessage stopMessage) {
        this.plugin = plugin;
        this.manaAbility = manaAbility;
        this.activateMessage = activateMessage;
        this.stopMessage = stopMessage;
    }

    public abstract void onActivate(Player player, User user);

    public abstract void onStop(Player player, User user);

    protected void checkActivation(Player player) {
        User user = plugin.getUser(player);

        ManaAbilityData data = user.getManaAbilityData(manaAbility);

        // Return if not ready or already activated
        if (!isReady(data) || data.isActivated()) {
            return;
        }

        int duration = getDuration(user);
        double manaCost = getManaCost(user);

        // Check that player has enough mana
        if (!hasEnoughMana(player, user, manaCost)) {
            return;
        }

        ManaAbilityActivateEvent event = new ManaAbilityActivateEvent(plugin.getApi(), user.toApi(), manaAbility, duration, manaCost);
        plugin.getEventManager().callEvent(event);
        if (event.isCancelled()) return;

        data.setActivated(true);

        onActivate(player, user); // Mana ability specific behavior is run
        consumeMana(player, user, manaCost);

        if (duration != 0) {
            //Schedules stop
            plugin.getScheduler().scheduleSync(() -> {
                stop(player, user, data);
            }, duration * 50L, TimeUnit.MILLISECONDS);
        } else {
            stop(player, user, data);
        }
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

    protected boolean isReady(ManaAbilityData data) {
        return true;
    }

    private int getDuration(User user) {
        return (int) Math.round(manaAbility.getValue(user.getManaAbilityLevel(manaAbility)) * 20);
    }

    private void consumeMana(Player player, User user, double manaConsumed) {
        user.setMana(user.getMana() - manaConsumed);

        plugin.getAbilityManager().sendMessage(player, TextUtil.replace(plugin.getMsg(activateMessage, user.getLocale())
                ,"{mana}", NumberUtil.format0(manaConsumed)));
    }

    private boolean hasEnoughMana(Player player, User user, double manaCost) {
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

    private double getManaCost(User user) {
        return manaAbility.getManaCost(user.getManaAbilityLevel(manaAbility));
    }

    private int getCooldownTicks(User user) {
        return (int) manaAbility.getCooldown(user.getManaAbilityLevel(manaAbility)) * 20;
    }

}
