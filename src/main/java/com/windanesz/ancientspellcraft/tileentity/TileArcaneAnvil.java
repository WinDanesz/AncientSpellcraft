package com.windanesz.ancientspellcraft.tileentity;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.client.gui.ContainerArcaneAnvil;
import com.windanesz.ancientspellcraft.client.gui.ContainerSphereCognizance;
import com.windanesz.ancientspellcraft.item.ItemRelic;
import com.windanesz.ancientspellcraft.item.WizardClassWeaponHelper;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.NBTExtras;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

@SuppressWarnings("Duplicates")
public class TileArcaneAnvil extends TileEntity implements IInventory {
	private NonNullList<ItemStack> inventory;

	private boolean changeState = false;

	private boolean inUse = false;
	private EntityPlayer currentPlayer;

	public TileArcaneAnvil() {
		inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
	}

	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}

	public EntityPlayer getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(EntityPlayer currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	@Override
	public String getName() {
		return "container." + AncientSpellcraft.MODID + ":arcane_anvil";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {

		super.readFromNBT(compound);

		NBTTagList inventoryList = compound.getTagList("Inventory", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < inventoryList.tagCount(); i++) {
			NBTTagCompound tag = inventoryList.getCompoundTagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < getSizeInventory()) {
				setInventorySlotContents(slot, new ItemStack(tag));
			}
		}

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		NBTTagList inventoryList = new NBTTagList();
		for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack stack = getStackInSlot(i);
			if (!stack.isEmpty()) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				stack.writeToNBT(tag);
				inventoryList.appendTag(tag);
			}
		}

		NBTExtras.storeTagSafely(compound, "Inventory", inventoryList);
		return compound;
	}

	/**
	 * Creates a tag containing the TileEntity information, used by vanilla to transmit from server to client
	 * Warning - although our getUpdatePacket() uses this method, vanilla also calls it directly, so don't remove it.
	 */
	@Override
	public final NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, getBlockMetadata(), this.getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	///////////////////////// IInventory field implementations /////////////////////////

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory() {
		return 3;
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < getSizeInventory(); i++) {
			if (!getStackInSlot(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the stack in the given slot.
	 */
	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.get(slot);
	}

	/**
	 * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack itemstack = this.inventory.get(slot);

		if (slot == 2 && !itemstack.isEmpty()) {
			ItemStack stack0 = this.inventory.get(0).copy();
			stack0.shrink(1);
			ItemStack stack1 = this.inventory.get(1).copy();
			stack1.shrink(1);

			this.inventory.set(0, stack0);
			this.inventory.set(1, stack1);
			this.markDirty();

			return ItemStackHelper.getAndSplit(this.inventory, slot, itemstack.getCount());
		} else {
			ItemStack itemstack1 = ItemStackHelper.getAndSplit(this.inventory, slot, amount);

			if (!itemstack1.isEmpty() && (slot == 0 || slot == 1)) {
				this.inventory.set(2, getResultItem());
			}
			this.markDirty();
			return itemstack1;
		}

	}

	/**
	 * Removes a stack from the given slot and returns it.
	 */
	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(this.inventory, index);
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		ItemStack itemstack = inventory.get(slot);
		boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
		inventory.set(slot, stack);

		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
			stack.setCount(getInventoryStackLimit());
		}

		if (slot == 0 || slot == 1) {
			inventory.set(ContainerArcaneAnvil.RESULT_SLOT, getResultItem());
		}

		markDirty();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		boolean withinDistance = world.getTileEntity(pos) == this && player.getDistanceSqToCenter(pos) < 64;
		return withinDistance && (!inUse || (player == getCurrentPlayer()));
	}

	private ItemStack getResultItem() {
		ItemStack stack0 = this.inventory.get(ContainerArcaneAnvil.INPUT_SLOT_0);
		ItemStack stack1 = this.inventory.get(ContainerArcaneAnvil.INPUT_SLOT_1);

		// novice spellblade crafting
		if (stack0.getItem() == ASItems.battlemage_sword_hilt && stack1.getItem() == ASItems.battlemage_sword_blade) {
			return new ItemStack(ASItems.battlemage_sword_novice);
		}

		int tierLevel = getSwordTier(stack0);

		// crystal silver plating from crystal silver ingot
		if (stack0.getItem() == ASItems.crystal_silver_ingot && stack1.isEmpty()) {
			return new ItemStack(WizardryItems.crystal_silver_plating);
		}

		// spellblade levelling
		if (tierLevel >= 0) {
			Tier tier = Tier.values()[tierLevel];
			int progression = WandHelper.getProgression(stack0);

			if (progression >= tier.next().getProgression() && stack1.getItem() == ASItems.crystal_silver_ingot) {
				ItemStack copy = stack0.copy();
				ItemStack newSword = new ItemStack(WizardClassWeaponHelper.getClassItemForTier(tier.next(), ItemWizardArmour.ArmourClass.BATTLEMAGE));
				newSword.setTagCompound(copy.getTagCompound());
				return newSword;
			}

		}

		return ItemStack.EMPTY;
	}

	private int getSwordTier(ItemStack stack) {
		if (stack.getItem() == ASItems.battlemage_sword_novice) {
			return 0;
		}

		if (stack.getItem() == ASItems.battlemage_sword_apprentice) {
			return 1;
		}

		if (stack.getItem() == ASItems.battlemage_sword_advanced) {
			return 2;
		}

		return -1;
	}

	/**
	 * Sets the current player user and limits usage to one player at a time. Sets the wizarddata to the current user data.
	 *
	 * @param player
	 */
	@Override
	public void openInventory(EntityPlayer player) {
		this.setInUse(true);
		this.setCurrentPlayer(player);
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		this.setInUse(false);
		this.setCurrentPlayer(null);
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
	 * guis use Slot.isItemValid
	 */
	@Override
	public boolean isItemValidForSlot(int slotNumber, ItemStack stack) {

		if (stack == ItemStack.EMPTY) { return true; }

		if (slotNumber == 0 &&
				stack.getItem() == ASItems.battlemage_sword_hilt ||
				stack.getItem() == ASItems.battlemage_sword_novice ||
				stack.getItem() == ASItems.battlemage_sword_apprentice ||
				stack.getItem() == ASItems.battlemage_sword_advanced) {
			return true;
		} else if (slotNumber == ContainerSphereCognizance.BOOK_SLOT) {
			return stack.getItem() instanceof ItemRelic || stack.getItem() instanceof ItemSpellBook || stack.getItem() instanceof ItemScroll;
		}

		if (slotNumber == 2) {
			return false;
		}

		return false;
	}

	public int getFieldCount() {
		return 0;
	}

	public int getField(int id) {
		return 0;
	}

	public void setField(int id, int value) {
	}

	@Override
	public void clear() {
		for (int i = 0; i < getSizeInventory(); i++) {
			setInventorySlotContents(i, ItemStack.EMPTY);
		}
	}

	///////////////////////// IInventory field implementations /////////////////////////
}
