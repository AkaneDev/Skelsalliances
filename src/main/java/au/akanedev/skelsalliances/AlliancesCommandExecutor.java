package au.akanedev.skelsalliances;

import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class AlliancesCommandExecutor implements CommandExecutor {

    private final Skelsalliances plugin;
    private static final Logger logger = LogManager.getLogger();
    private LoggerContext coreContext = (LoggerContext)LogManager.getContext();

    public AlliancesCommandExecutor(Skelsalliances plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // If no subcommand is provided, show a usage message
            player.sendMessage("Usage: /alliances <declare|whois|eval>");
            return false;
        }

        String subcommand = args[0].toLowerCase();

        // Handle subcommands
        switch (subcommand) {
            case "declare":
                // Pass control to the DeclareCommandExecutor
                return handleDeclareCommand(player, args);
            case "whois":
                // Pass control to the WhoIsCommandExecutor
                return handleWhoIsCommand(player, args);
            default:
                player.sendMessage("Unknown subcommand. Usage: /alliances <declare|whois>");
                return false;
        }
    }

    // Method to handle the "declare" subcommand
    private boolean handleDeclareCommand(Player player, String[] args) {
        if (args.length != 3) {
            player.sendMessage("Usage: /alliances declare <player> <ally|neutral|enemy>");
            return false;
        }

        String targetPlayerName = args[1];
        String relationship = args[2].toLowerCase();

        if (!relationship.equals("ally") && !relationship.equals("neutral") && !relationship.equals("enemy")) {
            player.sendMessage("Invalid relationship. Use ally, neutral, or enemy.");
            return false;
        }

        // Declare relationship
        new DeclareCommandExecutor(plugin).onCommand(player, null, null, args);
        return true;
    }

    // Method to handle the "whois" subcommand
    private boolean handleWhoIsCommand(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage("Usage: /alliances whois <player>");
            return false;
        }

        String targetPlayerName = args[1];

        // Display relationship info for the target player
        new WhoIsCommandExecutor(plugin).onCommand(player, null, null, args);
        return true;
    }
}
