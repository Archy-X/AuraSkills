package dev.aurelium.auraskills.bukkit.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public class UpdateChecker {

    private final Plugin plugin;
    private final String slug;

    public UpdateChecker(Plugin plugin) {
        this.plugin = plugin;
        this.slug = "auraskills";
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            final String url = "https://api.modrinth.com/v2/project/" + slug + "/version";
            try {
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String responseBody = response.body();

                    // Parse the JSON response using Gson
                    JsonArray jsonArray = JsonParser.parseString(responseBody).getAsJsonArray();
                    JsonObject firstElement = jsonArray.get(0).getAsJsonObject();
                    String versionNumber = firstElement.get("version_number").getAsString();

                    consumer.accept(versionNumber);
                } else {
                    this.plugin.getLogger().info("Cannot look for updates: Request failed with status code " + response.statusCode());
                }
            } catch (Exception e) {
                this.plugin.getLogger().info("Cannot look for updates: " + e.getMessage());
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
