package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.event.AuraSkillsEventHandler;
import dev.aurelium.auraskills.api.event.AuraSkillsListener;
import dev.aurelium.auraskills.api.event.data.UserLoadEvent;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HpTrait extends TraitImpl implements AuraSkillsListener {

    private final Map<UUID, Double> worldChangeHealth = new HashMap<>();
    private final Map<Integer, Double> hearts = new HashMap<>();
    private static final double threshold = 0.1;

    HpTrait(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute == null) return 0.0;
        double current = attribute.getValue();
        // Subtract skills attribute value
        for (AttributeModifier am : attribute.getModifiers()) {
            if (am.getName().equals("skillsHealth")) {
                current -= am.getAmount();
            }
        }
        return current;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        applyScaling(event.getPlayer());
    }

    @AuraSkillsEventHandler
    public void onLoad(UserLoadEvent event) {
        setHealth(BukkitUser.getPlayer(event.getUser()), BukkitUser.getUser(event.getUser()));
    }

    @Override
    public void reload(Player player, Trait trait) {
        setHealth(player, plugin.getUser(player));
        // TODO Remove Fleeting
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void worldChange(PlayerChangedWorldEvent event) {
        if (!Traits.HP.isEnabled()) return;
        Player player = event.getPlayer();
        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation()) && !plugin.getWorldManager().isDisabledWorld(event.getFrom().getName())) {
            worldChangeHealth.put(player.getUniqueId(), player.getHealth());
        }
        User user = plugin.getUser(player);
        if (plugin.configInt(Option.HEALTH_UPDATE_DELAY) > 0) {
            plugin.getScheduler().scheduleSync(() -> {
                setWorldChange(event, player, user);
            }, plugin.configInt(Option.HEALTH_UPDATE_DELAY) * 50L, TimeUnit.MILLISECONDS);
        } else {
            setWorldChange(event, player, user);
        }
    }

    private void setWorldChange(PlayerChangedWorldEvent event, Player player, User user) {
        setHealth(player, user);
        if (plugin.getWorldManager().isDisabledWorld(event.getFrom().getName()) && !plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
            if (worldChangeHealth.containsKey(player.getUniqueId())) {
                player.setHealth(worldChangeHealth.get(player.getUniqueId()));
                worldChangeHealth.remove(player.getUniqueId());
            }
        }
    }

    private void setHealth(Player player, User user) {
        if (!Traits.HP.isEnabled()) return;

        double modifier = user.getBonusTraitLevel(Traits.HP);
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute == null) return;
        double originalMaxHealth = attribute.getValue();
        boolean hasChange = true;
        // Removes existing modifiers of the same name and check for change
        for (AttributeModifier am : attribute.getModifiers()) {
            if (am.getName().equals("skillsHealth")) {
                // Check for any changes, if not, return
                if (Math.abs(originalMaxHealth - (originalMaxHealth - am.getAmount() + modifier)) <= threshold) {
                    hasChange = false;
                }
                // Removes if it has changed
                if (hasChange) {
                    attribute.removeModifier(am);
                }
            }
        }
        // Disable health if in disable world
        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
            player.setHealthScaled(false);
            for (AttributeModifier am : attribute.getModifiers()) {
                if (am.getName().equals("skillsHealth")) {
                    attribute.removeModifier(am);
                }
            }
            return;
        }
        // Force base health if enabled
        if (plugin.configBoolean(Option.HEALTH_FORCE_BASE_HEALTH)) {
            attribute.setBaseValue(20.0);
        }
        // Return if no change
        if (hasChange) {
            // Applies modifier
            attribute.addModifier(new AttributeModifier("skillsHealth", modifier, AttributeModifier.Operation.ADD_NUMBER));
            // Sets health to max if over max
            if (player.getHealth() > attribute.getValue()) {
                player.setHealth(attribute.getValue());
            }
            if (plugin.configBoolean(Option.HEALTH_KEEP_FULL_ON_INCREASE) && attribute.getValue() > originalMaxHealth) {
                // Heals player to full health if had full health before modifier
                if (player.getHealth() >= originalMaxHealth) {
                    player.setHealth(attribute.getValue());
                }
            }
        }
        applyScaling(player);
    }

    private void applyScaling(Player player) {
        if (!Traits.HP.isEnabled()) return;
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute == null) return;

        if (plugin.configBoolean(Option.HEALTH_HEALTH_SCALING)) {
            double health = attribute.getValue();
            player.setHealthScaled(true);
            int scaledHearts = 0;
            for (Integer heartNum : hearts.keySet()) {
                double healthNum = hearts.get(heartNum);
                if (health >= healthNum) {
                    if (heartNum > scaledHearts) {
                        scaledHearts = heartNum;
                    }
                }
            }
            if (scaledHearts == 0) {
                scaledHearts = 10;
            }
            player.setHealthScale(scaledHearts * 2);
        } else {
            player.setHealthScaled(false);
        }
    }

    public void loadHearts(ConfigurationNode config) {
        // Load default hearts
        this.hearts.clear();
        this.hearts.put(10, 0.0);
        this.hearts.put(11, 24.0);
        this.hearts.put(12, 29.0);
        this.hearts.put(13, 37.0);
        this.hearts.put(14, 50.0);
        this.hearts.put(15, 71.0);
        this.hearts.put(16, 105.0);
        this.hearts.put(17, 160.0);
        this.hearts.put(18, 249.0);
        this.hearts.put(19, 393.0);
        this.hearts.put(20, 626.0);
        // Load hearts from config
        try {
            for (Object keyObj : config.childrenMap().keySet()) {
                if (!(keyObj instanceof String key)) continue;

                int heartsNum = Integer.parseInt(key);
                double healthNum = config.node(keyObj).getDouble(-1.0);
                if (healthNum != -1.0) {
                    this.hearts.put(heartsNum, healthNum);
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("[AureliumSkills] There was an error loading health.hearts data! Check to make sure the keys are only integers and the values are only numbers.");
        }
    }

}
