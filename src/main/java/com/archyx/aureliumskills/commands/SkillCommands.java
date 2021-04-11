package com.archyx.aureliumskills.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.menu.LevelProgressionMenu;
import com.archyx.aureliumskills.menu.SkillsMenu;
import com.archyx.aureliumskills.skills.Skills;
import org.bukkit.entity.Player;

public class SkillCommands {

    @CommandAlias("farming")
    public static class FarmingCommand extends BaseCommand {

        private final AureliumSkills plugin;

        public FarmingCommand(AureliumSkills plugin) {
            this.plugin = plugin;
        }

        @Default
        public void onCommand(Player player) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData != null) {
                int page = SkillsMenu.getPage(Skills.FARMING, playerData);
                LevelProgressionMenu.getInventory(player, Skills.FARMING, page, plugin).open(player, page);
            }
        }
    }
    @CommandAlias("foraging")
    public static class ForagingCommand extends BaseCommand {

        private final AureliumSkills plugin;

        public ForagingCommand(AureliumSkills plugin) {
            this.plugin = plugin;
        }

        @Default
        public void onCommand(Player player) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData != null) {
                int page = SkillsMenu.getPage(Skills.FORAGING, playerData);
                LevelProgressionMenu.getInventory(player, Skills.FORAGING, page, plugin).open(player, page);
            }
        }
    }
    @CommandAlias("mining")
    public static class MiningCommand extends BaseCommand {

        private final AureliumSkills plugin;

        public MiningCommand(AureliumSkills plugin) {
            this.plugin = plugin;
        }

        @Default
        public void onCommand(Player player) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData != null) {
                int page = SkillsMenu.getPage(Skills.MINING, playerData);
                LevelProgressionMenu.getInventory(player, Skills.MINING, page, plugin).open(player, page);
            }
        }
    }
    @CommandAlias("fishing")
    public static class FishingCommand extends BaseCommand {

        private final AureliumSkills plugin;

        public FishingCommand(AureliumSkills plugin) {
            this.plugin = plugin;
        }

        @Default
        public void onCommand(Player player) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData != null) {
                int page = SkillsMenu.getPage(Skills.FISHING, playerData);
                LevelProgressionMenu.getInventory(player, Skills.FISHING, page, plugin).open(player, page);
            }
        }
    }
    @CommandAlias("excavation")
    public static class ExcavationCommand extends BaseCommand {

        private final AureliumSkills plugin;

        public ExcavationCommand(AureliumSkills plugin) {
            this.plugin = plugin;
        }

        @Default
        public void onCommand(Player player) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData != null) {
                int page = SkillsMenu.getPage(Skills.EXCAVATION, playerData);
                LevelProgressionMenu.getInventory(player, Skills.EXCAVATION, page, plugin).open(player, page);
            }
        }
    }
    @CommandAlias("archery")
    public static class ArcheryCommand extends BaseCommand {

        private final AureliumSkills plugin;

        public ArcheryCommand(AureliumSkills plugin) {
            this.plugin = plugin;
        }
        
        @Default
        public void onCommand(Player player) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData != null) {
                int page = SkillsMenu.getPage(Skills.ARCHERY, playerData);
                LevelProgressionMenu.getInventory(player, Skills.ARCHERY, page, plugin).open(player, page);
            }
        }
    }
    @CommandAlias("defense")
    public static class DefenseCommand extends BaseCommand {

        private final AureliumSkills plugin;

        public DefenseCommand(AureliumSkills plugin) {
            this.plugin = plugin;
        }
        
        @Default
        public void onCommand(Player player) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData != null) {
                int page = SkillsMenu.getPage(Skills.DEFENSE, playerData);
                LevelProgressionMenu.getInventory(player, Skills.DEFENSE, page, plugin).open(player, page);
            }
        }
    }
    @CommandAlias("fighting")
    public static class FightingCommand extends BaseCommand {

        private final AureliumSkills plugin;

        public FightingCommand(AureliumSkills plugin) {
            this.plugin = plugin;
        }
        
        @Default
        public void onCommand(Player player) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData != null) {
                int page = SkillsMenu.getPage(Skills.FIGHTING, playerData);
                LevelProgressionMenu.getInventory(player, Skills.FIGHTING, page, plugin).open(player, page);
            }
        }
    }
    @CommandAlias("endurance")
    public static class EnduranceCommand extends BaseCommand {

        private final AureliumSkills plugin;

        public EnduranceCommand(AureliumSkills plugin) {
            this.plugin = plugin;
        }
        
        @Default
        public void onCommand(Player player) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData != null) {
                int page = SkillsMenu.getPage(Skills.ENDURANCE, playerData);
                LevelProgressionMenu.getInventory(player, Skills.ENDURANCE, page, plugin).open(player, page);
            }
        }
    }
    @CommandAlias("agility")
    public static class AgilityCommand extends BaseCommand {

        private final AureliumSkills plugin;

        public AgilityCommand(AureliumSkills plugin) {
            this.plugin = plugin;
        }
        
        @Default
        public void onCommand(Player player) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData != null) {
                int page = SkillsMenu.getPage(Skills.AGILITY, playerData);
                LevelProgressionMenu.getInventory(player, Skills.AGILITY, page, plugin).open(player, page);
            }
        }
    }
    @CommandAlias("alchemy")
    public static class AlchemyCommand extends BaseCommand {

        private final AureliumSkills plugin;

        public AlchemyCommand(AureliumSkills plugin) {
            this.plugin = plugin;
        }

        @Default
        public void onCommand(Player player) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData != null) {
                int page = SkillsMenu.getPage(Skills.ALCHEMY, playerData);
                LevelProgressionMenu.getInventory(player, Skills.ALCHEMY, page, plugin).open(player, page);
            }
        }
    }
    @CommandAlias("enchanting")
    public static class EnchantingCommand extends BaseCommand {

        private final AureliumSkills plugin;

        public EnchantingCommand(AureliumSkills plugin) {
            this.plugin = plugin;
        }

        @Default
        public void onCommand(Player player) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData != null) {
                int page = SkillsMenu.getPage(Skills.ENCHANTING, playerData);
                LevelProgressionMenu.getInventory(player, Skills.ENCHANTING, page, plugin).open(player, page);
            }
        }
    }
    @CommandAlias("sorcery")
    public static class SorceryCommand extends BaseCommand {

        private final AureliumSkills plugin;

        public SorceryCommand(AureliumSkills plugin) {
            this.plugin = plugin;
        }

        @Default
        public void onCommand(Player player) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData != null) {
                int page = SkillsMenu.getPage(Skills.SORCERY, playerData);
                LevelProgressionMenu.getInventory(player, Skills.SORCERY, page, plugin).open(player, page);
            }
        }
    }
    @CommandAlias("healing")
    public static class HealingCommand extends BaseCommand {

        private final AureliumSkills plugin;

        public HealingCommand(AureliumSkills plugin) {
            this.plugin = plugin;
        }

        @Default
        public void onCommand(Player player) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData != null) {
                int page = SkillsMenu.getPage(Skills.HEALING, playerData);
                LevelProgressionMenu.getInventory(player, Skills.HEALING, page, plugin).open(player, page);
            }
        }
    }
    @CommandAlias("forging")
    public static class ForgingCommand extends BaseCommand {

        private final AureliumSkills plugin;

        public ForgingCommand(AureliumSkills plugin) {
            this.plugin = plugin;
        }

        @Default
        public void onCommand(Player player) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData != null) {
                int page = SkillsMenu.getPage(Skills.FORGING, playerData);
                LevelProgressionMenu.getInventory(player, Skills.FORGING, page, plugin).open(player, page);
            }
        }
    }
}
