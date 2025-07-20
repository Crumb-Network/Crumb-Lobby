package me.kalbskinder.crumbLobby.listeners;

import me.kalbskinder.crumbLobby.CrumbLobby;
import me.kalbskinder.crumbLobby.database.Database;
import me.kalbskinder.crumbLobby.database.Query;
import me.kalbskinder.crumbLobby.utils.LocationHelper;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.sql.SQLException;

public class BlockBreakListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("crumblobby.admin")) return;

        Location loc = event.getBlock().getLocation();
        String locString = LocationHelper.locationToString(loc);

        try {
            Database database = new Database(CrumbLobby.getInstance().getDataFolder().getAbsolutePath() + "/lobbyDatabase.db");
            Query query = new Query(database.getConnection());
            int lpId = query.getLaunchpadId(locString);
            if (lpId == -1) return;

            query.deleteLaunchpad(lpId);
            player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Deleted launchpad with id <yellow>" + lpId + "</yellow>"));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
