package me.kalbskinder.crumbLobby.systems;

import me.kalbskinder.crumbLobby.CrumbLobby;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class PVPSword {
    private static final Map<UUID, BukkitTask> startCountdowns = new HashMap<>();
    private static final Map<UUID, BukkitTask> endCountdowns = new HashMap<>();
    private static final Set<UUID> inPvp = new HashSet<>();
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final FileConfiguration config = CrumbLobby.getInstance().getConfig();
    private static final String pvpPrefix = config.getString("items.pvp-sword.messages.prefix", "<red>null");
    private static final String enablingMessage = config.getString("items.pvp-sword.messages.enabling", "<red>null");
    private static final String enabledMessage = config.getString("items.pvp-sword.messages.enabled", "<red>null");
    private static final String disablingMessage = config.getString("items.pvp-sword.messages.disabling", "<red>null");
    private static final String disabledMessage = config.getString("items.pvp-sword.messages.disabled", "<red>null");

    private static final NamespacedKey PVP_SWORD_KEY = new NamespacedKey(CrumbLobby.getInstance(), "pvp_sword");

    public static void startMonitoring() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    ItemStack held = player.getInventory().getItemInMainHand();

                    boolean holdingSword = isPvpSword(held);
                    boolean isInPvp = inPvp.contains(uuid);

                    if (holdingSword && !isInPvp && !startCountdowns.containsKey(uuid)) {
                        startPvPCountdown(player);
                    }

                    if (!holdingSword && isInPvp && !endCountdowns.containsKey(uuid)) {
                        startExitCountdown(player);
                    }

                    if (!holdingSword && startCountdowns.containsKey(uuid)) {
                        cancelStartCountdown(player);
                    }

                    if (holdingSword && endCountdowns.containsKey(uuid)) {
                        cancelEndCountdown(player);
                    }
                }
            }
        }.runTaskTimer(CrumbLobby.getInstance(), 0L, 10L);
    }

    public static boolean isPvpSword(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(PVP_SWORD_KEY, PersistentDataType.BYTE);
    }

    public static ItemStack tagAsPvpSword(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(PVP_SWORD_KEY, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    private static void startPvPCountdown(Player player) {
        UUID uuid = player.getUniqueId();
        BukkitTask task = new BukkitRunnable() {
            int seconds = 5;

            @Override
            public void run() {
                if (seconds == 0) {
                    inPvp.add(uuid);
                    equipArmor(player);
                    startCountdowns.remove(uuid);
                    player.sendMessage(mm.deserialize(enabledMessage));
                    cancel();
                    return;
                }
                player.sendMessage(mm.deserialize(pvpPrefix + enablingMessage.replace("%time%", String.valueOf(seconds))));
                seconds--;
            }
        }.runTaskTimer(CrumbLobby.getInstance(), 0L, 20L);

        startCountdowns.put(uuid, task);
    }

    private static void startExitCountdown(Player player) {
        UUID uuid = player.getUniqueId();
        BukkitTask task = new BukkitRunnable() {
            int seconds = 5;

            @Override
            public void run() {
                if (seconds == 0) {
                    inPvp.remove(uuid);
                    unequipArmor(player);
                    player.sendMessage(mm.deserialize(pvpPrefix + disabledMessage));
                    endCountdowns.remove(uuid);
                    cancel();
                    return;
                }
                player.sendMessage(mm.deserialize(pvpPrefix + disablingMessage.replace("%time%", String.valueOf(seconds))));
                seconds--;
            }
        }.runTaskTimer(CrumbLobby.getInstance(), 0L, 20L);

        endCountdowns.put(uuid, task);
    }

    private static void cancelStartCountdown(Player player) {
        UUID uuid = player.getUniqueId();
        BukkitTask task = startCountdowns.remove(uuid);
        if (task != null) task.cancel();
    }

    private static void cancelEndCountdown(Player player) {
        UUID uuid = player.getUniqueId();
        BukkitTask task = endCountdowns.remove(uuid);
        if (task != null) task.cancel();
    }

    private static void equipArmor(Player player) {
        player.getInventory().setHelmet(createArmorPiece(Material.NETHERITE_HELMET));
        player.getInventory().setChestplate(createArmorPiece(Material.NETHERITE_CHESTPLATE));
        player.getInventory().setLeggings(createArmorPiece(Material.NETHERITE_LEGGINGS));
        player.getInventory().setBoots(createArmorPiece(Material.NETHERITE_BOOTS));
    }

    private static void unequipArmor(Player player) {
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }

    private static ItemStack createArmorPiece(Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.PROTECTION, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isInPvp(Player player) {
        return inPvp.contains(player.getUniqueId());
    }

    public static Set<UUID> getPVPList() {
        return inPvp;
    }
}
