package com.windanesz.ancientspellcraft.client.gui;

import com.windanesz.ancientspellcraft.packet.PacketControlInput;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.tileentity.TileScribingDesk;
import electroblob.wizardry.inventory.SlotItemList;
import electroblob.wizardry.registry.WizardryItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class ContainerScribingDesk extends Container {

	//	private int cookTime;
	//	private int totalCookTime;
	//	private int furnaceBurnTime;
	//	private int currentItemBurnTime;

	// These store cache values, used by the server to only update the client side tile entity when values have changed

	private int[] cachedFields;

	private int researchDuration;    // not saved to nbt
	private int researchProgress;
	public int currentHintTypeId = 0;
	public int currentHintId = 0;

	public static int INGREDIENT_1_SLOT = 0;
	public static int INGREDIENT_2_SLOT = 1;
	public static int INGREDIENT_3_SLOT = 2;
	public static int CRYSTAL_SLOT = 3;
	public static int RELIC_SLOT = 4;
	public static int INK_SLOT = 5;
	public static int BOOK_SLOT = 6;

	public static List<String> HINT_TYPES = Arrays.asList("none", "failed", "discovered", "heal_ally", "fire", "earth", "ice", "necromancy", "healing", "lightning", "sorcery", "ancient", "buff", "attack", "projectile",
			"defense", "utility", "construct", "minion", "alteration", "pocket_furnace", "arcane_lock", "remove_curse", "resurrection", "ancient_knowledge");

	//	if ((HINT_TYPES.contains("utility"))) {
	//		HINT_TYPES.indexOf("utility")}
	//

	public static final LinkedHashMap<String, Integer> HINTS_COUNT = new LinkedHashMap<String, Integer>() {
		{
			put("none", 1);
			put("failed", 9);
			put("discovered", 10);
			put("fire", 6);
			put("earth", 5);
			put("ice", 6);
			put("necromancy", 5);
			put("healing", 4);
			put("lightning", 4);
			put("sorcery", 5);
			put("ancient", 7);
			put("buff", 4);
			put("attack", 4);
			put("projectile", 4);
			put("defense", 4);
			put("utility", 5);
			put("construct", 4);
			put("minion", 4);
			put("alteration", 4);
			put("heal_ally", 4);
			put("pocket_furnace", 4);
			put("arcane_lock", 4);
			put("remove_curse", 4);
			put("resurrection", 5);
			put("ancient_knowledge", 4);
		}
	};

	//	public static final ResourceLocation EMPTY_SLOT_CRYSTAL = new ResourceLocation(Wizardry.MODID, "gui/empty_slot_crystal");
	//
	//	public TileCrystalBallCognizance tileCrystalBallCognizance;
	//
	//	public ContainerCrystalBallCognizance(IInventory inventory, TileCrystalBallCognizance tileentity) {
	//		this.tileCrystalBallCognizance = tileentity;
	//
	//		this.addSlotToContainer(new SlotItemList(tileentity, 0, 13, 101, 64,
	//				WizardryItems.magic_crystal, WizardryItems.crystal_shard, WizardryItems.grand_crystal))
	//				.setBackgroundName(EMPTY_SLOT_CRYSTAL.toString());
	//	}
	//

	private TileScribingDesk TileScribingDesk;

	public ContainerScribingDesk(EntityPlayer player, IInventory playerInv, TileScribingDesk te) {
		this.TileScribingDesk = te;
		te.openInventory(player);

		/// reagent /Ingredient
		this.addSlotToContainer(new Slot(te, 0, 54, 35 + 26));
		this.addSlotToContainer(new Slot(te, 1, 80, 35 + 26));
		this.addSlotToContainer(new Slot(te, 2, 106, 35 + 26));

		// crystal fuel slot
		this.addSlotToContainer(new SlotItemList(te, 3, 19, 35 + 26, 64,
				WizardryItems.magic_crystal,
				WizardryItems.grand_crystal));

		// relic slot
		this.addSlotToContainer(new SlotItemList(te, 4, 19, 28, 64,
				AncientSpellcraftItems.stone_tablet_small,
				AncientSpellcraftItems.stone_tablet,
				AncientSpellcraftItems.stone_tablet_large,
				AncientSpellcraftItems.stone_tablet_grand));


		// ink slot
		this.addSlotToContainer(new SlotItemList(te, 5, 141, 38 , 64, Items.DYE));

		/// book
		this.addSlotToContainer(new SlotItemList(te, 6, 62 + 18, 28, 1, Items.BOOK));


		int n = 19;

		// Player Inventory, Slot 9-35, Slot IDs 9-35
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, n + 84 + y * 18));
			}
		}

		// Player Inventory, Slot 0-8, Slot IDs 36-44
		for (int x = 0; x < 9; ++x) {
			this.addSlotToContainer(new Slot(playerInv, x, 8 + x * 18, n + 142));
		}
	}

	/**
	 * Determines whether supplied player can use this container
	 */
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.TileScribingDesk.isUsableByPlayer(playerIn);
	}

	/**
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
	 * inventory and the other inventory(s).
	 */
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < this.TileScribingDesk.getSizeInventory()) {
				if (!this.mergeItemStack(itemstack1, this.TileScribingDesk.getSizeInventory(), this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, this.TileScribingDesk.getSizeInventory(), false)) {
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
		boolean fieldHasChanged[] = new boolean[TileScribingDesk.getFieldCount()];
		if (cachedFields == null) {
			cachedFields = new int[TileScribingDesk.getFieldCount()];
			allFieldsHaveChanged = true;
		}
		for (int i = 0; i < cachedFields.length; ++i) {
			if (allFieldsHaveChanged || cachedFields[i] != TileScribingDesk.getField(i)) {
				cachedFields[i] = TileScribingDesk.getField(i);
				fieldHasChanged[i] = true;
			}
		}

		// go through the list of listeners (players using this container) and update them if necessary
		for (IContainerListener listener : this.listeners) {
			for (int fieldID = 0; fieldID < TileScribingDesk.getFieldCount(); ++fieldID) {
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
		this.TileScribingDesk.closeInventory(playerIn);
	}

	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		this.TileScribingDesk.setField(id, data);
	}

	/**
	 * Called (via {@link PacketControlInput PacketControlInput}) when the apply button in
	 * the sphere of cognizance GUI is pressed.
	 */
	@SuppressWarnings("unused")
	public void onApplyButtonPressed() {
		TileScribingDesk.craftBook();
	}
}

