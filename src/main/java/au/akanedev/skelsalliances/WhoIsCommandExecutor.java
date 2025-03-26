package au.akanedev.skelsalliances;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class WhoIsCommandExecutor implements CommandExecutor {

    private final Skelsalliances plugin;

    public WhoIsCommandExecutor(Skelsalliances plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage("Usage: /whois <player>");
            return false;
        }

        String targetPlayerName = args[0];

        // Display relationship info for the target player
        displayRelationshipInfo(player, targetPlayerName);
        return true;
    }

    private void displayRelationshipInfo(Player player, String targetPlayerName) {
        FileConfiguration relationships = plugin.getRelationships();

        // Check if the target player has any relationship data
        if (!relationships.contains("Players." + targetPlayerName)) {
            player.sendMessage(targetPlayerName + " has no relationship data.");
            return;
        }

        // Get the lists of allies, neutrals, and enemies for the target player
        List<String> allies = relationships.getStringList("Players." + targetPlayerName + ".Allies");
        List<String> neutrals = relationships.getStringList("Players." + targetPlayerName + ".Neutral");
        List<String> enemies = relationships.getStringList("Players." + targetPlayerName + ".Enemies");

        // Build the message to display the relationship info
        StringBuilder message = new StringBuilder();
        message.append("Player: ").append(targetPlayerName).append("\n");

        // Display allies
        if (!allies.isEmpty()) {
            message.append("Allies: ").append(String.join(", ", allies)).append("\n");
        } else {
            message.append("Allies: None\n");
        }

        // Display enemies
        if (!enemies.isEmpty()) {
            message.append("Enemies: ").append(String.join(", ", enemies)).append("\n");
        } else {
            message.append("Enemies: None\n");
        }

        // Send the constructed message to the player
        player.sendMessage(message.toString());
    }
}
