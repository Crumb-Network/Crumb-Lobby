package me.kalbskinder.crumbLobby.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.kalbskinder.crumbLobby.CrumbLobby;
import me.kalbskinder.crumbLobby.database.Database;
import me.kalbskinder.crumbLobby.database.Query;
import me.kalbskinder.crumbLobby.utils.LocationHelper;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class SetSpawnCommand {
    private final CrumbLobby plugin = CrumbLobby.getInstance();

    LiteralArgumentBuilder<CommandSourceStack> setSpawnCommand = Commands.literal("setspawn")
            .requires(source -> source.getExecutor().hasPermission("crumblobby.setspawn"))
            .executes(ctx -> {
                CommandSender sender =  ctx.getSource().getSender();
                if (sender instanceof Player player) {
                    Location loc = player.getLocation();
                    String locString = LocationHelper.locationToString(loc);

                    try {
                        Database database = new Database(plugin.getDataFolder().getAbsolutePath() + "/lobbyDatabase.db");
                        Query query = new Query(database.getConnection());
                        query.setSpawn(locString);
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Updated spawn location!"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.1f, 2.0f);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    return 1;
                }
                return 0;
            });

    LiteralCommandNode<CommandSourceStack> spawn = setSpawnCommand.build();

    public LiteralCommandNode<CommandSourceStack> getSetSpawnCommand() {
        return spawn;
    }
}
