package com.archyx.aureliumskills.menu;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.menu.items.ConfigurableItem;
import com.archyx.aureliumskills.menu.items.ItemType;
import com.archyx.aureliumskills.menu.templates.ConfigurableTemplate;
import com.archyx.aureliumskills.menu.templates.TemplateType;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MenuLoader {

    private final Map<MenuType, MenuOption> menus;
    private final AureliumSkills plugin;

    public MenuLoader(AureliumSkills plugin) {
        this.plugin = plugin;
        menus = new HashMap<>();
    }

    public void load() throws IllegalAccessException, InstantiationException {
        File file = new File(plugin.getDataFolder(), "menus.yml");
        if (!file.exists()) {
            plugin.saveResource("menus.yml", false);
        }
        FileConfiguration config = updateFile(file, YamlConfiguration.loadConfiguration(file));
        int menusLoaded = 0;
        int itemsLoaded = 0;
        int templatesLoaded = 0;
        long start = System.currentTimeMillis();
        //Load skills menu
        for (MenuType menuType : MenuType.values()) {
            ConfigurationSection menu = config.getConfigurationSection(menuType.getPath());
            if (menu != null) {
                MenuOption menuOption = new MenuOption(menuType);
                menuOption.setTitle(menu.getString("title"));
                menuOption.setRows(menu.getInt("rows"));
                menuOption.setFillEnabled(menu.getBoolean("fill.enabled", true));
                //Load fill material
                String materialString = menu.getString("fill.material", "BLACK_STAINED_GLASS_PANE");
                ItemStack fillItem;
                if (materialString != null) {
                    try {
                        fillItem = XMaterial.valueOf(materialString.toUpperCase()).parseItem();
                    } catch (IllegalArgumentException e) {
                        fillItem = XMaterial.BLACK_STAINED_GLASS_PANE.parseItem();
                    }
                }
                else {
                    fillItem = XMaterial.BLACK_STAINED_GLASS_PANE.parseItem();
                }
                if (fillItem != null) {
                    ItemMeta meta = fillItem.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(" ");
                        fillItem.setItemMeta(meta);
                    }
                }
                menuOption.setFillItem(fillItem);
                // Load items
                for (ItemType itemType : menuType.getItems()) {
                    Class<?> loader = itemType.getLoader();
                    ConfigurableItem configurableItem = (ConfigurableItem) loader.newInstance();
                    configurableItem.load(Objects.requireNonNull(menu.getConfigurationSection("items." + itemType.name().toLowerCase())));
                    menuOption.putItem(configurableItem);
                    itemsLoaded++;
                }
                // Load templates
                for (TemplateType templateType : menuType.getTemplates()) {
                    Class<?> loader = templateType.getLoader();
                    ConfigurableTemplate configurableTemplate = (ConfigurableTemplate) loader.newInstance();
                    configurableTemplate.load(Objects.requireNonNull(menu.getConfigurationSection("templates." + templateType.name().toLowerCase())));
                    menuOption.putTemplate(configurableTemplate);
                    templatesLoaded++;
                }
                menus.put(menuType, menuOption);
                menusLoaded++;
            }
        }
        long end = System.currentTimeMillis();
        Bukkit.getLogger().info("[AureliumSkills] Loaded " + menusLoaded + " menus, " + itemsLoaded + " items, and " + templatesLoaded + " templates in " + (end - start) + " ms");
    }

    public MenuOption getMenu(MenuType type) {
        return menus.get(type);
    }

    public static ItemStack parseItem(String input) {
        if (input != null) {
            String[] splitInput = input.split(" ", 2);
            String material = splitInput[0];
            try {
                ItemStack item = XMaterial.valueOf(material.toUpperCase()).parseItem();
                if (item != null) {
                    // Set base meta options
                    ItemMeta originalMeta = item.getItemMeta();
                    if (originalMeta != null) {
                        originalMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        item.setItemMeta(originalMeta);
                    }
                    // Apply args
                    if (splitInput.length > 1) {
                        String[] args = splitInput[1].split(" ");
                        if (args.length > 0) {
                            for (String argument : args) {
                                if (argument.split(":").length > 1) {
                                    String key = argument.split(":")[0];
                                    String value = argument.split(":")[1];
                                    // Potion type meta
                                    if (key.equalsIgnoreCase("potion_type") && item.getItemMeta() instanceof PotionMeta) {
                                        PotionMeta meta = (PotionMeta) item.getItemMeta();
                                        meta.setBasePotionData(new PotionData(PotionType.valueOf(value.toUpperCase())));
                                        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                                        item.setItemMeta(meta);
                                    }
                                    // Glow meta
                                    else if (key.equalsIgnoreCase("glow") && value.equalsIgnoreCase("true")) {
                                        ItemMeta meta = item.getItemMeta();
                                        if (meta != null) {
                                            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
                                            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                                            item.setItemMeta(meta);
                                        }
                                    }
                                    // Hide attributes
                                    else if (key.equalsIgnoreCase("hide_attributes") && value.equalsIgnoreCase("false")) {
                                        ItemMeta meta = item.getItemMeta();
                                        if (meta != null) {
                                            if (meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
                                                meta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                                                item.setItemMeta(meta);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else {
                    Bukkit.getLogger().warning("[AureliumSkills] Error parsing material with input " + input + ", the input material " + material + " is invalid!");
                    item = new ItemStack(Material.STONE);
                }
                return item;
            } catch (Exception e) {
                Bukkit.getLogger().warning("[AureliumSkills] Error parsing material with input " + input);
                e.printStackTrace();
                return new ItemStack(Material.STONE);
            }
        }
        else {
            return new ItemStack(Material.STONE);
        }
    }

    private FileConfiguration updateFile(File file, FileConfiguration config) {
        InputStream stream = plugin.getResource("menus.yml");
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
                    Bukkit.getLogger().info("[AureliumSkills] menus.yml was updated to a new file version, " + keysAdded + " new keys were added.");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

}
