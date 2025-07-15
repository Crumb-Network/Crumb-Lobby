package me.kalbskinder.crumbLobby.listeners;

import me.kalbskinder.crumbLobby.CrumbLobby;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class LobbyEvents implements Listener {
    private final FileConfiguration config = CrumbLobby.getInstance().getConfig();
    boolean itemDrops = config.getBoolean("game-rules.dropItems");
    boolean pvp = config.getBoolean("game-rules.pvp");
    boolean hunger = config.getBoolean("game-rules.hunger");

    boolean fallDamage = config.getBoolean("game-rules.fallDamage");
    boolean fireDamage = config.getBoolean("game-rules.fireDamage");
    boolean suffocate = config.getBoolean("game-rules.suffocate");
    boolean drown = config.getBoolean("game-rules.drown");

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

}
