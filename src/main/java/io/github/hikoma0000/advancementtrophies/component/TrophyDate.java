package io.github.hikoma0000.advancementtrophies.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TrophyDate(int year, int month, int day, int hour, int minute, int second) {
    public static final Codec<TrophyDate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("year").forGetter(TrophyDate::year),
            Codec.INT.fieldOf("month").forGetter(TrophyDate::month),
            Codec.INT.fieldOf("day").forGetter(TrophyDate::day),
            Codec.INT.fieldOf("hour").forGetter(TrophyDate::hour),
            Codec.INT.fieldOf("minute").forGetter(TrophyDate::minute),
            Codec.INT.fieldOf("second").forGetter(TrophyDate::second)
    ).apply(instance, TrophyDate::new));

    public static final StreamCodec<ByteBuf, TrophyDate> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, TrophyDate::year,
            ByteBufCodecs.INT, TrophyDate::month,
            ByteBufCodecs.INT, TrophyDate::day,
            ByteBufCodecs.INT, TrophyDate::hour,
            ByteBufCodecs.INT, TrophyDate::minute,
            ByteBufCodecs.INT, TrophyDate::second,
            TrophyDate::new
    );
}
