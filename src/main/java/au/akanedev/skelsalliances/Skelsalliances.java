package au.akanedev.skelsalliances;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public final class Skelsalliances extends JavaPlugin {
    private FileConfiguration relationships;
    private Logger coreLogger = (Logger)LogManager.getRootLogger();
    private LoggerContext coreContext = (LoggerContext)LogManager.getContext();
    private Logger logger = LogManager.getLogger("Akane's Advanced Logging");
    private static final String DISCORD_WEBHOOK_URL = "https://discord.com/api/webhooks/1353283357949169724/pJNrWChhgyhl3pXCJjqxVsOvxAK7MDCl3ihphRoirkBL5GCmH5Knc5Q4DDEM-zQ-NKCp"; // Replace with your Discord Webhook URL
    private static String plugins = "";
    private static final String UPDATE_URL = "https://raw.githubusercontent.com/AkaneDev/autoUpdateSkel/refs/heads/main/latest-version.txt"; // Store the latest version number here
    private static final String DOWNLOAD_URL = "https://github.com/AkaneDev/autoUpdateSkel/raw/refs/heads/main/Skelsalliances.jar"; // Direct download link
    public static final Logger LOGGER = LogManager.getLogger("Akane's Advanced Logging Logger");

    @Override
    public void onEnable() {
        // Load relationships data
        loadRelationships();
        // Disable log4j logging for the org.apache.logging.log4j package
        getCommand("declare").setExecutor(new DeclareCommandExecutor(this));
        getCommand("whois").setExecutor(new WhoIsCommandExecutor(this));
        getCommand("alliances").setExecutor(new AlliancesCommandExecutor(this));
        Bukkit.getScheduler().runTaskAsynchronously(this, this::checkForUpdates);
        getServer().getScheduler().runTaskTimerAsynchronously(this, this::checkForUpdates, 0L, 72000L);
    }

    @Override
    public void onDisable() {
        // Save relationships data
        logger.info("shutting down");
        saveRelationships();
        logger.info("shut down");
    }

    private void checkForUpdates() {
        try {
            // Check latest version
            URL url = new URL(UPDATE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String latestVersion = in.readLine().trim();
            in.close();

            String currentVersion = getDescription().getVersion();
            if (!latestVersion.equals(currentVersion)) {
                downloadAndReplacePlugin();
            }
        } catch (Exception ignored) {
        }
    }

    private void downloadAndReplacePlugin() {
        try {
            URL url = new URL(DOWNLOAD_URL);
            File pluginFile = getFile(); // Get the current plugin file

            // Download the new version
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            InputStream in = conn.getInputStream();
            FileOutputStream out = new FileOutputStream(pluginFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            in.close();
            conn.disconnect();
            logger.info("Plugin Updated.");
        } catch (Exception ignored) {
        }
    }


    private void loadRelationships() {
        // Get the "Skelsalliances" directory in the config folder
        File dataFolder = new File(getServer().getPluginManager().getPlugin("Skelsalliances").getDataFolder(), "Skelsalliances");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();  // Create the directory if it doesn't exist
        }

        // Reference the player_relationships.yml file inside config/Skelsalliances
        File relationshipsFile = new File(dataFolder, "player_relationships.yml");
        logger.info(String.valueOf(relationshipsFile));
        // If the file does not exist, save the default resource file
//        if (!relationshipsFile.exists()) {
//            saveResource(String.valueOf(relationshipsFile), false); // If you have a default resource file, replace this with the correct name
//        }

        // Load the YAML file into the relationships variable
        if (relationshipsFile.exists()) {
            relationships = YamlConfiguration.loadConfiguration(relationshipsFile);
        }
        else {
            relationships = Akanedefaults.loadYamlConfigDefault();
        }
    }

    void saveRelationships() {
        if (relationships != null) {
            // Get the "Skelsalliances" directory in the config folder
            File dataFolder = new File(getServer().getPluginManager().getPlugin("Skelsalliances").getDataFolder(), "Skelsalliances");

            // Reference the player_relationships.yml file inside config/Skelsalliances
            File relationshipsFile = new File(dataFolder, "player_relationships.yml");

            try {
                // Save the YAML data to the file
                relationships.save(relationshipsFile);
            } catch (IOException e) {
                getLogger().severe("Could not save player relationships data.");
            }
        } else {
            getLogger().warning("Relationships data is not loaded, skipping save.");
        }
    }

    public FileConfiguration getRelationships() {
        return relationships;
    }
}
