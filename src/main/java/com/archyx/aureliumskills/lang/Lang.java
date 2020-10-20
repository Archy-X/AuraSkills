package com.archyx.aureliumskills.lang;

import co.aikar.commands.MessageKeys;
import co.aikar.commands.MinecraftMessageKeys;
import co.aikar.commands.PaperCommandManager;
import com.archyx.aureliumskills.util.LoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Lang implements Listener {

	private static final Map<Locale, Map<MessageKey, String>> messages = new HashMap<>();
	private static final Map<UUID, Locale> playerLanguages = new HashMap<>();
	public static Locale defaultLanguage;
	private final Plugin plugin;

	public Lang(Plugin plugin) {
		this.plugin = plugin;
		File file = new File(plugin.getDataFolder(), "messages_en.yml");
		if (!file.exists()) {
			plugin.saveResource("messages_en.yml", false);
		}
		if (!new File(plugin.getDataFolder(), "messages_id.yml").exists()) {
			plugin.saveResource("messages_id.yml", false);
		}
	}

	public void loadEmbeddedMessages(PaperCommandManager commandManager) {
		// Loads default file from embedded resource
		InputStream inputStream = plugin.getResource("messages_en.yml");
		if (inputStream != null) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
			Locale locale = new Locale("en");
			// Load messages
			loadMessages(config, locale, commandManager);
		}
	}

	public void loadLanguages(PaperCommandManager commandManager) {
		Bukkit.getLogger().info("[AureliumSkills] Loading languages...");
		long startTime = System.currentTimeMillis();
		FileConfiguration pluginConfig = plugin.getConfig();
		// Load languages list and default, add to default if not present
		List<String> languages = pluginConfig.getStringList("languages").stream().map(String::toLowerCase).collect(Collectors.toList());
		String defaultLanguageString = pluginConfig.getString("default-language");
		if (defaultLanguageString == null) {
			defaultLanguageString = "en";
		}
		else {
			defaultLanguageString = defaultLanguageString.toLowerCase();
		}
		if (!languages.contains(defaultLanguageString)) {
			languages.add(defaultLanguageString);
		}
		// Sets default language
		defaultLanguage = new Locale(defaultLanguageString);
		// Load languages
		for (String language : languages) {
			// Load file
			try {
				Locale locale = new Locale(language);
				File file = new File(plugin.getDataFolder(), "messages_" + language + ".yml");
				// Load and update file
				FileConfiguration config = updateFile(file, YamlConfiguration.loadConfiguration(file));
				// Load messages
				loadMessages(config, locale, commandManager);
			} catch (Exception e) {
				Bukkit.getLogger().warning("[AureliumSkills] Error loading messages file messages_" + language + ".yml");
				e.printStackTrace();
			}
		}
		long endTime = System.currentTimeMillis();
		Bukkit.getLogger().info("[AureliumSkills] Loaded " + languages.size() + " languages in " + (endTime - startTime) + "ms");
	}

	private void loadMessages(FileConfiguration config, Locale locale, PaperCommandManager commandManager) {
		// Load units
		Map<UnitMessage, String> units = new HashMap<>();
		for (UnitMessage key : UnitMessage.values()) {
			String message = config.getString(key.getPath());
			if (message != null) {
				units.put(key, message.replace('&', '§'));
			}
		}
		// Load message keys
		Map<MessageKey, String> messages = new HashMap<>();
		for (MessageKey key : MessageKey.values()) {
			String message = config.getString(key.getPath());
			if (message != null) {
				messages.put(key, LoreUtil.replace(message
						,"&", "§"
						,"{mana_unit}", units.get(UnitMessage.MANA)
						,"{hp_unit}", units.get(UnitMessage.HP)
						,"{xp_unit}", units.get(UnitMessage.XP)));
			} else {
				Bukkit.getLogger().severe("[AureliumSkills] Message with path " + key.getPath() + " was null!");
			}
		}
		for (ACFCoreMessage message : ACFCoreMessage.values()) {
			String path = message.getPath();
			commandManager.getLocales().addMessage(locale, MessageKeys.valueOf(message.name()), LoreUtil.replace(config.getString(path), "&", "§"));
		}
		for (ACFMinecraftMessage message : ACFMinecraftMessage.values()) {
			String path = message.getPath();
			commandManager.getLocales().addMessage(locale, MinecraftMessageKeys.valueOf(message.name()), LoreUtil.replace(config.getString(path), "&", "§"));
		}
		Lang.messages.put(locale, messages);
	}

	private FileConfiguration updateFile(File file, FileConfiguration config) {
		if (config.contains("file_version")) {
			InputStream stream = plugin.getResource("messages_en.yml");
			if (stream != null) {
				int currentVersion = config.getInt("file_version");
				FileConfiguration imbConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
				int imbVersion = imbConfig.getInt("file_version");
				//If versions do not match
				if (currentVersion != imbVersion) {
					try {
						ConfigurationSection configSection = imbConfig.getConfigurationSection("");
						int keysAdded = 0;
						if (configSection != null) {
							for (String key : configSection.getKeys(true)) {
								if (!config.contains(key)) {
									config.set(key, imbConfig.get(key));
									keysAdded++;
								}
							}
						}
						config.save(file);
						Bukkit.getLogger().info("[AureliumSkills] messages_" + defaultLanguage + ".yml was updated to a new file version, " + keysAdded + " new keys were added.");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return YamlConfiguration.loadConfiguration(file);
	}

	public static String getMessage(MessageKey key, Locale locale) {
		// Set default locale if locale not present
		if (!messages.containsKey(locale)) {
			locale = Locale.ENGLISH;
		}
		String message = messages.get(locale).get(key);
		if (message != null) {
			return message;
		} else {
			return Lang.messages.get(Locale.ENGLISH).get(key);
		}
	}

	public static Locale getLanguage(Player player) {
		Locale locale = playerLanguages.get(player.getUniqueId());
		return locale != null ? locale : Locale.ENGLISH;
	}

	public static Locale getLanguage(CommandSender sender) {
		if (sender instanceof Player) {
			Locale locale = playerLanguages.get(((Player) sender).getUniqueId());
			return locale != null ? locale : Locale.ENGLISH;
		}
		else {
			return Locale.ENGLISH;
		}
	}

	public static void setLanguage(Player player, Locale locale) {
		Lang.playerLanguages.put(player.getUniqueId(), locale);
	}

	public static boolean hasLocale(Locale locale) {
		return messages.containsKey(locale);
	}

	public static Set<String> getLanguages() {
		return messages.keySet().stream().map(Locale::toLanguageTag).collect(Collectors.toSet());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (!playerLanguages.containsKey(event.getPlayer().getUniqueId())) {
			try {
				Locale locale = new Locale(event.getPlayer().getLocale().split("_")[0].toLowerCase());
				if (messages.containsKey(locale)) {
					playerLanguages.put(event.getPlayer().getUniqueId(), locale);
				} else {
					playerLanguages.put(event.getPlayer().getUniqueId(), Locale.ENGLISH);
				}
			} catch (Exception e) {
				playerLanguages.put(event.getPlayer().getUniqueId(), Locale.ENGLISH);
			}
		}
	}

}
