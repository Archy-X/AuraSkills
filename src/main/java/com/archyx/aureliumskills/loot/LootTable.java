package com.archyx.aureliumskills.loot;

import java.util.List;

public class LootTable {
	
	private List<Loot> loot;
	private String id;
	
	public LootTable(String id, List<Loot> loot) {
		this.loot = loot;
		this.id = id;
	}

	public List<Loot> getLoot() {
		return loot;
	}
	
	public String getId() {
		return id;
	}
	
	public void setLoot(List<Loot> loot) {
		this.loot = loot;
	}
	
}
