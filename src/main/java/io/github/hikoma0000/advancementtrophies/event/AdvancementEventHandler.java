package io.github.hikoma0000.advancementtrophies.event;

import io.github.hikoma0000.advancementtrophies.component.TrophyData;
import io.github.hikoma0000.advancementtrophies.component.TrophyDate;
import io.github.hikoma0000.advancementtrophies.init.ModDataComponents;
import io.github.hikoma0000.advancementtrophies.init.ModItems;
import io.github.hikoma0000.advancementtrophies.item.TrophyCrateItem;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AdvancementEventHandler {

    private static final Map<AdvancementType, DeferredHolder<Item, Item>> FRAME_TYPE_TO_TROPHY = Map.of(
            AdvancementType.TASK, ModItems.IRON_TROPHY,
            AdvancementType.GOAL, ModItems.GOLD_TROPHY,
            AdvancementType.CHALLENGE, ModItems.DIAMOND_TROPHY);

    @SubscribeEvent
    public static void onAdvancementGranted(AdvancementEvent.AdvancementEarnEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }

        AdvancementHolder holder = event.getAdvancement();
        DisplayInfo display = holder.value().display().orElse(null);
        if (display == null || !display.shouldAnnounceChat()) {
            return;
        }

        DeferredHolder<Item, Item> trophyItem = display.isHidden()
                ? ModItems.NETHERITE_TROPHY
                : FRAME_TYPE_TO_TROPHY.get(display.getType());

        if (trophyItem == null) {
            return;
        }

        ItemStack trophyStack = new ItemStack(trophyItem.get());

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        TrophyDate date = new TrophyDate(
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

        TrophyData trophyData = new TrophyData(
                player.getName().getString(),
                date,
                advancementId,
                titleKey,
                titleJson,
                advancementId.getNamespace(),
                display.getIcon());
        trophyStack.set(ModDataComponents.TROPHY_DATA.get(), trophyData);

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
            boolean hasSpace = inventory.getFreeSlot() != -1
                    || inventory.getSlotWithRemainingSpace(trophyRemainder) != -1;
            if (!hasSpace || !inventory.add(trophyRemainder)) {
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
