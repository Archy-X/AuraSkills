package com.archyx.aureliumskills.loot;

import java.util.Random;

import org.bukkit.inventory.ItemStack;

public class Loot {

	private ItemStack item;
	private int minAmount;
	private int maxAmount;
	private Random r = new Random();
	
	public Loot(ItemStack item, int minAmount, int maxAmount) {
		this.item = item;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public int getMinAmount() {
		return minAmount;
	}
	
	public int getMaxAmount() {
		return maxAmount;
	}
	
	public ItemStack getDrop() {
		ItemStack drop = item.clone();
		int amount = r.nextInt(maxAmount - minAmount + 1) + minAmount;
		if (amount != 0) {
			drop.setAmount(amount);
			return drop;
		}
		else {
			return null;
		}
	}
}
