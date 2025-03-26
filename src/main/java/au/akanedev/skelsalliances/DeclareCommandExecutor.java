package au.akanedev.skelsalliances;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class DeclareCommandExecutor implements CommandExecutor {

    private final Skelsalliances plugin;

    public DeclareCommandExecutor(Skelsalliances plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length != 2) {
            player.sendMessage("Usage: /declare <player> <ally|neutral|enemy>");
            return false;
        }

        String targetPlayerName = args[0];
        String relationship = args[1].toLowerCase();

        if (!relationship.equals("ally") && !relationship.equals("neutral") && !relationship.equals("enemy")) {
            player.sendMessage("Invalid relationship. Use ally, neutral, or enemy.");
            return false;
        }

        // Declare relationship
        declareRelationship(player, targetPlayerName, relationship);
        player.sendMessage("You have declared " + targetPlayerName + " as " + relationship);
        return true;
    }

    private void declareRelationship(Player player, String targetPlayerName, String relationship) {
        FileConfiguration relationships = plugin.getRelationships();

        // Ensure both player and target player sections exist in the config
        String playerName = player.getName();
        if (!relationships.contains("Players." + playerName)) {
            relationships.createSection("Players." + playerName);
        }

        if (!relationships.contains("Players." + targetPlayerName)) {
            relationships.createSection("Players." + targetPlayerName);
        }

        // Get the current lists for the player and target player
        List<String> playerAllies = relationships.getStringList("Players." + playerName + ".Allies");
        List<String> playerNeutrals = relationships.getStringList("Players." + playerName + ".Neutral");
        List<String> playerEnemies = relationships.getStringList("Players." + playerName + ".Enemies");

        List<String> targetAllies = relationships.getStringList("Players." + targetPlayerName + ".Allies");
        List<String> targetNeutrals = relationships.getStringList("Players." + targetPlayerName + ".Neutral");
        List<String> targetEnemies = relationships.getStringList("Players." + targetPlayerName + ".Enemies");

        // Clear the target player's previous relationship in the relevant list
        switch (relationship) {
            case "ally":
                // Clear the existing relationship from all other lists
                playerEnemies.remove(targetPlayerName);
                playerNeutrals.remove(targetPlayerName);

                targetEnemies.remove(playerName);
                targetNeutrals.remove(playerName);

                // Add the player to the allies list
                if (!playerAllies.contains(targetPlayerName)) {
                    playerAllies.add(targetPlayerName);
                }
                if (!targetAllies.contains(playerName)) {
                    targetAllies.add(playerName);
                }
                break;
            case "neutral":
                // Clear the existing relationship from all other lists
                playerAllies.remove(targetPlayerName);
                playerEnemies.remove(targetPlayerName);

                targetAllies.remove(playerName);
                targetEnemies.remove(playerName);

                // Add the player to the neutrals list
                if (!playerNeutrals.contains(targetPlayerName)) {
                    playerNeutrals.add(targetPlayerName);
                }
                if (!targetNeutrals.contains(playerName)) {
                    targetNeutrals.add(playerName);
                }
                break;
            case "enemy":
                // Clear the existing relationship from all other lists
                playerAllies.remove(targetPlayerName);
                playerNeutrals.remove(targetPlayerName);

                targetAllies.remove(playerName);
                targetNeutrals.remove(playerName);

                // Add the player to the enemies list
                if (!playerEnemies.contains(targetPlayerName)) {
                    playerEnemies.add(targetPlayerName);
                }
                if (!targetEnemies.contains(playerName)) {
                    targetEnemies.add(playerName);
                }
                break;
        }

        // Save the updated lists back to the config
        relationships.set("Players." + playerName + ".Allies", playerAllies);
        relationships.set("Players." + playerName + ".Neutral", playerNeutrals);
        relationships.set("Players." + playerName + ".Enemies", playerEnemies);

        relationships.set("Players." + targetPlayerName + ".Allies", targetAllies);
        relationships.set("Players." + targetPlayerName + ".Neutral", targetNeutrals);
        relationships.set("Players." + targetPlayerName + ".Enemies", targetEnemies);

        plugin.saveRelationships();

        // Check if the target player is online
        Player targetPlayer = plugin.getServer().getPlayer(targetPlayerName);
        if (targetPlayer != null && targetPlayer.isOnline()) {
            // Send a message to the target player
            targetPlayer.sendMessage(playerName + " has declared you as " + relationship + ".");
        }
    }
}
