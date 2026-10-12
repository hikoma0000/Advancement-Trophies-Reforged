package io.github.hikoma0000.advancementtrophies.event;

import io.github.hikoma0000.advancementtrophies.init.ModItems;
import io.github.hikoma0000.advancementtrophies.util.TrophyUtils;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.GrindstoneEvent;

public class TrophyGrindstoneHandler {

    @SubscribeEvent
    public static void onGrindstonePlace(GrindstoneEvent.OnPlaceItem event) {
        ItemStack top = event.getTopItem();
        ItemStack bottom = event.getBottomItem();
        if (!top.isEmpty() && !bottom.isEmpty()) {
            return;
        }

        ItemStack trophy = top.isEmpty() ? bottom : top;
        if (!ModItems.isTrophy(trophy) || !TrophyUtils.hasTrophyData(trophy)) {
            return;
        }

        event.setOutput(new ItemStack(trophy.getItem()));
        event.setXp(0);
    }
}
