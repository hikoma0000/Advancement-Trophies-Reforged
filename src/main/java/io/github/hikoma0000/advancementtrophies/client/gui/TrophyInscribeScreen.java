package io.github.hikoma0000.advancementtrophies.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.hikoma0000.advancementtrophies.compat.alexscaves.AlexsCavesCompat;
import io.github.hikoma0000.advancementtrophies.compat.betteradvancements.BetterAdvancementsCompat;
import io.github.hikoma0000.advancementtrophies.mixin.client.AdvancementsScreenAccessor;
import io.github.hikoma0000.advancementtrophies.mixin.client.ClientAdvancementsAccessor;
import io.github.hikoma0000.advancementtrophies.network.ModNetwork;
import io.github.hikoma0000.advancementtrophies.network.packet.ServerboundInscribeTrophyPacket;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class TrophyInscribeScreen extends Screen {
    public static final Component HINT = Component.translatableWithFallback(
            "screen.advancementtrophies.inscribe.hint", "Click an advancement to inscribe");
    private static final Component WRONG_RARITY = Component.translatableWithFallback(
            "screen.advancementtrophies.inscribe.wrong_rarity", "Cannot be inscribed on a trophy of this rarity");
    private static final Component NOT_COMPLETED = Component.translatableWithFallback(
            "screen.advancementtrophies.inscribe.not_completed", "You have not completed this advancement yet");

    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(
            "textures/gui/advancements/widgets.png");
    private static final int FRAME_TEXTURE_V = 128;
    private static final int FRAME_OFFSET_X = 3;
    private static final int FRAME_SIZE = 26;
    private static final int LOCKED_OVERLAY_Z = 160;
    private static final float LOCKED_OVERLAY_ALPHA = 0.7F;

    private final ClientAdvancements clientAdvancements;
    private final Screen advancementsScreen;
    private final InteractionHand hand;
    private final Set<ResourceLocation> inscribableIds;
    @Nullable
    private Advancement hoveredAdvancement;

    private TrophyInscribeScreen(ClientAdvancements clientAdvancements, InteractionHand hand,
            Set<ResourceLocation> inscribableIds) {
        super(HINT);
        this.clientAdvancements = clientAdvancements;
        this.advancementsScreen = BetterAdvancementsCompat.isLoaded()
                ? BetterAdvancementsCompat.createScreen(clientAdvancements)
                : new AdvancementsScreen(clientAdvancements);
        this.hand = hand;
        this.inscribableIds = inscribableIds;
    }

    public static void open(InteractionHand hand, Set<ResourceLocation> inscribableIds) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null) {
            return;
        }
        minecraft.setScreen(new TrophyInscribeScreen(minecraft.getConnection().getAdvancements(), hand,
                inscribableIds));
    }

    @Override
    protected void init() {
        advancementsScreen.init(minecraft, width, height);
    }

    @Override
    public void tick() {
        if (advancementsScreen instanceof AdvancementsScreen screen) {
            AdvancementTab tab = ((AdvancementsScreenAccessor) screen).getSelectedTab();
            if (tab != null) {
                AlexsCavesCompat.tickAdvancementTab(tab.getAdvancement());
            }
        }
    }

    @Override
    public void onClose() {
        advancementsScreen.onClose();
    }

    @Override
    public void removed() {
        advancementsScreen.removed();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        hoveredAdvancement = null;
        advancementsScreen.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        if (hoveredAdvancement != null && !isInscribable(hoveredAdvancement)) {
            pGuiGraphics.renderTooltip(font, isDone(hoveredAdvancement) ? WRONG_RARITY : NOT_COMPLETED,
                    pMouseX, pMouseY);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0 && hoveredAdvancement != null && isInscribable(hoveredAdvancement)) {
            ModNetwork.getChannel().sendToServer(
                    new ServerboundInscribeTrophyPacket(hand, hoveredAdvancement.getId()));
            onClose();
            return true;
        }
        return advancementsScreen.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return advancementsScreen.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        return advancementsScreen.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        return advancementsScreen.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    public void renderLockedFrame(GuiGraphics guiGraphics, Advancement advancement, int x, int y) {
        if (isInscribable(advancement)) {
            return;
        }
        RenderSystem.enableBlend();
        guiGraphics.setColor(0.0F, 0.0F, 0.0F, LOCKED_OVERLAY_ALPHA);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, LOCKED_OVERLAY_Z);
        guiGraphics.blit(WIDGETS_LOCATION, x + FRAME_OFFSET_X, y, advancement.getDisplay().getFrame().getTexture(),
                FRAME_TEXTURE_V, FRAME_SIZE, FRAME_SIZE);
        guiGraphics.pose().popPose();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public boolean trackHover(Advancement advancement) {
        hoveredAdvancement = advancement;
        return isInscribable(advancement);
    }

    private boolean isInscribable(Advancement advancement) {
        return inscribableIds.contains(advancement.getId());
    }

    private boolean isDone(Advancement advancement) {
        AdvancementProgress progress = ((ClientAdvancementsAccessor) clientAdvancements).getProgress().get(advancement);
        return progress != null && progress.isDone();
    }
}
