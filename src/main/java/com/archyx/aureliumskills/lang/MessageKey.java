package com.archyx.aureliumskills.lang;

import java.util.*;

public interface MessageKey {

    String getPath();

    static Set<MessageKey> values() {
        Set<MessageKey> keys = new HashSet<>(Arrays.asList(AbilityMessage.values()));
        keys.addAll(Arrays.asList(Message.values()));
        return keys;
    }
}
