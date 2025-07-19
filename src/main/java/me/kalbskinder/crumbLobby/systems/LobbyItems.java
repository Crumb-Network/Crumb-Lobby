package me.kalbskinder.crumbLobby.systems;

import me.kalbskinder.crumbLobby.CrumbLobby;
import me.kalbskinder.crumbLobby.utils.ItemActionHandler;
import me.kalbskinder.crumbLobby.utils.ItemMaker;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;

public class LobbyItems implements Listener {
    private static final FileConfiguration config = CrumbLobby.getInstance().getConfig();

    private static final ItemStack teleportBow = ItemMaker.createItem(
            "minecraft:bow",
            1,
            config.getString("items.teleport-bow.name"),
            Collections.emptyList(),
            ""
    );
    private static final ItemStack teleportBowArrow = ItemMaker.createItem("minecraft:arrow", 1, "", Collections.emptyList(), "");

    private static ItemStack pvpSword = ItemMaker.createItem(
            "minecraft:netherite_sword",
            1,
            config.getString("items.pvp-sword.name"),
            Collections.emptyList(),
            ""
    );

    private static final ItemStack serverInfo = ItemMaker.createItem(
            "minecraft:book",
            1,
            config.getString("items.server-info.name"),
            config.getStringList("items.server-info.lore"),
            ""
    );

    private static final String visibilityNameShown = config.getString("items.player-visibility.name-shown");

    private static final boolean teleportBowEnabled = config.getBoolean("items.teleport-bow.enabled");
    private static final boolean pvpSwordEnabled = config.getBoolean("items.pvp-sword.enabled");
    private static final boolean serverInfoEnabled = config.getBoolean("items.server-info.enabled");
    private static final boolean playerVisibilityEnabled = config.getBoolean("items.player-visibility.enabled");

    private static final Integer teleportBowSlot = config.getInt("items.teleport-bow.slot");
    private static final Integer pvpSwordSlot = config.getInt("items.pvp-sword.slot");
    private static final Integer serverInfoSlot = config.getInt("items.server-info.slot");
    private static final Integer playerVisibilitySlot = config.getInt("items.player-visibility.slot");

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent event) {
        loadDefaultLobbyLayout(event.getPlayer());
    }

    public static void loadDefaultLobbyLayout(Player player) {
        Inventory inventory = player.getInventory();

        // Clear armor
        player.getInventory().setHelmet(new ItemStack(Material.AIR));
        player.getInventory().setChestplate(new ItemStack(Material.AIR));
        player.getInventory().setLeggings(new ItemStack(Material.AIR));
        player.getInventory().setBoots(new ItemStack(Material.AIR));

        if (teleportBowEnabled) {
            ItemMeta meta = teleportBow.getItemMeta();

            NamespacedKey key = new NamespacedKey(CrumbLobby.getInstance(), "teleport_bow");
            meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
            teleportBow.setItemMeta(meta);
            inventory.setItem(teleportBowSlot, teleportBow);
            inventory.setItem(9, teleportBowArrow);
        }

        if (pvpSwordEnabled) {
            ItemMeta meta = pvpSword.getItemMeta();
            meta.setUnbreakable(true);
            meta.setEnchantmentGlintOverride(true);
            pvpSword.setItemMeta(meta);
            pvpSword = PVPSword.tagAsPvpSword(pvpSword);
            inventory.setItem(pvpSwordSlot, pvpSword);
        }

        if (serverInfoEnabled) {
            inventory.setItem(serverInfoSlot, serverInfo);
        }

        if (playerVisibilityEnabled) {
            player.getInventory().setItem(playerVisibilitySlot, PlayerVisibility.createVisibilityItem(player, false));

        }
    }

    @EventHandler
    public void onPlayerShootBow(EntityShootBowEvent event) {
        if (! (event.getEntity() instanceof Player player)) return;
        ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
        if (meta== null) return;

        NamespacedKey key = new NamespacedKey(CrumbLobby.getInstance(), "teleport_bow");
        if (!meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) return;

        player.getInventory().setItem(9, teleportBowArrow);

        event.setCancelled(true);

        EnderPearl pearl = player.launchProjectile(EnderPearl.class);
        pearl.setVelocity(player.getLocation().getDirection().multiply(2));
        pearl.setShooter(player);
    }
}
