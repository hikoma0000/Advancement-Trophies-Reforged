package io.github.hikoma0000.advancementtrophies.util;

import io.github.hikoma0000.advancementtrophies.component.TrophyData;
import io.github.hikoma0000.advancementtrophies.init.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

public class TrophyUtils {
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
