package io.github.hikoma0000.advancementtrophies;

import com.mojang.logging.LogUtils;
import io.github.hikoma0000.advancementtrophies.client.ClientSetup;
import io.github.hikoma0000.advancementtrophies.config.ClientConfig;
import io.github.hikoma0000.advancementtrophies.event.AdvancementEventHandler;
import io.github.hikoma0000.advancementtrophies.event.PlayerEventHandler;
import io.github.hikoma0000.advancementtrophies.event.TrophyBrushHandler;
import io.github.hikoma0000.advancementtrophies.init.*;
import io.github.hikoma0000.advancementtrophies.network.ModNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
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
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC,
                "advancementtrophies-client.toml");

        modEventBus.addListener(ClientSetup::onClientSetup);
        modEventBus.addListener(ClientSetup::onRegisterRenderers);
        modEventBus.addListener(ClientSetup::onRegisterMenus);
        modEventBus.addListener(ClientSetup::onRegisterKeys);
        modEventBus.addListener(ModNetwork::register);

        NeoForge.EVENT_BUS.register(AdvancementEventHandler.class);
        NeoForge.EVENT_BUS.register(PlayerEventHandler.class);
        NeoForge.EVENT_BUS.register(TrophyBrushHandler.class);
    }
}
