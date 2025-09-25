package io.github.hikoma0000.advancementtrophies.client.event;

import io.github.hikoma0000.advancementtrophies.config.client.input.KeyBindings;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

public class ClientEvents {

    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.SHOW_DETAILS_KEY);
    }

    public static class ForgeBusEvents {
        @SubscribeEvent
        public void onScreenKeyPressed(ScreenEvent.KeyPressed.Post event) {
            if (event.getKeyCode() == KeyBindings.SHOW_DETAILS_KEY.getKey().getValue()) {
                KeyBindings.isDetailsKeyDown = true;
            }
        }

        @SubscribeEvent
        public void onScreenKeyReleased(ScreenEvent.KeyReleased.Post event) {
            if (event.getKeyCode() == KeyBindings.SHOW_DETAILS_KEY.getKey().getValue()) {
                KeyBindings.isDetailsKeyDown = false;
            }
        }

        @SubscribeEvent
        public void onScreenClose(ScreenEvent.Closing event) {
            KeyBindings.isDetailsKeyDown = false;
        }
    }
}