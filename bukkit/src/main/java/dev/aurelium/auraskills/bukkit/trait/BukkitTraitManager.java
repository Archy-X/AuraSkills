package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.bukkit.BukkitTraitHandler;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitHandler;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.trait.TraitManager;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BukkitTraitManager extends TraitManager {

    private final AuraSkills plugin;
    private final Map<Class<?>, BukkitTraitHandler> traitImpls = new HashMap<>();

    public BukkitTraitManager(AuraSkills plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    public void registerTraitImplementations() {
        registerTraitImpl(new HpTrait(plugin));
        registerTraitImpl(new HealthRegenTraits(plugin));
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
        registerTraitImpl(new GatheringLuckTraits(plugin));
    }

    public void registerTraitImpl(BukkitTraitHandler bukkitTrait) {
        traitImpls.put(bukkitTrait.getClass(), bukkitTrait);
        if (bukkitTrait instanceof Listener eventListener) {
            Bukkit.getPluginManager().registerEvents(eventListener, plugin);
        }
    }

    public <T extends BukkitTraitHandler> T getTraitImpl(Class<T> clazz) {
        BukkitTraitHandler traitHandler = traitImpls.get(clazz);
        if (traitHandler != null) {
            return clazz.cast(traitHandler);
        }
        throw new IllegalArgumentException("Trait implementation of type " + clazz.getSimpleName() + " not found!");
    }

    @Nullable
    public BukkitTraitHandler getTraitImpl(Trait trait) {
        for (BukkitTraitHandler traitImpl : traitImpls.values()) {
            for (Trait tr : traitImpl.getTraits()) {
                if (trait.getId().equals(tr.getId())) {
                    return traitImpl;
                }
            }
        }
        return null;
    }

    @Override
    public double getBaseLevel(User user, Trait trait) {
        Player player = ((BukkitUser) user).getPlayer();
        if (player == null) return 0.0;

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

    @Override
    public String getMenuDisplay(Trait trait, double value, Locale locale) {
        BukkitTraitHandler impl = getTraitImpl(trait);
        if (impl != null) {
            return impl.getMenuDisplay(value, trait, locale);
        } else {
            return NumberUtil.format1(value);
        }
    }
}
