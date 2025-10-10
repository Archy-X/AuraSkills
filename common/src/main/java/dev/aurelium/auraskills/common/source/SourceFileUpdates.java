package dev.aurelium.auraskills.common.source;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.common.util.file.ConfigUpdate;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SourceFileUpdates {

    public final Map<Skill, Map<Integer, ConfigUpdate>> fileUpdates = new ConcurrentHashMap<>();

    public SourceFileUpdates() {
        define(Skills.FORAGING, 1, this::replaceTrunkAndLeafWithMaxBlocks);
    }

    public Map<Skill, Map<Integer, ConfigUpdate>> getFileUpdates() {
        return fileUpdates;
    }

    private void define(Skill skill, int fileVersion, ConfigUpdate update) {
        fileUpdates.computeIfAbsent(skill, k -> new ConcurrentHashMap<>()).put(fileVersion, update);
    }

    private void replaceTrunkAndLeafWithMaxBlocks(ConfigurationNode embedded, ConfigurationNode user) throws SerializationException {
        // Remove trunk and leaf keys
        for (ConfigurationNode sourceNode : user.node("sources").childrenMap().values()) {
            sourceNode.removeChild("trunk");
            sourceNode.removeChild("leaf");
        }

        // Set max blocks
        for (ConfigurationNode embeddedSource : embedded.node("sources").childrenMap().values()) {
            Object key = embeddedSource.key();
            if (key == null) continue;

            if (!embeddedSource.node("max_blocks").empty()) {
                ConfigurationNode userSource = user.node("sources", key);
                if (!userSource.empty() && userSource.node("max_blocks").empty()) {
                    userSource.node("max_blocks").set(embeddedSource.node("max_blocks"));
                }
            }
        }
    }

}
