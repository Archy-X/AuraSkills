package com.archyx.aureliumskills.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.menu.LevelProgressionMenu;
import com.archyx.aureliumskills.menu.SkillsMenu;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
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
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.FARMING, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.FARMING, page, plugin).open(player, page);
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
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.FORAGING, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.FORAGING, page, plugin).open(player, page);
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
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.MINING, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.MINING, page, plugin).open(player, page);
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
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.FISHING, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.FISHING, page, plugin).open(player, page);
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
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.EXCAVATION, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.EXCAVATION, page, plugin).open(player, page);
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
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.ARCHERY, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.ARCHERY, page, plugin).open(player, page);
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
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.DEFENSE, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.DEFENSE, page, plugin).open(player, page);
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
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.FIGHTING, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.FIGHTING, page, plugin).open(player, page);
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
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.ENDURANCE, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.ENDURANCE, page, plugin).open(player, page);
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
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.AGILITY, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.AGILITY, page, plugin).open(player, page);
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
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.ALCHEMY, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.ALCHEMY, page, plugin).open(player, page);
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
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.ENCHANTING, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.ENCHANTING, page, plugin).open(player, page);
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
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.SORCERY, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.SORCERY, page, plugin).open(player, page);
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
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.HEALING, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.HEALING, page, plugin).open(player, page);
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
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.FORGING, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.FORGING, page, plugin).open(player, page);
            }
        }
    }
}
