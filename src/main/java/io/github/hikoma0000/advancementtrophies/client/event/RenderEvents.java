package io.github.hikoma0000.advancementtrophies.client.event;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.compat.carryon.CarryOnRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdvancementTrophies.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RenderEvents {
    private static final boolean isCarryOnLoaded = ModList.get().isLoaded("carryon");

    @SubscribeEvent
    public static void onRenderLevelLast(RenderLevelStageEvent event) {
        if (isCarryOnLoaded && event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            AdvancementTrophies.LOGGER.debug("RenderLevelStageEvent.Post fired");
            CarryOnRenderer.renderInWorld(event.getPoseStack(), event.getPartialTick());
        }
    }
}