package io.github.hikoma0000.advancementtrophies.component;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TrophyData(CompoundTag data) {
    public static final Codec<TrophyData> CODEC = CompoundTag.CODEC.xmap(TrophyData::new, TrophyData::data);

    public static final StreamCodec<FriendlyByteBuf, TrophyData> STREAM_CODEC = StreamCodec.of(
            (buf, data) -> buf.writeNbt(data.data()),
            buf -> new TrophyData(buf.readNbt())
    );
}