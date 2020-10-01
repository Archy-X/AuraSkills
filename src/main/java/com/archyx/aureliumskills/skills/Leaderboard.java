package com.archyx.aureliumskills.skills;

import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;


public class Leaderboard {

    private List<PlayerSkillInstance> powerLeaderboard;
    private List<PlayerSkillInstance> farmingLeaderboard;
    private List<PlayerSkillInstance> foragingLeaderboard;
    private List<PlayerSkillInstance> miningLeaderboard;
    private List<PlayerSkillInstance> fishingLeaderboard;
    private List<PlayerSkillInstance> excavationLeaderboard;
    private List<PlayerSkillInstance> archeryLeaderboard;
    private List<PlayerSkillInstance> defenseLeaderboard;
    private List<PlayerSkillInstance> fightingLeaderboard;
    private List<PlayerSkillInstance> enduranceLeaderboard;
    private List<PlayerSkillInstance> agilityLeaderboard;
    private List<PlayerSkillInstance> alchemyLeaderboard;
    private List<PlayerSkillInstance> enchantingLeaderboard;
    private List<PlayerSkillInstance> sorceryLeaderboard;
    private List<PlayerSkillInstance> healingLeaderboard;
    private List<PlayerSkillInstance> forgingLeaderboard;
    public static boolean isSorting;
    private final LeaderboardSorter leaderboardSorter;
    private final Plugin plugin;

    public Leaderboard(Plugin plugin) {
        this.plugin = plugin;
        clearLeaderboards();
        this.leaderboardSorter = new LeaderboardSorter();
        isSorting = false;
        scheduleTask();
    }

    public void queueAdd(PlayerSkillInstance playerSkill) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isSorting) {
                    addNewPlayer(playerSkill);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }

    public void updateLeaderboards(boolean silent) {
        isSorting = true;
        if (!silent) {
            Bukkit.getConsoleSender().sendMessage("[AureliumSkills] Updating leaderboards...");
        }
        long start = System.nanoTime();
        //Clear
        clearLeaderboards();
        //Add
        addLeaderboards();
        //Sort
        sortLeaderboards();
        if (!silent) {
            Bukkit.getConsoleSender().sendMessage("[AureliumSkills] Leaderboards updated in " + ((double) (System.nanoTime() - start)) / 1000000 + " ms");
        }
        isSorting = false;
    }

    private void scheduleTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateLeaderboards(true);
            }
        }.runTaskTimerAsynchronously(plugin, OptionL.getInt(Option.LEADERBOARDS_UPDATE_DELAY), OptionL.getInt(Option.LEADERBOARDS_UPDATE_PERIOD));
    }

    private void clearLeaderboards() {
        this.powerLeaderboard = new ArrayList<>();
        this.farmingLeaderboard = new ArrayList<>();
        this.foragingLeaderboard = new ArrayList<>();
        this.miningLeaderboard = new ArrayList<>();
        this.fishingLeaderboard = new ArrayList<>();
        this.excavationLeaderboard = new ArrayList<>();
        this.archeryLeaderboard = new ArrayList<>();
        this.defenseLeaderboard = new ArrayList<>();
        this.fightingLeaderboard = new ArrayList<>();
        this.enduranceLeaderboard = new ArrayList<>();
        this.agilityLeaderboard = new ArrayList<>();
        this.alchemyLeaderboard = new ArrayList<>();
        this.enchantingLeaderboard = new ArrayList<>();
        this.sorceryLeaderboard = new ArrayList<>();
        this.healingLeaderboard = new ArrayList<>();
        this.forgingLeaderboard = new ArrayList<>();
    }

    private void addLeaderboards() {
        //Add to list
        for (UUID id : SkillLoader.playerSkills.keySet()) {
            PlayerSkillInstance playerSkill = new PlayerSkillInstance(SkillLoader.playerSkills.get(id));
            powerLeaderboard.add(playerSkill);
            farmingLeaderboard.add(playerSkill);
            foragingLeaderboard.add(playerSkill);
            miningLeaderboard.add(playerSkill);
            fishingLeaderboard.add(playerSkill);
            excavationLeaderboard.add(playerSkill);
            archeryLeaderboard.add(playerSkill);
            defenseLeaderboard.add(playerSkill);
            fightingLeaderboard.add(playerSkill);
            enduranceLeaderboard.add(playerSkill);
            agilityLeaderboard.add(playerSkill);
            alchemyLeaderboard.add(playerSkill);
            enchantingLeaderboard.add(playerSkill);
            sorceryLeaderboard.add(playerSkill);
            healingLeaderboard.add(playerSkill);
            forgingLeaderboard.add(playerSkill);
        }
    }

    private void sortLeaderboards() {
        //Sort
        powerLeaderboard.sort(leaderboardSorter);
        farmingLeaderboard.sort(new SkillLeaderboardSorter(Skill.FARMING));
        foragingLeaderboard.sort(new SkillLeaderboardSorter(Skill.FORAGING));
        miningLeaderboard.sort(new SkillLeaderboardSorter(Skill.MINING));
        fishingLeaderboard.sort(new SkillLeaderboardSorter(Skill.FISHING));
        excavationLeaderboard.sort(new SkillLeaderboardSorter(Skill.EXCAVATION));
        archeryLeaderboard.sort(new SkillLeaderboardSorter(Skill.ARCHERY));
        defenseLeaderboard.sort(new SkillLeaderboardSorter(Skill.DEFENSE));
        fightingLeaderboard.sort(new SkillLeaderboardSorter(Skill.FIGHTING));
        enduranceLeaderboard.sort(new SkillLeaderboardSorter(Skill.ENDURANCE));
        agilityLeaderboard.sort(new SkillLeaderboardSorter(Skill.AGILITY));
        alchemyLeaderboard.sort(new SkillLeaderboardSorter(Skill.ALCHEMY));
        enchantingLeaderboard.sort(new SkillLeaderboardSorter(Skill.ENCHANTING));
        sorceryLeaderboard.sort(new SkillLeaderboardSorter(Skill.SORCERY));
        healingLeaderboard.sort(new SkillLeaderboardSorter(Skill.HEALING));
        forgingLeaderboard.sort(new SkillLeaderboardSorter(Skill.FORGING));
    }

    public List<PlayerSkillInstance> getPowerLeaderBoard() {
        return powerLeaderboard;
    }

    public List<PlayerSkillInstance> getSkillLeaderBoard(Skill skill) {
        switch (skill) {
            case FARMING:
                return farmingLeaderboard;
            case FORAGING:
                return foragingLeaderboard;
            case MINING:
                return miningLeaderboard;
            case FISHING:
                return fishingLeaderboard;
            case EXCAVATION:
                return excavationLeaderboard;
            case ARCHERY:
                return archeryLeaderboard;
            case DEFENSE:
                return defenseLeaderboard;
            case FIGHTING:
                return fightingLeaderboard;
            case ENDURANCE:
                return enduranceLeaderboard;
            case AGILITY:
                return agilityLeaderboard;
            case ALCHEMY:
                return alchemyLeaderboard;
            case ENCHANTING:
                return enchantingLeaderboard;
            case SORCERY:
                return sorceryLeaderboard;
            case HEALING:
                return healingLeaderboard;
            case FORGING:
                return forgingLeaderboard;
            default:
                return null;
        }
    }

    public List<PlayerSkillInstance> readSkillLeaderboard(Skill skill, int page, int entriesPerPage) {
        List<PlayerSkillInstance> fullLeaderboard = getSkillLeaderBoard(skill);
        int fromIndex = (Math.max(page, 1) - 1) * entriesPerPage;
        return fullLeaderboard.subList(Math.min(fromIndex, fullLeaderboard.size()), Math.min(fromIndex + entriesPerPage, fullLeaderboard.size()));
    }

    public List<PlayerSkillInstance> readPowerLeaderboard(int page, int entriesPerPage) {
        List<PlayerSkillInstance> fullLeaderboard = powerLeaderboard;
        int fromIndex = (Math.max(page, 1) - 1) * entriesPerPage;
        return fullLeaderboard.subList(Math.min(fromIndex, fullLeaderboard.size()), Math.min(fromIndex + entriesPerPage, fullLeaderboard.size()));
    }

    public int getPowerRank(UUID id) {
        PlayerSkillInstance instance = getInstance(id);
        if (powerLeaderboard.contains(instance)) {
            return powerLeaderboard.indexOf(instance) + 1;
        }
        return 0;
    }

    public int getSkillRank(Skill skill, UUID id) {
        List<PlayerSkillInstance> lb = getSkillLeaderBoard(skill);
        PlayerSkillInstance instance = getInstance(id);
        if (lb.contains(instance)) {
            return lb.indexOf(instance) + 1;
        }
        return 0;
    }

    public int getSize() {
        return powerLeaderboard.size();
    }

    private PlayerSkillInstance getInstance(UUID id) {
        for (PlayerSkillInstance psi : powerLeaderboard) {
            if (psi.getPlayerId().equals(id)) {
                return psi;
            }
        }
        return null;
    }

    public void addNewPlayer(PlayerSkillInstance playerSkill) {
        if (playerSkill != null) {
            powerLeaderboard.add(playerSkill);
            farmingLeaderboard.add(playerSkill);
            foragingLeaderboard.add(playerSkill);
            miningLeaderboard.add(playerSkill);
            fishingLeaderboard.add(playerSkill);
            excavationLeaderboard.add(playerSkill);
            archeryLeaderboard.add(playerSkill);
            defenseLeaderboard.add(playerSkill);
            fightingLeaderboard.add(playerSkill);
            enduranceLeaderboard.add(playerSkill);
            agilityLeaderboard.add(playerSkill);
            alchemyLeaderboard.add(playerSkill);
            enchantingLeaderboard.add(playerSkill);
            sorceryLeaderboard.add(playerSkill);
            healingLeaderboard.add(playerSkill);
            forgingLeaderboard.add(playerSkill);
        }
    }

    static class LeaderboardSorter implements Comparator<PlayerSkillInstance> {

        public int compare(PlayerSkillInstance a, PlayerSkillInstance b) {
            int powerA = a.getPowerLevel();
            int powerB = b.getPowerLevel();
            if (powerB != powerA) {
                return b.getPowerLevel() - a.getPowerLevel();
            }
            else {
                return (int) (b.getPowerXp() * 100) - (int) (a.getPowerXp() * 100);
            }
        }
    }

    static class SkillLeaderboardSorter implements Comparator<PlayerSkillInstance> {

        private final Skill skill;

        public SkillLeaderboardSorter(Skill skill) {
            this.skill = skill;
        }

        public int compare(PlayerSkillInstance a, PlayerSkillInstance b) {
            int levelA = a.getSkillLevel(skill);
            int levelB = b.getSkillLevel(skill);
            if (levelA != levelB) {
                return b.getSkillLevel(skill) - a.getSkillLevel(skill);
            }
            else {
                return (int) (b.getXp(skill) * 100) - (int) (a.getXp(skill) * 100);
            }
        }
    }
}
