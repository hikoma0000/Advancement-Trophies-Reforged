package io.github.hikoma0000.advancementtrophies.client.util;

import io.github.hikoma0000.advancementtrophies.init.ModItems;
import io.github.hikoma0000.advancementtrophies.mixin.client.AbstractContainerScreenAccessor;
import io.github.hikoma0000.advancementtrophies.mixin.client.AdvancementTabAccessor;
import io.github.hikoma0000.advancementtrophies.mixin.client.AdvancementWidgetAccessor;
import io.github.hikoma0000.advancementtrophies.compat.betteradvancements.BetterAdvancementsCompat;
import io.github.hikoma0000.advancementtrophies.mixin.client.ClientAdvancementsAccessor;
import io.github.hikoma0000.advancementtrophies.util.NBTKeys;
import java.util.Map;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class AdvancementJumpHelper {
    private static final int WINDOW_INSIDE_WIDTH = 234;
    private static final int WINDOW_INSIDE_HEIGHT = 113;
    private static final int WIDGET_CENTER_OFFSET_X = 13;
    private static final int WIDGET_CENTER_OFFSET_Y = 13;

    private AdvancementJumpHelper() {
    }

    public static boolean tryJumpFromHoveredTrophy(Screen screen, int keyCode, int scanCode) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.getConnection() == null) {
            return false;
        }
        if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) {
            return false;
        }
        if (!minecraft.options.keyAdvancements.matches(keyCode, scanCode)) {
            return false;
        }

        Slot hoveredSlot = ((AbstractContainerScreenAccessor) containerScreen).getHoveredSlot();
        if (hoveredSlot == null || !hoveredSlot.hasItem()) {
            return false;
        }

        ItemStack stack = hoveredSlot.getItem();
        if (!ModItems.isTrophy(stack)) {
            return false;
        }

        CompoundTag nbt = stack.getTag();
        if (nbt == null || !nbt.contains(NBTKeys.ADVANCEMENT_ID, Tag.TAG_STRING)) {
            return true;
        }

        ResourceLocation advancementId = ResourceLocation.tryParse(nbt.getString(NBTKeys.ADVANCEMENT_ID));
        if (advancementId == null) {
            minecraft.player.displayClientMessage(Component.translatableWithFallback(
                    "message.advancementtrophies.advancement_not_completed",
                    "§cYou have not completed this advancement."), false);
            return true;
        }

        ClientAdvancements clientAdvancements = minecraft.getConnection().getAdvancements();
        Advancement advancement = clientAdvancements.getAdvancements().get(advancementId);
        if (advancement == null) {
            minecraft.player.displayClientMessage(Component.translatableWithFallback(
                    "message.advancementtrophies.advancement_not_completed",
                    "§cYou have not completed this advancement."), false);
            return true;
        }

        Map<Advancement, AdvancementProgress> progressMap =
                ((ClientAdvancementsAccessor) clientAdvancements).getProgress();
        AdvancementProgress progress = progressMap.get(advancement);
        if (progress == null || !progress.isDone()) {
            minecraft.player.displayClientMessage(Component.translatableWithFallback(
                    "message.advancementtrophies.advancement_not_completed",
                    "§cYou have not completed this advancement."), false);
            return true;
        }

        AdvancementsScreen advancementsScreen = new AdvancementsScreen(clientAdvancements);
        minecraft.setScreen(advancementsScreen);
        Screen currentScreen = minecraft.screen;
        if (BetterAdvancementsCompat.isBetterAdvancementsScreen(currentScreen)) {
            minecraft.tell(() -> BetterAdvancementsCompat.focus(currentScreen, clientAdvancements, advancement));
        } else {
            minecraft.tell(() -> focusAdvancement(advancementsScreen, clientAdvancements, advancement));
        }
        return true;
    }

    private static void focusAdvancement(AdvancementsScreen screen, ClientAdvancements clientAdvancements,
            Advancement target) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen != screen) {
            return;
        }

        Advancement root = getRoot(target);
        clientAdvancements.setSelectedTab(root, true);

        AdvancementWidget widget = screen.getAdvancementWidget(target);
        if (widget == null) {
            return;
        }

        AdvancementTab tab = ((AdvancementWidgetAccessor) (Object) widget).getTab();
        if (tab == null) {
            return;
        }

        centerOnWidget(tab, widget);
    }


    private static Advancement getRoot(Advancement advancement) {
        Advancement current = advancement;
        while (current.getParent() != null) {
            current = current.getParent();
        }
        return current;
    }

    private static void centerOnWidget(AdvancementTab tab, AdvancementWidget widget) {
        AdvancementTabAccessor accessor = (AdvancementTabAccessor) (Object) tab;
        accessor.setCentered(true);

        double targetScrollX = (WINDOW_INSIDE_WIDTH / 2.0) - widget.getX() - WIDGET_CENTER_OFFSET_X;
        double targetScrollY = (WINDOW_INSIDE_HEIGHT / 2.0) - widget.getY() - WIDGET_CENTER_OFFSET_Y;

        double dx = targetScrollX - accessor.getScrollX();
        double dy = targetScrollY - accessor.getScrollY();
        tab.scroll(dx, dy);

        if (Math.abs(accessor.getScrollX() - targetScrollX) > 0.5D
                || Math.abs(accessor.getScrollY() - targetScrollY) > 0.5D) {
            accessor.setScrollX(targetScrollX);
            accessor.setScrollY(targetScrollY);
        }
    }
}
