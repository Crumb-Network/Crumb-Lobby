package me.kalbskinder.crumbLobby.systems;

import me.kalbskinder.crumbLobby.CrumbLobby;
import me.kalbskinder.crumbLobby.utils.ItemMaker;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class PlayerVisibility {
    private static final Set<UUID> hiddenPlayers = new HashSet<>();
    private static final FileConfiguration config = CrumbLobby.getInstance().getConfig();
    private static final String visibilityNameShown = config.getString("items.player-visibility.name-shown");
    private static final String visibilityNameHidden = config.getString("items.player-visibility.name-hidden");
    public static final NamespacedKey VISIBILITY_KEY = new NamespacedKey(CrumbLobby.getInstance(), "visibility_item");
    public static final CrumbLobby plugin = CrumbLobby.getInstance();

    public static void toggleVisibility(Player player) {
        UUID uuid = player.getUniqueId();

        boolean hiding = !hiddenPlayers.contains(uuid);

        if (hiding) {
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!other.equals(player)) {
                    player.hidePlayer(plugin, other);
                }
            }
            hiddenPlayers.add(uuid);
        } else {
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!other.equals(player)) {
                    player.showPlayer(plugin, other);
                }
            }
            hiddenPlayers.remove(uuid);
        }

        updateVisibilityItem(player, hiding);
    }

    public static ItemStack createVisibilityItem(Player player, boolean isHiding) {
        String name = isHiding ? visibilityNameHidden : visibilityNameShown;
        String material = isHiding ? "minecraft:gray_dye" : "minecraft:lime_dye";

        ItemStack item = ItemMaker.createItem(
                material,
                1,
                name,
                Collections.emptyList(),
                null
        );

        assert item != null;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(VISIBILITY_KEY, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
        }

        return item;
    }


    private static void updateVisibilityItem(Player player, boolean isHiding) {
        ItemStack newItem = createVisibilityItem(player, isHiding);

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack current = player.getInventory().getItem(i);
            if (current == null || !current.hasItemMeta()) continue;

            ItemMeta currentMeta = current.getItemMeta();
            if (currentMeta.getPersistentDataContainer().has(VISIBILITY_KEY, PersistentDataType.BYTE)) {
                player.getInventory().setItem(i, newItem);
                break;
            }
        }

        // Inventory Update for client
        player.updateInventory();
    }

    public static void resetVisibility(Player player) {
        hiddenPlayers.remove(player.getUniqueId());
        for (Player other : Bukkit.getOnlinePlayers()) {
            player.showPlayer(plugin, other);
        }
    }
}
