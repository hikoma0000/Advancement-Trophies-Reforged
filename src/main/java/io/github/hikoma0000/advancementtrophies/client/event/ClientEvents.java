package io.github.hikoma0000.advancementtrophies.client.event;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.client.util.AdvancementJumpHelper;
import io.github.hikoma0000.advancementtrophies.config.client.input.KeyBindings;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = AdvancementTrophies.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onScreenKeyPressedPre(ScreenEvent.KeyPressed.Pre event) {
        if (AdvancementJumpHelper.tryJumpFromHoveredTrophy(event.getScreen(), event.getKeyCode(),
                event.getScanCode())) {
            event.setCanceled(true);
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
