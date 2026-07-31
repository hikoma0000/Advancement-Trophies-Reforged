package io.github.hikoma0000.advancementtrophies.compat.betteradvancements;

import java.lang.reflect.Field;
import java.util.Map;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class BetterAdvancementsCompat {
    private BetterAdvancementsCompat() {
    }

    public static boolean isBetterAdvancementsScreen(Screen screen) {
        return screen != null && screen.getClass().getName().equals("betteradvancements.common.gui.BetterAdvancementsScreen");
    }

    public static void focus(Screen screen, ClientAdvancements clientAdvancements, Advancement target) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen != screen) {
            return;
        }

        Advancement root = getRoot(target);
        clientAdvancements.setSelectedTab(root, true);

        try {
            Field tabsField = screen.getClass().getDeclaredField("tabs");
            tabsField.setAccessible(true);
            Map<?, ?> tabs = (Map<?, ?>) tabsField.get(screen);
            if (tabs == null) {
                return;
            }

            Object tabObj = tabs.get(root);
            if (tabObj == null) {
                return;
            }

            Field widgetsField = tabObj.getClass().getDeclaredField("widgets");
            widgetsField.setAccessible(true);
            Map<?, ?> widgets = (Map<?, ?>) widgetsField.get(tabObj);
            if (widgets == null) {
                return;
            }

            Object widgetObj = widgets.get(target);
            if (widgetObj == null) {
                return;
            }

            centerOnBetterAdvancementsWidget(tabObj, widgetObj, screen);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void centerOnBetterAdvancementsWidget(Object tabObj, Object widgetObj, Screen screen) {
        try {
            Field scrollXField = tabObj.getClass().getDeclaredField("scrollX");
            Field scrollYField = tabObj.getClass().getDeclaredField("scrollY");
            scrollXField.setAccessible(true);
            scrollYField.setAccessible(true);

            Field minXField = tabObj.getClass().getDeclaredField("minX");
            Field maxXField = tabObj.getClass().getDeclaredField("maxX");
            Field minYField = tabObj.getClass().getDeclaredField("minY");
            Field maxYField = tabObj.getClass().getDeclaredField("maxY");
            minXField.setAccessible(true);
            maxXField.setAccessible(true);
            minYField.setAccessible(true);
            maxYField.setAccessible(true);

            int minX = minXField.getInt(tabObj);
            int maxX = maxXField.getInt(tabObj);
            int minY = minYField.getInt(tabObj);
            int maxY = maxYField.getInt(tabObj);

            Field xField = widgetObj.getClass().getDeclaredField("x");
            Field yField = widgetObj.getClass().getDeclaredField("y");
            xField.setAccessible(true);
            yField.setAccessible(true);
            int widgetX = xField.getInt(widgetObj);
            int widgetY = yField.getInt(widgetObj);

            Field internalWidthField = screen.getClass().getDeclaredField("internalWidth");
            Field internalHeightField = screen.getClass().getDeclaredField("internalHeight");
            internalWidthField.setAccessible(true);
            internalHeightField.setAccessible(true);
            int internalWidth = internalWidthField.getInt(screen);
            int internalHeight = internalHeightField.getInt(screen);

            int insideWidth = internalWidth - 60 - 27;
            int insideHeight = internalHeight - 40 - 30 - 27;

            double targetScrollX = (insideWidth / 2.0) - widgetX - 13;
            double targetScrollY = (insideHeight / 2.0) - widgetY - 13;

            int scrollX = (int) Math.round(targetScrollX);
            int scrollY = (int) Math.round(targetScrollY);

            if (maxX - minX > insideWidth) {
                scrollX = Mth.clamp(scrollX, -(maxX - insideWidth), -minX);
            }
            if (maxY - minY > insideHeight) {
                scrollY = Mth.clamp(scrollY, -(maxY - insideHeight), -minY);
            }

            scrollXField.setInt(tabObj, scrollX);
            scrollYField.setInt(tabObj, scrollY);

            Field centeredField = tabObj.getClass().getDeclaredField("centered");
            centeredField.setAccessible(true);
            centeredField.setBoolean(tabObj, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Advancement getRoot(Advancement advancement) {
        Advancement current = advancement;
        while (current.getParent() != null) {
            current = current.getParent();
        }
        return current;
    }
}
