package me.almana.pipezlagfix;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.literal;

public class Commands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("pipezfix")
                .then(literal("reload")
                        .requires(net.minecraft.commands.Commands.hasPermission(net.minecraft.commands.Commands.LEVEL_GAMEMASTERS))
                        .executes(context -> {
                            Config.bake();
                            context.getSource().sendSuccess(() -> Component.literal("PipezLagFix config reloaded"),
                                    true);
                            return 1;
                        })));
    }
}
