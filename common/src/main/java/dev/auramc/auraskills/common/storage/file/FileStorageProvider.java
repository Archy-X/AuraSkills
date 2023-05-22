package dev.auramc.auraskills.common.storage.file;

import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.data.PlayerData;
import dev.auramc.auraskills.common.data.PlayerDataState;
import dev.auramc.auraskills.common.storage.StorageProvider;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FileStorageProvider extends StorageProvider {

    public FileStorageProvider(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public PlayerData load(UUID uuid) throws Exception {
        return null;
    }

    @Override
    @NotNull
    public PlayerDataState loadState(UUID uuid) {
        return PlayerDataState.createEmpty(uuid, plugin);
    }

    @Override
    public void applyState(PlayerDataState state) throws Exception {

    }

    @Override
    public void save(@NotNull PlayerData playerData) throws Exception {

    }

    @Override
    public void delete(UUID uuid) throws Exception {

    }
}
