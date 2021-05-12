package com.archyx.aureliumskills.rewards.builder;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.rewards.CommandReward;
import com.archyx.aureliumskills.rewards.CommandReward.CommandExecutor;
import com.archyx.aureliumskills.rewards.Reward;
import com.archyx.aureliumskills.util.misc.Validate;

public class CommandRewardBuilder extends MessageCustomizableRewardBuilder {

    private CommandExecutor executor;
    private String command;

    public CommandRewardBuilder(AureliumSkills plugin) {
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

    @Override
    public Reward build() {
        Validate.notNull(command, "You must specify a command");
        return new CommandReward(plugin, menuMessage, chatMessage, executor, command);
    }
}
