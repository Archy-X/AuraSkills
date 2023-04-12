package dev.aurelium.skills.common.skill;

import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.common.registry.Namespace;

public class Skills {

    public static final Skill FARMING = new DefaultSkill(Namespace.withKey("farming"));
    public static final Skill FORAGING = new DefaultSkill(Namespace.withKey("foraging"));
    public static final Skill MINING = new DefaultSkill(Namespace.withKey("mining"));
    public static final Skill FISHING = new DefaultSkill(Namespace.withKey("fishing"));
    public static final Skill EXCAVATION = new DefaultSkill(Namespace.withKey("excavation"));
    public static final Skill ARCHERY = new DefaultSkill(Namespace.withKey("archery"));
    public static final Skill FIGHTING = new DefaultSkill(Namespace.withKey("fighting"));
    public static final Skill DEFENSE = new DefaultSkill(Namespace.withKey("defense"));
    public static final Skill AGILITY = new DefaultSkill(Namespace.withKey("agility"));
    public static final Skill ENDURANCE = new DefaultSkill(Namespace.withKey("endurance"));
    public static final Skill ALCHEMY = new DefaultSkill(Namespace.withKey("alchemy"));
    public static final Skill ENCHANTING = new DefaultSkill(Namespace.withKey("enchanting"));
    public static final Skill SORCERY = new DefaultSkill(Namespace.withKey("sorcery"));
    public static final Skill HEALING = new DefaultSkill(Namespace.withKey("healing"));
    public static final Skill FORGING = new DefaultSkill(Namespace.withKey("forging"));

    public static Skill[] values() {
        return new Skill[] {
            FARMING,
            FORAGING,
            MINING,
            FISHING,
            EXCAVATION,
            ARCHERY,
            FIGHTING,
            DEFENSE,
            AGILITY,
            ENDURANCE,
            ALCHEMY,
            ENCHANTING,
            SORCERY,
            HEALING,
            FORGING
        };
    }

}
