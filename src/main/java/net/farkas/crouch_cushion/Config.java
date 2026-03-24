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
    public static final ForgeConfigSpec.BooleanValue ENABLE_CUSHION;
    public static final ForgeConfigSpec.BooleanValue ENABLE_FALL_DISTANCE_SCALING;
    public static final ForgeConfigSpec.DoubleValue FALL_DISTANCE_SCALING_FACTOR;

    static {
        BUILDER.push("General");

        ENABLE_CUSHION = BUILDER
                .comment("Whether the Crouch Cushion mechanic is enabled.")
                .define("enable_cushion", true);

        MIN_DAMAGE_MULTIPLIER = BUILDER
                .comment("The minimum multiplier applied to fall damage when timed perfectly (0.0 = 0% damage, 1.0 = 100% damage)")
                .defineInRange("min_damage_multiplier", 0.35, 0.0, 1.0);

        CUSHION_WINDOW_TICKS = BUILDER
                .comment("The window of time (in ticks) the player must have been crouching for the cushion to take effect. " +
                        "If the player crouches longer than this, the effect fades.")
                .defineInRange("cushion_window_ticks", 10, 1, 100);

        BUILDER.pop();
        BUILDER.push("Scaling");

        ENABLE_FALL_DISTANCE_SCALING = BUILDER
                .comment("Whether fall damage reduction scales with fall distance (higher falls = less reduction).")
                .define("enable_fall_distance_scaling", true);

        FALL_DISTANCE_SCALING_FACTOR = BUILDER
                .comment("How much the damage multiplier increases per blocks fallen. " +
                        "Example: 0.02 means for every block fallen, the damage multiplier increases by 0.02. " +
                        "Higher values mean the cushion becomes ineffective faster at greater heights.")
                .defineInRange("fall_distance_scaling_factor", 0.02, 0.0, 1.0);

        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static double minDamageMultiplier;
    public static int cushionWindowTicks;
    public static boolean enableCushion;
    public static boolean enableFallDistanceScaling;
    public static double fallDistanceScalingFactor;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        minDamageMultiplier = MIN_DAMAGE_MULTIPLIER.get();
        cushionWindowTicks = CUSHION_WINDOW_TICKS.get();
        enableCushion = ENABLE_CUSHION.get();
        enableFallDistanceScaling = ENABLE_FALL_DISTANCE_SCALING.get();
        fallDistanceScalingFactor = FALL_DISTANCE_SCALING_FACTOR.get();
    }
}
