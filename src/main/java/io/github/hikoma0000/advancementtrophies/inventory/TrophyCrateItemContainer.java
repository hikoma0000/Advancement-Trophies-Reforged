package io.github.hikoma0000.advancementtrophies.inventory;

import io.github.hikoma0000.advancementtrophies.init.ModContainers;
import io.github.hikoma0000.advancementtrophies.init.ModItems;
import io.github.hikoma0000.advancementtrophies.util.NBTKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class TrophyCrateItemContainer extends AbstractContainerMenu {
    private final ItemStack crateStack;
    private final IItemHandler crateInventory;
    private final int lockedSlot;
    private final InteractionHand hand;

    public TrophyCrateItemContainer(int pContainerId, Inventory pPlayerInventory, FriendlyByteBuf extraData) {
        this(pContainerId, pPlayerInventory, extraData.readEnum(InteractionHand.class));
    }

    private TrophyCrateItemContainer(int pContainerId, Inventory pPlayerInventory, InteractionHand hand) {
        this(pContainerId, pPlayerInventory, pPlayerInventory.player.getItemInHand(hand), hand);
    }

    public TrophyCrateItemContainer(int pContainerId, Inventory pPlayerInventory, ItemStack crateStack, InteractionHand hand) {
        super(ModContainers.TROPHY_CRATE_ITEM_CONTAINER.get(), pContainerId);
        this.crateStack = crateStack;
        this.crateInventory = getInventory(crateStack);
        this.hand = hand;

        addPlayerInventory(pPlayerInventory);
        addPlayerHotbar(pPlayerInventory);
        this.addSlot(new Slot(pPlayerInventory, 40, -1000, -1000));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new SlotItemHandler(crateInventory, j + i * 9, 8 + j * 18, 18 + i * 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return ModItems.isTrophy(stack);
                    }
                });
            }
        }
        crateStack.getOrCreateTag().putBoolean("open", true);

        int lockedPlayerSlot = pPlayerInventory.selected;
        if(hand == InteractionHand.OFF_HAND) {
            lockedPlayerSlot = 40;
        }
        this.lockedSlot = findSlotMatching(pPlayerInventory, lockedPlayerSlot);
    }

    private int findSlotMatching(Inventory playerInv, int playerSlot) {
        for (int i = 0; i < this.slots.size(); i++) {
            Slot s = this.slots.get(i);
            if (s.container == playerInv && s.getSlotIndex() == playerSlot) {
                return i;
            }
        }
        return -1;
    }

    private IItemHandler getInventory(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTagElement(NBTKeys.BLOCK_ENTITY_TAG);
        ItemStackHandler handler = new ItemStackHandler(27);
        if (nbt.contains(NBTKeys.INVENTORY)) {
            handler.deserializeNBT(nbt.getCompound(NBTKeys.INVENTORY));
        }
        return handler;
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        if (crateStack.hasTag()) {
            crateStack.getTag().putBoolean("open", false);
        }
        CompoundTag nbt = crateStack.getOrCreateTagElement(NBTKeys.BLOCK_ENTITY_TAG);
        nbt.put(NBTKeys.INVENTORY, ((ItemStackHandler) crateInventory).serializeNBT());
        if (!pPlayer.level().isClientSide()) {
            pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.BARREL_CLOSE, SoundSource.PLAYERS, 0.5F, pPlayer.level().random.nextFloat() * 0.1F + 0.9F);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        final int PLAYER_INVENTORY_END = 36;
        final int CRATE_INVENTORY_START = PLAYER_INVENTORY_END + 1;
        final int CRATE_INVENTORY_END = CRATE_INVENTORY_START + 27;

        if (index < PLAYER_INVENTORY_END) {
            if (!moveItemStackTo(sourceStack, CRATE_INVENTORY_START, CRATE_INVENTORY_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= CRATE_INVENTORY_START && index < CRATE_INVENTORY_END) {
            if (!moveItemStackTo(sourceStack, 0, PLAYER_INVENTORY_END, false)) {
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

        return copyOfSourceStack;
    }

    @Override
    public void clicked(int pSlotId, int pButton, ClickType pClickType, Player pPlayer) {
        if (pSlotId == this.lockedSlot) {
            return;
        }
        super.clicked(pSlotId, pButton, pClickType, pPlayer);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return ItemStack.matches(this.crateStack, pPlayer.getItemInHand(this.hand));
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