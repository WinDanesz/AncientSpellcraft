package com.windanesz.ancientspellcraft.tileentity;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.client.gui.ContainerSphereCognizance;
import com.windanesz.ancientspellcraft.item.ItemMoonLetterDictionary;
import com.windanesz.ancientspellcraft.item.ItemRelic;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.event.DiscoverSpellEvent;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemCrystal;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.NBTExtras;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class TileSphereCognizance extends TileEntity implements IInventory, ITickable {
	private NonNullList<ItemStack> inventory;

	private int researchDuration;
	private int researchProgress;
	//	private int researchCompleted = 0;
	public int currentHintId = 0;
	public int currentHintTypeId = 0;

	private boolean changedResearchState = false;

	private boolean inUse = false;
	private EntityPlayer currentPlayer;

	public WizardData getPlayerWizardData() {
		return playerWizardData;
	}

	private WizardData playerWizardData;

	private NonNullList<ItemStack> furnaceItemStacks = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);

	public int tickCount;
	public float pageFlip;
	public float pageFlipPrev;
	public float flipT;
	public float flipA;
	public float bookSpread;
	public float sphereRotation;
	public float sphereRotationPrev;
	public float tRot;

	public TileSphereCognizance() {
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
		currentHintTypeId = compound.getShort("currentHintTypeId");
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
		compound.setInteger("currentHintTypeId", (short) this.currentHintTypeId);

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

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB bb = INFINITE_EXTENT_AABB;
		Block type = getBlockType();
		if (type == ASBlocks.SPHERE_COGNIZANCE) {
			bb = new AxisAlignedBB(pos, pos.add(1, 1, 1));
		} else if (type != null) {
			AxisAlignedBB cbb = this.getWorld().getBlockState(pos).getBoundingBox(world, pos);
			if (cbb != null) {
				bb = cbb;
			}
		}
		return bb;
	}

	private boolean canBeginResearch() {
		return (isResearchFinished() || researchProgress == 0) && hasSomethingToResearch() && hasCrystalForFuel();
	}

	private boolean isResearchFinished() {
		return (researchProgress != 0 && researchProgress == researchDuration);
	}

	private boolean isCurrentBookKnown() {
		if (getCurrentSpell() != null) {
			return playerWizardData.hasSpellBeenDiscovered(getCurrentSpell());
		}
		return false;
	}

	public Spell getCurrentSpell() {
		if (getInputStack().getItemDamage() != OreDictionary.WILDCARD_VALUE) {
			return Spell.byMetadata(getInputStack().getItemDamage());
		}
		return null;
	}

	public ItemStack getCrystalStack() {
		return inventory.get(0);
	}

	public ItemStack getInputStack() {
		return inventory.get(1);
	}

	// this function indicates whether container texture should be drawn
	@SideOnly(Side.CLIENT)
	public static boolean func_174903_a(IInventory parIInventory) {
		return true;
	}

	@SuppressWarnings("Duplicates")
	public void update() {
		this.sphereRotationPrev = this.sphereRotation;
		EntityPlayer entityplayer = this.world.getClosestPlayer((double) ((float) this.pos.getX() + 0.5F), (double) ((float) this.pos.getY() + 0.5F), (double) ((float) this.pos.getZ() + 0.5F), 2.5D, false);

		this.tRot += 0.02F;
		this.bookSpread -= 0.1F;
		//		}

		while (this.sphereRotation >= (float) Math.PI) {
			this.sphereRotation -= ((float) Math.PI * 2F);
		}

		while (this.sphereRotation < -(float) Math.PI) {
			this.sphereRotation += ((float) Math.PI * 2F);
		}

		while (this.tRot >= (float) Math.PI) {
			this.tRot -= ((float) Math.PI * 2F);
		}

		while (this.tRot < -(float) Math.PI) {
			this.tRot += ((float) Math.PI * 2F);
		}

		float f2;

		for (f2 = this.tRot - this.sphereRotation; f2 >= (float) Math.PI; f2 -= ((float) Math.PI * 2F)) {
			;
		}

		while (f2 < -(float) Math.PI) {
			f2 += ((float) Math.PI * 2F);
		}

		this.sphereRotation += f2 * 0.4F;

		if (getInputStack().isEmpty()) {
			researchProgress = 0;
			setResearchDuration(0);
		}

		if (!world.isRemote) {

			if (inUse && currentPlayer != null) {

				if (hasSomethingToResearch()) {

					if (shouldReseach()) {
						progressResearch();
						if (researchProgress >= researchDuration) {
							onResearchComplete();
						}
						changedResearchState = true;
					}

				} else {
					researchProgress = 0;
				}
			}
		}

		if (changedResearchState) {
			markDirty();
		}
	}

	public void setResearchDuration() {
		if (getInputStack().getItem() instanceof ItemRelic) {
			researchDuration = 80;
		} else {
			researchDuration = getResearchDuration(getCurrentSpell());
		}
		this.markDirty();
	}

	public void setResearchDuration(int duration) {
		researchDuration = duration;
		this.markDirty();
	}

	public boolean shouldReseach() {
		return (researchProgress != 0 && researchDuration > researchProgress);
	}

	public void progressResearch() {
		researchProgress++;
	}

	public void onResearchComplete() {
		double special = AncientSpellcraft.rand.nextDouble();
		boolean spellItem = getInputStack().getItem() instanceof ItemSpellBook || getInputStack().getItem() instanceof ItemScroll;

		// relic item
		if (!spellItem) {

			this.currentHintTypeId = 24;
			int count = ContainerSphereCognizance.HINTS_COUNT.get("ancient_knowledge");
			this.currentHintId = ASUtils.randIntBetween(1, count);

			if (currentPlayer != null && !world.isRemote) {

				if (ItemArtefact.isArtefactActive(currentPlayer, ASItems.charm_stone_tablet) && ItemMoonLetterDictionary.isFullMoon(currentPlayer.world)) {
					ItemRelic.setRelicType(getInputStack(), currentPlayer, ItemRelic.RelicType.SPELL);
				}
				ItemRelic.setRandomContentType(getInputStack(), currentPlayer, null);
			}

			markDirty();
			return;
		}

		if (special < Settings.generalSettings.sphere_spell_identification_chance) {
			// discover spell

			this.currentHintTypeId = 2; // discovered
			int count = ContainerSphereCognizance.HINTS_COUNT.get("discovered");
			int id = ASUtils.randIntBetween(1, count);
			this.currentHintId = id;

			if (!MinecraftForge.EVENT_BUS.post(new DiscoverSpellEvent(getCurrentPlayer(), getCurrentSpell(),
					DiscoverSpellEvent.Source.OTHER))) {
				// Identification scrolls give the chat readout in creative mode, otherwise it looks like
				// nothing happens!
				if (getPlayerWizardData().discoverSpell(getCurrentSpell()) && !world.isRemote) {
					playerWizardData.sync();
				}

				getCurrentPlayer().playSound(WizardrySounds.MISC_DISCOVER_SPELL, 1.25f, 1);
				if (!world.isRemote)
					getCurrentPlayer().sendMessage(new TextComponentTranslation("spell.discover",
							getCurrentSpell().getNameForTranslationFormatted()));
				setPlayerWizardData(currentPlayer);
			}

		} else if (special < 0.4) {
			// failed attempt
			this.currentHintTypeId = 1; // failed
			int count = ContainerSphereCognizance.HINTS_COUNT.get("failed");
			int id = ASUtils.randIntBetween(1, count);

			this.currentHintId = id;
		} else {

			Spell spell = getCurrentSpell();
			String name = spell.getUnlocalisedName();
			String type = spell.getType().getUnlocalisedName();
			String element = spell.getElement().getName();

			boolean t = ContainerSphereCognizance.HINT_TYPES.contains(type);
			boolean n = ContainerSphereCognizance.HINT_TYPES.contains(name);
			boolean e = ContainerSphereCognizance.HINT_TYPES.contains(element);

			List<String> list = new ArrayList<String>() {};

			if (n) {
				int i = ContainerSphereCognizance.HINTS_COUNT.get(name);
				list.add(name);
			}
			if (t) {
				int i = ContainerSphereCognizance.HINTS_COUNT.get(type);
				list.add(type);
			}
			if (e) {
				int i = ContainerSphereCognizance.HINTS_COUNT.get(element);
				list.add(element);
			}

			String selected = ASUtils.getRandomListItem(list);
			int count = ContainerSphereCognizance.HINTS_COUNT.get(selected);
			int id = ASUtils.randIntBetween(1, count);
			String string = "gui.ancientspellcraft:sphere_cognizance.hint." + selected + "." + id;
			this.currentHintTypeId = ContainerSphereCognizance.HINT_TYPES.indexOf(selected);
			this.currentHintId = id;

		}
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

	public boolean hasSomethingToResearch() {
		Item item = getInputStack().getItem();
		return (!getInputStack().isEmpty() && (((item == ASItems.stone_tablet_small || item == ASItems.stone_tablet ||
				item == ASItems.stone_tablet_large || item == ASItems.stone_tablet_grand)
				&& !ItemRelic.isResearched(getInputStack())) || (item instanceof ItemSpellBook ||
				item instanceof ItemScroll && !isCurrentBookKnown())));
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

	public static int getResearchDuration(Spell spell) {
		switch (spell.getTier()) {
			case NOVICE:
				return 100;
			case APPRENTICE:
				return 150;
			case ADVANCED:
				return 200;
			case MASTER:
				return 250;
			default:
				return 10;
		}
	}

	public void attemptBeginResearch() {
		if (canBeginResearch()) {

			this.currentHintTypeId = 0;
			this.currentHintId = 1;

			researchProgress = 1;
			if (getInputStack().getItem() instanceof ItemSpellBook || getInputStack().getItem() instanceof ItemScroll) {
				int researchcost = getResearchCost(getCurrentSpell());
				getCrystalStack().shrink(researchcost);
			} else {
				// relic item
				getCrystalStack().shrink(2);
			}
		}
	}

	public boolean shouldDisplayHint() {
		return researchDuration != 0 && researchDuration == researchProgress;
	}

	///////////////////////// IInventory field implementations /////////////////////////

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory() {
		return 2;
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
		if (slot == ContainerSphereCognizance.CRYSTAL_SLOT) {
			//NOOP
		}
		if (slot == ContainerSphereCognizance.BOOK_SLOT) {
			researchProgress = 0;
			setResearchDuration();
			this.currentHintId = 0;
			this.currentHintTypeId = 0;
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

		if (slotNumber == ContainerSphereCognizance.CRYSTAL_SLOT) {
			return stack.getItem() instanceof ItemCrystal;
		} else if (slotNumber == ContainerSphereCognizance.BOOK_SLOT) {
			return stack.getItem() instanceof ItemRelic || stack.getItem() instanceof ItemSpellBook || stack.getItem() instanceof ItemScroll;
		}
		return false;
	}

	public int getFieldCount() {
		return 4;
	}

	public int getField(int id) {
		switch (id) {
			case 0:
				return this.researchProgress;
			case 1:
				return this.researchDuration;
			case 2:
				return this.currentHintTypeId;
			case 3:
				return this.currentHintId;
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
				this.currentHintTypeId = value;
				break;
			case 3:
				this.currentHintId = value;
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
