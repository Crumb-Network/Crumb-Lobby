package me.kalbskinder.crumbLobby.listeners;

import me.kalbskinder.crumbLobby.CrumbLobby;
import me.kalbskinder.crumbLobby.utils.TextFormatter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Map;

public class PlayerJoinLeaveEvents implements Listener {
    private final FileConfiguration config = CrumbLobby.getInstance().getConfig();
    private static final TextFormatter textFormatter = new TextFormatter();
    private static final MiniMessage mm = MiniMessage.miniMessage();

    boolean welcomeMessageEnabled = config.getBoolean("messages.welcome-message.enabled");
    List<String> welcomeMessage = config.getStringList("messages.welcome-message.content");

    boolean joinMessageEnabled = config.getBoolean("messages.join-message.enabled");
    String joinMessage = config.getString("messages.join-message.content");

    boolean leaveMessageEnabled = config.getBoolean("messages.leave-message.enabled");
    String leaveMessage = config.getString("messages.leave-message.content");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Map<String, String> placeholders = Map.of(
                "player_display_name", mm.serialize(player.displayName()),
                "player_username", player.getName()
        );

        if (welcomeMessageEnabled) {
            welcomeMessage.forEach(line -> player.sendMessage(textFormatter.formatString(line, placeholders)));
        }

        if (joinMessageEnabled) {
            event.joinMessage(textFormatter.formatString(joinMessage, placeholders));
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (leaveMessageEnabled) {
            Player player = event.getPlayer();

            Map<String, String> placeholders = Map.of(
                    "player_display_name", mm.serialize(player.displayName()),
                    "player_username", player.getName()
            );
            event.quitMessage(textFormatter.formatString(leaveMessage, placeholders));
        }
    }
}
