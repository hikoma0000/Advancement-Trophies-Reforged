package io.github.hikoma0000.advancementtrophies.init;

import com.mojang.serialization.Codec;
import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.component.TrophyData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, AdvancementTrophies.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TrophyData>> TROPHY_DATA =
            DATA_COMPONENTS.register("trophy_data", () ->
                    DataComponentType.<TrophyData>builder()
                            .persistent(TrophyData.CODEC)
                            .networkSynchronized(TrophyData.STREAM_CODEC)
                            .build());
}