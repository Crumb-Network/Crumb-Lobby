package me.kalbskinder.crumbLobby.listeners;

import me.kalbskinder.crumbLobby.CrumbLobby;
import me.kalbskinder.crumbLobby.database.Database;
import me.kalbskinder.crumbLobby.database.Query;
import me.kalbskinder.crumbLobby.systems.PVPSword;
import me.kalbskinder.crumbLobby.systems.PlayerVisibility;
import me.kalbskinder.crumbLobby.utils.LocationHelper;
import me.kalbskinder.crumbLobby.utils.TextFormatter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class PlayerJoinLeaveEvents implements Listener {
    private final FileConfiguration config = CrumbLobby.getInstance().getConfig();
    private static final TextFormatter textFormatter = new TextFormatter();
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final CrumbLobby plugin = CrumbLobby.getInstance();

    boolean joinFireworks = config.getBoolean("settings.join-fireworks", true);

    boolean welcomeMessageEnabled = config.getBoolean("messages.welcome-message.enabled");
    List<String> welcomeMessage = config.getStringList("messages.welcome-message.content");

    boolean joinMessageEnabled = config.getBoolean("messages.join-message.enabled");
    String joinMessage = config.getString("messages.join-message.content");

    boolean leaveMessageEnabled = config.getBoolean("messages.leave-message.enabled");
    String leaveMessage = config.getString("messages.leave-message.content");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.getInventory().setHeldItemSlot(2);
        PlayerVisibility.resetVisibility(player);

        Map<String, String> placeholders = Map.of(
                "player_display_name", mm.serialize(player.displayName()),
                "player_username", player.getName()
        );

        if (PVPSword.isInPvp(player)) {
            PVPSword.getPVPList().remove(player.getUniqueId());
        }

        if (welcomeMessageEnabled) {
            welcomeMessage.forEach(line -> player.sendMessage(textFormatter.formatString(line, placeholders)));
        }

        if (joinMessageEnabled) {
            event.joinMessage(textFormatter.formatString(joinMessage, placeholders));
        }

        try {
            Database database = new Database(plugin.getDataFolder().getAbsolutePath() + "/lobbyDatabase.db");
            Query query = new Query(database.getConnection());
            String locationString = query.getSpawn();
            if (locationString == null) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Unable to teleport player to spawn because location is null! You can set a spawn location with the command <yellow>/setspawn<red>."));
                return;
            }
            Location location = LocationHelper.stringToLocation(locationString);
            player.teleport(location);
        } catch (SQLException ex) {
            player.sendMessage(mm.deserialize("<red>Failed to teleport you to spawn!"));
            ex.printStackTrace();
        }

        if (joinFireworks) {
            Location loc = player.getLocation();
            World world = loc.getWorld();
            if (world != null) {
                Firework firework = world.spawn(loc, Firework.class);

                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffect(FireworkEffect.builder()
                        .withColor(Color.AQUA)
                        .withFade(Color.WHITE)
                        .with(FireworkEffect.Type.BALL_LARGE)
                        .flicker(true)
                        .trail(true)
                        .build());
                meta.setPower(1);
                firework.setFireworkMeta(meta);
            }
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
