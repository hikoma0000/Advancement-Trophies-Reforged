package io.github.hikoma0000.advancementtrophies.client;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.client.gui.TrophyCrateScreen;
import io.github.hikoma0000.advancementtrophies.client.renderer.TrophyBlockEntityRenderer;
import io.github.hikoma0000.advancementtrophies.config.client.input.KeyBindings;
import io.github.hikoma0000.advancementtrophies.init.ModBlockEntities;
import io.github.hikoma0000.advancementtrophies.init.ModContainers;
import io.github.hikoma0000.advancementtrophies.init.ModDataComponents;
import io.github.hikoma0000.advancementtrophies.init.ModItems;
import io.github.hikoma0000.advancementtrophies.inventory.TrophyCrateContainer;
import io.github.hikoma0000.advancementtrophies.inventory.TrophyCrateItemContainer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ClientSetup {
    public static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ModItems.TROPHY_CRATE.get(),
                    ResourceLocation.fromNamespaceAndPath(AdvancementTrophies.MOD_ID, "open"),
                    (stack, level, entity, seed) -> stack.has(ModDataComponents.CRATE_OPEN.get()) ? 1.0f : 0.0f);
        });
    }

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.TROPHY.get(), TrophyBlockEntityRenderer::new);
    }

    public static void onRegisterMenus(RegisterMenuScreensEvent event) {
        event.register(ModContainers.TROPHY_CRATE_CONTAINER.get(),
                (TrophyCrateContainer container, Inventory inv, Component title) -> new TrophyCrateScreen<>(container, inv, title));
        event.register(ModContainers.TROPHY_CRATE_ITEM_CONTAINER.get(),
                (TrophyCrateItemContainer container, Inventory inv, Component title) -> new TrophyCrateScreen<>(container, inv, title));
    }

    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.SHOW_DETAILS_KEY);
    }
}
