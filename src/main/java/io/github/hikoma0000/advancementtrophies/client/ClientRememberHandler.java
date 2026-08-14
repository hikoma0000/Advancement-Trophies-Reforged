package io.github.hikoma0000.advancementtrophies.client;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientRememberHandler {
    private ClientRememberHandler() {
    }

    public static void remember(ResourceLocation advancementId) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null) {
            return;
        }

        AdvancementHolder advancement = minecraft.getConnection().getAdvancements().get(advancementId);
        if (advancement == null) {
            return;
        }

        DisplayInfo display = advancement.value().display().orElse(null);
        if (display == null) {
            return;
        }

        minecraft.getToasts().addToast(new AdvancementToast(advancement));

        if (display.getType() != AdvancementType.CHALLENGE) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_IN, 1.0F, 1.0F));
        }
    }
}
