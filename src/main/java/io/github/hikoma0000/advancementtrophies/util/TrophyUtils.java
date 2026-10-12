package io.github.hikoma0000.advancementtrophies.util;

import io.github.hikoma0000.advancementtrophies.component.TrophyData;
import io.github.hikoma0000.advancementtrophies.component.TrophyDate;
import io.github.hikoma0000.advancementtrophies.init.ModDataComponents;
import io.github.hikoma0000.advancementtrophies.init.ModItems;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

public class TrophyUtils {
    private static final Map<AdvancementType, DeferredHolder<Item, Item>> FRAME_TYPE_TO_TROPHY = Map.of(
            AdvancementType.TASK, ModItems.IRON_TROPHY,
            AdvancementType.GOAL, ModItems.GOLD_TROPHY,
            AdvancementType.CHALLENGE, ModItems.DIAMOND_TROPHY);

    @Nullable
    public static Item getTrophyItem(@Nullable DisplayInfo display) {
        if (display == null || !display.shouldAnnounceChat()) {
            return null;
        }
        DeferredHolder<Item, Item> trophyItem = display.isHidden()
                ? ModItems.NETHERITE_TROPHY
                : FRAME_TYPE_TO_TROPHY.get(display.getType());
        return trophyItem == null ? null : trophyItem.get();
    }

    public static boolean hasTrophyData(ItemStack stack) {
        TrophyData data = stack.get(ModDataComponents.TROPHY_DATA.get());
        return data != null && data.hasAdvancementId();
    }

    public static TrophyData createTrophyData(Player player, AdvancementHolder holder, DisplayInfo display, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        TrophyDate trophyDate = new TrophyDate(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND));

        ResourceLocation advancementId = holder.id();

        Component advancementTitleComponent = display.getTitle();
        Optional<String> titleKey = Optional.empty();
        Optional<String> titleJson = Optional.empty();
        if (advancementTitleComponent.getContents() instanceof TranslatableContents contents) {
            titleKey = Optional.of(contents.getKey());
        } else {
            titleJson = Optional.of(Component.Serializer.toJson(
                    advancementTitleComponent, player.level().registryAccess()));
        }

        return new TrophyData(
                player.getName().getString(),
                trophyDate,
                advancementId,
                titleKey,
                titleJson,
                advancementId.getNamespace(),
                display.getIcon());
    }

    @Nullable
    public static Component getAdvancementTitle(ItemStack stack) {
        return getAdvancementTitle(stack.get(ModDataComponents.TROPHY_DATA.get()));
    }

    @Nullable
    public static Component getAdvancementTitle(@Nullable TrophyData data) {
        return data != null ? data.resolveTitle() : null;
    }

    public static String resolveModDisplayName(String modId) {
        return ModList.get().getModContainerById(modId)
                .map(c -> c.getModInfo().getDisplayName())
                .orElse(modId);
    }
}
