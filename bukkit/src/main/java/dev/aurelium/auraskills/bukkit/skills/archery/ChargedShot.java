package dev.aurelium.auraskills.bukkit.skills.archery;

import dev.aurelium.auraskills.api.damage.DamageMeta;
import dev.aurelium.auraskills.api.damage.DamageType;
import dev.aurelium.auraskills.api.event.damage.DamageEvent;
import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.mana.ManaAbilityProvider;
import dev.aurelium.auraskills.common.ability.AbilityData;
import dev.aurelium.auraskills.common.mana.ManaAbilityData;
import dev.aurelium.auraskills.common.message.type.ManaAbilityMessage;
import dev.aurelium.auraskills.api.damage.DamageModifier;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ChargedShot extends ManaAbilityProvider {

    public ChargedShot(AuraSkills plugin) {
        super(plugin, ManaAbilities.CHARGED_SHOT, ManaAbilityMessage.CHARGED_SHOT_SHOOT, null);
        tickChargedShotCooldown();
    }

    @Override
    public void onActivate(Player player, User user) {
        // Calculate damage increase
        double manaConsumed = getManaCost(user);
        double damagePercent = manaConsumed * getValue(user);
        // Add meta to entity
        Object obj = user.getMetadata().get("charged_shot_projectile");
        if (!(obj instanceof Entity projectile)) return;

        projectile.setMetadata("ChargedShotMultiplier", new FixedMetadataValue(plugin, 1 + damagePercent / 100));
        // Play sound
        if (manaAbility.optionBoolean("enable_sound", true)) {
            player.playSound(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 0.5f, 1);
        }
    }

    @Override
    public void onStop(Player player, User user) {
        user.getMetadata().remove("charged_shot_projectile");
        user.getMetadata().remove("charged_shot_force");
    }

    @EventHandler
    public void onToggle(PlayerInteractEvent event) {
        if (isDisabled()) return;

        Player player = event.getPlayer();
        if (failsChecks(player)) return;

        // Disable toggling when always enabled
        if (manaAbility.optionBoolean("always_enabled", false)) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null) return;
        if (item.getType() != Material.BOW) return;
        if (shouldIgnoreItem(item)) return;
        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
            User user = plugin.getUser(player);

            Locale locale = user.getLocale();
            AbilityData abilityData = user.getAbilityData(manaAbility);
            if (abilityData.getInt("toggle_cooldown") == 0) {
                if (!abilityData.getBoolean("enabled")) { // Toggle on
                    abilityData.setData("enabled", true);
                    plugin.getAbilityManager().sendMessage(player, plugin.getMsg(ManaAbilityMessage.CHARGED_SHOT_ENABLE, locale));
                } else { // Toggle off
                    abilityData.setData("enabled", false);
                    plugin.getAbilityManager().sendMessage(player, plugin.getMsg(ManaAbilityMessage.CHARGED_SHOT_DISABLE, locale));
                }
                abilityData.setData("toggle_cooldown", 8);
            }
        }
    }

    private void tickChargedShotCooldown() {
        var task = new TaskRunnable() {
            @Override
            public void run() {
                for (User user : plugin.getUserManager().getOnlineUsers()) {
                    AbilityData abilityData = user.getAbilityData(manaAbility);
                    int cooldown = abilityData.getInt("toggle_cooldown");
                    if (cooldown != 0) {
                        abilityData.setData("toggle_cooldown", cooldown - 1);
                    }
                }
            }
        };
        plugin.getScheduler().timerSync(task, 3 * 50, 5 * 50, TimeUnit.MILLISECONDS);
    }

    @EventHandler
    public void activationListener(EntityShootBowEvent event) {
        if (isDisabled()) return;

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (failsChecks(player)) return;

        User user = plugin.getUser(player);

        if (!user.getAbilityData(manaAbility).getBoolean("enabled") && !manaAbility.optionBoolean("always_enabled", false)) {
            return;
        }

        ManaAbilityData data = user.getManaAbilityData(manaAbility);

        int cooldown = data.getCooldown();
        if (cooldown == 0) {
            user.getMetadata().put("charged_shot_projectile", event.getProjectile());
            user.getMetadata().put("charged_shot_force", event.getForce());
            checkActivation(player);
        } else {
            if (data.getErrorTimer() == 0) {
                Locale locale = user.getLocale();
                plugin.getAbilityManager().sendMessage(player, TextUtil.replace(plugin.getMsg(ManaAbilityMessage.NOT_READY, locale), "{cooldown}", NumberUtil.format1((double) (cooldown) / 20)));
                data.setErrorTimer(2);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void damageListener(DamageEvent event) {
        DamageMeta meta = event.getDamageMeta();
        Entity attacker = meta.getAttacker();

        if (attacker != null && meta.getDamageType() == DamageType.BOW) {
            meta.addAttackModifier(applyChargedShot(attacker));
        }
    }

    private DamageModifier applyChargedShot(Entity attacker) {
        if (!attacker.hasMetadata("ChargedShotMultiplier")) {
            return DamageModifier.none();
        }
        double multiplier = attacker.getMetadata("ChargedShotMultiplier").get(0).asDouble();
        return new DamageModifier(multiplier - 1.0, DamageModifier.Operation.ADD_COMBINED);
    }

    @Override
    protected void consumeMana(Player player, User user, double manaConsumed) {
        if (manaConsumed <= 0) return;
        double damagePercent = manaConsumed * manaAbility.getValue(user.getManaAbilityLevel(manaAbility));
        user.setMana(user.getMana() - manaConsumed);

        if (manaAbility.optionBoolean("enable_message", true)) {
            String manaStr = manaConsumed < 1 ? NumberUtil.format1(manaConsumed) : NumberUtil.format0(manaConsumed);
            String damagePercentStr = damagePercent < 1 ? NumberUtil.format1(damagePercent) : NumberUtil.format0(damagePercent);
            plugin.getAbilityManager().sendMessage(player, TextUtil.replace(plugin.getMsg(getActivateMessage(), user.getLocale()),
                    "{mana}", manaStr,
                    "{percent}", damagePercentStr));
        }
    }

    @Override
    public double getManaCost(User user) {
        Object obj = user.getMetadata().get("charged_shot_force");
        float force = 0;
        if (obj instanceof Float) {
            force = (float) obj;
        }
        return Math.min(manaAbility.getManaCost(user.getManaAbilityLevel(manaAbility)) * force, user.getMana());
    }

    @Override
    protected int getDuration(User user) {
        return 0;
    }
}
