package com.windanesz.ancientspellcraft.client.gui;

import com.windanesz.ancientspellcraft.item.ItemGlyphArtefact;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerInventoryInItemStack extends Container {
	private final IInventory itemInventory;

	public ContainerInventoryInItemStack(IInventory playerInventory, IInventory itemInventory, EntityPlayer player) {
		this.itemInventory = itemInventory;
		itemInventory.openInventory(player);

		if (itemInventory instanceof InventoryInItemStack) {

			int rowCount = ((InventoryInItemStack) itemInventory).getRowCount();
			int columnCount = ((InventoryInItemStack) itemInventory).getColumnCount();
			int index = 0;

			int offsetX = itemInventory.getSizeInventory() == 9 ? 0 : -54;

			this.addSlotToContainer(new Slot(itemInventory, index, 80, 36) {
				@Override
				public boolean isItemValid(ItemStack stack) {
					return stack.getItem() instanceof ItemGlyphArtefact;
				}
			});
			index++;
			this.addSlotToContainer(new Slot(itemInventory, index, 60, 76) {
				@Override
				public boolean isItemValid(ItemStack stack) {
					return stack.getItem() instanceof ItemGlyphArtefact;
				}
			});
			index++;
			this.addSlotToContainer(new Slot(itemInventory, index, 100, 76) {
				@Override
				public boolean isItemValid(ItemStack stack) {
					return stack.getItem() instanceof ItemGlyphArtefact;
				}
			});

			// player's inventory
			for (int i1 = 0; i1 < 3; ++i1) {
				for (int k1 = 0; k1 < 9; ++k1) {
					this.addSlotToContainer(new Slot(playerInventory, k1 + i1 * 9 + 9, 8 + k1 * 18, 134 + i1 * 18));
				}
			}

			// player's inventory hotbar
			for (int j1 = 0; j1 < 9; ++j1) {
				this.addSlotToContainer(new Slot(playerInventory, j1, 8 + j1 * 18, 192));
			}
		}
	}

	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.itemInventory.isUsableByPlayer(playerIn);
	}

	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < this.itemInventory.getSizeInventory()) {
				if (!this.mergeItemStack(itemstack1, this.itemInventory.getSizeInventory(), this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (this.getSlot(1).isItemValid(itemstack1) && !this.getSlot(1).getHasStack()) {
				if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
					return ItemStack.EMPTY;
				}
			} else if (this.getSlot(0).isItemValid(itemstack1)) {
				if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (this.itemInventory.getSizeInventory() <= 2 || !this.mergeItemStack(itemstack1, 2, this.itemInventory.getSizeInventory(), false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

		}

		return itemstack;
	}

	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		this.itemInventory.closeInventory(playerIn);
	}

}
