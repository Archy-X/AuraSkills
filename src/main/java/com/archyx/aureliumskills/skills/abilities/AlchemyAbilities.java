package com.archyx.aureliumskills.skills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.AbilityMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.stats.PlayerStat;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.LoreUtil;
import com.archyx.aureliumskills.util.PotionUtil;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class AlchemyAbilities implements Listener {

    private final AureliumSkills plugin;

    public AlchemyAbilities(AureliumSkills plugin) {
        this.plugin = plugin;
        wiseEffect();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void alchemist(BrewEvent event) {
        if (OptionL.isEnabled(Skill.ALCHEMY) && AureliumSkills.abilityOptionManager.isEnabled(Ability.ALCHEMIST)) {
            if (!event.isCancelled()) {
                if (event.getBlock().hasMetadata("skillsBrewingStandOwner")) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(event.getBlock().getMetadata("skillsBrewingStandOwner").get(0).asString()));
                    if (offlinePlayer.isOnline()) {
                        Player player = offlinePlayer.getPlayer();
                        BrewerInventory inventory = event.getContents();
                        if (player != null) {
                            //Checks if in blocked world
                            if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
                                return;
                            }
                            //Check for permission
                            if (!player.hasPermission("aureliumskills.alchemy")) {
                                return;
                            }
                            //Check creative mode disable
                            if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
                                if (player.getGameMode().equals(GameMode.CREATIVE)) {
                                    return;
                                }
                            }
                            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
                            if (playerSkill != null) {
                                if (playerSkill.getAbilityLevel(Ability.ALCHEMIST) > 0) {
                                    updateBrewingStand(inventory, playerSkill, Lang.getLanguage(player));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateBrewingStand(BrewerInventory inventory, PlayerSkill playerSkill, Locale locale) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack[] contents = inventory.getContents();
                double multiplier = 1 + (Ability.ALCHEMIST.getValue(playerSkill.getAbilityLevel(Ability.ALCHEMIST)) / 100);
                for (int i = 0; i < contents.length; i++) {
                    ItemStack item = contents[i];
                    if (item != null) {
                        if (item.getItemMeta() instanceof PotionMeta) {
                            contents[i] = applyDurationData(item, multiplier, locale);
                        }
                    }
                }
                inventory.setContents(contents);
            }
        }.runTaskLater(plugin, 1L);
    }

    private boolean isApplicablePotion(PotionType potionType) {
        switch (potionType) {
            case INSTANT_DAMAGE:
            case INSTANT_HEAL:
            case AWKWARD:
            case MUNDANE:
            case THICK:
            case WATER:
                return false;
            default:
                return true;
        }
    }

    private ItemStack applyDurationData(ItemStack originalItem, double multiplier, Locale locale) {
        PotionMeta potionMeta = (PotionMeta) originalItem.getItemMeta();
        if (potionMeta != null) {
            PotionData potionData = potionMeta.getBasePotionData();
            if (isApplicablePotion(potionData.getType())) {
                int originalDuration = PotionUtil.getDuration(potionData);
                int duration = (int) (originalDuration * multiplier); // Get duration in ticks
                int durationBonus = duration - originalDuration;
                // Add NBT data
                NBTItem nbtItem = new NBTItem(originalItem);
                NBTCompound nbtCompound = nbtItem.getCompound("skillsPotion");
                if (nbtCompound == null) {
                    nbtItem.addCompound("skillsPotion").setInteger("durationBonus", durationBonus);
                }
                if (nbtCompound != null) {
                    nbtCompound.setInteger("durationBonus", durationBonus);
                }
                ItemStack item = nbtItem.getItem();
                ItemMeta meta = item.getItemMeta();
                if (duration != 0 && meta != null) {
                    // Add lore
                    NumberFormat nf = new DecimalFormat("#.#");
                    List<String> lore = new ArrayList<>();
                    lore.add(LoreUtil.replace(Lang.getMessage(AbilityMessage.ALCHEMIST_LORE, locale)
                            , "{duration}", PotionUtil.formatDuration(durationBonus)
                            , "{value}", nf.format((multiplier - 1) * 100)));
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
                return item;
            }
        }
        return originalItem;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrink(PlayerItemConsumeEvent event) {
        if (OptionL.isEnabled(Skill.ALCHEMY) && AureliumSkills.abilityOptionManager.isEnabled(Ability.ALCHEMIST) && !event.isCancelled()) {
            ItemStack item = event.getItem();
            if (item.getType() == Material.POTION && item.getItemMeta() instanceof PotionMeta) {
                Player player = event.getPlayer();
                //Checks if in blocked world
                if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
                    return;
                }
                //Check for permission
                if (!player.hasPermission("aureliumskills.alchemy")) {
                    return;
                }
                //Check creative mode disable
                if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
                    if (player.getGameMode().equals(GameMode.CREATIVE)) {
                        return;
                    }
                }
                NBTItem nbtItem = new NBTItem(item);
                NBTCompound compound = nbtItem.getCompound("skillsPotion");
                if (compound != null) {
                    Integer durationBonus = compound.getInteger("durationBonus");
                    if (durationBonus != null) {
                        PotionMeta meta = (PotionMeta) item.getItemMeta();
                        if (meta != null) {
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
                                        PotionUtil.applyEffect(player, new PotionEffect(effectType, (int) ((PotionUtil.getDuration(potionData) + durationBonus) * AgilityAbilities.getSugarRushSplashMultiplier(player)), amplifier));
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
                    }
                }
            }
        }
    }

    // Handles duration boosts for splash potions. Includes Alchemist, Sugar Rush, and Splasher.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSplash(PotionSplashEvent event) {
        if (OptionL.isEnabled(Skill.ALCHEMY) && AureliumSkills.abilityOptionManager.isEnabled(Ability.ALCHEMIST) && !event.isCancelled()) {
            ItemStack item = event.getPotion().getItem();
            if (item.getItemMeta() instanceof PotionMeta && item.getItemMeta() != null) {
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                PotionData potionData = meta.getBasePotionData();
                // Get potion duration bonus from Alchemist ability
                NBTItem nbtItem = new NBTItem(item);
                int durationBonus = 0;
                NBTCompound compound = nbtItem.getCompound("skillsPotion");
                if (compound != null) {
                    durationBonus = compound.getInteger("durationBonus");
                }
                // Add effects for each player
                for (PotionEffect effect : event.getPotion().getEffects()) {
                    for (LivingEntity entity : event.getAffectedEntities()) {
                        if (entity instanceof Player) {
                            Player player = (Player) entity;
                            //Checks if in blocked world
                            if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
                                return;
                            }
                            //Check for permission
                            if (!player.hasPermission("aureliumskills.alchemy")) {
                                return;
                            }
                            //Check creative mode disable
                            if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
                                if (player.getGameMode().equals(GameMode.CREATIVE)) {
                                    return;
                                }
                            }
                            // Calculate and get multipliers
                            double splasherMultiplier = getSplasherMultiplier(event.getPotion().getShooter(), event.getAffectedEntities());
                            double intensity = event.getIntensity(player);
                            int duration = (int) ((PotionUtil.getDuration(potionData) + durationBonus) * splasherMultiplier * intensity);
                            // Apply normal effects
                            if (!potionData.getType().toString().equals("TURTLE_MASTER")) {
                                // Apply Sugar Rush
                                if (effect.getType().equals(PotionEffectType.SPEED) || effect.getType().equals(PotionEffectType.JUMP)) {
                                    PotionUtil.applyEffect(player, new PotionEffect(effect.getType(), (int) (duration * AgilityAbilities.getSugarRushSplashMultiplier(player)), effect.getAmplifier()));
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
            }
        }
    }

    private double getSplasherMultiplier(ProjectileSource source, Collection<LivingEntity> affectedEntities) {
        double splasherMultiplier = 1.0;
        if (source instanceof Player) {
            Player player = (Player) source;
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null && AureliumSkills.abilityOptionManager.isEnabled(Ability.SPLASHER)) {
                if (playerSkill.getAbilityLevel(Ability.SPLASHER) > 0) {
                    double splasherPercent = Ability.SPLASHER.getValue(playerSkill.getAbilityLevel(Ability.SPLASHER));
                    int affectedPlayers = (int) affectedEntities.stream().filter(entity -> entity instanceof Player).filter(entity -> SkillLoader.playerSkills.containsKey(entity.getUniqueId())).count();
                    splasherMultiplier = 1 + (splasherPercent / 100 * affectedPlayers);
                }
            }
        }
        return splasherMultiplier;
    }

    // Handles the Lingering ability
    @EventHandler
    public void lingering(LingeringPotionSplashEvent event) {
        if (OptionL.isEnabled(Skill.ALCHEMY) && AureliumSkills.abilityOptionManager.isEnabled(Ability.LINGERING) && !event.isCancelled()) {
            if (event.getEntity().getShooter() instanceof Player) {
                Player player = (Player) event.getEntity().getShooter();
                //Checks if in blocked world
                if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
                    return;
                }
                //Check for permission
                if (!player.hasPermission("aureliumskills.alchemy")) {
                    return;
                }
                //Check creative mode disable
                if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
                    if (player.getGameMode().equals(GameMode.CREATIVE)) {
                        return;
                    }
                }
                PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
                if (playerSkill != null) {
                    if (playerSkill.getAbilityLevel(Ability.LINGERING) > 0) {
                        AreaEffectCloud cloud = event.getAreaEffectCloud();
                        // Get values
                        double naturalDecay = 1 - (Ability.LINGERING.getValue(playerSkill.getAbilityLevel(Ability.LINGERING)) / 100);
                        double entityDecay = 1 - (Ability.LINGERING.getValue2(playerSkill.getAbilityLevel(Ability.LINGERING)) / 100);
                        // 1% limit
                        if (naturalDecay <= 0.01) naturalDecay = 0.01;
                        if (entityDecay <= 0.01) entityDecay = 0.01;
                        // Apply values
                        cloud.setRadiusPerTick(cloud.getRadiusPerTick() * (float) naturalDecay);
                        cloud.setRadiusOnUse(cloud.getRadiusOnUse() * (float) entityDecay);
                    }
                }
            }
        }
    }

    private void wiseEffect() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (OptionL.isEnabled(Skill.ALCHEMY) && AureliumSkills.abilityOptionManager.isEnabled(Ability.WISE_EFFECT)) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
                        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
                        if (playerStat != null && playerSkill != null) {
                            if (player.getActivePotionEffects().size() > 0) {
                                boolean skip = false;
                                //Checks if in blocked world
                                if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) skip = true;
                                //Check for permission
                                if (!player.hasPermission("aureliumskills.alchemy")) skip = true;
                                //Check creative mode disable
                                if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
                                    if (player.getGameMode().equals(GameMode.CREATIVE)) skip = true;
                                }
                                // Get unique active potion effects
                                if (!skip) {
                                    Set<PotionEffectType> uniqueTypesSet = new HashSet<>();
                                    for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                                        uniqueTypesSet.add(potionEffect.getType());
                                    }
                                    int uniqueTypes = uniqueTypesSet.size();
                                    // Apply modifier
                                    double wisdomPerType = Ability.WISE_EFFECT.getValue(playerSkill.getAbilityLevel(Ability.WISE_EFFECT));
                                    StatModifier modifier = new StatModifier("AbilityModifier-WiseEffect", Stat.WISDOM, (int) (wisdomPerType * uniqueTypes));
                                    playerStat.addModifier(modifier, false);
                                }
                            } else {
                                playerStat.removeModifier("AbilityModifier-WiseEffect", false);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 1L, 10L);
    }

}
