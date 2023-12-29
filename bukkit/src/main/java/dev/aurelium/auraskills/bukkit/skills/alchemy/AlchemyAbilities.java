package dev.aurelium.auraskills.bukkit.skills.alchemy;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.bukkit.skills.agility.AgilityAbilities;
import dev.aurelium.auraskills.bukkit.util.PotionUtil;
import dev.aurelium.auraskills.common.message.type.AbilityMessage;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
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
        return switch (potionType) {
            case INSTANT_DAMAGE, INSTANT_HEAL, AWKWARD, MUNDANE, THICK, WATER -> false;
            default -> true;
        };
    }

    private ItemStack applyDurationData(ItemStack item, double multiplier, Locale locale) {
        PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
        if (potionMeta != null) {
            PotionData potionData = potionMeta.getBasePotionData();
            if (isApplicablePotion(potionData.getType())) {
                int originalDuration = PotionUtil.getDuration(potionData);
                int duration = (int) (originalDuration * multiplier); // Get duration in ticks
                int durationBonus = duration - originalDuration;
                // Add duration to PersistentDataContainer
                var container = item.getItemMeta().getPersistentDataContainer();

                container.set(DURATION_BONUS_KEY, PersistentDataType.INTEGER, durationBonus);

                ItemMeta meta = item.getItemMeta();
                if (duration != 0 && meta != null) {
                    // Add lore
                    if (Abilities.ALCHEMIST.optionBoolean("add_item_lore", true)) {
                        List<String> lore = new ArrayList<>();
                        lore.add(TextUtil.replace(plugin.getMsg(AbilityMessage.ALCHEMIST_LORE, locale)
                                , "{duration}", PotionUtil.formatDuration(durationBonus)
                                , "{value}", NumberUtil.format1((multiplier - 1) * 100)));
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                    }
                }
                return item;
            }
        }
        return item;
    }

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

        PotionData potionData = meta.getBasePotionData();
        PotionType potionType = potionData.getType();
        PotionEffectType effectType = potionType.getEffectType();
        if (effectType != null) {
            if (!potionType.toString().equals("TURTLE_MASTER")) {
                // Get amplifier
                int amplifier = 0;
                if (potionData.isUpgraded()) {
                    if (potionType.equals(PotionType.SLOWNESS)) {
                        amplifier = 3;
                    } else {
                        amplifier = 1;
                    }
                }
                // Apply effect
                if (effectType.equals(PotionEffectType.SPEED) || effectType.equals(PotionEffectType.JUMP)) {
                    var agilityAbilities = plugin.getAbilityManager().getAbilityImpl(AgilityAbilities.class);
                    PotionUtil.applyEffect(player, new PotionEffect(effectType, (int) ((PotionUtil.getDuration(potionData) + durationBonus) * agilityAbilities.getSugarRushSplashMultiplier(player)), amplifier));
                } else {
                    PotionUtil.applyEffect(player, new PotionEffect(effectType, PotionUtil.getDuration(potionData) + durationBonus, amplifier));
                }
            }
            // Special case for Turtle Master
            else {
                if (!potionData.isUpgraded()) {
                    PotionUtil.applyEffect(player, new PotionEffect(PotionEffectType.SLOW, PotionUtil.getDuration(potionData) + durationBonus, 3));
                    PotionUtil.applyEffect(player, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, PotionUtil.getDuration(potionData) + durationBonus, 2));
                } else {
                    PotionUtil.applyEffect(player, new PotionEffect(PotionEffectType.SLOW, PotionUtil.getDuration(potionData) + durationBonus, 5));
                    PotionUtil.applyEffect(player, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, PotionUtil.getDuration(potionData) + durationBonus, 3));
                }
            }
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
        PotionData potionData = meta.getBasePotionData();
        if (meta.hasCustomEffects() && Skills.ALCHEMY.optionBoolean("ignore_custom_potions")) return;
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
                int duration = (int) ((PotionUtil.getDuration(potionData) + durationBonus) * splasherMultiplier * intensity);
                // Apply normal effects
                if (!potionData.getType().toString().equals("TURTLE_MASTER")) {
                    // Apply Sugar Rush
                    if (effect.getType().equals(PotionEffectType.SPEED) || effect.getType().equals(PotionEffectType.JUMP)) {
                        var agilityAbilities = plugin.getAbilityManager().getAbilityImpl(AgilityAbilities.class);
                        PotionUtil.applyEffect(player, new PotionEffect(effect.getType(), (int) (duration * agilityAbilities.getSugarRushSplashMultiplier(player)), effect.getAmplifier()));
                    } else {
                        PotionUtil.applyEffect(player, new PotionEffect(effect.getType(), duration, effect.getAmplifier()));
                    }

                }
                // Special case for Turtle Master
                else {
                    if (!potionData.isUpgraded()) {
                        PotionUtil.applyEffect(player, new PotionEffect(PotionEffectType.SLOW, duration, 3));
                        PotionUtil.applyEffect(player, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration, 2));
                    } else {
                        PotionUtil.applyEffect(player, new PotionEffect(PotionEffectType.SLOW, duration, 5));
                        PotionUtil.applyEffect(player, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,duration, 3));
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
        if (cloud.hasCustomEffects() && Skills.ALCHEMY.optionBoolean("ignore_custom_potions")) return;

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

}
