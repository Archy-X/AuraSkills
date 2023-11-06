package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitHandler;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.trait.TraitManager;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.api.bukkit.BukkitTraitHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class BukkitTraitManager extends TraitManager {

    private final AuraSkills plugin;
    private final Set<BukkitTraitHandler> traitImpls = new HashSet<>();

    public BukkitTraitManager(AuraSkills plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    public void registerTraitImplementations() {
        traitImpls.clear();
        registerTraitImpl(new HpTrait(plugin));
        registerTraitImpl(new HealthRegenTrait(plugin));
        registerTraitImpl(new ManaRegenTrait(plugin));
        registerTraitImpl(new LuckTrait(plugin));
        registerTraitImpl(new DoubleDropTrait(plugin));
        registerTraitImpl(new AttackDamageTrait(plugin));
        registerTraitImpl(new ExperienceBonusTrait(plugin));
        registerTraitImpl(new AnvilDiscountTrait(plugin));
        registerTraitImpl(new MaxManaTrait(plugin));
        registerTraitImpl(new DamageReductionTrait(plugin));
        registerTraitImpl(new CritChanceTrait(plugin));
        registerTraitImpl(new CritDamageTrait(plugin));
        registerTraitImpl(new MovementSpeedTrait(plugin));
    }

    public void registerTraitImpl(BukkitTraitHandler bukkitTrait) {
        traitImpls.add(bukkitTrait);
        if (bukkitTrait instanceof TraitImpl traitImpl) {
            Bukkit.getPluginManager().registerEvents(traitImpl, plugin);
        }
    }

    public <T extends BukkitTraitHandler> T getTraitImpl(Class<T> clazz) {
        for (BukkitTraitHandler traitImpl : traitImpls) {
            if (traitImpl.getClass().equals(clazz)) {
                return clazz.cast(traitImpl);
            }
        }
        throw new IllegalArgumentException("Trait implementation of type " + clazz.getSimpleName() + " not found!");
    }

    @Nullable
    public BukkitTraitHandler getTraitImpl(Trait trait) {
        for (BukkitTraitHandler traitImpl : traitImpls) {
            for (Trait tr : traitImpl.getTraits()) {
                if (trait.equals(tr)) {
                    return traitImpl;
                }
            }
        }
        return null;
    }

    @Override
    public double getBaseLevel(User user, Trait trait) {
        Player player = ((BukkitUser) user).getPlayer();
        BukkitTraitHandler traitImpl = getTraitImpl(trait);
        if (traitImpl != null) {
            return traitImpl.getBaseLevel(player, trait);
        } else {
            return 0.0;
        }
    }

    @Override
    public void registerTraitHandler(TraitHandler traitHandler) {
        if (traitHandler instanceof BukkitTraitHandler bukkitTraitHandler) {
            registerTraitImpl(bukkitTraitHandler);
        }
    }
}
