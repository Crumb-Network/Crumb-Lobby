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

public class SpawnCommand {
    private final CrumbLobby plugin = CrumbLobby.getInstance();
    private final MiniMessage mm = MiniMessage.miniMessage();

    LiteralArgumentBuilder<CommandSourceStack> spawnCommand = Commands.literal("spawn")
            .executes(ctx -> {
                CommandSender sender =  ctx.getSource().getSender();
                if (sender instanceof Player player) {
                    try {
                        Database database = new Database(plugin.getDataFolder().getAbsolutePath() + "/lobbyDatabase.db");
                        Query query = new Query(database.getConnection());
                        String locationString = query.getSpawn();
                        if (locationString == null) {
                            player.sendMessage(mm.deserialize("<red>Unable to teleport you to spawn. Spawn is not set!"));
                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.7f, 1f);
                            return 0;
                        }
                        Location location = LocationHelper.stringToLocation(locationString);
                        player.teleport(location);
                    } catch (SQLException ex) {
                        player.sendMessage(mm.deserialize("<red>An error occured!"));
                        ex.printStackTrace();
                    }
                    return 1;
                }
                return 0;
            });

    LiteralCommandNode<CommandSourceStack> spawn = spawnCommand.build();

    public LiteralCommandNode<CommandSourceStack> getSpawnCommand() {
        return spawn;
    }
}
