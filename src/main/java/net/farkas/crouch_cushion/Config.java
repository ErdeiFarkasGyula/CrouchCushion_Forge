package net.farkas.crouch_cushion;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = CrouchCushion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.DoubleValue MIN_DAMAGE_MULTIPLIER;
    public static final ForgeConfigSpec.IntValue CUSHION_WINDOW_TICKS;

    static {
        MIN_DAMAGE_MULTIPLIER = BUILDER
                .comment("The minimum multiplier applied to fall damage when timed perfectly (0.0 = 0% damage, 1.0 = 100% damage)")
                .defineInRange("min_damage_multiplier", 0.35, 0.0, 1.0);

        CUSHION_WINDOW_TICKS = BUILDER
                .comment("The window of time (in ticks) the player must have been crouching for the cushion to take effect. " +
                        "If the player crouches longer than this, the effect fades.")
                .defineInRange("cushion_window_ticks", 20, 1, 100);
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static double minDamageMultiplier;
    public static int cushionWindowTicks;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        minDamageMultiplier = MIN_DAMAGE_MULTIPLIER.get();
        cushionWindowTicks = CUSHION_WINDOW_TICKS.get();
    }
}
