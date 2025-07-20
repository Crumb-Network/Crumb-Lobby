package me.kalbskinder.crumbLobby.listeners;


import me.kalbskinder.crumbLobby.CrumbLobby;
import me.kalbskinder.crumbLobby.database.Database;
import me.kalbskinder.crumbLobby.database.Query;
import me.kalbskinder.crumbLobby.guis.PlateTypeMenu;
import me.kalbskinder.crumbLobby.utils.ItemMaker;
import me.kalbskinder.crumbLobby.utils.LocationHelper;
import me.kalbskinder.crumbLobby.utils.PressurePlates;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryClickListener implements Listener {
    private static final CrumbLobby plugin = CrumbLobby.getInstance();
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        Inventory clickedInventory = e.getClickedInventory();
        if (!player.hasPermission("crumblobby.admin")) return;

        if (clickedInventory == null) return;

        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String displayName = "";
        ItemMeta meta = clickedItem.getItemMeta();

        if (meta.hasDisplayName()) {
            displayName = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
        }

        String menuTitle = PlainTextComponentSerializer.plainText().serialize(e.getView().title());
        menuTitle = menuTitle.trim();

        if (menuTitle.equals("ʟᴀᴜɴᴄʜ-ᴘᴀᴅѕ")) {
            e.setCancelled(true);

            if (displayName.equals("Close")) {
                clickedInventory.close();
                return;
            }

            if (displayName.equals("Plate Launchpad")) {
                ItemStack item = ItemMaker.createItem("minecraft:oak_pressure_plate", 1, "<yellow>Plate Launchpad", List.of("<gray>Place this where you want", "<gray>your launchpad to be."), "");
                player.getInventory().setItem(0, item);
                player.closeInventory();
                player.getInventory().setHeldItemSlot(0);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.1f, 2.0f);
            }
        }

        if (menuTitle.equals("Change Type")) {
            e.setCancelled(true);

            if (displayName.equals("Close")) {
                clickedInventory.close();
                return;
            }

            final String itemName = displayName;

            PressurePlates.get().forEach(plate -> {
                if (itemName.equals(PressurePlates.formatPlateName(plate))) {
                    Component loreLine = e.getView().getItem(0).getItemMeta().lore().get(0);
                    int lpId = Integer.parseInt(PlainTextComponentSerializer.plainText().serialize(e.getView().getItem(0).getItemMeta().lore().get(0)));
                    Location loc = null;

                    // Get location of plate
                    try {
                        Database database = new Database(CrumbLobby.getInstance().getDataFolder().getAbsolutePath() + "/lobbyDatabase.db");
                        Query query = new Query(database.getConnection());
                        loc = LocationHelper.stringToLocation(query.getLaunchpadLocation(lpId));
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                    if (loc == null) return;
                    Material material = Material.matchMaterial(plate.replace("minecraft:", ""));

                    if (material != null) {
                        loc.getBlock().setType(material);

                        try {
                            Database database = new Database(CrumbLobby.getInstance().getDataFolder().getAbsolutePath() + "/lobbyDatabase.db");
                            Query query = new Query(database.getConnection());
                            query.updateLaunchpadType(lpId, plate);
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.1f, 2.0f);
                            PlateTypeMenu.openMenu(player, lpId);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }

                    } else {
                        plugin.getLogger().warning("Unknown material: " + plate);
                    }
                }
            });
        }


    }
}
