package me.kalbskinder.crumbLobby.guis;

import me.kalbskinder.crumbLobby.CrumbLobby;
import me.kalbskinder.crumbLobby.database.Database;
import me.kalbskinder.crumbLobby.database.Query;
import me.kalbskinder.crumbLobby.utils.ItemMaker;
import me.kalbskinder.crumbLobby.utils.PressurePlates;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlateTypeMenu {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final CrumbLobby plugin = CrumbLobby.getInstance();

    public static void openMenu(Player player, int lpId) {
        if (!player.hasPermission("crumblobby.admin")) return;
        Inventory gui = Bukkit.createInventory(null, 9 * 5, miniMessage.deserialize("<bold><gradient:#369e36:#2bbf11>Change Type<reset>"));
        List<String> emptyLore = new ArrayList<>();

        ItemStack background = ItemMaker.createItem("minecraft:lime_stained_glass_pane", 1, "", emptyLore, "");
        ItemStack closeButton = ItemMaker.createItem("minecraft:barrier", 1, "<red>Close", emptyLore, "");

        // Make secret info item
        ItemStack secretItem = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
        ItemMeta secretMeta = secretItem.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(String.valueOf(lpId)));
        secretMeta.lore(lore);
        secretMeta.setHideTooltip(true);

        secretItem.setItemMeta(secretMeta);

        ItemStack currentType = null;

        try {
            Database database = new Database(CrumbLobby.getInstance().getDataFolder().getAbsolutePath() + "/lobbyDatabase.db");
            Query query = new Query(database.getConnection());

            String materialName = query.getLaunchpadType(lpId);
            Material currentMaterial = Material.matchMaterial(materialName);

            if (currentMaterial != null) {
                currentType = new ItemStack(currentMaterial);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        int slotCount = 10;
        for (String plate : PressurePlates.get()) {
            while (isBorderSlot(slotCount)) {
                slotCount++;
                if (slotCount >= gui.getSize()) return;
            }

            ItemStack plateItem = ItemMaker.createItem(
                    plate,
                    1,
                    "<green>" + PressurePlates.formatPlateName(plate),
                    List.of("<yellow>Click to select!"),
                    ""
            );

            if (currentType != null && plateItem.getType().equals(currentType.getType())) {
                ItemMeta meta = plateItem.getItemMeta();
                meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                plateItem.setItemMeta(meta);

                gui.setItem(slotCount, plateItem);
                slotCount++;
                continue;
            }

            gui.setItem(slotCount, plateItem);
            slotCount++;
        }

        int size = gui.getSize();

        for (int slot = 0; slot < size; slot++) {
            int row = slot / 9;
            int col = slot % 9;

            boolean isTopOrBottom = row == 0 || row == 4;
            boolean isLeftOrRight = col == 0 || col == 8;

            if (isTopOrBottom || isLeftOrRight) {
                gui.setItem(slot, background);
            }
        }

        gui.setItem(0, secretItem);
        gui.setItem(40, closeButton);


        player.openInventory(gui);
    }

    private static boolean isBorderSlot(int slot) {
        int row = slot / 9;
        int col = slot % 9;
        return row == 0 || row == 5 || col == 0 || col == 8;
    }
}
