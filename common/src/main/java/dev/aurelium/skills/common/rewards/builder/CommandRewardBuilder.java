package dev.aurelium.skills.common.rewards.builder;

import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.commands.CommandExecutor;
import dev.aurelium.skills.common.rewards.Reward;
import dev.aurelium.skills.common.rewards.type.CommandReward;
import dev.aurelium.skills.common.util.data.Validate;

public class CommandRewardBuilder extends MessagedRewardBuilder {

    private CommandExecutor executor;
    private String command;
    private CommandExecutor revertExecutor;
    private String revertCommand;

    public CommandRewardBuilder(AureliumSkillsPlugin plugin) {
        super(plugin);
        this.executor = CommandExecutor.CONSOLE;
    }

    public CommandRewardBuilder executor(CommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    public CommandRewardBuilder command(String command) {
        this.command = command;
        return this;
    }

    public CommandRewardBuilder revertCommand(String revertCommand) {
        this.revertCommand = revertCommand;
        return this;
    }

    public CommandRewardBuilder revertExecutor(CommandExecutor revertExecutor) {
        this.revertExecutor = revertExecutor;
        return this;
    }

    @Override
    public Reward build() {
        Validate.notNull(command, "You must specify a command");
        return new CommandReward(plugin, menuMessage, chatMessage, executor, command, revertExecutor, revertCommand);
    }
}
