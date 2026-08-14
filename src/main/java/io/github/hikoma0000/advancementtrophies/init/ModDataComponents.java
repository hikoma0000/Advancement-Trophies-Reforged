package io.github.hikoma0000.advancementtrophies.init;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.component.TrophyData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, AdvancementTrophies.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TrophyData>> TROPHY_DATA =
            DATA_COMPONENTS.registerComponentType(
                    "trophy_data",
                    builder -> builder
                            .persistent(TrophyData.CODEC)
                            .networkSynchronized(TrophyData.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> CRATE_OPEN =
            DATA_COMPONENTS.registerComponentType(
                    "crate_open",
                    builder -> builder
                            .persistent(Unit.CODEC)
                            .networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));

    private ModDataComponents() {}
}
