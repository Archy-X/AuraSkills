package dev.aurelium.skills.api.skill;

import dev.aurelium.skills.api.AureliumSkillsProvider;

import java.util.Locale;

class SkillWrapper extends Skill {

    SkillWrapper(String id) {
        super(id);
    }

    @Override
    public int getMaxLevel() {
        return AureliumSkillsProvider.getInstance().getConfigManager().getMaxLevel(this);
    }

    @Override
    public String getDisplayName(Locale locale) {
        return AureliumSkillsProvider.getInstance().getMessageManager().getSkillDisplayName(locale, this);
    }

    @Override
    public String getDescription(Locale locale) {
        return AureliumSkillsProvider.getInstance().getMessageManager().getSkillDescription(locale, this);
    }
}
