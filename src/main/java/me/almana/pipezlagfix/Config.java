package me.almana.pipezlagfix;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Pipezlagfix.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue BASE_BACKOFF_TICKS;
    public static final ModConfigSpec.IntValue MAX_BACKOFF_TICKS;
    public static final ModConfigSpec.BooleanValue ENABLED;
    static final ModConfigSpec SPEC;

    public static int baseBackoffTicks;
    public static int maxBackoffTicks;
    public static boolean enabled;

    static {
        BUILDER.push("general");
        ENABLED = BUILDER.comment("Whether the mod is enabled.")
                .define("enabled", true);
        BASE_BACKOFF_TICKS = BUILDER
                .comment("The initial backoff delay in ticks when an item pipe fails to insert items.")
                .defineInRange("baseBackoffTicks", 2, 1, 1200);
        MAX_BACKOFF_TICKS = BUILDER.comment("The maximum backoff delay in ticks for item pipes.")
                .defineInRange("maxBackoffTicks", 32, 1, 1200);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        bake();
    }

    public static void bake() {
        baseBackoffTicks = BASE_BACKOFF_TICKS.get();
        maxBackoffTicks = MAX_BACKOFF_TICKS.get();
        enabled = ENABLED.get();
    }
}
