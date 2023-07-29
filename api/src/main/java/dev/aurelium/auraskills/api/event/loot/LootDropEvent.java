package dev.aurelium.auraskills.api.event.loot;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.event.AuraSkillsEvent;
import dev.aurelium.auraskills.api.event.Cancellable;
import dev.aurelium.auraskills.api.item.ItemHolder;
import dev.aurelium.auraskills.api.player.SkillsPlayer;
import dev.aurelium.auraskills.api.util.LocationHolder;

public class LootDropEvent extends AuraSkillsEvent implements Cancellable {

    private final SkillsPlayer skillsPlayer;
    private ItemHolder item;
    private LocationHolder location;
    private final Cause cause;
    private boolean cancelled = false;

    public LootDropEvent(AuraSkillsApi api, SkillsPlayer skillsPlayer, ItemHolder item, LocationHolder location, Cause cause) {
        super(api);
        this.skillsPlayer = skillsPlayer;
        this.item = item;
        this.location = location;
        this.cause = cause;
    }

    public SkillsPlayer getSkillsPlayer() {
        return skillsPlayer;
    }

    public ItemHolder getItem() {
        return item;
    }

    public LootDropEvent setItem(ItemHolder item) {
        this.item = item;
        return this;
    }

    public LocationHolder getLocation() {
        return location;
    }

    public LootDropEvent setLocation(LocationHolder location) {
        this.location = location;
        return this;
    }

    public Cause getCause() {
        return cause;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public enum Cause {

        BOUNTIFUL_HARVEST,
        TRIPLE_HARVEST,
        LUMBERJACK,
        LUCKY_MINER,
        LUCKY_CATCH,
        TREASURE_HUNTER,
        EPIC_CATCH,
        METAL_DETECTOR,
        BIGGER_SCOOP,
        LUCKY_SPADES,
        LUCK_DOUBLE_DROP,
        FISHING_OTHER_LOOT,
        EXCAVATION_OTHER_LOOT,
        MINING_OTHER_LOOT,
        FORAGING_OTHER_LOOT,
        UNKNOWN

    }

}
