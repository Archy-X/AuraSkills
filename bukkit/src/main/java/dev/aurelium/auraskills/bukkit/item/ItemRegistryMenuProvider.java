package dev.aurelium.auraskills.bukkit.item;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.slate.item.provider.KeyedItemProvider;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemRegistryMenuProvider implements KeyedItemProvider {

    private final BukkitItemRegistry itemRegistry;

    public ItemRegistryMenuProvider(BukkitItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    @Override
    public @Nullable ItemStack getItem(String key) {
        return itemRegistry.getItem(NamespacedId.fromDefault(key));
    }
}
