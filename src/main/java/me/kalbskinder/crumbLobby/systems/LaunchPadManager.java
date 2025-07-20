package me.kalbskinder.crumbLobby.systems;


import me.kalbskinder.crumbLobby.CrumbLobby;
import me.kalbskinder.crumbLobby.database.Database;
import me.kalbskinder.crumbLobby.database.Query;
import org.bukkit.Location;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class LaunchPadManager {
    private static final Set<Location> launchPadLocations = new HashSet<>();

    public static void loadPadsFromDatabase() {
        try {
            Database database = new Database(CrumbLobby.getInstance().getDataFolder().getAbsolutePath() + "/lobbyDatabase.db");
            Query query = new Query(database.getConnection());
            launchPadLocations.clear();
            launchPadLocations.addAll(query.getLaunchpadLocations());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isLaunchPad(Location loc) {
        Location blockLoc = loc.getBlock().getLocation();
        return launchPadLocations.contains(blockLoc);
    }

    public static void addPad(Location loc) {
        launchPadLocations.add(loc.getBlock().getLocation());
    }
}

