package io.github.hikoma0000.advancementtrophies.event;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.item.TrophyCrateItem;
import io.github.hikoma0000.advancementtrophies.util.TrophyUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Mod.EventBusSubscriber(modid = AdvancementTrophies.MOD_ID)
public class AdvancementEventHandler {

    @SubscribeEvent
    public static void onAdvancementGranted(AdvancementEvent.AdvancementEarnEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }

        Advancement advancement = event.getAdvancement();
        Item trophyItem = TrophyUtils.getTrophyItem(advancement.getDisplay());
        if (trophyItem == null) {
            return;
        }

        ItemStack trophyStack = new ItemStack(trophyItem);
        TrophyUtils.writeTrophyData(trophyStack.getOrCreateTag(), player, advancement, new Date());

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