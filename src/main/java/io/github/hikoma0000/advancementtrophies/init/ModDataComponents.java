package io.github.hikoma0000.advancementtrophies.init;

import com.mojang.serialization.Codec;
import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DEFERRED_REGISTER =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, AdvancementTrophies.MOD_ID);

    public static final Supplier<DataComponentType<CompoundTag>> TROPHY_DATA = DEFERRED_REGISTER.register("trophy_data", () ->
            DataComponentType.<CompoundTag>builder()
                    .persistent(CompoundTag.CODEC)
                    .build());

    public static final Supplier<DataComponentType<Boolean>> OPEN = DEFERRED_REGISTER.register("open", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .build());
}