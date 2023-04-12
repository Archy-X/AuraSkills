package dev.aurelium.skills.common.stat;

import dev.aurelium.skills.api.stat.Stat;
import dev.aurelium.skills.common.registry.Namespace;

public class Stats {

    public static final Stat STRENGTH = new DefaultStat(Namespace.withKey("strength"));
    public static final Stat HEALTH = new DefaultStat(Namespace.withKey("health"));
    public static final Stat REGENERATION = new DefaultStat(Namespace.withKey("regeneration"));
    public static final Stat LUCK = new DefaultStat(Namespace.withKey("luck"));
    public static final Stat WISDOM = new DefaultStat(Namespace.withKey("wisdom"));
    public static final Stat TOUGHNESS = new DefaultStat(Namespace.withKey("toughness"));


}
