package io.github.hikoma0000.advancementtrophies.client.event;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.client.gui.TrophyCrateScreen;
import io.github.hikoma0000.advancementtrophies.config.client.input.KeyBindings;
import io.github.hikoma0000.advancementtrophies.init.ModContainers;
import io.github.hikoma0000.advancementtrophies.inventory.TrophyCrateContainer;
import io.github.hikoma0000.advancementtrophies.inventory.TrophyCrateItemContainer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = AdvancementTrophies.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @EventBusSubscriber(modid = AdvancementTrophies.MOD_ID, value = Dist.CLIENT)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyBindings.SHOW_DETAILS_KEY);
        }

        @SubscribeEvent
        public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
            event.register(ModContainers.TROPHY_CRATE_CONTAINER.get(), (TrophyCrateContainer menu, net.minecraft.world.entity.player.Inventory inv, net.minecraft.network.chat.Component title) -> new TrophyCrateScreen<>(menu, inv, title));
            event.register(ModContainers.TROPHY_CRATE_ITEM_CONTAINER.get(), (TrophyCrateItemContainer menu, net.minecraft.world.entity.player.Inventory inv, net.minecraft.network.chat.Component title) -> new TrophyCrateScreen<>(menu, inv, title));
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