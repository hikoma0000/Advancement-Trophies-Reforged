package io.github.hikoma0000.advancementtrophies.util;

import io.github.hikoma0000.advancementtrophies.init.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class TrophyUtils {
    private static final Map<FrameType, RegistryObject<Item>> FRAME_TYPE_TO_TROPHY = Map.of(
            FrameType.TASK, ModItems.IRON_TROPHY,
            FrameType.GOAL, ModItems.GOLD_TROPHY,
            FrameType.CHALLENGE, ModItems.DIAMOND_TROPHY);

    @Nullable
    public static Item getTrophyItem(@Nullable DisplayInfo display) {
        if (display == null || !display.shouldAnnounceChat()) {
            return null;
        }
        RegistryObject<Item> trophyItem = display.isHidden()
                ? ModItems.NETHERITE_TROPHY
                : FRAME_TYPE_TO_TROPHY.get(display.getFrame());
        return trophyItem == null ? null : trophyItem.get();
    }

    public static boolean hasTrophyData(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt != null && nbt.contains(NBTKeys.ADVANCEMENT_ID, Tag.TAG_STRING);
    }

    public static void writeTrophyData(CompoundTag nbt, Player player, Advancement advancement, Date date) {
        DisplayInfo display = advancement.getDisplay();

        nbt.putString(NBTKeys.ACHIEVER, player.getName().getString());

        CompoundTag dateTag = new CompoundTag();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        dateTag.putInt(NBTKeys.YEAR, cal.get(Calendar.YEAR));
        dateTag.putInt(NBTKeys.MONTH, cal.get(Calendar.MONTH) + 1);
        dateTag.putInt(NBTKeys.DAY, cal.get(Calendar.DAY_OF_MONTH));
        dateTag.putInt(NBTKeys.HOUR, cal.get(Calendar.HOUR_OF_DAY));
        dateTag.putInt(NBTKeys.MINUTE, cal.get(Calendar.MINUTE));
        dateTag.putInt(NBTKeys.SECOND, cal.get(Calendar.SECOND));
        nbt.put(NBTKeys.DATE, dateTag);

        nbt.putString(NBTKeys.ADVANCEMENT_ID, advancement.getId().toString());

        Component advancementTitleComponent = display.getTitle();
        if (advancementTitleComponent.getContents() instanceof TranslatableContents contents) {
            nbt.putString(NBTKeys.ADVANCEMENT_TITLE, contents.getKey());
        } else {
            nbt.putString(NBTKeys.ADVANCEMENT_TITLE_JSON, Component.Serializer.toJson(advancementTitleComponent));
        }

        nbt.putString(NBTKeys.ADVANCEMENT_MOD, advancement.getId().getNamespace());

        CompoundTag iconTag = new CompoundTag();
        display.getIcon().save(iconTag);
        nbt.put(NBTKeys.ICON, iconTag);
    }

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

    public static String resolveModDisplayName(String modId) {
        return ModList.get().getModContainerById(modId)
                .map(container -> container.getModInfo().getDisplayName())
                .orElse(modId);
    }
}
