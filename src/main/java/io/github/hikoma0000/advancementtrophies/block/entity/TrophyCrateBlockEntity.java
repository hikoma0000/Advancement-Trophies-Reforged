package io.github.hikoma0000.advancementtrophies.block.entity;

import io.github.hikoma0000.advancementtrophies.block.TrophyCrateBlock;
import io.github.hikoma0000.advancementtrophies.init.ModBlockEntities;
import io.github.hikoma0000.advancementtrophies.init.ModItems;
import io.github.hikoma0000.advancementtrophies.inventory.TrophyCrateContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class TrophyCrateBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
    public static final int CONTAINER_SIZE = 27;
    private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    private static final int[] SLOTS = IntStream.range(0, CONTAINER_SIZE).toArray();
    private int openCount;
    private boolean initialized = false;

    public TrophyCrateBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.TROPHY_CRATE.get(), pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items, registries);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        if (!tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items, registries);
        }
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public Component getDisplayName() {
        return this.hasCustomName() ? this.getCustomName() : this.getDefaultName();
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.advancementtrophies.trophy_crate");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory) {
        return new TrophyCrateContainer(containerId, playerInventory, this);
    }

    public void startOpen(Player player) {
        if (!this.isRemoved() && !player.isSpectator()) {
            if (this.openCount < 0) {
                this.openCount = 0;
            }
            ++this.openCount;
            this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
            if (this.openCount == 1) {
                this.level.playSound(null, this.worldPosition, SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
            }
        }
    }

    public void stopOpen(Player player) {
        if (!this.isRemoved() && !player.isSpectator()) {
            --this.openCount;
            this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
            if (this.openCount <= 0) {
                this.level.playSound(null, this.worldPosition, SoundEvents.BARREL_CLOSE, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
            }
        }
    }
    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            this.openCount = type;
            boolean isOpen = this.openCount > 0;
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(TrophyCrateBlock.OPEN, isOpen), 3);
            return true;
        } else {
            return super.triggerEvent(id, type);
        }
    }

    @Override
    public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
        return ModItems.isTrophy(stack) && super.canPlaceItem(index, stack);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
        return this.canPlaceItem(index, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TrophyCrateBlockEntity blockEntity) {
        if (!blockEntity.initialized) {
            if (state.getValue(TrophyCrateBlock.OPEN)) {
                level.setBlock(pos, state.setValue(TrophyCrateBlock.OPEN, Boolean.valueOf(false)), 3);
            }
            blockEntity.initialized = true;
        }
    }
}
