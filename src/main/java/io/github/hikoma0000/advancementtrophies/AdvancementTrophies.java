package io.github.hikoma0000.advancementtrophies;

import com.mojang.logging.LogUtils;
import io.github.hikoma0000.advancementtrophies.client.ClientSetup;
import io.github.hikoma0000.advancementtrophies.client.event.RenderEvents;
import io.github.hikoma0000.advancementtrophies.config.ClientConfig;
import io.github.hikoma0000.advancementtrophies.event.AdvancementEventHandler;
import io.github.hikoma0000.advancementtrophies.event.PlayerEventHandler;
import io.github.hikoma0000.advancementtrophies.init.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@SuppressWarnings("removal")
@Mod(AdvancementTrophies.MOD_ID)
public class AdvancementTrophies {
    public static final String MOD_ID = "advancementtrophies";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AdvancementTrophies() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModContainers.CONTAINERS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);


        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC, "advancementtrophies-client.toml");

        modEventBus.addListener(this::init);
        modEventBus.addListener(ClientSetup::init);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(AdvancementEventHandler.class);
        MinecraftForge.EVENT_BUS.register(PlayerEventHandler.class);
        MinecraftForge.EVENT_BUS.register(RenderEvents.class);
    }

    private void init(final FMLCommonSetupEvent event) {
    }
}