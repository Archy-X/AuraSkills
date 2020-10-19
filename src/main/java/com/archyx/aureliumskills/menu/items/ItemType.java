package com.archyx.aureliumskills.menu.items;

public enum ItemType {

    YOUR_SKILLS(YourSkillsItem.class),
    SKILL(SkillItem.class),
    CLOSE(CloseItem.class),
    SKULL(SkullItem.class),
    RANK(RankItem.class),
    BACK(BackItem.class),
    NEXT_PAGE(NextPageItem.class),
    PREVIOUS_PAGE(PreviousPageItem.class);

    private final Class<?> loader;

    ItemType(Class<?> loader) {
        this.loader = loader;
    }

    public Class<?> getLoader() {
        return loader;
    }

}
