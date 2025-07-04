package dev.aurelium.auraskills.common;

import com.google.common.io.Resources;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestUtil {

    public static void copyResourceToTemp(String filePath, AuraSkillsPlugin plugin) {
        File tempMetadataFile = new File(plugin.getPluginFolder(), filePath);
        try {
            Files.createDirectories(tempMetadataFile.toPath().getParent());
            try {
                Files.copy(Path.of(Resources.getResource(filePath).toURI()), tempMetadataFile.toPath());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
