package dev.aurelium.auraskills.bukkit.skills.farming;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.damage.DamageType;
import dev.aurelium.auraskills.api.event.damage.DamageEvent;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.api.damage.DamageModifier;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class FarmingAbilities extends AbilityImpl {

    private final Random random = new Random();

    public FarmingAbilities(AuraSkills plugin) {
        super(plugin, Abilities.BOUNTIFUL_HARVEST, Abilities.FARMER, Abilities.GENETICIST, Abilities.SCYTHE_MASTER, Abilities.GROWTH_AURA);
    }

    @EventHandler
    public void geneticist(PlayerItemConsumeEvent event) {
        var ability = Abilities.GENETICIST;

        if (isDisabled(ability)) return;

        Player player = event.getPlayer();
        if (failsChecks(player, ability)) return;

        Material mat = event.getItem().getType();
        if (isPlantBased(mat)) { // Only allow plant based foods
            User user = plugin.getUser(player);

            float amount = (float) getValue(ability, user) / 10;
            player.setSaturation(player.getSaturation() + amount);
        }
    }

    private boolean isPlantBased(Material mat) {
        return mat.equals(Material.BREAD) || mat.equals(Material.APPLE) || mat.equals(Material.GOLDEN_APPLE) || mat.equals(Material.POTATO)
                || mat.equals(Material.BAKED_POTATO) || mat.equals(Material.CARROT) || mat.equals(Material.GOLDEN_CARROT) || mat.equals(Material.MELON)
                || mat.equals(Material.PUMPKIN_PIE) || mat.equals(Material.BEETROOT) || mat.equals(Material.BEETROOT_SOUP) || mat.equals(Material.MUSHROOM_STEW)
                || mat.equals(Material.POISONOUS_POTATO);
    }

    private DamageModifier scytheMaster(Player player, User user) {
        var ability = Abilities.SCYTHE_MASTER;

        if (isDisabled(ability) || failsChecks(player, ability)) return DamageModifier.none();

        if (user.getAbilityLevel(ability) <= 0) return DamageModifier.none();

        return new DamageModifier(getValue(ability, user) / 100, DamageModifier.Operation.ADD_COMBINED);
    }

    @Override
    public String replaceDescPlaceholders(String input, Ability ability, User user) {
        if (ability.equals(Abilities.GROWTH_AURA)) {
            return TextUtil.replace(input, "{radius}", String.valueOf(ability.optionInt("radius", 30)));
        }
        return input;
    }

    @EventHandler(ignoreCancelled = true)
    public void damageListener(DamageEvent event) {
        var meta = event.getDamageMeta();
        var attacker = meta.getAttackerAsPlayer();

        if (attacker != null) {
            if (meta.getDamageType() == DamageType.HOE) {
                var user = plugin.getUser(attacker);
                meta.addAttackModifier(scytheMaster(attacker, user));
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCropGrowth(BlockGrowEvent event) {
        var ability = Abilities.GROWTH_AURA;

        if (isDisabled(ability)) return;

        Block block = event.getBlock();
        Location ogLoc = block.getLocation();
        int radius = ability.optionInt("radius", 30);

        // Check for players nearby
        Collection<Entity> entities = block.getWorld().getNearbyEntities(ogLoc, radius, radius, radius);
        List<Player> playerList = entities.stream().filter(e -> e instanceof Player).map(e -> (Player) e).toList();

        for (Player player : playerList) {
            handleGrowthAura(player, block, event.getNewState());
        }
    }

    private void handleGrowthAura(Player player, Block block, BlockState state) {
        var ability = Abilities.GROWTH_AURA;

        if (failsChecks(player, ability)) return;
        User user = plugin.getUser(player);

        int extraStages = rollExtraStages(user);
        if (extraStages == 0) return;

        if (state.getBlockData() instanceof Ageable ageable) {
            // Add growth stages with 1 tick delay
            plugin.getScheduler().scheduleSync(() -> {
                if (block.getType() != state.getType()) return;

                ageable.setAge(Math.min(ageable.getAge() + extraStages, ageable.getMaximumAge()));
                block.setBlockData(ageable);
            }, 50, TimeUnit.MILLISECONDS);
        }
    }

    private int rollExtraStages(User user) {
        double value = getValue(Abilities.GROWTH_AURA, user);
        int guaranteed = (int) (value / 100);
        double chance = (value - guaranteed * 100) / 100;
        int extra = random.nextDouble() < chance ? 1 : 0;
        return guaranteed + extra;
    }

}
