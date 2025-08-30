package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class AttireAlteration extends Spell {

	private static final IStoredVariable<ItemStack> HEAD_SLOT = IStoredVariable.StoredVariable.ofItemStack("attire_head_slot", Persistence.ALWAYS).withTicker(AttireAlteration::update);
	private static final IStoredVariable<ItemStack> CHEST_SLOT = IStoredVariable.StoredVariable.ofItemStack("attire_chest_slot", Persistence.ALWAYS).withTicker(AttireAlteration::update);
	private static final IStoredVariable<ItemStack> LEGS_SLOT = IStoredVariable.StoredVariable.ofItemStack("attire_legs_slot", Persistence.ALWAYS).withTicker(AttireAlteration::update);
	private static final IStoredVariable<ItemStack> FEET_SLOT = IStoredVariable.StoredVariable.ofItemStack("attire_feet_slot", Persistence.ALWAYS).withTicker(AttireAlteration::update);
	
	// Wardrobe variables for 5 sets of apparel
	private static final IStoredVariable<NBTTagCompound> WARDROBE_SET_1 = IStoredVariable.StoredVariable.ofNBT("wardrobe_set_1", Persistence.ALWAYS);
	private static final IStoredVariable<NBTTagCompound> WARDROBE_SET_2 = IStoredVariable.StoredVariable.ofNBT("wardrobe_set_2", Persistence.ALWAYS);
	private static final IStoredVariable<NBTTagCompound> WARDROBE_SET_3 = IStoredVariable.StoredVariable.ofNBT("wardrobe_set_3", Persistence.ALWAYS);
	private static final IStoredVariable<NBTTagCompound> WARDROBE_SET_4 = IStoredVariable.StoredVariable.ofNBT("wardrobe_set_4", Persistence.ALWAYS);
	private static final IStoredVariable<NBTTagCompound> WARDROBE_SET_5 = IStoredVariable.StoredVariable.ofNBT("wardrobe_set_5", Persistence.ALWAYS);
	private static final IStoredVariable<Integer> CURRENT_WARDROBE_SET = IStoredVariable.StoredVariable.ofInt("current_wardrobe_set", Persistence.ALWAYS);

	public AttireAlteration() {
		super(AncientSpellcraft.MODID, "attire_alteration", EnumAction.BLOCK, false);
		WizardData.registerStoredVariables(HEAD_SLOT, CHEST_SLOT, LEGS_SLOT, FEET_SLOT, 
			WARDROBE_SET_1, WARDROBE_SET_2, WARDROBE_SET_3, WARDROBE_SET_4, WARDROBE_SET_5, CURRENT_WARDROBE_SET);
	}

	@Override
	public boolean cast(World world, EntityPlayer player, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (world.isRemote) {
			this.spawnParticles(world, player, modifiers);
			this.playSound(world, player, ticksInUse, -1, modifiers);
		}
		return swapArmour(player, world, modifiers, ticksInUse);
	}

	public static boolean swapArmour(EntityPlayer player, World world, SpellModifiers modifiers, int ticksInUse) {
		WizardData data = WizardData.get(player);

		if (data != null) {
			// Check if player has the wardrobe charm
			boolean hasWardrobe = ItemArtefact.isArtefactActive(player, ASItems.charm_wardrobe);
			
			if (hasWardrobe) {
				// Use wardrobe system
				return swapWardrobeSet(player, data, world);
			} else {
				// Use original single set system
				return swapSingleSet(player, data);
			}
		}

		return true;
	}
	
	private static boolean swapWardrobeSet(EntityPlayer player, WizardData data, World world) {
		// Get current set number
		Integer currentSet = data.getVariable(CURRENT_WARDROBE_SET);
		if (currentSet == null) currentSet = 0;
		
		// Get the next set number (cycle through 0-4)
		int nextSet = (currentSet + 1) % 5;
		
		// Save current equipment to the current wardrobe set
		saveToWardrobeSet(data, currentSet, player);
		
		// Update the current set to the next set
		data.setVariable(CURRENT_WARDROBE_SET, nextSet);
		
		// Load equipment from the next set
		loadFromWardrobeSet(data, nextSet, player);
		
		return true;
	}
	
	private static boolean swapSingleSet(EntityPlayer player, WizardData data) {
		ItemStack storedHead = getStack(HEAD_SLOT, data);
		ItemStack storedChest = getStack(CHEST_SLOT, data);
		ItemStack storedLegs = getStack(LEGS_SLOT, data);
		ItemStack storedFeet = getStack(FEET_SLOT, data);

		data.setVariable(HEAD_SLOT, player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
		data.setVariable(CHEST_SLOT, player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
		data.setVariable(LEGS_SLOT, player.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
		data.setVariable(FEET_SLOT, player.getItemStackFromSlot(EntityEquipmentSlot.FEET));

		player.setItemStackToSlot(EntityEquipmentSlot.HEAD, storedHead);
		player.setItemStackToSlot(EntityEquipmentSlot.CHEST, storedChest);
		player.setItemStackToSlot(EntityEquipmentSlot.LEGS, storedLegs);
		player.setItemStackToSlot(EntityEquipmentSlot.FEET, storedFeet);
		
		return true;
	}
	
	private static void saveToWardrobeSet(WizardData data, int setNumber, EntityPlayer player) {
		NBTTagCompound setData = new NBTTagCompound();
		
		// Save current equipment (including empty slots)
		ItemStack head = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		ItemStack legs = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
		ItemStack feet = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
		
		// Save all slots, including empty ones
		NBTTagCompound headTag = new NBTTagCompound();
		head.writeToNBT(headTag);
		setData.setTag("head", headTag);
		
		NBTTagCompound chestTag = new NBTTagCompound();
		chest.writeToNBT(chestTag);
		setData.setTag("chest", chestTag);
		
		NBTTagCompound legsTag = new NBTTagCompound();
		legs.writeToNBT(legsTag);
		setData.setTag("legs", legsTag);
		
		NBTTagCompound feetTag = new NBTTagCompound();
		feet.writeToNBT(feetTag);
		setData.setTag("feet", feetTag);
		
		// Save to appropriate wardrobe set
		switch (setNumber) {
			case 0: data.setVariable(WARDROBE_SET_1, setData); break;
			case 1: data.setVariable(WARDROBE_SET_2, setData); break;
			case 2: data.setVariable(WARDROBE_SET_3, setData); break;
			case 3: data.setVariable(WARDROBE_SET_4, setData); break;
			case 4: data.setVariable(WARDROBE_SET_5, setData); break;
		}
	}
	
	private static void loadFromWardrobeSet(WizardData data, int setNumber, EntityPlayer player) {
		NBTTagCompound setData = null;
		
		// Load from appropriate wardrobe set
		switch (setNumber) {
			case 0: setData = data.getVariable(WARDROBE_SET_1); break;
			case 1: setData = data.getVariable(WARDROBE_SET_2); break;
			case 2: setData = data.getVariable(WARDROBE_SET_3); break;
			case 3: setData = data.getVariable(WARDROBE_SET_4); break;
			case 4: setData = data.getVariable(WARDROBE_SET_5); break;
		}
		
		if (setData != null) {
			// Load equipment from the set
			if (setData.hasKey("head")) {
				ItemStack head = new ItemStack(setData.getCompoundTag("head"));
				player.setItemStackToSlot(EntityEquipmentSlot.HEAD, head);
			} else {
				player.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
			}
			
			if (setData.hasKey("chest")) {
				ItemStack chest = new ItemStack(setData.getCompoundTag("chest"));
				player.setItemStackToSlot(EntityEquipmentSlot.CHEST, chest);
			} else {
				player.setItemStackToSlot(EntityEquipmentSlot.CHEST, ItemStack.EMPTY);
			}
			
			if (setData.hasKey("legs")) {
				ItemStack legs = new ItemStack(setData.getCompoundTag("legs"));
				player.setItemStackToSlot(EntityEquipmentSlot.LEGS, legs);
			} else {
				player.setItemStackToSlot(EntityEquipmentSlot.LEGS, ItemStack.EMPTY);
			}
			
			if (setData.hasKey("feet")) {
				ItemStack feet = new ItemStack(setData.getCompoundTag("feet"));
				player.setItemStackToSlot(EntityEquipmentSlot.FEET, feet);
			} else {
				player.setItemStackToSlot(EntityEquipmentSlot.FEET, ItemStack.EMPTY);
			}
		} else {
			// If no data exists for this set, clear all slots
			player.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
			player.setItemStackToSlot(EntityEquipmentSlot.CHEST, ItemStack.EMPTY);
			player.setItemStackToSlot(EntityEquipmentSlot.LEGS, ItemStack.EMPTY);
			player.setItemStackToSlot(EntityEquipmentSlot.FEET, ItemStack.EMPTY);
		}
	}

	private static ItemStack getStack(IStoredVariable<ItemStack> variable, WizardData data) {
		ItemStack stack = data.getVariable(variable);
		return stack != null ? stack : ItemStack.EMPTY;
	}

	/**
	 * Spawns buff particles around the caster. Override to add a custom particle effect. Only called client-side.
	 */
	private void spawnParticles(World world, EntityLivingBase caster, SpellModifiers modifiers) {

		ParticleBuilder.create(ParticleBuilder.Type.BUFF).entity(caster).clr(1, 72, 0).spawn(world);
	}

	private static ItemStack update(EntityPlayer player, ItemStack stack) {
		if (!player.world.isRemote && player.ticksExisted % 200 == 0) {

			if (ItemArtefact.isArtefactActive(player, ASItems.charm_enchanted_needle)) {

				if (stack != null && stack != ItemStack.EMPTY && stack.getItem() instanceof IManaStoringItem) {
					((IManaStoringItem) stack.getItem()).rechargeMana(stack, 2);
				}
			}
		}
		return stack;
		// Needs to be both of these interfaces because this ring only recharges wands
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
