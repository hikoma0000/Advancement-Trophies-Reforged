package io.github.hikoma0000.advancementtrophies.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record TrophyData(
        String achiever,
        TrophyDate date,
        ResourceLocation advancementId,
        Optional<String> titleKey,
        Optional<String> titleJson,
        String advancementMod,
        ItemStack icon
) {
    public TrophyData {
        icon = icon.copy();
    }

    public static final Codec<TrophyData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("achiever").forGetter(TrophyData::achiever),
            TrophyDate.CODEC.fieldOf("date").forGetter(TrophyData::date),
            ResourceLocation.CODEC.fieldOf("advancement_id").forGetter(TrophyData::advancementId),
            Codec.STRING.optionalFieldOf("title_key").forGetter(TrophyData::titleKey),
            Codec.STRING.optionalFieldOf("title_json").forGetter(TrophyData::titleJson),
            Codec.STRING.fieldOf("advancement_mod").forGetter(TrophyData::advancementMod),
            ItemStack.CODEC.fieldOf("icon").forGetter(TrophyData::icon)
    ).apply(instance, TrophyData::new));

    public static final StreamCodec<? super RegistryFriendlyByteBuf, TrophyData> STREAM_CODEC = StreamCodec.of(
            TrophyData::encode,
            TrophyData::decode
    );

    private static void encode(RegistryFriendlyByteBuf buf, TrophyData value) {
        ByteBufCodecs.STRING_UTF8.encode(buf, value.achiever());
        TrophyDate.STREAM_CODEC.encode(buf, value.date());
        ResourceLocation.STREAM_CODEC.encode(buf, value.advancementId());
        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).encode(buf, value.titleKey());
        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).encode(buf, value.titleJson());
        ByteBufCodecs.STRING_UTF8.encode(buf, value.advancementMod());
        ItemStack.STREAM_CODEC.encode(buf, value.icon());
    }

    private static TrophyData decode(RegistryFriendlyByteBuf buf) {
        return new TrophyData(
                ByteBufCodecs.STRING_UTF8.decode(buf),
                TrophyDate.STREAM_CODEC.decode(buf),
                ResourceLocation.STREAM_CODEC.decode(buf),
                ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).decode(buf),
                ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).decode(buf),
                ByteBufCodecs.STRING_UTF8.decode(buf),
                ItemStack.STREAM_CODEC.decode(buf)
        );
    }

    @Nullable
    public Component resolveTitle() {
        if (titleKey.isPresent()) {
            String key = titleKey.get();
            return Component.translatableWithFallback(key, key);
        }
        if (titleJson.isPresent()) {
            try {
                return Component.Serializer.fromJson(titleJson.get(), RegistryAccess.EMPTY);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public boolean hasAdvancementId() {
        return advancementId != null
                && !advancementId.getNamespace().isEmpty()
                && !advancementId.getPath().isEmpty();
    }

    public boolean hasIcon() {
        return icon != null && !icon.isEmpty();
    }

    public boolean hasAchiever() {
        return achiever != null && !achiever.isEmpty();
    }
}
