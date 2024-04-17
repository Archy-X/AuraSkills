package dev.aurelium.auraskills.api.loot;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class LootTable {

	private final NamespacedId id;
	private final UUID uuid;
	private final LootTableType type;
	private final List<LootPool> pools;
	
	public LootTable(NamespacedId id, UUID uuid, LootTableType type, List<LootPool> pools) {
		this.id = id;
		this.uuid = uuid;
		this.type = type;
		this.pools = pools;
	}

	public NamespacedId getId() {
		return id;
	}

	public UUID uuid() {
		return uuid;
	}

	public LootTableType getType() {
		return type;
	}

	@Nullable
	public LootPool getPool(String name) {
		for (LootPool pool : pools) {
			if (pool.getName().equals(name)) {
				return pool;
			}
		}
		return null;
	}

	public List<LootPool> getPools() {
		return pools;
	}

}
