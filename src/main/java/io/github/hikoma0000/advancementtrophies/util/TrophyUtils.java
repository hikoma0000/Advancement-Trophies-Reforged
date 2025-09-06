package io.github.hikoma0000.advancementtrophies.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class TrophyUtils {
    @Nullable
    public static Component getAdvancementTitleFromNBT(CompoundTag nbt) {
        if (nbt.contains(NBTKeys.ADVANCEMENT_TITLE, Tag.TAG_STRING)) {
            return Component.translatableWithFallback(nbt.getString(NBTKeys.ADVANCEMENT_TITLE), nbt.getString(NBTKeys.ADVANCEMENT_TITLE));
        }
        if (nbt.contains(NBTKeys.ADVANCEMENT_TITLE_JSON, Tag.TAG_STRING)) {
            try {
                return Component.Serializer.fromJson(nbt.getString(NBTKeys.ADVANCEMENT_TITLE_JSON));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}