package dev.aurelium.auraskills.bukkit.ability;

import com.archyx.aureliumskills.AureliumSkills;
import dev.aurelium.auraskills.api.event.AuraSkillsListener;
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

import java.util.HashSet;
import java.util.Set;

public class BukkitAbilityManager extends AbilityManager {

    private final AuraSkills plugin;
    private final Set<AbilityImpl> abilityImpls = new HashSet<>();

    public BukkitAbilityManager(AuraSkills plugin) {
        super(plugin);
        this.plugin = plugin;
        registerAbilityImplementations();
    }

    private void registerAbilityImplementations() {
        registerAbilityImpl(new FarmingAbilities(plugin));
        registerAbilityImpl(new ForagingAbilities(plugin));
        registerAbilityImpl(new MiningAbilities(plugin));
        registerAbilityImpl(new FishingAbilities(plugin));
        registerAbilityImpl(new ExcavationAbilities(plugin));
        registerAbilityImpl(new ArcheryAbilities(plugin));
        registerAbilityImpl(new DefenseAbilities(plugin));
        registerAbilityImpl(new FightingAbilities(plugin));
        registerAbilityImpl(new EnduranceAbilities(plugin));
        registerAbilityImpl(new AgilityAbilities(plugin));
        registerAbilityImpl(new AlchemyAbilities(plugin));
        registerAbilityImpl(new EnchantingAbilities(plugin));
        registerAbilityImpl(new HealingAbilities(plugin));
        registerAbilityImpl(new ForgingAbilities(plugin));
    }

    public void registerAbilityImpl(AbilityImpl abilityImpl) {
        abilityImpls.add(abilityImpl);
        Bukkit.getPluginManager().registerEvents(abilityImpl, plugin);
        if (abilityImpl instanceof AuraSkillsListener listener) {
            plugin.getEventManager().registerEvents(plugin, listener);
        }
    }

    public <T extends AbilityImpl> T getAbilityImpl(Class<T> clazz) {
        for (AbilityImpl abilityImpl : abilityImpls) {
            if (abilityImpl.getClass().equals(clazz)) {
                return clazz.cast(abilityImpl);
            }
        }
        throw new IllegalArgumentException("Ability implementation of type " + clazz.getSimpleName() + " not found!");
    }

    public void sendMessage(Player player, String message) {
        User user = plugin.getUser(player);
        if (plugin.configBoolean(Option.ACTION_BAR_ABILITY) && plugin.configBoolean(Option.ACTION_BAR_ENABLED)) {
            plugin.getUiProvider().getActionBarManager().sendAbilityActionBar(user, message);
        } else {
            if (message == null || message.isEmpty()) return; // Don't send empty message
            player.sendMessage(AureliumSkills.getPrefix(user.getLocale()) + message);
        }
    }

}
