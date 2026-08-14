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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Calendar;
import java.util.Date;
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

        ItemStack trophyRemainder = trophyStack.copy();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack inventoryStack = player.getInventory().getItem(i);
            if (inventoryStack.getItem() instanceof TrophyCrateItem) {
                trophyRemainder = TrophyCrateItem.addItemToCrate(inventoryStack, trophyRemainder);
                if (trophyRemainder.isEmpty()) {
                    break;
                }
            }
        }

        if (!trophyRemainder.isEmpty()) {
            if (!player.getInventory().add(trophyRemainder)) {
                player.drop(trophyRemainder, false);
                player.sendSystemMessage(
                        Component.translatableWithFallback("message.advancementtrophies.inventory_full",
                                "§cYour inventory is full! The trophy has been dropped nearby."));
            }
        }
    }
}
