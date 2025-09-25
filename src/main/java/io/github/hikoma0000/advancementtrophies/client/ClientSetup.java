package io.github.hikoma0000.advancementtrophies.client;

import io.github.hikoma0000.advancementtrophies.client.gui.TrophyCrateScreen;
import io.github.hikoma0000.advancementtrophies.client.renderer.TrophyBlockEntityRenderer;
import io.github.hikoma0000.advancementtrophies.init.*;
import io.github.hikoma0000.advancementtrophies.inventory.TrophyCrateContainer;
import io.github.hikoma0000.advancementtrophies.inventory.TrophyCrateItemContainer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ClientSetup {
    public static void init(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.IRON_TROPHY.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.GOLD_TROPHY.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.DIAMOND_TROPHY.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.NETHERITE_TROPHY.get(), RenderType.cutout());

            ItemProperties.register(ModItems.TROPHY_CRATE.get(), ResourceLocation.fromNamespaceAndPath("advancementtrophies", "open"), (stack, level, entity, seed) -> {
                return stack.getOrDefault(ModDataComponents.OPEN.get(), false) ? 1.0f : 0.0f;
            });
        });
    }

    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.TROPHY.get(), TrophyBlockEntityRenderer::new);
    }

    public static void onRegisterMenuScreens(final RegisterMenuScreensEvent event) {
        event.register(ModContainers.TROPHY_CRATE_CONTAINER.get(), (TrophyCrateContainer container, Inventory inv, Component title) -> new TrophyCrateScreen<>(container, inv, title));
        event.register(ModContainers.TROPHY_CRATE_ITEM_CONTAINER.get(), (TrophyCrateItemContainer container, Inventory inv, Component title) -> new TrophyCrateScreen<>(container, inv, title));
    }
}