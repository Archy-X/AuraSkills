package dev.aurelium.auraskills.bukkit.hooks;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.hooks.EconomyHook;
import dev.aurelium.auraskills.common.hooks.Hook;
import dev.aurelium.auraskills.common.hooks.HookRegistrationException;
import dev.aurelium.auraskills.common.user.User;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.spongepowered.configurate.ConfigurationNode;

public class VaultHook extends EconomyHook {

    private final Economy economy;

    public VaultHook(AuraSkills plugin, ConfigurationNode config) {
        super(plugin, config);
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
        } else {
            throw new HookRegistrationException("Failed to get Vault economy RegisteredServiceProvider");
        }
    }

    @Override
    public void deposit(User user, double amount) {
        economy.depositPlayer(((BukkitUser) user).getPlayer(), amount);
    }

    @Override
    public Class<? extends Hook> getTypeClass() {
        return EconomyHook.class;
    }
}
