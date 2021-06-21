package com.archyx.aureliumskills.lang;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface MessageKey {

    String getPath();

    static Set<MessageKey> values() {
        Set<MessageKey> keys = new HashSet<>(Arrays.asList(AbilityMessage.values()));
        keys.addAll(Arrays.asList(CommandMessage.values()));
        keys.addAll(Arrays.asList(MenuMessage.values()));
        keys.addAll(Arrays.asList(ManaAbilityMessage.values()));
        keys.addAll(Arrays.asList(SkillMessage.values()));
        keys.addAll(Arrays.asList(StatMessage.values()));
        keys.addAll(Arrays.asList(UnitMessage.values()));
        keys.addAll(Arrays.asList(ActionBarMessage.values()));
        keys.addAll(Arrays.asList(LevelerMessage.values()));
        keys.addAll(Arrays.asList(RewardMessage.values()));
        return keys;
    }
}
