package dev.aurelium.auraskills.bukkit.skills.alchemy;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.bukkit.item.BukkitPotionType;
import dev.aurelium.auraskills.bukkit.skills.agility.AgilityAbilities;
import dev.aurelium.auraskills.bukkit.util.AttributeCompat;
import dev.aurelium.auraskills.bukkit.util.CompatUtil;
import dev.aurelium.auraskills.bukkit.util.PotionUtil;
import dev.aurelium.auraskills.common.message.type.AbilityMessage;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class AlchemyAbilities extends AbilityImpl {

    private final NamespacedKey DURATION_BONUS_KEY;

    public AlchemyAbilities(AuraSkills plugin) {
        super(plugin, Abilities.ALCHEMIST, Abilities.BREWER, Abilities.SPLASHER, Abilities.LINGERING, Abilities.WISE_EFFECT);
        this.DURATION_BONUS_KEY = new NamespacedKey(plugin, "duration_bonus");
        wiseEffect(); // Start Wise Effect timer task
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void alchemist(BrewEvent event) {
        var ability = Abilities.ALCHEMIST;

        if (isDisabled(ability)) return;

        if (event.isCancelled()) return;

        if (!event.getBlock().hasMetadata("skillsBrewingStandOwner")) return;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(event.getBlock().getMetadata("skillsBrewingStandOwner").get(0).asString()));
        if (!offlinePlayer.isOnline()) return;

        Player player = offlinePlayer.getPlayer();
        BrewerInventory inventory = event.getContents();
        if (player == null) return;

        if (failsChecks(player, ability)) return;

        User user = plugin.getUser(player);
        updateBrewingStand(inventory, user, user.getLocale());
    }

    private void updateBrewingStand(BrewerInventory inventory, User user, Locale locale) {
        var ability = Abilities.ALCHEMIST;

        plugin.getScheduler().scheduleSync(() -> {
            ItemStack[] contents = inventory.getContents();
            double multiplier = 1 + (getValue(ability, user) / 100);
            for (int i = 0; i < contents.length; i++) {
                ItemStack item = contents[i];
                if (item == null) continue;

                if (item.getItemMeta() instanceof PotionMeta) {
                    contents[i] = applyDurationData(item, multiplier, locale);
                }
            }
            inventory.setContents(contents);
        }, 50, TimeUnit.MILLISECONDS);
    }

    private boolean isApplicablePotion(PotionType potionType) {
        switch (potionType) {
            case AWKWARD, MUNDANE, THICK, WATER -> {
                return false;
            }
        }
        return switch (potionType.toString()) {
            // INSTANT_DAMAGE and INSTANT_HEAL are replaced by HARMING and HEALING in 1.20.5
            case "HARMING", "HEALING", "STRONG_HARMING", "STRONG_HEALING", "INSTANT_DAMAGE", "INSTANT_HEAL" -> false;
            default -> true;
        };
    }

    private ItemStack applyDurationData(ItemStack item, double multiplier, Locale locale) {
        PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
        if (potionMeta == null) {
            return item;
        }
        BukkitPotionType bukkitPotionType = new BukkitPotionType(potionMeta);

        PotionType potionType = bukkitPotionType.getType();
        if (potionType != null && !isApplicablePotion(potionType)) {
            return item;
        }
        int originalDuration = PotionUtil.getDuration(bukkitPotionType);
        int duration = (int) (originalDuration * multiplier); // Get duration in ticks
        int durationBonus = duration - originalDuration;

        ItemMeta meta = item.getItemMeta();
        if (duration == 0 || meta == null) {
            return item;
        }
        // Add duration to PersistentDataContainer
        var container = meta.getPersistentDataContainer();
        // Don't apply duration to the same item
        if (container.has(DURATION_BONUS_KEY, PersistentDataType.INTEGER)) {
            return item;
        }
        container.set(DURATION_BONUS_KEY, PersistentDataType.INTEGER, durationBonus);
        // Add lore
        if (Abilities.ALCHEMIST.optionBoolean("add_item_lore", true)) {
            List<String> lore = meta.getLore() != null ? meta.getLore() : new ArrayList<>();
            lore.add(TextUtil.replace(plugin.getMsg(AbilityMessage.ALCHEMIST_LORE, locale),
                    "{duration}", PotionUtil.formatDuration(durationBonus),
                    "{value}", NumberUtil.format1((multiplier - 1) * 100)));
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrink(PlayerItemConsumeEvent event) {
        var ability = Abilities.ALCHEMIST;

        if (isDisabled(ability)) return;

        Player player = event.getPlayer();

        if (event.isCancelled()) return;
        if (failsChecks(player, ability)) return;

        ItemStack item = event.getItem();
        if (item.getType() != Material.POTION || !(item.getItemMeta() instanceof PotionMeta meta)) return;

        int durationBonus = item.getItemMeta().getPersistentDataContainer().getOrDefault(DURATION_BONUS_KEY, PersistentDataType.INTEGER, 0);
        if (durationBonus <= 0) return;

        BukkitPotionType bukkitPotionType = new BukkitPotionType(meta);
        PotionType potionType = bukkitPotionType.getType();
        if (potionType == null) return;

        PotionEffectType effectType = potionType.getEffectType();
        if (effectType != null) {
            int duration = PotionUtil.getDuration(bukkitPotionType);
            if (!potionType.toString().contains("TURTLE_MASTER")) {
                // Get amplifier
                int amplifier = 0;
                if (bukkitPotionType.isUpgraded()) {
                    if (potionType.equals(PotionType.SLOWNESS)) {
                        amplifier = 3;
                    } else {
                        amplifier = 1;
                    }
                }
                // Apply effect
                if (effectType.equals(PotionEffectType.SPEED) || CompatUtil.isEffect(effectType, Set.of("jump_boost", "jump"))) {
                    var agilityAbilities = plugin.getAbilityManager().getAbilityImpl(AgilityAbilities.class);
                    PotionUtil.applyEffect(player, new PotionEffect(effectType, (int) ((duration + durationBonus) * agilityAbilities.getSugarRushSplashMultiplier(player)), amplifier));
                } else {
                    PotionUtil.applyEffect(player, new PotionEffect(effectType, duration + durationBonus, amplifier));
                }
            }
            // Special case for Turtle Master
            else {
                if (!bukkitPotionType.isUpgraded()) {
                    PotionUtil.applyEffect(player, new PotionEffect(CompatUtil.slowness(), duration + durationBonus, 3));
                    PotionUtil.applyEffect(player, new PotionEffect(CompatUtil.resistance(), duration + durationBonus, 2));
                } else {
                    PotionUtil.applyEffect(player, new PotionEffect(CompatUtil.slowness(), duration + durationBonus, 5));
                    PotionUtil.applyEffect(player, new PotionEffect(CompatUtil.resistance(), duration + durationBonus, 3));
                }
            }
        }
        // Apply bonus for custom effects
        for (PotionEffect effect : meta.getCustomEffects()) {
            PotionUtil.applyEffect(player, new PotionEffect(effect.getType(), effect.getDuration() + durationBonus, effect.getAmplifier()));
        }
    }

    // Handles duration boosts for splash potions. Includes Alchemist, Sugar Rush, and Splasher.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSplash(PotionSplashEvent event) {
        if (event.isCancelled()) return;

        ItemStack item = event.getPotion().getItem();
        if (!(item.getItemMeta() instanceof PotionMeta meta) || item.getItemMeta() == null) {
            return;
        }
        BukkitPotionType bukkitPotionType = new BukkitPotionType(meta);
        PotionType potionType = bukkitPotionType.getType();
        if (potionType == null) return;

        if (meta.hasCustomEffects() && Skills.ALCHEMY.optionBoolean("ignore_custom_potions", false)) return;
        // Get potion duration bonus from Alchemist ability
        int durationBonus = 0;
        if (!isDisabled(Abilities.ALCHEMIST)) {
            if (event.getPotion().getShooter() instanceof Player player) {
                if (!failsChecks(player, Abilities.ALCHEMIST)) {
                    durationBonus = meta.getPersistentDataContainer().getOrDefault(DURATION_BONUS_KEY, PersistentDataType.INTEGER, 0);
                }
            } else {
                durationBonus = meta.getPersistentDataContainer().getOrDefault(DURATION_BONUS_KEY, PersistentDataType.INTEGER, 0);
            }
        }
        // Add effects for each player
        for (PotionEffect effect : event.getPotion().getEffects()) {
            for (LivingEntity entity : event.getAffectedEntities()) {
                if (!(entity instanceof Player player)) {
                    continue;
                }
                // Calculate and get multipliers
                double splasherMultiplier = getSplasherMultiplier(event.getPotion().getShooter(), event.getAffectedEntities());
                double intensity = event.getIntensity(player);
                int duration = (int) ((PotionUtil.getDuration(bukkitPotionType) + durationBonus) * splasherMultiplier * intensity);
                // Apply normal effects
                if (!potionType.toString().contains("TURTLE_MASTER")) {
                    // Apply Sugar Rush
                    if (effect.getType().equals(PotionEffectType.SPEED) || CompatUtil.isEffect(effect.getType(), Set.of("jump_boost", "jump"))) {
                        var agilityAbilities = plugin.getAbilityManager().getAbilityImpl(AgilityAbilities.class);
                        PotionUtil.applyEffect(player, new PotionEffect(effect.getType(), (int) (duration * agilityAbilities.getSugarRushSplashMultiplier(player)), effect.getAmplifier()));
                    } else {
                        PotionUtil.applyEffect(player, new PotionEffect(effect.getType(), duration, effect.getAmplifier()));
                    }

                }
                // Special case for Turtle Master
                else {
                    if (!bukkitPotionType.isUpgraded()) {
                        PotionUtil.applyEffect(player, new PotionEffect(CompatUtil.slowness(), duration, 3));
                        PotionUtil.applyEffect(player, new PotionEffect(CompatUtil.resistance(), duration, 2));
                    } else {
                        PotionUtil.applyEffect(player, new PotionEffect(CompatUtil.slowness(), duration, 5));
                        PotionUtil.applyEffect(player, new PotionEffect(CompatUtil.resistance(), duration, 3));
                    }
                }
            }
        }
    }

    private double getSplasherMultiplier(ProjectileSource source, Collection<LivingEntity> affectedEntities) {
        var ability = Abilities.SPLASHER;
        double splasherMultiplier = 1.0;
        if (source instanceof Player player) {
            if (!isDisabled(ability) && !failsChecks(player, ability)) {
                User user = plugin.getUser(player);

                double splasherPercent = getValue(ability, user);
                int affectedPlayers = (int) affectedEntities.stream().filter(entity -> entity instanceof Player).filter(entity -> plugin.getUserManager().getUser(entity.getUniqueId()) != null).count();
                splasherMultiplier = 1 + (splasherPercent / 100 * affectedPlayers);
            }
        }
        return splasherMultiplier;
    }

    // Handles the Lingering ability
    @EventHandler
    public void lingering(LingeringPotionSplashEvent event) {
        var ability = Abilities.LINGERING;

        if (isDisabled(ability)) return;
        if (event.isCancelled()) return;

        Player player = null;
        if (event.getEntity().getShooter() instanceof Player) {
            player = (Player) event.getEntity().getShooter();
        }

        if (player == null) return;

        if (failsChecks(player, ability)) return;

        AreaEffectCloud cloud = event.getAreaEffectCloud();
        if (cloud.hasCustomEffects() && Skills.ALCHEMY.optionBoolean("ignore_custom_potions", false)) return;

        User user = plugin.getUser(player);
        // Get values
        double naturalDecay = 1 - (getValue(ability, user) / 100);
        double entityDecay = 1 - (getSecondaryValue(ability, user) / 100);
        // 1% limit
        if (naturalDecay <= 0.01) naturalDecay = 0.01;
        if (entityDecay <= 0.01) entityDecay = 0.01;
        // Apply values
        cloud.setRadiusPerTick(cloud.getRadiusPerTick() * (float) naturalDecay);
        cloud.setRadiusOnUse(cloud.getRadiusOnUse() * (float) entityDecay);
    }

    private void wiseEffect() {
        var ability = Abilities.WISE_EFFECT;
        var task = new TaskRunnable() {
            @Override
            public void run() {
                if (isDisabled(ability)) return;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    User user = plugin.getUser(player);

                    if (!player.getActivePotionEffects().isEmpty()) {
                        if (failsChecks(player, ability)) continue;
                        // Get unique active potion effects
                        Set<PotionEffectType> uniqueTypesSet = new HashSet<>();
                        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                            uniqueTypesSet.add(potionEffect.getType());
                        }
                        int uniqueTypes = uniqueTypesSet.size();
                        // Apply modifier
                        double wisdomPerType = getValue(ability, user);
                        double modifierValue = wisdomPerType * uniqueTypes;
                        if (modifierValue > 0.0) {
                            StatModifier modifier = new StatModifier("AbilityModifier-WiseEffect", Stats.WISDOM, modifierValue);
                            user.addStatModifier(modifier, true);
                        }
                    } else {
                        user.removeStatModifier("AbilityModifier-WiseEffect", true);
                    }
                }
            }
        };
        plugin.getScheduler().timerSync(task, 50, 10 * 50, TimeUnit.MILLISECONDS);
    }

    @EventHandler
    public void lifeSteal(EntityDeathEvent event) {
        var ability = Abilities.LIFE_STEAL;

        if (isDisabled(ability)) return;

        LivingEntity entity = event.getEntity();
        boolean hostile = entity instanceof Monster || entity instanceof Player || entity instanceof Phantom;

        if (!hostile) {
            return;
        }

        if (entity.getKiller() == null) return;
        Player player = entity.getKiller();
        if (player.equals(entity)) return;

        if (failsChecks(player, ability)) return;

        User user = plugin.getUser(player);

        AttributeInstance entityAttribute = entity.getAttribute(AttributeCompat.MAX_HEALTH);
        if (entityAttribute == null) return;

        // Get the health to regen from a percent of the mob's health
        double maxHealth = entityAttribute.getValue();
        double percent = getValue(ability, user) / 100;
        double healthRegen = maxHealth * percent;

        AttributeInstance playerAttribute = player.getAttribute(AttributeCompat.MAX_HEALTH);
        if (playerAttribute == null) return;

        player.setHealth(player.getHealth() + Math.min(healthRegen, playerAttribute.getValue() - player.getHealth()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void goldenHeart(EntityDamageEvent event) {
        var ability = Abilities.GOLDEN_HEART;

        if (isDisabled(ability)) return;

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (failsChecks(player, ability)) return;

        User user = plugin.getUser(player);

        if (player.getAbsorptionAmount() <= 0) {
            return;
        }

        double multiplier = 1 - getValue(ability, user) / 100;
        if (multiplier < 0.01) { // Cap at 99% reduction
            multiplier = 0.01;
        }
        event.setDamage(event.getDamage() * multiplier);
    }

}
