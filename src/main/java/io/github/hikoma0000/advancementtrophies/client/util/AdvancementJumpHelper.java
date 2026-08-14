package io.github.hikoma0000.advancementtrophies.client.util;

import io.github.hikoma0000.advancementtrophies.component.TrophyData;
import io.github.hikoma0000.advancementtrophies.init.ModDataComponents;
import io.github.hikoma0000.advancementtrophies.init.ModItems;
import io.github.hikoma0000.advancementtrophies.mixin.client.AdvancementTabAccessor;
import io.github.hikoma0000.advancementtrophies.mixin.client.AdvancementWidgetAccessor;
import io.github.hikoma0000.advancementtrophies.compat.betteradvancements.BetterAdvancementsCompat;
import io.github.hikoma0000.advancementtrophies.mixin.client.ClientAdvancementsAccessor;
import java.util.Map;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class AdvancementJumpHelper {
    private static final int WINDOW_INSIDE_WIDTH = AdvancementsScreen.WINDOW_INSIDE_WIDTH;
    private static final int WINDOW_INSIDE_HEIGHT = AdvancementsScreen.WINDOW_INSIDE_HEIGHT;
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

        Slot hoveredSlot = containerScreen.getSlotUnderMouse();
        if (hoveredSlot == null || !hoveredSlot.hasItem()) {
            return false;
        }

        ItemStack stack = hoveredSlot.getItem();
        if (!ModItems.isTrophy(stack)) {
            return false;
        }

        TrophyData data = stack.get(ModDataComponents.TROPHY_DATA.get());
        if (data == null || !data.hasAdvancementId()) {
            return true;
        }

        ResourceLocation advancementId = data.advancementId();

        ClientAdvancements clientAdvancements = minecraft.getConnection().getAdvancements();
        AdvancementHolder advancement = clientAdvancements.get(advancementId);
        if (advancement == null) {
            minecraft.player.displayClientMessage(Component.translatableWithFallback(
                    "message.advancementtrophies.advancement_not_completed",
                    "§cYou have not completed this advancement."), false);
            return true;
        }

        Map<AdvancementHolder, AdvancementProgress> progressMap =
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
            AdvancementHolder target) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen != screen) {
            return;
        }

        AdvancementNode node = clientAdvancements.getTree().get(target);
        if (node == null) {
            return;
        }

        AdvancementHolder root = node.root().holder();
        clientAdvancements.setSelectedTab(root, true);

        AdvancementWidget widget = screen.getAdvancementWidget(node);
        if (widget == null) {
            return;
        }

        AdvancementTab tab = ((AdvancementWidgetAccessor) (Object) widget).getTab();
        if (tab == null) {
            return;
        }

        centerOnWidget(tab, widget);
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
