package dev.aurelium.auraskills.bukkit.leaderboard;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.SyncOnlyScheduler;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.leaderboard.LeaderboardManager;
import dev.aurelium.auraskills.common.leaderboard.SkillValue;
import dev.aurelium.auraskills.common.user.SkillLevelMaps;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.TestSession;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import static dev.aurelium.auraskills.api.skill.Skills.*;
import static dev.aurelium.auraskills.bukkit.ref.BukkitPlayerRef.wrap;
import static dev.aurelium.auraskills.common.TestUtil.copyResourceToTemp;
import static org.junit.jupiter.api.Assertions.*;

public class LeaderboardManagerTest {

    private ServerMock server;
    private AuraSkills plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(AuraSkills.class, TestSession.create());
        server.getScheduler().performOneTick();
    }

    @AfterEach
    void unload() {
        MockBukkit.unmock();
    }

    @Test
    void testOnlineUsers() {
        LeaderboardManager leaderboardManager = new LeaderboardManager(plugin, new BukkitLeaderboardExclusion(plugin));

        User user1 = addUser(new SkillLevelMaps(Map.of(AGILITY, 3, FARMING, 5), Map.of(AGILITY, 10.0)));
        User user2 = addUser(new SkillLevelMaps(Map.of(AGILITY, 3), Map.of(AGILITY, 10.1)));
        User user3 = addUser(new SkillLevelMaps(Map.of(AGILITY, 4), Map.of(AGILITY, 0.0)));

        leaderboardManager.updateLeaderboards();

        List<SkillValue> agilityLb = leaderboardManager.getLeaderboard(AGILITY);
        assertNotNull(agilityLb);
        assertEquals(3, agilityLb.size());

        assertEquals(user3.getUuid(), agilityLb.getFirst().id());
        assertEquals(user3.getSkillLevel(AGILITY), agilityLb.getFirst().level());
        assertEquals(user3.getSkillXp(AGILITY), agilityLb.getFirst().xp());

        assertEquals(user2.getUuid(), agilityLb.get(1).id());
        assertEquals(user2.getSkillLevel(AGILITY), agilityLb.get(1).level());
        assertEquals(user2.getSkillXp(AGILITY), agilityLb.get(1).xp());

        assertEquals(user1.getUuid(), agilityLb.getLast().id());
        assertEquals(user1.getSkillLevel(AGILITY), agilityLb.getLast().level());
        assertEquals(user1.getSkillXp(AGILITY), agilityLb.getLast().xp());

        assertEquals(1, leaderboardManager.getSkillRank(AGILITY, user3.getUuid()));
        assertEquals(2, leaderboardManager.getSkillRank(AGILITY, user2.getUuid()));
        assertEquals(3, leaderboardManager.getSkillRank(AGILITY, user1.getUuid()));

        assertEquals(2, leaderboardManager.getLeaderboard(AGILITY, 1, 2).size());

        List<SkillValue> farmingLb = leaderboardManager.getLeaderboard(FARMING);
        assertNotNull(farmingLb);
        assertEquals(3, farmingLb.size());

        assertEquals(user1.getUuid(), farmingLb.getFirst().id());
        assertEquals(user1.getSkillLevel(FARMING), farmingLb.getFirst().level());
        assertEquals(user1.getSkillXp(FARMING), farmingLb.getFirst().xp());

        assertEquals(0, farmingLb.get(1).level());
        assertEquals(0.0, farmingLb.get(1).xp());

        assertEquals(0, farmingLb.getLast().level());
        assertEquals(0.0, farmingLb.getLast().xp());

        assertEquals(1, leaderboardManager.getSkillRank(FARMING, user1.getUuid()));

        List<SkillValue> powerLb = leaderboardManager.getPowerLeaderboard();
        assertNotNull(powerLb);
        assertEquals(3, powerLb.size());

        assertEquals(user1.getUuid(), powerLb.getFirst().id());
        assertEquals(8, powerLb.getFirst().level());
        assertEquals(10.0, powerLb.getFirst().xp());

        assertEquals(user3.getUuid(), powerLb.get(1).id());
        assertEquals(4, powerLb.get(1).level());
        assertEquals(0, powerLb.get(1).xp());

        assertEquals(user2.getUuid(), powerLb.getLast().id());
        assertEquals(3, powerLb.getLast().level());
        assertEquals(10.1, powerLb.getLast().xp());

        assertEquals(1, leaderboardManager.getPowerRank(user1.getUuid()));
        assertEquals(2, leaderboardManager.getPowerRank(user3.getUuid()));
        assertEquals(3, leaderboardManager.getPowerRank(user2.getUuid()));

        assertEquals(2, leaderboardManager.getPowerLeaderboard(1, 2).size());

        List<SkillValue> averageLb = leaderboardManager.getAverageLeaderboard();
        assertNotNull(averageLb);
        assertEquals(3, averageLb.size());

        assertEquals(user1.getUuid(), averageLb.getFirst().id());
        assertEquals(0, averageLb.getFirst().level());
        assertEquals(8.0 / plugin.getSkillManager().getEnabledSkills().size(), averageLb.getFirst().xp());

        assertEquals(user3.getUuid(), averageLb.get(1).id());
        assertEquals(0, averageLb.get(1).level());
        assertEquals(4.0 / plugin.getSkillManager().getEnabledSkills().size(), averageLb.get(1).xp());

        assertEquals(user2.getUuid(), averageLb.getLast().id());
        assertEquals(0, averageLb.getLast().level());
        assertEquals(3.0 / plugin.getSkillManager().getEnabledSkills().size(), averageLb.getLast().xp());

        assertEquals(1, leaderboardManager.getAverageRank(user1.getUuid()));
        assertEquals(2, leaderboardManager.getAverageRank(user3.getUuid()));
        assertEquals(3, leaderboardManager.getAverageRank(user2.getUuid()));

        assertEquals(2, leaderboardManager.getAverageLeaderboard(1, 2).size());
    }

    @Test
    void testOfflineUsers() {
        plugin.setScheduler(new SyncOnlyScheduler(plugin));

        UUID uuid = UUID.fromString("4954374f-e6c8-4c0d-b5fb-686cde397d8d");
        copyResourceToTemp("userdata/" + uuid + ".yml", plugin);

        PlayerMock player = new PlayerMock(server, "player1", uuid);
        server.addPlayer(player);

        server.getScheduler().performOneTick();

        assertTrue(plugin.getUserManager().hasUser(uuid));

        User user = plugin.getUser(player);
        assertEquals(uuid, user.getUuid());
        assertEquals(5, user.getSkillLevel(MINING));
        assertEquals(1.5, user.getSkillXp(MINING));

        LeaderboardManager leaderboardManager = new LeaderboardManager(plugin, new BukkitLeaderboardExclusion(plugin));

        leaderboardManager.updateLeaderboards();

        List<SkillValue> miningLb = leaderboardManager.getLeaderboard(MINING);
        assertNotNull(miningLb);
        assertEquals(1, miningLb.size());

        assertEquals(uuid, miningLb.getFirst().id());
        assertEquals(5, miningLb.getFirst().level());
        assertEquals(1.5, miningLb.getFirst().xp());
    }

    @Test
    void testInvalidUuid() {
        LeaderboardManager leaderboardManager = new LeaderboardManager(plugin, new BukkitLeaderboardExclusion(plugin));

        assertEquals(0, leaderboardManager.getSkillRank(FARMING, UUID.randomUUID()));
        assertEquals(0, leaderboardManager.getPowerRank(UUID.randomUUID()));
        assertEquals(0, leaderboardManager.getAverageRank(UUID.randomUUID()));
    }

    @Test
    void testExcludedUsers() {
        LeaderboardManager leaderboardManager = new LeaderboardManager(plugin, new BukkitLeaderboardExclusion(plugin));
        server.getPluginManager().registerEvents(((BukkitLeaderboardExclusion) leaderboardManager.getLeaderboardExclusion()), plugin);

        User excludedUser = addUser(new SkillLevelMaps(Map.of(FARMING, 10), Map.of()));
        Player excludedPlayer = ((BukkitUser) excludedUser).getPlayer();
        assertNotNull(excludedPlayer);

        // Add excluded permission
        PermissionAttachment permissionAttachment = excludedPlayer.addAttachment(plugin);
        permissionAttachment.setPermission(BukkitLeaderboardExclusion.PERMISSION, true);

        User nonExcludedUser = addUser(new SkillLevelMaps(Map.of(FARMING, 9), Map.of()));
        Player nonExcludedPlayer = ((BukkitUser) nonExcludedUser).getPlayer();
        assertNotNull(nonExcludedPlayer);

        server.addPlayer((PlayerMock) excludedPlayer); // Call PlayerJoinEvent
        server.addPlayer((PlayerMock) nonExcludedPlayer);

        server.getScheduler().performTicks(20); // Let BukkitLeaderboardExclusion run its scheduler to add excluded player

        leaderboardManager.updateLeaderboards();

        assertEquals(1, leaderboardManager.getLeaderboard(FARMING).size());
        assertEquals(1, leaderboardManager.getPowerLeaderboard().size());
        assertEquals(1, leaderboardManager.getAverageLeaderboard().size());

        assertEquals(0, leaderboardManager.getSkillRank(FARMING, excludedUser.getUuid()));
        assertEquals(0, leaderboardManager.getPowerRank(excludedUser.getUuid()));
        assertEquals(0, leaderboardManager.getAverageRank(excludedUser.getUuid()));

        assertEquals(1, leaderboardManager.getSkillRank(FARMING, nonExcludedUser.getUuid()));
        assertEquals(1, leaderboardManager.getPowerRank(nonExcludedUser.getUuid()));
        assertEquals(1, leaderboardManager.getAverageRank(nonExcludedUser.getUuid()));
    }

    private User addUser(SkillLevelMaps skillLevelMaps) {
        // Create a PlayerMock instance directly so that PlayerJoinEvent isn't called
        PlayerMock playerMock = new PlayerMock(server, "player1", UUID.randomUUID());
        var user = plugin.getUserManager().createNewUser(playerMock.getUniqueId(), wrap(playerMock));
        for (Entry<Skill, Integer> entry : skillLevelMaps.levels().entrySet()) {
            user.setSkillLevel(entry.getKey(), entry.getValue());
        }
        for (Entry<Skill, Double> entry : skillLevelMaps.xp().entrySet()) {
            user.setSkillXp(entry.getKey(), entry.getValue());
        }
        plugin.getUserManager().addUser(user);
        return user;
    }

}
