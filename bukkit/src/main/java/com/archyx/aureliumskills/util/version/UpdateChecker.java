package com.archyx.aureliumskills.util.version;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {

    private final Plugin plugin;
    private final int resourceId;

    public UpdateChecker(Plugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.nextLine());
                }
            } catch (IOException exception) {
                this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        });
    }

    public boolean isOutdated(String localVersion, String resourceVersion) {
        if (localVersion.equalsIgnoreCase(resourceVersion)) { // Versions match exactly
            return false;
        }
        if (localVersion.contains("Pre-Release") || localVersion.contains("Build")) { // Ignore Pre-Release or dev builds
            return false;
        }
        String[] localSplit = localVersion.split(" ");
        String[] resourceSplit = resourceVersion.split(" ");
        if (localSplit.length >= 2 && resourceSplit.length >= 2) {
            String localVersionNumber = localSplit[1];
            String resourceVersionNumber = resourceSplit[1];
            String[] localVersionSplit = localVersionNumber.split("\\.");
            String[] resourceVersionSplit = resourceVersionNumber.split("\\.");

            // Check each part of the version number between the dots
            for (int i = 0; i < localVersionSplit.length; i++) {
                if (i >= resourceVersionSplit.length) {
                    break;
                }
                try {
                    int local = Integer.parseInt(localVersionSplit[i]);
                    int resource = Integer.parseInt(resourceVersionSplit[i]);
                    if (local < resource) { // If local is less than resource, return as outdated
                        return true;
                    } else if (local > resource) { // If local is greater than resource, return as not outdated
                        return false;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return true;
    }

}
