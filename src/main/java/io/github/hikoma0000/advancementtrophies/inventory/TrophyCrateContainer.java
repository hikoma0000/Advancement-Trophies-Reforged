package io.github.hikoma0000.advancementtrophies.inventory;

import io.github.hikoma0000.advancementtrophies.block.entity.TrophyCrateBlockEntity;
import io.github.hikoma0000.advancementtrophies.init.ModBlocks;
import io.github.hikoma0000.advancementtrophies.init.ModContainers;
import io.github.hikoma0000.advancementtrophies.init.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class TrophyCrateContainer extends AbstractContainerMenu {
    private final TrophyCrateBlockEntity blockEntity;
    private final Level level;

    public TrophyCrateContainer(int pContainerId, Inventory pPlayerInventory, FriendlyByteBuf extraData) {
        this(pContainerId, pPlayerInventory, pPlayerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public TrophyCrateContainer(int pContainerId, Inventory pPlayerInventory, BlockEntity blockEntity) {
        super(ModContainers.TROPHY_CRATE_CONTAINER.get(), pContainerId);
        this.blockEntity = (TrophyCrateBlockEntity) blockEntity;
        this.level = pPlayerInventory.player.level();
        checkContainerSize(this.blockEntity, TrophyCrateBlockEntity.CONTAINER_SIZE);
        this.blockEntity.startOpen(pPlayerInventory.player);

        final int containerRows = 3;
        final int containerCols = 9;

        for (int i = 0; i < containerRows; ++i) {
            for (int j = 0; j < containerCols; ++j) {
                this.addSlot(new Slot(this.blockEntity, j + i * containerCols, 8 + j * 18, 18 + i * 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return ModItems.isTrophy(stack);
                    }
                });
            }
        }

        addPlayerInventory(pPlayerInventory);
        addPlayerHotbar(pPlayerInventory);
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.blockEntity.stopOpen(pPlayer);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack sourceStack = slot.getItem();
            itemstack = sourceStack.copy();

            if (pIndex < TrophyCrateBlockEntity.CONTAINER_SIZE) {
                if (!this.moveItemStackTo(sourceStack, TrophyCrateBlockEntity.CONTAINER_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(sourceStack, 0, TrophyCrateBlockEntity.CONTAINER_SIZE, false)) {
                return ItemStack.EMPTY;
            }

            if (sourceStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }



    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.TROPHY_CRATE.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}