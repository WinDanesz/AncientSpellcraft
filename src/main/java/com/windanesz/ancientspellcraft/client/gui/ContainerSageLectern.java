package com.windanesz.ancientspellcraft.client.gui;

import com.windanesz.ancientspellcraft.packet.PacketControlInput;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.tileentity.TileSageLectern;
import electroblob.wizardry.inventory.SlotItemList;
import electroblob.wizardry.registry.WizardrySounds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;

public class ContainerSageLectern extends Container {

	private int[] cachedFields;

	public static int INPUT_SLOT_0 = 0;
	public static int INPUT_SLOT_1 = 1;
	public static int RESULT_SLOT = 2;

	private TileSageLectern lectern;

	public ContainerSageLectern(EntityPlayer player, IInventory playerInv, TileSageLectern te) {
		this.lectern = te;
		te.openInventory(player);

		// INPUT_SLOT_0
		this.addSlotToContainer(new SlotItemList(te, 0, 26, 24, 1,
				ASItems.sage_tome_novice_fire,
				ASItems.sage_tome_apprentice_fire,
				ASItems.sage_tome_advanced_fire,
				ASItems.sage_tome_master_fire,
				ASItems.sage_tome_novice_ice,
				ASItems.sage_tome_apprentice_ice,
				ASItems.sage_tome_advanced_ice,
				ASItems.sage_tome_master_ice,
				ASItems.sage_tome_novice_lightning,
				ASItems.sage_tome_apprentice_lightning,
				ASItems.sage_tome_advanced_lightning,
				ASItems.sage_tome_master_lightning,
				ASItems.sage_tome_novice_necromancy,
				ASItems.sage_tome_apprentice_necromancy,
				ASItems.sage_tome_advanced_necromancy,
				ASItems.sage_tome_master_necromancy,
				ASItems.sage_tome_novice_earth,
				ASItems.sage_tome_apprentice_earth,
				ASItems.sage_tome_advanced_earth,
				ASItems.sage_tome_master_earth,
				ASItems.sage_tome_novice_sorcery,
				ASItems.sage_tome_apprentice_sorcery,
				ASItems.sage_tome_advanced_sorcery,
				ASItems.sage_tome_master_sorcery,
				ASItems.sage_tome_novice_healing,
				ASItems.sage_tome_apprentice_healing,
				ASItems.sage_tome_advanced_healing,
				ASItems.sage_tome_master_healing
		));

		// INPUT_SLOT_1
		this.addSlotToContainer(new SlotItemList(te, 1, 75, 24, 1, ASItems.battlemage_sword_blade, ASItems.crystal_silver_ingot));

		/// RESULT_SLOT
		this.addSlotToContainer(new SlotItemList(te, 2, 133, 24, 1,
										ASItems.enchanted_page) {
									@Override
									public ItemStack onTake(EntityPlayer player, ItemStack stack) {
										if (!player.world.isRemote) 			{
											player.world.playSound(null, player.getPosition(), WizardrySounds.MISC_BOOK_OPEN, SoundCategory.BLOCKS, 1.0F, player.world.rand.nextFloat() * 0.1F + 0.9F);
										}
										return super.onTake(player, stack);
									}
								}
		);

		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 79 + y * 18));
			}
		}

		// Player Inventory, Slot 0-8, Slot IDs 36-44
		for (int x = 0; x < 9; ++x) {
			this.addSlotToContainer(new Slot(playerInv, x, 8 + x * 18, 137));
		}
	}

	/**
	 * Determines whether supplied player can use this container
	 */
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.lectern.isUsableByPlayer(playerIn);
	}

	/**
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
	 * inventory and the other inventory(s).
	 */
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (index == 2) {
			return ItemStack.EMPTY;
		}

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < this.lectern.getSizeInventory()) {
				if (!this.mergeItemStack(itemstack1, this.lectern.getSizeInventory(), this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, this.lectern.getSizeInventory(), false)) {
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

	/**
	 * Looks for changes made in the container, sends them to every listener.
	 */
	@SuppressWarnings("Duplicates")
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		boolean allFieldsHaveChanged = false;
		boolean[] fieldHasChanged = new boolean[lectern.getFieldCount()];
		if (cachedFields == null) {
			cachedFields = new int[lectern.getFieldCount()];
			allFieldsHaveChanged = true;
		}
		for (int i = 0; i < cachedFields.length; ++i) {
			if (allFieldsHaveChanged || cachedFields[i] != lectern.getField(i)) {
				cachedFields[i] = lectern.getField(i);
				fieldHasChanged[i] = true;
			}
		}

		// go through the list of listeners (players using this container) and update them if necessary
		for (IContainerListener listener : this.listeners) {
			for (int fieldID = 0; fieldID < lectern.getFieldCount(); ++fieldID) {
				if (fieldHasChanged[fieldID]) {
					// Note that although sendWindowProperty takes 2 ints on a server these are truncated to shorts
					listener.sendWindowProperty(this, fieldID, cachedFields[fieldID]);
				}
			}
		}
	}

	/**
	 * Called when the container is closed.
	 */
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		this.lectern.closeInventory(playerIn);
	}

	/**
	 * Called (via {@link PacketControlInput PacketControlInput}) when the apply button in
	 * the sphere of cognizance GUI is pressed.
	 */
	@SuppressWarnings("unused")
	public void onApplyButtonPressed() { }
}

