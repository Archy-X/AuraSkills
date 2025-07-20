package dev.aurelium.auraskills.common.loot;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.common.loot.enchant.LootEnchantments;
import dev.aurelium.auraskills.common.ref.ItemRef;
import org.jetbrains.annotations.Nullable;

public record ItemSupplier(
        @Nullable ItemRef baseItem,
        @Nullable NamespacedId baseItemKey,
        @Nullable LootEnchantments enchantments
) {}
