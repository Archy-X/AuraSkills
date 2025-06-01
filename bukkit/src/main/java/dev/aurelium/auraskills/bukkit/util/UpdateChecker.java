package dev.aurelium.auraskills.bukkit.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.function.BiConsumer;

public class UpdateChecker {

    public static final String MODRINTH_ID = "uDdZAVls";

    private final AuraSkills plugin;
    private final String projectId;

    public UpdateChecker(AuraSkills plugin) {
        this.plugin = plugin;
        this.projectId = MODRINTH_ID;
    }

    public void sendUpdateMessageAsync(CommandSender sender) {
        getVersion((versionOpt, idOpt) -> versionOpt.ifPresent(version -> idOpt.ifPresent(id -> {
            if (isOutdated(plugin.getDescription().getVersion(), version)) {
                final String prefix = sender instanceof Player ? plugin.getPrefix(plugin.getDefaultLanguage()) : "[AuraSkills] ";
                final String downloadLink = "https://modrinth.com/plugin/" + UpdateChecker.MODRINTH_ID + "/version/" + id;

                String msg = TextUtil.replace(plugin.getMsg(CommandMessage.VERSION_NEW_UPDATE, plugin.getLocale(sender)),
                        "{current_version}", plugin.getDescription().getVersion(),
                        "{latest_version}", version,
                        "{link}", downloadLink,
                        "{prefix}", prefix);

                if (!msg.isEmpty()) {
                    sender.sendMessage(msg);
                }
            }
        })));
    }

    // Consumer accepts versionNumber and versionId
    public void getVersion(final BiConsumer<Optional<String>, Optional<String>> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            String loader;
            String serverName = Bukkit.getServer().getName();
            if (serverName.equalsIgnoreCase("CraftBukkit") || serverName.equalsIgnoreCase("Spigot")) {
                loader = "spigot";
            } else {
                loader = "paper";
            }

            final String baseUrl = "https://api.modrinth.com/v2/project/" + projectId + "/version";

            final String gameVersion = VersionUtils.getVersionString(Bukkit.getBukkitVersion());
            final String query = "loaders=%5B%22" + loader + "%22%5D&game_versions=%5B%22" + gameVersion + "%22%5D";
            final String url = baseUrl + "?" + query;
            try (HttpClient client = HttpClient.newHttpClient()) {

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String responseBody = response.body();
                    if (responseBody == null || responseBody.trim().isEmpty()) {
                        acceptEmpty(consumer);
                        return;
                    }

                    // Parse the JSON response using Gson
                    JsonArray jsonArray = JsonParser.parseString(responseBody).getAsJsonArray();
                    if (jsonArray.isEmpty()) {
                        acceptEmpty(consumer);
                        return;
                    }

                    JsonObject firstRelease = null;
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject versionObj = jsonArray.get(i).getAsJsonObject();
                        JsonElement element = versionObj.get("version_type");
                        // Filter for release versions
                        if (element != null && element.getAsString().equals("release")) {
                            firstRelease = versionObj;
                            break;
                        }
                    }
                    if (firstRelease == null) {
                        acceptEmpty(consumer);
                        return;
                    }

                    JsonElement versionNumElement = firstRelease.get("version_number");
                    if (versionNumElement == null) {
                        acceptEmpty(consumer);
                        return;
                    }
                    String versionNumber = versionNumElement.getAsString();

                    JsonElement idElement = firstRelease.get("id");
                    if (idElement == null) {
                        acceptEmpty(consumer);
                        return;
                    }
                    String id = idElement.getAsString();

                    consumer.accept(Optional.of(versionNumber), Optional.of(id));
                    return;
                } else {
                    this.plugin.getLogger().info("Cannot look for updates: Request failed with status code " + response.statusCode());
                }
            } catch (Exception e) {
                this.plugin.getLogger().info("Cannot look for updates: " + e.getMessage());
            }
            acceptEmpty(consumer);
        });
    }

    private void acceptEmpty(BiConsumer<Optional<String>, Optional<String>> consumer) {
        consumer.accept(Optional.empty(), Optional.empty());
    }

    private boolean isOutdated(String localVersion, String resourceVersion) {
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
            } catch (NumberFormatException ignored) {
            }
        }
        return true;
    }

}
