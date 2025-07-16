package me.kalbskinder.crumbLobby.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class TextFormatter {
    public Component formatString(String text, @Nullable OfflinePlayer player, @Nullable Map<String, String> data) {
        if (text == null) {
            return Component.empty();
        }

        String formatted = text;

        if (data != null) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                formatted = formatted.replace("%" + entry.getKey() + "%", entry.getValue());
            }
        }

        return MiniMessage.miniMessage().deserialize(formatted);
    }


    public Component formatString(String text, @Nullable Player player, @Nullable Map<String, String> data) {
        return formatString(text, (OfflinePlayer) player, data);
    }

    public Component formatString(String text, @Nullable OfflinePlayer player) {
        return formatString(text, player, null);
    }

    public Component formatString(String text, @Nullable Player player) {
        return formatString(text, (OfflinePlayer) player, null);
    }

    public Component formatString(String text, @Nullable Map<String, String> data) {
        return formatString(text, null, data);
    }

    public Component formatString(String text) {
        return formatString(text, null, null);
    }
}
