package com.archyx.aureliumskills.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.archyx.aureliumskills.menu.LevelProgressionMenu;
import com.archyx.aureliumskills.menu.SkillsMenu;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import org.bukkit.entity.Player;

public class SkillCommands {

    @CommandAlias("farming")
    public static class FarmingCommand extends BaseCommand {
        @Default
        public void onCommand(Player player) {
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.FARMING, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.FARMING, page).open(player, page);
            }
        }
    }
    @CommandAlias("foraging")
    public static class ForagingCommand extends BaseCommand {
        @Default
        public void onCommand(Player player) {
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.FORAGING, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.FORAGING, page).open(player, page);
            }
        }
    }
    @CommandAlias("mining")
    public static class MiningCommand extends BaseCommand {
        @Default
        public void onCommand(Player player) {
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.MINING, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.MINING, page).open(player, page);
            }
        }
    }
    @CommandAlias("fishing")
    public static class FishingCommand extends BaseCommand {
        @Default
        public void onCommand(Player player) {
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.FISHING, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.FISHING, page).open(player, page);
            }
        }
    }
    @CommandAlias("excavation")
    public static class ExcavationCommand extends BaseCommand {
        @Default
        public void onCommand(Player player) {
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.EXCAVATION, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.EXCAVATION, page).open(player, page);
            }
        }
    }
    @CommandAlias("archery")
    public static class ArcheryCommand extends BaseCommand {
        @Default
        public void onCommand(Player player) {
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.ARCHERY, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.ARCHERY, page).open(player, page);
            }
        }
    }
    @CommandAlias("defense")
    public static class DefenseCommand extends BaseCommand {
        @Default
        public void onCommand(Player player) {
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.DEFENSE, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.DEFENSE, page).open(player, page);
            }
        }
    }
    @CommandAlias("fighting")
    public static class FightingCommand extends BaseCommand {
        @Default
        public void onCommand(Player player) {
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.FIGHTING, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.FIGHTING, page).open(player, page);
            }
        }
    }
    @CommandAlias("endurance")
    public static class EnduranceCommand extends BaseCommand {
        @Default
        public void onCommand(Player player) {
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.ENDURANCE, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.ENDURANCE, page).open(player, page);
            }
        }
    }
    @CommandAlias("agility")
    public static class AgilityCommand extends BaseCommand {
        @Default
        public void onCommand(Player player) {
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.AGILITY, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.AGILITY, page).open(player, page);
            }
        }
    }
    @CommandAlias("alchemy")
    public static class AlchemyCommand extends BaseCommand {
        @Default
        public void onCommand(Player player) {
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.ALCHEMY, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.ALCHEMY, page).open(player, page);
            }
        }
    }
    @CommandAlias("enchanting")
    public static class EnchantingCommand extends BaseCommand {
        @Default
        public void onCommand(Player player) {
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.ENCHANTING, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.ENCHANTING, page).open(player, page);
            }
        }
    }
    @CommandAlias("sorcery")
    public static class SorceryCommand extends BaseCommand {
        @Default
        public void onCommand(Player player) {
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.SORCERY, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.SORCERY, page).open(player, page);
            }
        }
    }
    @CommandAlias("healing")
    public static class HealingCommand extends BaseCommand {
        @Default
        public void onCommand(Player player) {
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.HEALING, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.HEALING, page).open(player, page);
            }
        }
    }
    @CommandAlias("forging")
    public static class ForgingCommand extends BaseCommand {
        @Default
        public void onCommand(Player player) {
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                int page = SkillsMenu.getPage(Skill.FORGING, playerSkill);
                LevelProgressionMenu.getInventory(player, Skill.FORGING, page).open(player, page);
            }
        }
    }
}
