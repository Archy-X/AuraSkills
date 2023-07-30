package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.event.AuraSkillsListener;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.trait.TraitManager;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class BukkitTraitManager extends TraitManager {

    private final AuraSkills plugin;
    private final Map<Trait, TraitImpl> traitImpls = new HashMap<>();

    public BukkitTraitManager(AuraSkills plugin) {
        super(plugin);
        this.plugin = plugin;
        registerTraitImplementations();
    }

    public void registerTraitImplementations() {
        registerTraitImpl(Traits.HP, new HpTrait(plugin));
        RegenTrait regen = new RegenTrait(plugin);
        registerTraitImpl(Traits.SATURATION_REGEN, regen);
        registerTraitImpl(Traits.HUNGER_REGEN, regen);
        registerTraitImpl(Traits.MANA_REGEN, new ManaRegenTrait(plugin));
        registerTraitImpl(Traits.LUCK, new LuckTrait(plugin));
        registerTraitImpl(Traits.DOUBLE_DROP, new DoubleDropTrait(plugin));
        registerTraitImpl(Traits.ATTACK_DAMAGE, new AttackDamageTrait(plugin));
        registerTraitImpl(Traits.EXPERIENCE_BONUS, new ExperienceBonusTrait(plugin));
        registerTraitImpl(Traits.ANVIL_DISCOUNT, new AnvilDiscountTrait(plugin));
        registerTraitImpl(Traits.MAX_MANA, new MaxManaTrait(plugin));
        registerTraitImpl(Traits.DEFENSE, new DefenseTrait(plugin));
    }

    public void registerTraitImpl(Trait trait, TraitImpl traitImpl) {
        // Only register events if implementation isn't already registered for a different trait
        if (!traitImpls.containsValue(traitImpl)) {
            Bukkit.getPluginManager().registerEvents(traitImpl, plugin);
            if (traitImpl instanceof AuraSkillsListener listener) {
                plugin.getEventManager().registerEvents(plugin, listener);
            }
        }
        traitImpls.put(trait, traitImpl);
    }

    public TraitImpl getTraitImpl(Trait trait) {
        return traitImpls.get(trait);
    }

    @Override
    public double getBaseLevel(User user, Trait trait) {
        Player player = ((BukkitUser) user).getPlayer();
        TraitImpl traitImpl = traitImpls.get(trait);
        if (traitImpl != null) {
            return traitImpl.getBaseLevel(player, trait);
        } else {
            return 0.0;
        }
    }
}
