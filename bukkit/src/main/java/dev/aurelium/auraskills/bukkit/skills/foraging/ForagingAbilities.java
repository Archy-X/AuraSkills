package dev.aurelium.auraskills.bukkit.skills.foraging;

import com.cryptomorin.xseries.XMaterial;
import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.api.event.loot.LootDropEvent;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.common.modifier.DamageModifier;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

public class ForagingAbilities extends AbilityImpl {

    public ForagingAbilities(AuraSkills plugin) {
        super(plugin, Abilities.LUMBERJACK, Abilities.FORAGER, Abilities.AXE_MASTER, Abilities.SHREDDER, Abilities.VALOR);
    }

    public void lumberjack(Player player, User user, Block block) {
        var ability = Abilities.LUMBERJACK;

        if (isDisabled(ability)) return;

        if (!player.getGameMode().equals(GameMode.SURVIVAL)) return;

        if (failsChecks(player, ability)) return;

        if (user.getAbilityLevel(ability) == 0) return;

        if (rand.nextDouble() < (getValue(ability, user) / 100)) {
            for (ItemStack item : block.getDrops(player.getInventory().getItemInMainHand())) {
                Location location = block.getLocation().add(0.5, 0.5, 0.5);
                boolean toInventory = ItemUtils.hasTelekinesis(player.getInventory().getItemInMainHand());
                LootDropEvent event = new LootDropEvent(player, user.toApi(), item.clone(), location, LootDropEvent.Cause.LUMBERJACK, toInventory);
                Bukkit.getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    ItemUtils.giveBlockLoot(player, block, event);
                }
            }
        }
    }

    public DamageModifier axeMaster(Player player, User user) {
        var ability = Abilities.AXE_MASTER;

        if (isDisabled(ability) || failsChecks(player, ability)) return DamageModifier.none();

        if (user.getAbilityLevel(ability) <= 0) return DamageModifier.none();

        return new DamageModifier(getValue(ability, user) / 100, DamageModifier.Operation.ADD_COMBINED);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void shredder(PlayerItemDamageEvent event) {
        var ability = Abilities.SHREDDER;

        if (isDisabled(ability)) return;

        if (event.isCancelled()) return;

        // If is item taking durabilty damage is armor
        if (!ItemUtils.isArmor(event.getItem().getType())) {
            return;
        }
        // If last damage was from entity
        if (!(event.getPlayer().getLastDamageCause() instanceof EntityDamageByEntityEvent e)) return;
        // If last damage was from player
        if (!(e.getDamager() instanceof Player player)) return;

        if (failsChecks(player, ability)) return;
        // If damage was an attack
        if (!e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) return;
        // If item used was an axe
        Material mat = player.getInventory().getItemInMainHand().getType();
        if (mat.equals(Material.DIAMOND_AXE) || mat.equals(Material.IRON_AXE) || mat.equals(XMaterial.GOLDEN_AXE.parseMaterial())
                || mat.equals(Material.STONE_AXE) || mat.equals(XMaterial.WOODEN_AXE.parseMaterial())) {
            User user = plugin.getUser(player);
            //Checks if shredder is used
            if (user.getAbilityLevel(ability) == 0) return;

            if (rand.nextDouble() < (getValue(ability, user)) / 100) {
                event.setDamage(event.getDamage() * 3);
            }
        }
    }

    public void applyValor(User user) {
        var ability = Abilities.VALOR;
        if (isDisabled(ability)) return;

        if (user.getAbilityLevel(ability) == 0) return;

        user.addStatModifier(new StatModifier("foraging-valor", Stats.STRENGTH, (int) getValue(ability, user)));
    }

    public void removeValor(User user) {
        user.removeStatModifier("foraging-valor");
    }

}
