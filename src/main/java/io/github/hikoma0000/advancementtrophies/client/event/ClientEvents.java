package io.github.hikoma0000.advancementtrophies.client.event;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.config.client.input.KeyBindings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdvancementTrophies.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @Mod.EventBusSubscriber(modid = AdvancementTrophies.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyBindings.SHOW_DETAILS_KEY);
        }
    }

    @SubscribeEvent
    public static void onScreenKeyPressed(ScreenEvent.KeyPressed.Post event) {
        if (event.getKeyCode() == KeyBindings.SHOW_DETAILS_KEY.getKey().getValue()) {
            KeyBindings.isDetailsKeyDown = true;
        }
    }

    @SubscribeEvent
    public static void onScreenKeyReleased(ScreenEvent.KeyReleased.Post event) {
        if (event.getKeyCode() == KeyBindings.SHOW_DETAILS_KEY.getKey().getValue()) {
            KeyBindings.isDetailsKeyDown = false;
        }
    }

    @SubscribeEvent
    public static void onScreenClose(ScreenEvent.Closing event) {
        KeyBindings.isDetailsKeyDown = false;
    }
}