package au.akanedev.skelsalliances;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.StringReader;

public class Akanedefaults {
    public static FileConfiguration loadYamlConfigDefault() {
        // Example YAML content (replace this with your desired YAML string)
        String yamlContent = "Players:\n  Console:\n    Allies:\n    Enemies:\n    Neutral:";

        // Create a YamlConfiguration instance
        YamlConfiguration config = new YamlConfiguration();

        // Load the YAML from the string content
        try {
            config.load(new StringReader(yamlContent));
        } catch (Exception e) {
            e.printStackTrace();  // Handle the exception as necessary
        }

        // Return the loaded configuration
        return config;
    }
}
