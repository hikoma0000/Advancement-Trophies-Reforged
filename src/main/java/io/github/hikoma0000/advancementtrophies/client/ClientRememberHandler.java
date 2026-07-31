package io.github.hikoma0000.advancementtrophies.client;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientRememberHandler {
    private ClientRememberHandler() {
    }

    public static void remember(ResourceLocation advancementId) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null) {
            return;
        }

        Advancement advancement = minecraft.getConnection().getAdvancements().getAdvancements().get(advancementId);
        if (advancement == null || advancement.getDisplay() == null) {
            return;
        }

        minecraft.getToasts().addToast(new AdvancementToast(advancement));

        if (advancement.getDisplay().getFrame() != FrameType.CHALLENGE) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_IN, 1.0F, 1.0F));
        }
    }
}
