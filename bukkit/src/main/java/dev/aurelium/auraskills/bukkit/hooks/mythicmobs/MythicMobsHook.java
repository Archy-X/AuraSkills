package dev.aurelium.auraskills.bukkit.hooks.mythicmobs;

import dev.aurelium.auraskills.api.event.damage.DamageEvent;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.damage.DamageHandler;
import dev.aurelium.auraskills.bukkit.damage.DamageResult;
import dev.aurelium.auraskills.bukkit.hooks.mythicmobs.loot.MythicEntityLootParser;
import dev.aurelium.auraskills.common.hooks.Hook;
import dev.aurelium.auraskills.api.damage.DamageType;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicDamageEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.spongepowered.configurate.ConfigurationNode;

public class MythicMobsHook extends Hook implements Listener {

    private final AuraSkills plugin;
    private final DamageHandler damageHandler;

    public MythicMobsHook(AuraSkills plugin, ConfigurationNode config) {
        super(plugin, config);
        this.plugin = plugin;
        this.damageHandler = new DamageHandler();

        registerItemProvider();

        // Wait for loot manager to be created, but add parser before it is loaded
        plugin.getScheduler().executeSync(() ->
                plugin.getLootTableManager().getLootManager().registerCustomEntityParser(new MythicEntityLootParser(plugin)));
    }

    private void registerItemProvider() {
        plugin.getItemRegistry().registerExternalItemProvider("mythicmobs",
                (id) -> MythicBukkit.inst().getItemManager().getItemStack(id));
    }

    @EventHandler
    public void onMythicSkillDamage(MythicDamageEvent event) {
        // This is always some sort of skill/mechanic damage.

        if (!getConfig().node("handle_damage_increase").getBoolean() &&
                !getConfig().node("handle_damage_reduction").getBoolean()) {
            return;
        }

        AbstractEntity attacker = event.getCaster().getEntity();
        AbstractEntity target = event.getTarget();

        if (attacker.isPlayer()) {
            Player player = BukkitAdapter.adapt(attacker.asPlayer());
            if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
                return;
            }
            if (player.hasMetadata("NPC")) return;
            if (event.getDamageMetadata().getDamageCause() == EntityDamageEvent.DamageCause.THORNS) return;
        }

        if (target.isPlayer()) {
            Player player = BukkitAdapter.adapt(target.asPlayer());
            if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
                return;
            }
            if (target.hasMetadata("NPC")) return;
        }

        DamageResult result = damageHandler.handleDamage(
                BukkitAdapter.adapt(attacker), BukkitAdapter.adapt(target), getDamageType(attacker),
                event.getDamageMetadata().getDamageCause(), event.getDamage(), "mythicmobs");

        if (result.cancel()) {
            event.setCancelled(true);
        } else {
            event.setDamage(result.damage());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void damageListener(DamageEvent event) {
        if (!event.getDamageMeta().getSource().equals("mythicmobs")) {
            return;
        }

        if (!getConfig().node("handle_damage_increase").getBoolean()) {
            event.getDamageMeta().clearAttackModifiers();
        }

        if (!getConfig().node("handle_damage_reduction").getBoolean()) {
            event.getDamageMeta().clearDefenseModifiers();
        }
    }

    public boolean shouldPreventEntityXp(Entity entity) {
        if (!getConfig().node("prevent_regular_xp").getBoolean()) return false;
        // Do not care about that this is terminable... It's a lie in this context
        return MythicBukkit.inst().getMobManager().isMythicMob(entity);
    }

    @EventHandler
    public void onMechanicLoad(MythicMechanicLoadEvent event) {
        if (event.getMechanicName().equalsIgnoreCase("takeMana")) {
            event.register(new TakeManaMechanic(plugin, event));
        } else if (event.getMechanicName().equalsIgnoreCase("giveSkillXP")) {
            event.register(new GiveSkillXpMechanic(plugin, event));
        }
    }

    @EventHandler
    public void onConditionLoad(MythicConditionLoadEvent event) {
        if (event.getConditionName().equalsIgnoreCase("hasMana")) {
            event.register(new HasManaCondition(plugin, event));
        }
    }

    @Override
    public Class<? extends Hook> getTypeClass() {
        return MythicMobsHook.class;
    }

    private DamageType getDamageType(AbstractEntity attacker) {
        /*
        With MythicDamageEvent we don't know if a projectile/trident is involved but
        for the most part it doesn't really matter. Only thing that matters if what item was
        used when the skill/mechanic was cast.
        There can be edge cases if someone puts a damage mechanic on the armor for example
        with the ~onDamaged trigger, although this is unlikely.
         */
        Material material;

        if (attacker.isPlayer()) {
            material = BukkitAdapter.adapt(attacker.asPlayer()).getInventory().getItemInMainHand().getType();
        } else {
            return DamageType.OTHER;
        }

        if (material.name().contains("SWORD")) {
            return DamageType.SWORD;
        } else if (material.name().contains("_AXE")) {
            return DamageType.AXE;
        } else if (material.name().contains("PICKAXE")) {
            return DamageType.PICKAXE;
        } else if (material.name().contains("SHOVEL") || material.name().contains("SPADE")) {
            return DamageType.SHOVEL;
        } else if (material.name().contains("HOE")) {
            return DamageType.HOE;
        } else if (material.equals(Material.AIR)) {
            return DamageType.HAND;
        } else if (material.equals(Material.BOW) || material.equals(Material.TRIDENT)) {
            return DamageType.BOW;
        }
        return DamageType.OTHER;
    }
}
