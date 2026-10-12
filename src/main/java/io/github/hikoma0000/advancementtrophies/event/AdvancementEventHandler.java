package io.github.hikoma0000.advancementtrophies.event;

import io.github.hikoma0000.advancementtrophies.init.ModDataComponents;
import io.github.hikoma0000.advancementtrophies.item.TrophyCrateItem;
import io.github.hikoma0000.advancementtrophies.util.TrophyUtils;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdvancementEventHandler {

    @SubscribeEvent
    public static void onAdvancementGranted(AdvancementEvent.AdvancementEarnEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }

        AdvancementHolder holder = event.getAdvancement();
        DisplayInfo display = holder.value().display().orElse(null);
        Item trophyItem = TrophyUtils.getTrophyItem(display);
        if (trophyItem == null) {
            return;
        }

        ItemStack trophyStack = new ItemStack(trophyItem);
        trophyStack.set(ModDataComponents.TROPHY_DATA.get(),
                TrophyUtils.createTrophyData(player, holder, display, new Date()));

        Inventory inventory = player.getInventory();
        List<ItemStack> inventoryBefore = new ArrayList<>(inventory.getContainerSize());
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            inventoryBefore.add(inventory.getItem(i).copy());
        }

        ItemStack trophyRemainder = trophyStack.copy();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack inventoryStack = inventory.getItem(i);
            if (inventoryStack.getItem() instanceof TrophyCrateItem) {
                trophyRemainder = TrophyCrateItem.addItemToCrate(inventoryStack, trophyRemainder);
                if (trophyRemainder.isEmpty()) {
                    break;
                }
            }
        }

        if (!trophyRemainder.isEmpty()) {
            if (!inventory.add(trophyRemainder)) {
                player.drop(trophyRemainder, false);
                player.sendSystemMessage(
                        Component.translatableWithFallback("message.advancementtrophies.inventory_full",
                                "§cYour inventory is full! The trophy has been dropped nearby."));
            }
        }

        if (player instanceof ServerPlayer serverPlayer) {
            syncChangedSlots(serverPlayer, inventoryBefore);
        }
    }

    private static void syncChangedSlots(ServerPlayer player, List<ItemStack> inventoryBefore) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventoryBefore.size(); i++) {
            ItemStack current = inventory.getItem(i);
            if (!ItemStack.matches(inventoryBefore.get(i), current)) {
                player.connection.send(new ClientboundContainerSetSlotPacket(
                        ClientboundContainerSetSlotPacket.PLAYER_INVENTORY, 0, i, current.copy()));
            }
        }
    }
}
