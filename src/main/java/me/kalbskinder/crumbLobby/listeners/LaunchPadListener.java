package me.kalbskinder.crumbLobby.listeners;

import me.kalbskinder.crumbLobby.systems.LaunchPadManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class LaunchPadListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();

        if (LaunchPadManager.isLaunchPad(player.getLocation())) {
            Vector velocity = player.getLocation().getDirection().multiply(1.5).setY(1.0);
            player.setVelocity(velocity);
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 1.1f);
        }

    }
}
