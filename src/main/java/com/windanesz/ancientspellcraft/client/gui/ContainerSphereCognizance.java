package com.windanesz.ancientspellcraft.client.gui;

import com.windanesz.ancientspellcraft.packet.PacketControlInput;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.tileentity.TileSphereCognizance;
import electroblob.wizardry.inventory.SlotItemList;
import electroblob.wizardry.registry.WizardryItems;
import net.minecraft.entity.player.EntityPlayer;
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

public class ContainerSphereCognizance extends Container {

	private int[] cachedFields;
	private int researchDuration;    // not saved to nbt
	private int researchProgress;
	public int currentHintTypeId = 0;
	public int currentHintId = 0;

	public static int CRYSTAL_SLOT = 0;
	public static int BOOK_SLOT = 1;

	public static List<String> HINT_TYPES = Arrays.asList("none", "failed", "discovered", "heal_ally", "fire", "earth", "ice", "necromancy", "healing", "lightning", "sorcery", "ancient", "buff", "attack", "projectile",
			"defense", "utility", "construct", "minion", "alteration", "pocket_furnace", "arcane_lock", "remove_curse", "resurrection", "ancient_knowledge");

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

	private TileSphereCognizance tileSphereCognizance;

	public ContainerSphereCognizance(EntityPlayer player, IInventory playerInv, TileSphereCognizance te) {
		this.tileSphereCognizance = te;
		te.openInventory(player);

		// crystal slot
		this.addSlotToContainer(new SlotItemList(te, 0, 14, 100 + 26, 64, WizardryItems.magic_crystal, WizardryItems.crystal_shard, WizardryItems.grand_crystal));

		/// book
		this.addSlotToContainer(new SlotItemList(te, 1, 62 + 17, 100 + 15, 1,
				WizardryItems.spell_book,
				WizardryItems.scroll,
				ASItems.ancient_spellcraft_spell_book,
				ASItems.ancient_spellcraft_scroll,
				ASItems.stone_tablet_small,
				ASItems.stone_tablet,
				ASItems.stone_tablet_large,
				ASItems.stone_tablet_grand,
				ASItems.ancient_mana_flask,
				ASItems.ancient_bound_stone
		));

		int n = 75;

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
		return this.tileSphereCognizance.isUsableByPlayer(playerIn);
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

			if (index < this.tileSphereCognizance.getSizeInventory()) {
				if (!this.mergeItemStack(itemstack1, this.tileSphereCognizance.getSizeInventory(), this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, this.tileSphereCognizance.getSizeInventory(), false)) {
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
		boolean[] fieldHasChanged = new boolean[tileSphereCognizance.getFieldCount()];
		if (cachedFields == null) {
			cachedFields = new int[tileSphereCognizance.getFieldCount()];
			allFieldsHaveChanged = true;
		}
		for (int i = 0; i < cachedFields.length; ++i) {
			if (allFieldsHaveChanged || cachedFields[i] != tileSphereCognizance.getField(i)) {
				cachedFields[i] = tileSphereCognizance.getField(i);
				fieldHasChanged[i] = true;
			}
		}

		// go through the list of listeners (players using this container) and update them if necessary
		for (IContainerListener listener : this.listeners) {
			for (int fieldID = 0; fieldID < tileSphereCognizance.getFieldCount(); ++fieldID) {
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
		this.tileSphereCognizance.closeInventory(playerIn);
	}

	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		this.tileSphereCognizance.setField(id, data);
	}

	/**
	 * Called (via {@link PacketControlInput PacketControlInput}) when the apply button in
	 * the sphere of cognizance GUI is pressed.
	 */
	@SuppressWarnings("unused")
	public void onApplyButtonPressed() {
		tileSphereCognizance.attemptBeginResearch();
	}
}

