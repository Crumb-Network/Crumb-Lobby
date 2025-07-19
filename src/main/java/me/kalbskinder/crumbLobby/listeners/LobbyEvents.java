package me.kalbskinder.crumbLobby.listeners;

import me.kalbskinder.crumbLobby.CrumbLobby;
import me.kalbskinder.crumbLobby.database.Database;
import me.kalbskinder.crumbLobby.database.Query;
import me.kalbskinder.crumbLobby.systems.LobbyItems;
import me.kalbskinder.crumbLobby.systems.PVPSword;
import me.kalbskinder.crumbLobby.utils.LocationHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import javax.swing.text.html.HTMLDocument;
import java.sql.SQLException;
import java.util.List;

public class LobbyEvents implements Listener {
    private static final FileConfiguration config = CrumbLobby.getInstance().getConfig();
    private static boolean isInstantRespawnEnabled;
    private static final List<EntityDamageEvent.DamageCause> defaultCauses = List.of(
            EntityDamageEvent.DamageCause.CAMPFIRE,
            EntityDamageEvent.DamageCause.FREEZE,
            EntityDamageEvent.DamageCause.PROJECTILE,
            EntityDamageEvent.DamageCause.LIGHTNING,
            EntityDamageEvent.DamageCause.STARVATION,
            EntityDamageEvent.DamageCause.VOID,
            EntityDamageEvent.DamageCause.CRAMMING,
            EntityDamageEvent.DamageCause.FREEZE,
            EntityDamageEvent.DamageCause.HOT_FLOOR,
            EntityDamageEvent.DamageCause.ENTITY_EXPLOSION,
            EntityDamageEvent.DamageCause.MAGIC,
            EntityDamageEvent.DamageCause.POISON,
            EntityDamageEvent.DamageCause.WITHER,
            EntityDamageEvent.DamageCause.FLY_INTO_WALL
    );

    // GameRules
    private static boolean itemDrops;
    private static boolean pvp;
    private static boolean hunger;
    private static boolean fallDamage;
    private static boolean fireDamage;
    private static boolean suffocate;
    private static boolean drown;
    private static boolean blockBreak;
    private static boolean blockPlace;
    private static boolean blockInteract;

    public static void reloadGameRules() {
        itemDrops = config.getBoolean("game-rules.itemDrops");
        pvp = config.getBoolean("game-rules.pvp");
        hunger = config.getBoolean("game-rules.hunger");
        fallDamage = config.getBoolean("game-rules.fallDamage");
        fireDamage = config.getBoolean("game-rules.fireDamage");
        suffocate = config.getBoolean("game-rules.suffocate");
        drown = config.getBoolean("game-rules.drown");
        blockBreak = config.getBoolean("game-rules.blockBreak");
        blockPlace = config.getBoolean("game-rules.blockPlace");
        blockInteract = config.getBoolean("game-rules.blockInteract");
    }



    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().hasPermission("crumblobby.admin")) return;
        if (!itemDrops) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (!hunger) {
            if (event.getEntity() instanceof Player player) {
                player.setFoodLevel(20);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player attacker)) return;

        boolean attackerInPvp = PVPSword.isInPvp(attacker);
        boolean victimInPvp = PVPSword.isInPvp(victim);

        if (attackerInPvp && victimInPvp) {
            return;
        }

        if (!pvp) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerTakeDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (defaultCauses.contains(event.getCause())) {
                event.setCancelled(true);
                return;
            }

            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                if (!fallDamage) {
                    event.setCancelled(true);
                }
            }

            if (event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
                if (!suffocate) {
                    event.setCancelled(true);
                }
            }

            if (event.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
                if (!drown) {
                    event.setCancelled(true);
                }
            }

            if (event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                if (!fireDamage) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().hasPermission("crumblobby.admin")) return;
        if (!blockBreak) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().hasPermission("crumblobby.admin")) return;
        Action a = event.getAction();
        if (a.equals(Action.PHYSICAL) || a.equals(Action.RIGHT_CLICK_BLOCK)) {
            if (!blockInteract) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        if (event.getPlayer().hasPermission("crumblobby.admin")) return;
        if (!blockPlace) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
        event.deathMessage(Component.empty());
        isInstantRespawnEnabled = Boolean.TRUE.equals(event.getPlayer().getWorld().getGameRuleValue(GameRule.DO_IMMEDIATE_RESPAWN));
        event.getPlayer().getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        player.getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, isInstantRespawnEnabled);

        try {
            Database database = new Database(CrumbLobby.getInstance().getDataFolder().getAbsolutePath() + "/lobbyDatabase.db");
            Query query = new Query(database.getConnection());
            Location loc = LocationHelper.stringToLocation(query.getSpawn());

            if (loc == null || loc.getWorld() == null) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Spawn ist ungültig! Nutze <yellow>/setspawn<red>."));
                return;
            }

            event.setRespawnLocation(loc);

            Location finalLoc = loc.clone();
            Bukkit.getScheduler().runTaskLater(CrumbLobby.getInstance(), () -> {
                player.teleport(finalLoc);
                LobbyItems.loadDefaultLobbyLayout(player);
                PVPSword.getPVPList().remove(player.getUniqueId());
            }, 2L);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


}
