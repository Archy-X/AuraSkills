package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.MessageType;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.util.LevelProgressionOpener;
import org.bukkit.entity.Player;

public class SkillCommands {

    public static class SkillCommand extends BaseCommand {
        
        protected final AuraSkills plugin;
        protected final Skill skill;
        
        public SkillCommand(AuraSkills plugin, Skill skill) {
            this.plugin = plugin;
            this.skill = skill;
        }
        
        protected void openMenu(Player player) {
            if (skill.isEnabled()) {
                new LevelProgressionOpener(plugin).open(player, skill);
            } else {
                player.sendMessage(plugin.getCommandManager().formatMessage(plugin.getCommandManager().getCommandIssuer(player), MessageType.ERROR, MessageKeys.UNKNOWN_COMMAND));
            }
        }
    }

    @CommandAlias("farming")
    public static class FarmingCommand extends SkillCommand {
        
        public FarmingCommand(AuraSkills plugin) {
            super(plugin, Skills.FARMING);
        }

        @Default
        public void onCommand(Player player) {
            openMenu(player);
        }
    }
    @CommandAlias("foraging")
    public static class ForagingCommand extends SkillCommand {

        public ForagingCommand(AuraSkills plugin) {
            super(plugin, Skills.FORAGING);
        }

        @Default
        public void onCommand(Player player) {
            openMenu(player);
        }
    }
    @CommandAlias("mining")
    public static class MiningCommand extends SkillCommand {

        public MiningCommand(AuraSkills plugin) {
            super(plugin, Skills.MINING);
        }

        @Default
        public void onCommand(Player player) {
            openMenu(player);
        }
    }
    @CommandAlias("fishing")
    public static class FishingCommand extends SkillCommand {

        public FishingCommand(AuraSkills plugin) {
            super(plugin, Skills.FISHING);
        }

        @Default
        public void onCommand(Player player) {
            openMenu(player);
        }
    }
    @CommandAlias("excavation")
    public static class ExcavationCommand extends SkillCommand {

        public ExcavationCommand(AuraSkills plugin) {
            super(plugin, Skills.EXCAVATION);
        }

        @Default
        public void onCommand(Player player) {
            openMenu(player);
        }
    }
    @CommandAlias("archery")
    public static class ArcheryCommand extends SkillCommand {

        public ArcheryCommand(AuraSkills plugin) {
            super(plugin, Skills.ARCHERY);
        }
        
        @Default
        public void onCommand(Player player) {
            openMenu(player);
        }
    }
    @CommandAlias("defense")
    public static class DefenseCommand extends SkillCommand {

        public DefenseCommand(AuraSkills plugin) {
            super(plugin, Skills.DEFENSE);
        }
        
        @Default
        public void onCommand(Player player) {
            openMenu(player);
        }
    }
    @CommandAlias("fighting")
    public static class FightingCommand extends SkillCommand {

        public FightingCommand(AuraSkills plugin) {
            super(plugin, Skills.FIGHTING);
        }
        
        @Default
        public void onCommand(Player player) {
            openMenu(player);
        }
    }
    @CommandAlias("endurance")
    public static class EnduranceCommand extends SkillCommand {

        public EnduranceCommand(AuraSkills plugin) {
            super(plugin, Skills.ENDURANCE);
        }
        
        @Default
        public void onCommand(Player player) {
            openMenu(player);
        }
    }
    @CommandAlias("agility")
    public static class AgilityCommand extends SkillCommand {

        public AgilityCommand(AuraSkills plugin) {
            super(plugin, Skills.AGILITY);
        }
        
        @Default
        public void onCommand(Player player) {
            openMenu(player);
        }
    }
    @CommandAlias("alchemy")
    public static class AlchemyCommand extends SkillCommand {

        public AlchemyCommand(AuraSkills plugin) {
            super(plugin, Skills.ALCHEMY);
        }

        @Default
        public void onCommand(Player player) {
            openMenu(player);
        }
    }
    @CommandAlias("enchanting")
    public static class EnchantingCommand extends SkillCommand {

        public EnchantingCommand(AuraSkills plugin) {
            super(plugin, Skills.ENCHANTING);
        }

        @Default
        public void onCommand(Player player) {
            openMenu(player);
        }
    }
    @CommandAlias("sorcery")
    public static class SorceryCommand extends SkillCommand {

        public SorceryCommand(AuraSkills plugin) {
            super(plugin, Skills.SORCERY);
        }

        @Default
        public void onCommand(Player player) {
            openMenu(player);
        }
    }
    @CommandAlias("healing")
    public static class HealingCommand extends SkillCommand {

        public HealingCommand(AuraSkills plugin) {
            super(plugin, Skills.HEALING);
        }

        @Default
        public void onCommand(Player player) {
            openMenu(player);
        }
    }
    @CommandAlias("forging")
    public static class ForgingCommand extends SkillCommand {

        public ForgingCommand(AuraSkills plugin) {
            super(plugin, Skills.FORGING);
        }

        @Default
        public void onCommand(Player player) {
            openMenu(player);
        }
    }
}
