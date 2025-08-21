package dev.aurelium.auraskills.bukkit.item;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.item.LootItemFilter;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.item.LootSourceItem;
import dev.aurelium.auraskills.common.item.SourceItem;
import dev.aurelium.auraskills.common.item.SourceItemMeta;
import dev.aurelium.auraskills.common.item.SourcePotionData;
import dev.aurelium.auraskills.common.util.TestSession;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class BukkitItemRegistryTest {

    private static ServerMock server;
    private static AuraSkills plugin;

    @BeforeAll
    static void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(AuraSkills.class, TestSession.create());
        server.getScheduler().performOneTick();
    }

    @AfterAll
    static void unload() {
        MockBukkit.unmock();
    }

    @Test
    void testRegister() {
        BukkitItemRegistry registry = new BukkitItemRegistry(plugin);

        var id = NamespacedId.of("auraskills", "some_item");
        registry.register(id, new ItemStack(Material.STONE));

        assertEquals(Material.STONE, Objects.requireNonNull(registry.getItem(id)).getType());
        assertEquals(Set.of(id), registry.getIds());
        assertTrue(registry.containsItem(id));
        assertEquals(1, registry.getItemAmount(id));

        registry.unregister(id);

        assertNull(registry.getItem(id));
    }

    @Test
    void testGiveItem() {
        BukkitItemRegistry registry = new BukkitItemRegistry(plugin);

        PlayerMock player = server.addPlayer();
        BukkitUser user = new BukkitUser(player.getUniqueId(), player, plugin);

        var id = NamespacedId.of("auraskills", "some_item");
        registry.register(id, new ItemStack(Material.DIAMOND));

        registry.giveItem(user, id, 1);
        server.getScheduler().performOneTick();

        ItemStack item0 = player.getInventory().getItem(0);
        assertNotNull(item0);
        assertEquals(Material.DIAMOND, item0.getType());
        assertEquals(1, item0.getAmount());

        // Fill the entire inventory to test leftovers
        registry.giveItem(user, id, 64 * 36);
        server.getScheduler().performOneTick();

        for (int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);
            assertNotNull(item);
            assertEquals(Material.DIAMOND, item.getType());
            assertEquals(64, item.getAmount());
        }

        assertEquals(1, user.getUnclaimedItems().size());
        assertEquals(id.toString(), user.getUnclaimedItems().getFirst().getKey());
        assertEquals(1, user.getUnclaimedItems().getFirst().getValue());
    }

    @Test
    @SuppressWarnings("deprecation")
    void testGetEffectiveItemName() {
        BukkitItemRegistry registry = new BukkitItemRegistry(plugin);

        var id1 = NamespacedId.of("auraskills", "stone");
        var item1 = new ItemStack(Material.STONE);
        ItemMeta meta1 = item1.getItemMeta();
        meta1.setDisplayName("Name");
        item1.setItemMeta(meta1);
        registry.register(id1, item1);

        assertNotNull(registry.getEffectiveItemName(id1));
        assertEquals("Name", registry.getEffectiveItemName(id1));

        var id2 = NamespacedId.of("auraskills", "white_stained_glass");
        var item2 = new ItemStack(Material.WHITE_STAINED_GLASS);
        registry.register(id2, item2);

        assertNotNull(registry.getEffectiveItemName(id2));
        assertEquals("white stained glass", registry.getEffectiveItemName(id2));

        assertNull(registry.getEffectiveItemName(NamespacedId.of("auraskills", "nonexistent_key")));
    }

    @Test
    @SuppressWarnings("deprecation")
    void testPassesFilter() {
        BukkitItemRegistry registry = new BukkitItemRegistry(plugin);

        ItemStack item = new ItemStack(Material.POTION, 1);
        var meta = ((PotionMeta) item.getItemMeta());
        meta.setDisplayName("test");
        meta.setLore(List.of("line1", "line2"));
        meta.setBasePotionType(PotionType.STRONG_STRENGTH);
        item.setItemMeta(meta);

        ItemFilter filter = new SourceItem(
                new String[]{"potion", "splash_potion"},
                new String[]{"diamond", "iron_ingot", "stone"},
                null,
                new SourceItemMeta(
                        "test",
                        List.of("line1", "line2"),
                        new SourcePotionData(
                                new String[]{"strength", "strong_strength"},
                                new String[]{"poison"},
                                false,
                                true,
                                true),
                        false,
                        0,
                        false));

        assertTrue(registry.passesFilter(item, filter));
        assertFalse(registry.passesFilter(new ItemStack(Material.IRON_HELMET), filter));
        assertFalse(registry.passesFilter(new ItemStack(Material.DIAMOND), filter));
    }

    @Test
    void testPassesFilterWithLootItemFilter() {
        BukkitItemRegistry registry = new BukkitItemRegistry(plugin);

        LootItemFilter filter = new LootSourceItem(
                null,
                null,
                null,
                null,
                "rare");

        assertTrue(registry.passesFilter(new ItemStack(Material.BONE), filter, Skills.FISHING));
        assertFalse(registry.passesFilter(new ItemStack(Material.WHITE_STAINED_GLASS_PANE), filter, Skills.FISHING));
        assertFalse(registry.passesFilter(new ItemStack(Material.BONE), filter, Skills.AGILITY));
    }

}
