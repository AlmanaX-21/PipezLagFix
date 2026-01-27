package me.almana.pipezlagfix;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.literal;

public class Commands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("pipezfix")
                .then(literal("reload")
                        .requires(s -> s.hasPermission(2))
                        .executes(context -> {
                            Config.bake();
                            context.getSource().sendSuccess(() -> Component.literal("PipezLagFix config reloaded"),
                                    true);
                            return 1;
                        })));
    }
}
