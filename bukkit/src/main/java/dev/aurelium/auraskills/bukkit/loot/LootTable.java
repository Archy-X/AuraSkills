package dev.aurelium.auraskills.bukkit.loot;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class LootTable {

	private final String name;
	private final UUID uuid;
	private final LootTableType type;
	private final List<LootPool> pools;
	
	public LootTable(String name, UUID uuid, LootTableType type, List<LootPool> pools) {
		this.name = name;
		this.uuid = uuid;
		this.type = type;
		this.pools = pools;
	}

	public String getName() {
		return name;
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
