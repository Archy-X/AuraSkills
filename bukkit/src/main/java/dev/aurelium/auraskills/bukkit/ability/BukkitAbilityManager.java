package dev.aurelium.auraskills.bukkit.ability;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.skills.agility.AgilityAbilities;
import dev.aurelium.auraskills.bukkit.skills.alchemy.AlchemyAbilities;
import dev.aurelium.auraskills.bukkit.skills.archery.ArcheryAbilities;
import dev.aurelium.auraskills.bukkit.skills.defense.DefenseAbilities;
import dev.aurelium.auraskills.bukkit.skills.enchanting.EnchantingAbilities;
import dev.aurelium.auraskills.bukkit.skills.endurance.EnduranceAbilities;
import dev.aurelium.auraskills.bukkit.skills.excavation.ExcavationAbilities;
import dev.aurelium.auraskills.bukkit.skills.farming.FarmingAbilities;
import dev.aurelium.auraskills.bukkit.skills.fighting.FightingAbilities;
import dev.aurelium.auraskills.bukkit.skills.fishing.FishingAbilities;
import dev.aurelium.auraskills.bukkit.skills.foraging.ForagingAbilities;
import dev.aurelium.auraskills.bukkit.skills.forging.ForgingAbilities;
import dev.aurelium.auraskills.bukkit.skills.healing.HealingAbilities;
import dev.aurelium.auraskills.bukkit.skills.mining.MiningAbilities;
import dev.aurelium.auraskills.common.ability.AbilityManager;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitAbilityManager extends AbilityManager {

    private final AuraSkills plugin;

    public BukkitAbilityManager(AuraSkills plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void registerAbilityImplementations() {
        registerAbilityImpl(new FishingAbilities(plugin));
        registerAbilityImpl(new EnduranceAbilities(plugin));
        registerAbilityImpl(new AgilityAbilities(plugin));
        registerAbilityImpl(new AlchemyAbilities(plugin));
        registerAbilityImpl(new EnchantingAbilities(plugin));
        registerAbilityImpl(new HealingAbilities(plugin));
        registerAbilityImpl(new ForgingAbilities(plugin));
        registerAbilityImpl(new FightingAbilities(plugin));
        registerAbilityImpl(new ArcheryAbilities(plugin));
        registerAbilityImpl(new FarmingAbilities(plugin));
        registerAbilityImpl(new ForagingAbilities(plugin));
        registerAbilityImpl(new MiningAbilities(plugin));
        registerAbilityImpl(new ExcavationAbilities(plugin));
        registerAbilityImpl(new DefenseAbilities(plugin));
    }

    public void registerAbilityImpl(BukkitAbilityImpl abilityImpl) {
        addImplToMap(abilityImpl);
        Bukkit.getPluginManager().registerEvents(abilityImpl, plugin);
    }

    /**
     * Sends a message to the ability action bar if enabled, or the chat otherwise.
     *
     * @param player the player to send the message to
     * @param message the message to send
     */
    public void sendMessage(Player player, String message) {
        User user = plugin.getUser(player);
        if (plugin.configBoolean(Option.ACTION_BAR_ABILITY) && plugin.configBoolean(Option.ACTION_BAR_ENABLED)) {
            plugin.getUiProvider().getActionBarManager().sendAbilityActionBar(user, message);
        } else {
            if (message == null || message.isEmpty()) return; // Don't send empty message
            player.sendMessage(plugin.getPrefix(user.getLocale()) + message);
        }
    }

}
