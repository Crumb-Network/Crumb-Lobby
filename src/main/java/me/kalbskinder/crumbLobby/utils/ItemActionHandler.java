package me.kalbskinder.crumbLobby.utils;

import me.kalbskinder.crumbLobby.CrumbLobby;
import me.kalbskinder.crumbLobby.systems.PlayerVisibility;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;


public class ItemActionHandler implements Listener {
    private static final Logger logger = Logger.getLogger("Crumb-Lobby");
    private static final Map<String, Consumer<Player>> actions = new HashMap<>();
    NamespacedKey key = new NamespacedKey(CrumbLobby.getInstance(), "visibility_item");

    // Registers a right-click action for an item.
    public static void registerAction(String actionId, Consumer<Player> action) {
        if (actionId == null || actionId.trim().isEmpty()) {
            logger.warning("Invalid actionId for item action registration");
            return;
        }
        actions.put(actionId, action);
    }

    // Executes when a player right-clicks something
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;

        if (item.getItemMeta().getPersistentDataContainer().has(PlayerVisibility.VISIBILITY_KEY, PersistentDataType.BYTE)) {
            PlayerVisibility.toggleVisibility(event.getPlayer());
            event.setCancelled(true);
        }

        String actionId = ItemMaker.getActionId(item);
        if (actionId == null) return;

        Consumer<Player> action = actions.get(actionId);
        if (action == null) {
            logger.warning("No action registered for actionId: " + actionId);
            return;
        }

        action.accept(event.getPlayer());
        event.setCancelled(true);
    }

}
