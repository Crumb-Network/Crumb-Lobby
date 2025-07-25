package me.kalbskinder.crumbLobby;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.kalbskinder.crumbLobby.commands.BaseCommand;
import me.kalbskinder.crumbLobby.commands.SetSpawnCommand;
import me.kalbskinder.crumbLobby.commands.SpawnCommand;
import me.kalbskinder.crumbLobby.listeners.*;
import me.kalbskinder.crumbLobby.systems.LaunchPadManager;
import me.kalbskinder.crumbLobby.systems.LobbyItems;
import me.kalbskinder.crumbLobby.systems.PVPSword;
import me.kalbskinder.crumbLobby.utils.ItemActionHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import me.kalbskinder.crumbLobby.database.Database;

import java.sql.SQLException;
import java.util.logging.Logger;

public final class CrumbLobby extends JavaPlugin {
    private Database database;
    private static CrumbLobby instance;

    public static CrumbLobby getInstance() {
        return instance;
    }

    private void startUpMessage() {
        Logger logger = Logger.getLogger("Crumb-Lobby");
        logger.info("-------------------------------");
        logger.info("        CM - Crumb Lobby       ");
        logger.info("          Version: 1.0.0");
        logger.info("      Author: Kalbskinder");
        logger.info("--------------------------------");
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new LobbyEvents(), this);
        pm.registerEvents(new PlayerJoinLeaveEvents(), this);
        pm.registerEvents(new ItemActionHandler(), this);
        pm.registerEvents(new LobbyItems(), this);

        pm.registerEvents(new BlockBreakListener(), this);
        pm.registerEvents(new BlockPlaceListener(), this);
        pm.registerEvents(new InventoryClickListener(), this);
        pm.registerEvents(new LaunchPadListener(), this);
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        registerListeners();
        startUpMessage();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(new SetSpawnCommand().getSetSpawnCommand());
            commands.registrar().register(new SpawnCommand().getSpawnCommand());
            commands.registrar().register(new BaseCommand().getBaseCommand());
        });

        try {
            database = new Database(getDataFolder().getAbsolutePath() + "/lobbyDatabase.db");
        } catch (SQLException ex) {
            ex.printStackTrace();
            getLogger().severe("Failed to connect to the database! " + ex.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        LobbyEvents.reloadGameRules();
        PVPSword.startMonitoring();
        LaunchPadManager.loadPadsFromDatabase();
    }

    @Override
    public void onDisable() {
        try {
            database.closeConnection();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
