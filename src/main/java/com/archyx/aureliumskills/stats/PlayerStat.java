package com.archyx.aureliumskills.stats;

import java.util.HashMap;
import java.util.UUID;

public class PlayerStat {

	private UUID playerId;
	
	private HashMap<Stat, Integer> stats = new HashMap<Stat, Integer>();
	
	public PlayerStat(UUID id) {
		this.playerId = id;
		for (Stat stat : Stat.values()) {
			stats.put(stat, 0);
		}
	}
	
	public int getStatLevel(Stat stat) {
		return stats.get(stat);
	}
	
	public void addStatLevel(Stat stat, int level) {
		stats.put(stat, getStatLevel(stat) + level);
	}
	
	public void setStatLevel(Stat stat, int level) {
		stats.put(stat, level);
	}
	
	public UUID getPlayerId() {
		return playerId;
	}

}
