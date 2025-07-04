package dev.aurelium.auraskills.bukkit.user;

import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.user.SkillsUser;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.util.TestSession;
import org.bukkit.permissions.PermissionAttachment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.UUID;

import static dev.aurelium.auraskills.api.skill.Skills.*;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;

public class BukkitUserTest {

    private static ServerMock server;
    private static AuraSkills plugin;

    @BeforeAll
    static void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(AuraSkills.class, TestSession.create());
    }

    @AfterAll
    static void unload() {
        MockBukkit.unmock();
    }

    @Test
    void testGetPlayer() {
        PlayerMock player = server.addPlayer();
        BukkitUser user = new BukkitUser(player.getUniqueId(), player, plugin);

        assertNotNull(BukkitUser.getPlayer(user));
        assertEquals(player.getUniqueId(), requireNonNull(BukkitUser.getPlayer(user)).getUniqueId());

        SkillsUser skillsUser = user.toApi();

        assertNotNull(BukkitUser.getPlayer(skillsUser));
        assertEquals(player.getUniqueId(), requireNonNull(BukkitUser.getPlayer(skillsUser)).getUniqueId());

        BukkitUser userWithoutPlayer = new BukkitUser(player.getUniqueId(), null, plugin);

        assertNull(BukkitUser.getPlayer(userWithoutPlayer));

        SkillsUser skillsUserWithoutPlayer = userWithoutPlayer.toApi();

        assertNull(BukkitUser.getPlayer(skillsUserWithoutPlayer));
    }

    @Test
    void testGetUser() {
        PlayerMock player = server.addPlayer();
        BukkitUser user = new BukkitUser(player.getUniqueId(), player, plugin);

        SkillsUser skillsUser = user.toApi();

        assertEquals(user, BukkitUser.getUser(skillsUser));
    }

    @Test
    void testGetUsername() {
        PlayerMock player = server.addPlayer("SomeUsername");
        BukkitUser user = new BukkitUser(player.getUniqueId(), player, plugin);

        assertEquals("SomeUsername", user.getUsername());
    }

    @Test
    void testSendMessage() {
        PlayerMock player = server.addPlayer();
        BukkitUser user = new BukkitUser(player.getUniqueId(), player, plugin);

        user.sendMessage("test message");
        assertEquals("test message", player.nextMessage());

        BukkitUser userWithoutPlayer = new BukkitUser(player.getUniqueId(), null, plugin);

        userWithoutPlayer.sendMessage("test message");
        assertNull(player.nextMessage());
    }

    @Test
    void testPermissionMultipliers() {
        PlayerMock player = server.addPlayer();
        BukkitUser user = new BukkitUser(player.getUniqueId(), player, plugin);

        PermissionAttachment permissionAttachment = player.addAttachment(plugin);
        permissionAttachment.setPermission("auraskills.multiplier.50", true);

        assertEquals(0.5, user.getPermissionMultiplier(null));
        assertEquals(0.5, user.getPermissionMultiplier(FARMING));

        permissionAttachment.setPermission("auraskills.multiplier.10", true);
        permissionAttachment.setPermission("auraskills.multiplier.malformed", true);

        assertEquals(0.6, user.getPermissionMultiplier(null));
        assertEquals(0.6, user.getPermissionMultiplier(FARMING));

        permissionAttachment.setPermission("auraskills.multiplier.farming.40", true);
        permissionAttachment.setPermission("auraskills.multiplier.auraskills/mining.100", true);

        assertEquals(0.6, user.getPermissionMultiplier(null));
        assertEquals(1, user.getPermissionMultiplier(FARMING));
        assertEquals(1.6, user.getPermissionMultiplier(MINING));
        for (Skills skill : values()) {
            if (skill == FARMING || skill == MINING) continue;

            assertEquals(0.6, user.getPermissionMultiplier(skill));
        }
    }

    @Test
    void testPermissionJobLimit() {
        PlayerMock player = server.addPlayer();
        BukkitUser user = new BukkitUser(player.getUniqueId(), player, plugin);

        PermissionAttachment permissionAttachment = player.addAttachment(plugin);
        permissionAttachment.setPermission("auraskills.jobs.limit.1", true);

        assertEquals(1, user.getPermissionJobLimit());

        permissionAttachment.setPermission("auraskills.jobs.limit.4", true);
        permissionAttachment.setPermission("auraskills.jobs.limit.malformed", true);

        assertEquals(4, user.getPermissionJobLimit());
    }

    @Test
    void testGetWorld() {
        PlayerMock player = server.addPlayer();
        BukkitUser user = new BukkitUser(player.getUniqueId(), player, plugin);

        assertEquals("world", user.getWorld());

        BukkitUser userWithoutPlayer = new BukkitUser(UUID.randomUUID(), null, plugin);

        assertEquals("world", userWithoutPlayer.getWorld());
    }

    @Test
    void testHasPermission() {
        PlayerMock player = server.addPlayer();
        BukkitUser user = new BukkitUser(player.getUniqueId(), player, plugin);

        String permission = "some.permission";
        assertFalse(user.hasPermission(permission));

        PermissionAttachment permissionAttachment = player.addAttachment(plugin);
        permissionAttachment.setPermission(permission, true);

        assertTrue(user.hasPermission(permission));

        permissionAttachment.setPermission(permission, false);

        assertFalse(user.hasPermission(permission));

        BukkitUser userWithoutPlayer = new BukkitUser(UUID.randomUUID(), null, plugin);

        assertFalse(userWithoutPlayer.hasPermission(permission));
    }

    @Test
    void testCanSelectJob() {
        PlayerMock player = server.addPlayer();
        BukkitUser user = new BukkitUser(player.getUniqueId(), player, plugin);

        assertTrue(user.canSelectJob(FARMING));
        assertTrue(user.canSelectJob(MINING));

        PermissionAttachment permissionAttachment = player.addAttachment(plugin);
        permissionAttachment.setPermission("auraskills.jobs.block.farming", true);
        permissionAttachment.setPermission("auraskills.jobs.block.auraskills/mining", true);
        permissionAttachment.setPermission("auraskills.jobs.block.defense", false);

        assertFalse(user.canSelectJob(FARMING));
        assertFalse(user.canSelectJob(MINING));
        assertTrue(user.canSelectJob(DEFENSE));

        BukkitUser userWithoutPlayer = new BukkitUser(UUID.randomUUID(), null, plugin);

        assertTrue(userWithoutPlayer.canSelectJob(MINING));
    }

}
