package dev.auramc.auraskills.common.storage.file;

import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.data.PlayerData;
import dev.auramc.auraskills.common.data.PlayerDataState;
import dev.auramc.auraskills.common.storage.StorageProvider;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.UUID;

public class FileStorageProvider extends StorageProvider {

    public FileStorageProvider(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void load(UUID uuid) {

    }

    @Override
    public @Nullable PlayerDataState loadState(UUID uuid) {
        return null;
    }

    @Override
    public boolean applyState(PlayerDataState state) {
        return false;
    }

    @Override
    public void save(PlayerData player, boolean removeFromMemory) {

    }

    @Override
    public void updateLeaderboards() {

    }

    @Override
    public void delete(UUID uuid) throws IOException {

    }
}
