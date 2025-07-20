package me.kalbskinder.crumbLobby.guis;

import me.kalbskinder.crumbLobby.utils.ItemMaker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class LaunchPadMenu {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9 * 3, miniMessage.deserialize("<bold><color:#43c467>ʟᴀᴜɴᴄʜ-ᴘᴀᴅѕ"));

        ItemStack background = ItemMaker.createItem("minecraft:lime_stained_glass_pane", 1, "", Collections.emptyList(), "");
        ItemStack plate = ItemMaker.createItem("minecraft:oak_pressure_plate", 1, "<yellow>Plate Launchpad", List.of("<yellow>Click to setup!"), "");
        ItemStack closeButton = ItemMaker.createItem("minecraft:barrier", 1, "<red>Close", Collections.emptyList(), "");

        int size = gui.getSize();

        for (int slot = 0; slot < size; slot++) {
            int row = slot / 9;
            int col = slot % 9;

            boolean isTopOrBottom = row == 0 || row == 2;
            boolean isLeftOrRight = col == 0 || col == 8;

            if (isTopOrBottom || isLeftOrRight) {
                gui.setItem(slot, background);
            }
        }

        gui.setItem(10, plate);
        gui.setItem(22, closeButton);

        player.openInventory(gui);
    }
}
