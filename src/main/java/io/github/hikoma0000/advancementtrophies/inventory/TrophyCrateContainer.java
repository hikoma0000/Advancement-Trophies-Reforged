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
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class TrophyCrateContainer extends AbstractContainerMenu {
    private final TrophyCrateBlockEntity blockEntity;
    private final Level level;

    public TrophyCrateContainer(int pContainerId, Inventory pPlayerInventory, FriendlyByteBuf extraData) {
        this(pContainerId, pPlayerInventory, pPlayerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public TrophyCrateContainer(int pContainerId, Inventory pPlayerInventory, BlockEntity blockEntity) {
        super(ModContainers.TROPHY_CRATE_CONTAINER.get(), pContainerId);
        checkContainerSize(pPlayerInventory, 27);
        this.blockEntity = (TrophyCrateBlockEntity) blockEntity;
        this.level = pPlayerInventory.player.level();

        addPlayerInventory(pPlayerInventory);
        addPlayerHotbar(pPlayerInventory);

        var handler = this.blockEntity.getItemHandler();
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new SlotItemHandler(handler, j + i * 9, 8 + j * 18, 18 + i * 18){
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return ModItems.isTrophy(stack);
                    }
                });
            }
        }
        this.blockEntity.startOpen(pPlayerInventory.player);
    }

    public BlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.blockEntity.stopOpen(pPlayer);
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = 27;

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
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