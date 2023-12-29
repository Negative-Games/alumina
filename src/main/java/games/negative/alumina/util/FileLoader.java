package games.negative.alumina.util;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileLoader {

    /**
     * Loads a file from the plugin's resources folder.
     *
     * @param plugin   The plugin to load the file from.
     * @param resource The name of the file to load.
     * @return The loaded file.
     */
    public static File loadFile(@NotNull final JavaPlugin plugin, @NotNull final String resource) {
        Preconditions.checkNotNull(plugin, "Plugin cannot be null.");
        Preconditions.checkNotNull(resource, "Resource cannot be null.");

        File folder = plugin.getDataFolder();
        if (!folder.exists()) folder.mkdir();

        File resourceFile = new File(folder, resource);
        if (resourceFile.exists()) return resourceFile;

        resourceFile.getParentFile().mkdirs();

        try (InputStream in = plugin.getResource(resource)) {
            Preconditions.checkNotNull(in, "Plugin Resource " + resource + " does not exist.");

            OutputStream out = new FileOutputStream(resourceFile);
            ByteStreams.copy(in, out);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load resource " + resource + ".");
            return null;
        }

        return resourceFile;
    }

    /**
     * Loads a file configuration from the plugin's resources folder.
     * @param plugin The plugin to load the file from.
     * @param resource The name of the file to load.
     * @return The loaded file configuration.
     */
    public static FileConfiguration loadFileConfiguration(@NotNull final JavaPlugin plugin, @NotNull final String resource) {
        File file = loadFile(plugin, resource);
        if (file == null) return null;

        return YamlConfiguration.loadConfiguration(file);
    }
}
