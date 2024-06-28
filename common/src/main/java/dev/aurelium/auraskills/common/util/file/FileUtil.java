package dev.aurelium.auraskills.common.util.file;

import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FileUtil {

    @Nullable
    public static String renameNoDuplicates(File file, String resultName, File directory) {
        // Count duplicates
        int duplicates = 0;
        File[] subFiles = directory.listFiles();
        if (subFiles != null) {
            for (File subFile : subFiles) {
                if (subFile.getName().equals(resultName)) {
                    if (1 > duplicates) {
                        duplicates = 1;
                    }
                    break;
                } else {
                    String baseName = getBaseName(resultName);
                    if (subFile.getName().startsWith(baseName + " (")) {
                        int fileNameNumber = NumberUtil.toInt(subFile.getName().substring(baseName.length() + 2, baseName.length() + 3)) + 1;
                        if (fileNameNumber > duplicates) {
                            duplicates = fileNameNumber;
                        }
                    }
                }
            }
        }
        // Rename old file
        String renamedName;
        if (duplicates == 0) {
            renamedName = resultName;
        } else {
            String resultBase = getBaseName(resultName);
            String resultExtension = getExtension(resultName);
            renamedName = resultBase + " (" + duplicates + ")." + resultExtension;
        }
        if (file.renameTo(new File(file.getParent(), renamedName))) {
            return renamedName;
        } else {
            return null;
        }
    }

    public static String getBaseName(String fileName) {
        return fileName.split("\\.(?=[^.]+$)")[0];
    }

    public static String getExtension(String fileName) {
        try {
            return fileName.split("\\.(?=[^.]+$)")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return fileName;
        }
    }

    public static ConfigurationNode loadYamlFile(File file) throws IOException {
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .file(file)
                .nodeStyle(NodeStyle.BLOCK)
                .build();
        return loader.load();
    }

    public static void saveYamlFile(File file, @NotNull ConfigurationNode config) throws IOException {
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .file(file)
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .build();
        loader.save(config);
    }

    public static ConfigurationNode loadEmbeddedYamlFile(String fileName, AuraSkillsPlugin plugin) throws IOException {
        return loadEmbeddedYamlFile(fileName, plugin.getClass().getClassLoader(), true);
    }

    public static ConfigurationNode loadEmbeddedYamlFile(String fileName, ClassLoader classLoader, boolean createFileSystem) throws IOException {
        URI uri = getEmbeddedURI(fileName, classLoader);

        if (uri == null) {
            throw new IllegalArgumentException("File " + fileName + " does not exist");
        }

        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        if (createFileSystem) {
            try (FileSystem ignored = FileSystems.newFileSystem(uri, env)) {
                return loadFromUri(uri);
            }
        } else {
            return loadFromUri(uri);
        }
    }

    private static ConfigurationNode loadFromUri(URI uri) throws ConfigurateException {
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(Path.of(uri))
                .build();

        return loader.load();
    }

    public static URI getEmbeddedURI(String fileName, ClassLoader classLoader) {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName cannot be null");
        }

        try {
            URL url = classLoader.getResource(fileName);

            if (url == null) {
                return null;
            }

            // Convert URL to URI
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static NodePath toPath(String path) {
        String[] split = path.split("\\.");
        return NodePath.of(split);
    }

}
