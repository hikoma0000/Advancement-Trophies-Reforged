package io.github.hikoma0000.advancementtrophies;

import com.mojang.logging.LogUtils;
import io.github.hikoma0000.advancementtrophies.client.ClientSetup;
import io.github.hikoma0000.advancementtrophies.config.ClientConfig;
import io.github.hikoma0000.advancementtrophies.init.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(AdvancementTrophies.MOD_ID)
public class AdvancementTrophies {
    public static final String MOD_ID = "advancementtrophies";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AdvancementTrophies(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModContainers.CONTAINERS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC, "advancementtrophies-client.toml");

        modEventBus.addListener(this::init);
        modEventBus.addListener(ClientSetup::init);
    }

    private void init(final FMLCommonSetupEvent event) {
    }
}