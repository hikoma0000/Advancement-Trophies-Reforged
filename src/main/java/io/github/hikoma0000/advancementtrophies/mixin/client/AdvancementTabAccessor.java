package io.github.hikoma0000.advancementtrophies.mixin.client;

import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AdvancementTab.class)
public interface AdvancementTabAccessor {
    @Accessor("scrollX")
    double getScrollX();

    @Accessor("scrollX")
    void setScrollX(double scrollX);

    @Accessor("scrollY")
    double getScrollY();

    @Accessor("scrollY")
    void setScrollY(double scrollY);

    @Accessor("centered")
    void setCentered(boolean centered);
}
