package dev.aurelium.auraskills.common.config.preset;

import java.io.File;
import java.util.List;

public record ConfigPreset(String name, File zipFile, List<PresetEntry> entries) {

}
