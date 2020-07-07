package io.github.archy_x.aureliumskills;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;
import fr.minuskube.inv.InventoryManager;
import io.github.archy_x.aureliumskills.commands.SkillsCommand;
import io.github.archy_x.aureliumskills.commands.StatsCommand;
import io.github.archy_x.aureliumskills.listeners.BlockBreak;
import io.github.archy_x.aureliumskills.listeners.BlockPlace;
import io.github.archy_x.aureliumskills.listeners.PlayerJoin;
import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import io.github.archy_x.aureliumskills.skills.abilities.Ability;
import io.github.archy_x.aureliumskills.skills.abilities.FarmingAbilities;
import io.github.archy_x.aureliumskills.skills.levelers.AgilityLeveler;
import io.github.archy_x.aureliumskills.skills.levelers.AlchemyLeveler;
import io.github.archy_x.aureliumskills.skills.levelers.ArcheryLeveler;
import io.github.archy_x.aureliumskills.skills.levelers.DefenseLeveler;
import io.github.archy_x.aureliumskills.skills.levelers.EnchantingLeveler;
import io.github.archy_x.aureliumskills.skills.levelers.EnduranceLeveler;
import io.github.archy_x.aureliumskills.skills.levelers.ExcavationLeveler;
import io.github.archy_x.aureliumskills.skills.levelers.FarmingLeveler;
import io.github.archy_x.aureliumskills.skills.levelers.FightingLeveler;
import io.github.archy_x.aureliumskills.skills.levelers.FishingLeveler;
import io.github.archy_x.aureliumskills.skills.levelers.ForagingLeveler;
import io.github.archy_x.aureliumskills.skills.levelers.ForgingLeveler;
import io.github.archy_x.aureliumskills.skills.levelers.HealingLeveler;
import io.github.archy_x.aureliumskills.skills.levelers.Leveler;
import io.github.archy_x.aureliumskills.skills.levelers.MiningLeveler;
import io.github.archy_x.aureliumskills.stats.ActionBar;
import io.github.archy_x.aureliumskills.stats.Health;
import io.github.archy_x.aureliumskills.stats.Regeneration;
import io.github.archy_x.aureliumskills.stats.Strength;
import io.github.archy_x.aureliumskills.stats.Toughness;

public class AureliumSkills extends JavaPlugin{

	private PaperCommandManager commandManager;
	private File dataFile = new File(getDataFolder(), "data.yml");
	private FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
	private SkillLoader skillLoader = new SkillLoader(dataFile, config);
	public static InventoryManager invManager;
	
	public static String tag = ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "Skills" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET;
	
	public void onEnable() {
		invManager = new InventoryManager(this);
		invManager.init();
		//Registers Commands
		registerCommands();
		//Registers Events
		getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
		getServer().getPluginManager().registerEvents(new BlockPlace(this), this);
		getServer().getPluginManager().registerEvents(new BlockBreak(this), this);
		getServer().getPluginManager().registerEvents(new FarmingLeveler(), this);
		getServer().getPluginManager().registerEvents(new ForagingLeveler(), this);
		getServer().getPluginManager().registerEvents(new MiningLeveler(), this);
		getServer().getPluginManager().registerEvents(new ExcavationLeveler(), this);
		getServer().getPluginManager().registerEvents(new FishingLeveler(), this);
		getServer().getPluginManager().registerEvents(new FightingLeveler(), this);
		getServer().getPluginManager().registerEvents(new ArcheryLeveler(), this);
		getServer().getPluginManager().registerEvents(new DefenseLeveler(), this);
		getServer().getPluginManager().registerEvents(new AgilityLeveler(this), this);
		getServer().getPluginManager().registerEvents(new AlchemyLeveler(this), this);
		getServer().getPluginManager().registerEvents(new EnchantingLeveler(), this);
		getServer().getPluginManager().registerEvents(new ForgingLeveler(), this);
		getServer().getPluginManager().registerEvents(new HealingLeveler(), this);
		getServer().getPluginManager().registerEvents(new Health(), this);
		getServer().getPluginManager().registerEvents(new Toughness(), this);
		getServer().getPluginManager().registerEvents(new Strength(), this);
		getServer().getPluginManager().registerEvents(new FarmingAbilities(this), this);
		Regeneration regeneration = new Regeneration(this);
		getServer().getPluginManager().registerEvents(regeneration, this);
		regeneration.startRegen();
		regeneration.startSaturationRegen();
		EnduranceLeveler enduranceLeveler = new EnduranceLeveler(this);
		ActionBar actionBar = new ActionBar(this);
		actionBar.startUpdateActionBar();
		//Load Data
		if (dataFile.exists()) {
			skillLoader.loadSkillData();
		}	
		else {
			saveResource("data.yml", false);
		}
		Leveler.plugin = this;
		Leveler.loadLevelReqs();
		enduranceLeveler.startTracking();
		Bukkit.getConsoleSender().sendMessage(tag + ChatColor.GREEN + "Aurelium Skills has been enabled");
	}
	
	public void onDisable() {
		//Save Data
		skillLoader.saveSkillData();
	}
	
	private void registerCommands() {
		commandManager = new PaperCommandManager(this);
		commandManager.getCommandCompletions().registerAsyncCompletion("skills", c -> {
			List<String> values = new ArrayList<String>();
			for (Skill skill : Skill.values()) {
				values.add(skill.toString().toLowerCase());
			}
			return values;
		});
		commandManager.getCommandCompletions().registerAsyncCompletion("abilities", c -> {
			List<String> values = new ArrayList<String>();
			for (Ability value : Ability.values()) {
				values.add(value.toString().toLowerCase());
			}
			return values;
		});
		commandManager.registerCommand(new SkillsCommand());
		commandManager.registerCommand(new StatsCommand());
	}
	
}
