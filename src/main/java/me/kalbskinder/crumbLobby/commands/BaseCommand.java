package me.kalbskinder.crumbLobby.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.kalbskinder.crumbLobby.CrumbLobby;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BaseCommand {
    private final CrumbLobby plugin = CrumbLobby.getInstance();
    private final MiniMessage mm = MiniMessage.miniMessage();

    List<String> gameruleList = List.of(
            "dropItems", "pvp", "fallDamage", "fireDamage", "suffocate", "drown", "hunger"
    );


    LiteralArgumentBuilder<CommandSourceStack> baseCommand = Commands.literal("clobby")
            .requires(source -> source.getExecutor().hasPermission("crumblobby.admin"))
            .executes(ctx -> {
                // List of commands
                CommandSender sender =  ctx.getSource().getSender();
                if (sender instanceof Player player) {
                    List.of(
                            "<gold>/clobby <gray>- Shows this message",
                            "<gold>/clobby gamerules <yellow>[gamerule] [true/false] <gray>- Edit gamerules",
                            "<gold>/setspawn <gray>- Set the spawn location",
                            "<gold>/spawn <gray>- Teleport to the spawn location"
                    ).forEach(line -> {
                        player.sendMessage(mm.deserialize(line));
                    });
                }
                return 0;
            })
            .then(Commands.literal("gamerules")
                    .then(Commands.argument("rule", StringArgumentType.word())
                            .suggests((ctx, builder) -> {
                                gameruleList.forEach(builder::suggest);
                                return builder.buildFuture();
                            })
                            .then(Commands.argument("value", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        String rule = StringArgumentType.getString(ctx, "rule");
                                        boolean value = BoolArgumentType.getBool(ctx, "value");
                                        CommandSender sender = ctx.getSource().getSender();

                                        if (!gameruleList.contains(rule)) {
                                            sender.sendMessage(mm.deserialize("<red>Invalid gamerule."));
                                            return 1;
                                        }

                                        // Update config
                                        plugin.getConfig().set("game-rules." + rule, value);
                                        plugin.saveConfig();

                                        sender.sendMessage(mm.deserialize("<green>Updated gamerule '" + rule + "' to " + value));
                                        return 0;
                                    })
                            )
                    )
            );

    LiteralCommandNode<CommandSourceStack> base = baseCommand.build();

    public LiteralCommandNode<CommandSourceStack> getBaseCommand() {
        return base;
    }
}
