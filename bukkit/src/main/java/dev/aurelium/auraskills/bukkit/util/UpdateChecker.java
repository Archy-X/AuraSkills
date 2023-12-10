package dev.aurelium.auraskills.bukkit.util;

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
        if (localVersion.contains("Pre-Release") || localVersion.contains("Build") || localVersion.contains("pre") || localVersion.contains("SNAPSHOT")) { // Ignore Pre-Release or dev builds
            return false;
        }
        String[] localSplit = localVersion.split(" ");
        String[] resourceSplit = resourceVersion.split(" ");
        String localNum;
        String resourceNum;
        if (localSplit.length >= 2 && resourceSplit.length >= 2) {
            localNum = localSplit[1];
        } else {
            localNum = localSplit[0];
        }
        if (resourceSplit.length >= 2) {
            resourceNum = resourceSplit[1];
        } else {
            resourceNum = resourceSplit[0];
        }
        // Remove part after any hyphens including the hyphen
        int localIndex = localNum.indexOf("-");
        if (localIndex != -1) {
            localNum = localNum.substring(0, localIndex);
        }
        int resourceIndex = resourceNum.indexOf("-");
        if (resourceIndex != -1) {
            resourceNum = resourceNum.substring(0, resourceIndex);
        }

        String[] localVersionSplit = localNum.split("\\.");
        String[] resourceVersionSplit = resourceNum.split("\\.");

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
        return true;
    }

}
