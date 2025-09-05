package io.github.hikoma0000.advancementtrophies.client;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.client.gui.TrophyCrateScreen;
import io.github.hikoma0000.advancementtrophies.client.renderer.TrophyBlockEntityRenderer;
import io.github.hikoma0000.advancementtrophies.init.ModBlockEntities;
import io.github.hikoma0000.advancementtrophies.init.ModBlocks;
import io.github.hikoma0000.advancementtrophies.init.ModContainers;
import io.github.hikoma0000.advancementtrophies.init.ModItems;
import io.github.hikoma0000.advancementtrophies.inventory.TrophyCrateContainer;
import io.github.hikoma0000.advancementtrophies.inventory.TrophyCrateItemContainer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings("removal")
public class ClientSetup {
    public static void init(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.IRON_TROPHY.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.GOLD_TROPHY.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.DIAMOND_TROPHY.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.NETHERITE_TROPHY.get(), RenderType.cutout());


            BlockEntityRenderers.register(ModBlockEntities.TROPHY.get(), TrophyBlockEntityRenderer::new);

            MenuScreens.register(ModContainers.TROPHY_CRATE_CONTAINER.get(), (TrophyCrateContainer container, net.minecraft.world.entity.player.Inventory inv, net.minecraft.network.chat.Component title) -> new TrophyCrateScreen<>(container, inv, title));
            MenuScreens.register(ModContainers.TROPHY_CRATE_ITEM_CONTAINER.get(), (TrophyCrateItemContainer container, net.minecraft.world.entity.player.Inventory inv, net.minecraft.network.chat.Component title) -> new TrophyCrateScreen<>(container, inv, title));

            ItemProperties.register(ModItems.TROPHY_CRATE.get(), new ResourceLocation(AdvancementTrophies.MOD_ID, "open"), (stack, level, entity, seed) -> {
                return stack.hasTag() && stack.getTag().getBoolean("open") ? 1.0f : 0.0f;
            });
        });
    }
}