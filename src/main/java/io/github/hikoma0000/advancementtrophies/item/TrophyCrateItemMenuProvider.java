package io.github.hikoma0000.advancementtrophies.item;

import io.github.hikoma0000.advancementtrophies.inventory.TrophyCrateItemContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class TrophyCrateItemMenuProvider implements MenuProvider {
    private final ItemStack crateStack;
    private final InteractionHand hand;

    public TrophyCrateItemMenuProvider(ItemStack crateStack, InteractionHand hand) {
        this.crateStack = crateStack;
        this.hand = hand;
    }

    @Override
    public Component getDisplayName() {
        return this.crateStack.getHoverName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new TrophyCrateItemContainer(pContainerId, pPlayerInventory, crateStack, hand);
    }
}