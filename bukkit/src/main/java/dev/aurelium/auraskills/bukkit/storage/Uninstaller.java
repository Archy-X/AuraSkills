package dev.aurelium.auraskills.bukkit.storage;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.NBTFileHandle;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBTCompoundList;
import dev.aurelium.auraskills.bukkit.util.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Uninstaller {

    public void removeAttributes(CommandSender sender) {
        File playerDataFolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "playerdata");
        int successful = 0;
        int error = 0;
        int total = 0;
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (!player.isOnline()) {
                total++;
                File playerFile = new File(playerDataFolder, player.getUniqueId() + ".dat");
                if (playerFile.exists() && playerFile.canWrite()) {
                    try {
                        NBTFileHandle nbtFile = NBT.getFileHandle(playerFile);
                        if (VersionUtils.isAtLeastVersion(21)) {
                            if (removePlayer(nbtFile)) {
                                successful++;
                            }
                        } else {
                            if (removeLegacyPlayer(nbtFile)) {
                                successful++;
                            }
                        }
                    } catch (Exception e) {
                        error++;
                    }
                }
            }
        }
        sender.sendMessage("Searched " + total + " offline players. Successfully removed attributes from " + successful + " players. Failed to remove on " + error + " players.");
    }

    private boolean removePlayer(NBTFileHandle nbtFile) throws IOException {
        ReadWriteNBTCompoundList compoundList = nbtFile.getCompoundList("attributes");
        if (compoundList == null) {
            return false;
        }
        final AtomicBoolean save = new AtomicBoolean(false);
        for (ReadWriteNBT listCompound : compoundList.subList(0, compoundList.size())) {
            ReadWriteNBTCompoundList modifierList = listCompound.getCompoundList("modifiers");
            if (modifierList == null) continue;
            // Check all modifiers of this attribute
            modifierList.removeIf(modifier -> {
                String id = modifier.getString("id");
                // Remove any attribute modifiers with the auraskills namespace
                if (id != null && id.startsWith("auraskills:")) {
                    save.set(true);
                    return true;
                }
                return false;
            });
            if (modifierList.isEmpty()) {
                listCompound.removeKey("modifiers");
            }
        }
        if (save.get()) {
            nbtFile.save();
            return true;
        }
        return false;
    }

    private boolean removeLegacyPlayer(NBTFileHandle nbtFile) throws IOException  {
        ReadWriteNBTCompoundList compoundList = nbtFile.getCompoundList("Attributes");
        if (compoundList == null) {
            return false;
        }
        final AtomicBoolean save = new AtomicBoolean(false);
        for (ReadWriteNBT listCompound : compoundList.subList(0, compoundList.size())) {
            switch (listCompound.getString("Name")) {
                case "generic.maxHealth", "minecraft:generic.max_health" -> {
                    ReadWriteNBTCompoundList modifierList = listCompound.getCompoundList("Modifiers");
                    if (modifierList != null) {
                        modifierList.removeIf((modifier) -> {
                            if (modifier.getString("Name").equals("skillsHealth")) {
                                save.set(true);
                                return true;
                            } else {
                                return false;
                            }
                        });
                        if (modifierList.isEmpty()) {
                            listCompound.removeKey("Modifiers");
                        }
                    }
                }
                case "generic.luck", "minecraft:generic.luck" -> {
                    ReadWriteNBTCompoundList modifierList = listCompound.getCompoundList("Modifiers");
                    if (modifierList != null) {
                        modifierList.removeIf((modifier) -> {
                            if (modifier.getString("Name").equals("AureliumSkills-Luck")) {
                                save.set(true);
                                return true;
                            } else {
                                return false;
                            }
                        });
                        if (modifierList.isEmpty()) {
                            listCompound.removeKey("Modifiers");
                        }
                    }
                }
                case "generic.attackSpeed", "minecraft:generic.attack_speed" -> {
                    ReadWriteNBTCompoundList modifierList = listCompound.getCompoundList("Modifiers");
                    if (modifierList != null) {
                        modifierList.removeIf((modifier) -> {
                            if (modifier.getString("Name").equals("AureliumSkills-LightningBlade")) {
                                save.set(true);
                                return true;
                            } else {
                                return false;
                            }
                        });
                        if (modifierList.isEmpty()) {
                            listCompound.removeKey("Modifiers");
                        }
                    }
                }
            }
        }
        if (save.get()) {
            nbtFile.save();
            return true;
        }
        return false;
    }

}
