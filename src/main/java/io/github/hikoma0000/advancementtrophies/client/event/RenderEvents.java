package io.github.hikoma0000.advancementtrophies.client.event;

import io.github.hikoma0000.advancementtrophies.compat.carryon.CarryOnRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public class RenderEvents {
    private static final boolean isCarryOnLoaded = ModList.get().isLoaded("carryon");

    @SubscribeEvent
    public void onRenderLevelLast(RenderLevelStageEvent event) {
        if (isCarryOnLoaded && event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            CarryOnRenderer.renderInWorld(event.getPoseStack(), event.getPartialTick().getGameTimeDeltaPartialTick(true));
        }
    }
}