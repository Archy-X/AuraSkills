package dev.aurelium.auraskills.common.api;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.message.MessageManager;
import dev.aurelium.auraskills.api.skill.XpRequirements;
import dev.aurelium.auraskills.api.user.UserManager;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.api.implementation.ApiMessageManager;
import dev.aurelium.auraskills.common.api.implementation.ApiUserManager;
import dev.aurelium.auraskills.common.api.implementation.ApiXpRequirements;

public class ApiAuraSkills implements AuraSkillsApi {

    private final UserManager userManager;
    private final MessageManager messageManager;
    private final XpRequirements xpRequirements;

    public ApiAuraSkills(AuraSkillsPlugin plugin) {
        this.userManager = new ApiUserManager(plugin);
        this.messageManager = new ApiMessageManager(plugin);
        this.xpRequirements = new ApiXpRequirements(plugin);
    }

    @Override
    public UserManager getUserManager() {
        return userManager;
    }

    @Override
    public MessageManager getMessageManager() {
        return messageManager;
    }

    @Override
    public XpRequirements getXpRequirements() {
        return xpRequirements;
    }

}
