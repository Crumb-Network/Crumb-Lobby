package me.kalbskinder.crumbLobby.listeners;

import me.kalbskinder.crumbLobby.CrumbLobby;
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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.swing.text.html.HTMLDocument;

public class LobbyEvents implements Listener {
    private static final FileConfiguration config = CrumbLobby.getInstance().getConfig();

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
        if (event.getEntity() instanceof Player) {
            if (event.getDamager() instanceof Player) {
                if (!pvp) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerTakeDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
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
}
