package com.archyx.aureliumskills.menu;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.Stat;
import com.cryptomorin.xseries.XMaterial;
import fr.minuskube.inv.content.SlotPos;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MenuLoader {

    private final Map<String, MenuOption> menus;
    private final AureliumSkills plugin;

    public MenuLoader(AureliumSkills plugin) {
        this.plugin = plugin;
        menus = new HashMap<>();
    }

    public void load() {
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
        for (String menuName : MenuConstants.MENU_NAMES) {
            ConfigurationSection menu = config.getConfigurationSection(menuName);
            if (menu != null) {
                MenuOption menuOption = new MenuOption(menuName);
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
                //Load items
                for (String itemName : MenuConstants.menuItems.get(menuName)) {
                    ConfigurationSection item = config.getConfigurationSection(menuName + ".items." + itemName);
                    if (item != null) {
                        int row = item.getInt("row");
                        int column = item.getInt("column");
                        ItemStack baseItem = null;
                        Map<Object, ItemStack> baseItems = new HashMap<>();
                        if (!itemName.equals("skill")) {
                            baseItem = parseItem(item.getString("material"));
                            if (baseItem == null) {
                                baseItem = new ItemStack(Material.STONE);
                            }
                            ItemMeta meta = baseItem.getItemMeta();
                            if (meta != null) {
                                meta.setDisplayName(Objects.requireNonNull(item.getString("display_name"), "Item " + itemName + " cannot have a null display_name!").replace('&', '§'));
                                List<String> lore = item.getStringList("lore").stream().map(line -> line.replace('&', '§')).collect(Collectors.toList());
                                meta.setLore(lore);
                                baseItem.setItemMeta(meta);
                            }
                        }
                        else {
                            for (String line : item.getStringList("material")) {
                                if (line.split(" ").length > 1) {
                                    Skill skill = Skill.valueOf(line.split(" ")[0]);
                                    ItemStack itemStack = parseItem(line.split(" ")[1]);
                                    if (itemStack == null) {
                                        itemStack = new ItemStack(Material.STONE);
                                    }
                                    ItemMeta itemMeta = itemStack.getItemMeta();
                                    if (itemMeta != null) {
                                        itemMeta.setDisplayName(Objects.requireNonNull(item.getString("display_name"), "Item " + itemName + " cannot have a null display_name!").replace('&', '§'));
                                        List<String> lore = item.getStringList("lore").stream().map(l -> l.replace('&', '§')).collect(Collectors.toList());
                                        itemMeta.setLore(lore);
                                        itemStack.setItemMeta(itemMeta);
                                    }
                                    baseItems.put(skill, itemStack);
                                }
                            }
                        }
                        ItemOption itemOption;
                        if (itemName.equals("skill")) {
                            itemOption = new ItemOption(itemName, row, column, baseItems);
                        }
                        else {
                            itemOption = new ItemOption(itemName, row, column, baseItem);
                        }
                        menuOption.putItem(itemOption);
                        itemsLoaded++;
                    }
                }
                //Load templates
                for (String templateName : MenuConstants.menuTemplates.get(menuName)) {
                    ConfigurationSection template = config.getConfigurationSection(menuName + ".templates." + templateName);
                    if (template != null) {
                        String displayName = Objects.requireNonNull(template.getString("display_name"), "Template " + templateName + " cannot have a null display_name!").replace('&', '§');
                        List<String> lore = template.getStringList("lore");
                        Map<Object, ItemStack> baseItems = new HashMap<>();
                        Map<Object, SlotPos> positions = new HashMap<>();
                        //Load skill template
                        switch (templateName) {
                            case "skill":
                                for (String line : template.getStringList("material")) {
                                    if (line.split(" ").length > 1) {
                                        Skill skill = Skill.valueOf(line.split(" ")[0]);
                                        ItemStack baseItem = parseItem(line.split(" ")[1]);
                                        if (baseItem == null) {
                                            baseItem = new ItemStack(Material.STONE);
                                        }
                                        ItemMeta meta = baseItem.getItemMeta();
                                        if (meta != null) {
                                            meta.setDisplayName(displayName);
                                            meta.setLore(lore);
                                            baseItem.setItemMeta(meta);
                                        }
                                        baseItems.put(skill, baseItem);
                                    }
                                }
                                for (String line : template.getStringList("pos")) {
                                    if (line.split(" ").length > 2) {
                                        Skill skill = Skill.valueOf(line.split(" ")[0]);
                                        SlotPos pos = SlotPos.of(Integer.parseInt(line.split(" ")[1]), Integer.parseInt(line.split(" ")[2]));
                                        positions.put(skill, pos);
                                    }
                                }
                                break;
                            case "stat":
                                for (String line : template.getStringList("material")) {
                                    if (line.split(" ").length > 1) {
                                        Stat stat = Stat.valueOf(line.split(" ")[0]);
                                        ItemStack baseItem = parseItem(line.split(" ")[1]);
                                        if (baseItem == null) {
                                            baseItem = new ItemStack(Material.STONE);
                                        }
                                        ItemMeta meta = baseItem.getItemMeta();
                                        if (meta != null) {
                                            meta.setDisplayName(displayName);
                                            meta.setLore(lore);
                                            baseItem.setItemMeta(meta);
                                        }
                                        baseItems.put(stat, baseItem);
                                    }
                                }
                                for (String line : template.getStringList("pos")) {
                                    if (line.split(" ").length > 2) {
                                        Stat stat = Stat.valueOf(line.split(" ")[0]);
                                        SlotPos pos = SlotPos.of(Integer.parseInt(line.split(" ")[1]), Integer.parseInt(line.split(" ")[2]));
                                        positions.put(stat, pos);
                                    }
                                }
                                break;
                            case "unlocked":
                            case "in_progress":
                            case "locked":
                                ItemStack baseItem = parseItem(template.getString("material"));
                                if (baseItem == null) {
                                    baseItem = new ItemStack(Material.STONE);
                                }
                                ItemMeta meta = baseItem.getItemMeta();
                                if (meta != null) {
                                    meta.setDisplayName(displayName);
                                    meta.setLore(lore);
                                    baseItem.setItemMeta(meta);
                                }
                                baseItems.put("constant", baseItem);
                                positions.put("constant", SlotPos.of(0, 0));
                                break;
                        }
                        ItemTemplate itemTemplate = new ItemTemplate(templateName, baseItems, positions);
                        menuOption.putTemplate(itemTemplate);
                        templatesLoaded++;
                    }
                }
                menus.put(menuName, menuOption);
                menusLoaded++;
            }
        }
        long end = System.currentTimeMillis();
        Bukkit.getLogger().info("[AureliumSkills] Loaded " + menusLoaded + " menus, " + itemsLoaded + " items, and " + templatesLoaded + " templates in " + (end - start) + " ms");
    }

    public MenuOption getMenu(String menuName) {
        return menus.get(menuName);
    }

    private ItemStack parseItem(String input) {
        if (input != null) {
            String material = input.split(" ")[0];
            try {
                ItemStack item = XMaterial.valueOf(material.toUpperCase()).parseItem();
                if (item != null) {
                    String[] args = input.split(" ");
                    if (args.length > 1) {
                        for (int i = 1; i < args.length; i++) {
                            String argument = args[i];
                            if (argument.split(":").length > 1) {
                                String key = argument.split(":")[0];
                                String value = argument.split(":")[1];
                                if (key.equalsIgnoreCase("potion_type") && item.getItemMeta() instanceof PotionMeta) {
                                    PotionMeta meta = (PotionMeta) item.getItemMeta();
                                    meta.setBasePotionData(new PotionData(PotionType.valueOf(value.toUpperCase())));
                                    item.setItemMeta(meta);
                                }
                                else if (key.equalsIgnoreCase("glow") && value.equalsIgnoreCase("true")) {
                                    ItemMeta meta = item.getItemMeta();
                                    if (meta != null) {
                                        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
                                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                                        item.setItemMeta(meta);
                                    }
                                }
                            }
                        }
                    }
                }
                return item;
            } catch (Exception e) {
                Bukkit.getLogger().warning("[AureliumSkills] Error parsing item with input " + input);
                e.printStackTrace();
                return null;
            }
        }
        else {
            return null;
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
