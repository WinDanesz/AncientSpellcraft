package com.windanesz.ancientspellcraft.tileentity;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.client.gui.ContainerScribingDesk;
import com.windanesz.ancientspellcraft.item.ItemRelic;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemCrystal;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.NBTExtras;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

@SuppressWarnings("Duplicates")
public class TileScribingDesk extends TileEntity implements IInventory, ITickable {
	private NonNullList<ItemStack> inventory;

	private int researchDuration;
	private int researchProgress;
	public int currentHintId = 0;
	public int ready = 0;

	private boolean changedResearchState = false;

	private boolean inUse = false;
	private EntityPlayer currentPlayer;

	public WizardData getPlayerWizardData() {
		return playerWizardData;
	}

	private WizardData playerWizardData;

	private NonNullList<ItemStack> furnaceItemStacks = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);

	public float rotation;

	public TileScribingDesk() {
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
		return "container." + AncientSpellcraft.MODID + ":sphere_cognizance";
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

		researchDuration = compound.getShort("researchDuration");
		researchProgress = compound.getShort("researchProgress");
		currentHintId = compound.getShort("currentHintId");
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

		compound.setInteger("researchDuration", (short) this.researchDuration); // calculated
		compound.setInteger("researchProgress", (short) this.researchProgress);
		compound.setInteger("currentHintId", (short) this.currentHintId);
		compound.setInteger("currentHintTypeId", (short) this.ready);

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

	public Spell getCurrentSpell() {
		if (getInputStack().getItemDamage() != OreDictionary.WILDCARD_VALUE) {
			return Spell.byMetadata(getInputStack().getItemDamage());
		}
		return null;
	}

	public ItemStack getCrystalStack() {
		return inventory.get(ContainerScribingDesk.CRYSTAL_SLOT);
	}

	public ItemStack getInputStack() {
		return inventory.get(1);
	}

	@SuppressWarnings("Duplicates")
	public void update() {
		if (!world.isRemote) {

			if (inUse && currentPlayer != null) {

				boolean craftBook = false;
				if (hasCrystalForFuel() && (ItemStack.areItemsEqualIgnoreDurability((inventory.get(ContainerScribingDesk.INK_SLOT)), new ItemStack(Items.DYE, 1, 0))) &&
						inventory.get(ContainerScribingDesk.RELIC_SLOT) != null && inventory.get(ContainerScribingDesk.RELIC_SLOT).getItem() instanceof ItemRelic) {

					ItemStack relic = inventory.get(ContainerScribingDesk.RELIC_SLOT);
					if (ItemRelic.isResearched(relic)) {

						if (inventory.get(ContainerScribingDesk.BOOK_SLOT).getItem() == Items.BOOK) {

							List<ItemStack> componentList = ItemRelic.getSpellComponentItems(relic);

							int matches = 0;

							if (componentList != null && !componentList.isEmpty()) {
								for (int i = 0; i < componentList.size(); i++) {
									ItemStack stack = componentList.get(i);
									ItemStack currItem = inventory.get(i);

									if (ItemStack.areItemsEqualIgnoreDurability(currItem, stack)) {
										matches++;
									}
								}
								if (componentList.size() == matches) {
									ready = 1;
									craftBook = true;
								}

							}
						}
					}
				}
				ready = craftBook ? 1 : 0;
			}
		}

		if (changedResearchState) {
			markDirty();
		}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, 0, this.getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	/**
	 * @return true if there is at least one crystal in the crystal inventory slot
	 */
	public boolean hasCrystalForFuel() {
		boolean b = (getCrystalStack() != null && !getCrystalStack().isEmpty() && getCrystalStack().getItem() instanceof ItemCrystal);
		if (b && getCrystalStack().getCount() >= getResearchCost(getCurrentSpell())) {
			return true;
		}
		return false;
	}

	//////////////////////////

	public int getResearchCost(Spell spell) {
		switch (spell.getTier()) {
			case NOVICE:
				return 1;
			case APPRENTICE:
				return 1;
			case ADVANCED:
				return 1;
			case MASTER:
				return 2;
			default:
				return 1;
		}
	}

	public void craftBook() {

		decrStackSize(ContainerScribingDesk.CRYSTAL_SLOT, 1);

		decrStackSize(ContainerScribingDesk.INGREDIENT_1_SLOT, 1);
		decrStackSize(ContainerScribingDesk.INGREDIENT_2_SLOT, 1);
		decrStackSize(ContainerScribingDesk.INGREDIENT_3_SLOT, 1);

		decrStackSize(ContainerScribingDesk.BOOK_SLOT, 1);

		ItemStack relic = getStackInSlot(ContainerScribingDesk.RELIC_SLOT);
		Spell spell = ItemRelic.getSpell(relic);
		decrStackSize(ContainerScribingDesk.RELIC_SLOT, 1);
		decrStackSize(ContainerScribingDesk.INK_SLOT, 1);

		ItemStack spellBook = new ItemStack(ASItems.ancient_spell_book, 1, spell.metadata());
		setInventorySlotContents(ContainerScribingDesk.BOOK_SLOT, spellBook);

		if (WizardData.get(currentPlayer) != null) {

			WizardData data = WizardData.get(currentPlayer);

			if (!data.hasSpellBeenDiscovered(spell)) {
				data.discoverSpell(spell);
				data.sync();
				if (!world.isRemote) {
					player.sendMessage(new TextComponentTranslation("spell.discover", spell.getNameForTranslationFormatted()));
				}
			}
		}

	}

	///////////////////////// IInventory field implementations /////////////////////////

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory() {
		return 7;
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
		ItemStack itemstack = ItemStackHelper.getAndSplit(this.inventory, slot, amount);
		if (!itemstack.isEmpty()) {
			this.markDirty();
		}
		return itemstack;
	}

	/**
	 * Removes a stack from the given slot and returns it.
	 */
	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(this.furnaceItemStacks, index);
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
		if (slot == ContainerScribingDesk.CRYSTAL_SLOT) {
			//NOOP
		}
		if (slot == ContainerScribingDesk.BOOK_SLOT) {
			researchProgress = 0;
			this.currentHintId = 0;
			this.ready = 0;
		}

		markDirty();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		boolean withinDistance = world.getTileEntity(pos) == this && player.getDistanceSqToCenter(pos) < 64;
		return withinDistance && (!inUse || (player == getCurrentPlayer()));
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
		setPlayerWizardData(player);
	}

	public void setPlayerWizardData(EntityPlayer player) {
		this.playerWizardData = WizardData.get(player);
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		this.setInUse(false);
		this.setCurrentPlayer(null);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
	 * guis use Slot.isItemValid
	 */
	@Override
	public boolean isItemValidForSlot(int slotNumber, ItemStack stack) {

		if (stack == ItemStack.EMPTY)
			return true;

		if (slotNumber == ContainerScribingDesk.CRYSTAL_SLOT) {
			return stack.getItem() instanceof ItemCrystal;
		} else if (slotNumber == ContainerScribingDesk.BOOK_SLOT) {
			return stack.getItem() instanceof ItemRelic || stack.getItem() instanceof ItemSpellBook || stack.getItem() instanceof ItemScroll;
		}
		return false;
	}

	public int getFieldCount() {
		return 3;
	}

	public int getField(int id) {
		switch (id) {
			case 0:
				return this.researchProgress;
			case 1:
				return this.researchDuration;
			case 2:
				return this.ready;
			default:
				return 0;
		}
	}

	public void setField(int id, int value) {
		switch (id) {
			case 0:
				this.researchProgress = value;
				break;
			case 1:
				this.researchDuration = value;
				break;
			case 2:
				this.ready = value;
				break;
		}
	}

	@Override
	public void clear() {
		for (int i = 0; i < getSizeInventory(); i++) {
			setInventorySlotContents(i, ItemStack.EMPTY);
		}
	}

	///////////////////////// IInventory field implementations /////////////////////////
}
