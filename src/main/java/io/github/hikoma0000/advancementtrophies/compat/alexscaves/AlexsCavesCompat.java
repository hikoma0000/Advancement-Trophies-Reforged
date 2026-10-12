package io.github.hikoma0000.advancementtrophies.compat.alexscaves;

import com.github.alexmodguy.alexscaves.client.gui.ACAdvancementTabs;
import net.minecraft.advancements.Advancement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;

@OnlyIn(Dist.CLIENT)
public final class AlexsCavesCompat {
    private AlexsCavesCompat() {
    }

    public static void tickAdvancementTab(Advancement tabRoot) {
        if (ModList.get().isLoaded("alexscaves") && ACAdvancementTabs.isAlexsCavesWidget(tabRoot)) {
            ACAdvancementTabs.tick();
        }
    }
}
