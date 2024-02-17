package dev.aurelium.auraskills.bukkit.skills.farming;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.common.modifier.DamageModifier;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class FarmingAbilities extends AbilityImpl {

    public FarmingAbilities(AuraSkills plugin) {
        super(plugin, Abilities.BOUNTIFUL_HARVEST, Abilities.FARMER, Abilities.TRIPLE_HARVEST, Abilities.GENETICIST, Abilities.SCYTHE_MASTER);
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

    public DamageModifier scytheMaster(Player player, User user) {
        var ability = Abilities.SCYTHE_MASTER;

        if (isDisabled(ability) || failsChecks(player, ability)) return DamageModifier.none();

        if (user.getAbilityLevel(ability) <= 0) return DamageModifier.none();

        return new DamageModifier(getValue(ability, user) / 100, DamageModifier.Operation.ADD_COMBINED);
    }

}
