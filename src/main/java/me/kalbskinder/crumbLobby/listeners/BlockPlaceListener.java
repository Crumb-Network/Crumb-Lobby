package me.kalbskinder.crumbLobby.listeners;

import me.kalbskinder.crumbLobby.CrumbLobby;
import me.kalbskinder.crumbLobby.database.Database;
import me.kalbskinder.crumbLobby.database.Query;
import me.kalbskinder.crumbLobby.systems.LaunchPadManager;
import me.kalbskinder.crumbLobby.utils.LocationHelper;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.Objects;

public class BlockPlaceListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("crumblobby.admin")) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        String displayName = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(meta.displayName()));
        if (!displayName.equals("Plate Launchpad")) return;

        String location = LocationHelper.locationToString(event.getBlock().getLocation());

        try {
            Database database = new Database(CrumbLobby.getInstance().getDataFolder().getAbsolutePath() + "/lobbyDatabase.db");
            Query query = new Query(database.getConnection());
            query.createLaunchpad(location, "minecraft:oak_pressure_plate");
            LaunchPadManager.addPad(event.getBlock().getLocation());
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.1f, 2.0f);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
