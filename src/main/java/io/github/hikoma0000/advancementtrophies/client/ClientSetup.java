package io.github.hikoma0000.advancementtrophies.client;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.client.renderer.TrophyBlockEntityRenderer;
import io.github.hikoma0000.advancementtrophies.init.ModBlockEntities;
import io.github.hikoma0000.advancementtrophies.init.ModBlocks;
import io.github.hikoma0000.advancementtrophies.init.ModItems;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {
    public static void init(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.IRON_TROPHY.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.GOLD_TROPHY.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.DIAMOND_TROPHY.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.NETHERITE_TROPHY.get(), RenderType.cutout());


            BlockEntityRenderers.register(ModBlockEntities.TROPHY.get(), TrophyBlockEntityRenderer::new);

            ItemProperties.register(ModItems.TROPHY_CRATE.get(),
                    ResourceLocation.fromNamespaceAndPath(AdvancementTrophies.MOD_ID, "open"), (stack, level, entity, seed) -> {
                        CustomData customdata = stack.get(DataComponents.CUSTOM_DATA);
                        if (customdata != null && customdata.copyTag().getBoolean("open")) {
                            return 1.0f;
                        }
                        return 0.0f;
                    });
        });
    }
}