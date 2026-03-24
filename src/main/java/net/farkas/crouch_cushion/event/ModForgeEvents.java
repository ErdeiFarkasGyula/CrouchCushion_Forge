package net.farkas.crouch_cushion.event;

import net.farkas.crouch_cushion.Config;
import net.farkas.crouch_cushion.CrouchCushion;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CrouchCushion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModForgeEvents {
    private static final String CROUCH_START_TIME_TAG = "crouch_cushion_start_time";

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        event.getEntity().getPersistentData().remove(CROUCH_START_TIME_TAG);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!Config.enableCushion || event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;

        Player player = event.player;

        if (player.isCrouching() && player.tickCount > Config.cushionWindowTicks) {
            if (!player.getPersistentData().contains(CROUCH_START_TIME_TAG)) {
                player.getPersistentData().putLong(CROUCH_START_TIME_TAG, player.level().getGameTime());
            }
        } else {
            if (player.getPersistentData().contains(CROUCH_START_TIME_TAG)) {
                player.getPersistentData().remove(CROUCH_START_TIME_TAG);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!Config.enableCushion) return;

        if (event.getEntity() instanceof Player player && !player.level().isClientSide()) {
            if (event.getSource().is(DamageTypes.FALL) && player.isCrouching() && player.fallDistance > 0.1f) {
                if (player.getPersistentData().contains(CROUCH_START_TIME_TAG)) {
                    long startTime = player.getPersistentData().getLong(CROUCH_START_TIME_TAG);
                    long duration = player.level().getGameTime() - startTime;
                    int window = Config.cushionWindowTicks;

                    if (duration >= 0 && duration <= window) {
                        double baseMultiplier = Config.minDamageMultiplier;

                        if (Config.enableFallDistanceScaling) {
                            baseMultiplier += (player.fallDistance * Config.fallDistanceScalingFactor);
                        }

                        baseMultiplier = Math.min(1.0, baseMultiplier);
                        double progress = (double) duration / (double) window;
                        double finalMultiplier = baseMultiplier + (1.0 - baseMultiplier) * progress;

                        finalMultiplier = Math.max(0.0, Math.min(1.0, finalMultiplier));

                        event.setAmount((float) (event.getAmount() * finalMultiplier));

                        player.getPersistentData().remove(CROUCH_START_TIME_TAG);
                    }
                }
            }
        }
    }
}