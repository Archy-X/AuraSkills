package dev.aurelium.auraskills.bukkit.storage.sql;

import org.testcontainers.DockerClientFactory;

/**
 * Lets the database-backed tests report as skipped rather than failed on machines without Docker,
 * so that a plain {@code ./gradlew build} still works there. The probe runs once per JVM and
 * before Testcontainers tries to start anything.
 */
public final class DockerAvailable {

    private static final boolean AVAILABLE = probe();

    private DockerAvailable() {
    }

    private static boolean probe() {
        try {
            return DockerClientFactory.instance().isDockerAvailable();
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean isAvailable() {
        return AVAILABLE;
    }

}
