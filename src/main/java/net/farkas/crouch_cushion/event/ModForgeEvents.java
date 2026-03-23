package net.farkas.crouch_cushion.event;

import net.farkas.crouch_cushion.Config;
import net.farkas.crouch_cushion.CrouchCushion;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CrouchCushion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModForgeEvents {

    private static final String CROUCH_START_TIME_TAG = "crouch_cushion_start_time";

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide()) {
            Player player = event.player;

            if (player.isCrouching()) {
                if (!player.getPersistentData().contains(CROUCH_START_TIME_TAG)) {
                    player.getPersistentData().putLong(CROUCH_START_TIME_TAG, player.level().getGameTime());
                }
            } else {
                if (player.getPersistentData().contains(CROUCH_START_TIME_TAG)) {
                    player.getPersistentData().remove(CROUCH_START_TIME_TAG);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide()) {
            if (event.getSource().is(DamageTypes.FALL) && player.isCrouching()) {
                if (player.getPersistentData().contains(CROUCH_START_TIME_TAG)) {
                    long startTime = player.getPersistentData().getLong(CROUCH_START_TIME_TAG);
                    long currentTime = player.level().getGameTime();
                    long duration = currentTime - startTime;
                    int window = Config.CUSHION_WINDOW_TICKS.get();

                    if (duration <= window) {
                        double minMultiplier = Config.MIN_DAMAGE_MULTIPLIER.get();

                        double progress = (double) duration / (double) window;

                        double finalMultiplier = minMultiplier + (1.0 - minMultiplier) * progress;

                        if (finalMultiplier < 0.0) finalMultiplier = 0.0;
                        if (finalMultiplier > 1.0) finalMultiplier = 1.0;

                        event.setAmount((float) (event.getAmount() * finalMultiplier));
                    }
                }
            }
        }
    }
}
