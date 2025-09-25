package io.github.hikoma0000.advancementtrophies;

import com.mojang.logging.LogUtils;
import io.github.hikoma0000.advancementtrophies.client.ClientSetup;
import io.github.hikoma0000.advancementtrophies.client.event.ClientEvents;
import io.github.hikoma0000.advancementtrophies.client.event.RenderEvents;
import io.github.hikoma0000.advancementtrophies.config.ClientConfig;
import io.github.hikoma0000.advancementtrophies.event.AdvancementEventHandler;
import io.github.hikoma0000.advancementtrophies.init.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(AdvancementTrophies.MOD_ID)
public class AdvancementTrophies {
    public static final String MOD_ID = "advancementtrophies";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AdvancementTrophies(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModContainers.CONTAINERS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModDataComponents.DEFERRED_REGISTER.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC, "advancementtrophies-client.toml");

        modEventBus.addListener(this::init);

        NeoForge.EVENT_BUS.register(new AdvancementEventHandler());

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(ClientSetup::init);
            modEventBus.addListener(ClientSetup::registerRenderers);
            modEventBus.addListener(ClientSetup::onRegisterMenuScreens);
            modEventBus.addListener(ClientEvents::onKeyRegister);
            NeoForge.EVENT_BUS.register(new ClientEvents.ForgeBusEvents());
            NeoForge.EVENT_BUS.register(new RenderEvents());
        }
    }

    private void init(final FMLCommonSetupEvent event) {
    }
}