package io.github.hikoma0000.advancementtrophies.client.event;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.compat.carryon.CarryOnRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = AdvancementTrophies.MOD_ID, value = Dist.CLIENT)
public class RenderEvents {
    private static final boolean isCarryOnLoaded = ModList.get().isLoaded("carryon");

    @SubscribeEvent
    public static void onRenderLevelLast(RenderLevelStageEvent event) {
        if (isCarryOnLoaded && event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            AdvancementTrophies.LOGGER.debug("RenderLevelStageEvent.Post fired");
            CarryOnRenderer.renderInWorld(event.getPoseStack(), event.getPartialTick().getGameTimeDeltaPartialTick(true));
        }
    }
}