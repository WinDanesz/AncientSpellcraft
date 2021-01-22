package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
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
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class AttireAlteration extends Spell {

	private static final int REPAIR_RATE = 1; // per every 100 tick

	public static final IStoredVariable<ItemStack> HEAD_SLOT = IStoredVariable.StoredVariable.ofItemStack("attire_head_slot", Persistence.ALWAYS).withTicker(AttireAlteration::update);
	public static final IStoredVariable<ItemStack> CHEST_SLOT = IStoredVariable.StoredVariable.ofItemStack("attire_chest_slot", Persistence.ALWAYS).withTicker(AttireAlteration::update);
	public static final IStoredVariable<ItemStack> LEGS_SLOT = IStoredVariable.StoredVariable.ofItemStack("attire_legs_slot", Persistence.ALWAYS).withTicker(AttireAlteration::update);
	public static final IStoredVariable<ItemStack> FEET_SLOT = IStoredVariable.StoredVariable.ofItemStack("attire_feet_slot", Persistence.ALWAYS).withTicker(AttireAlteration::update);

	public AttireAlteration() {
		super(AncientSpellcraft.MODID, "attire_alteration", EnumAction.BLOCK, false);
		WizardData.registerStoredVariables(HEAD_SLOT, CHEST_SLOT, LEGS_SLOT, FEET_SLOT);
	}

	@Override
	public boolean cast(World world, EntityPlayer player, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		WizardData data = WizardData.get(player);

		// Fixes the sound not playing in first person.
		if (world.isRemote)
			this.playSound(world, player, ticksInUse, -1, modifiers);

		// Only works when the caster is in the same dimension.
		if (data != null) {

			ItemStack storedHead = data.getVariable(HEAD_SLOT);
			ItemStack storedChest = data.getVariable(CHEST_SLOT);
			ItemStack storedLegs = data.getVariable(LEGS_SLOT);
			ItemStack storedFeet = data.getVariable(FEET_SLOT);

			data.setVariable(HEAD_SLOT, player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
			data.setVariable(CHEST_SLOT, player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
			data.setVariable(LEGS_SLOT, player.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
			data.setVariable(FEET_SLOT, player.getItemStackFromSlot(EntityEquipmentSlot.FEET));

			if (storedHead != null) {
				player.setItemStackToSlot(EntityEquipmentSlot.HEAD, storedHead);

			}
			if (storedChest != null) {
				player.setItemStackToSlot(EntityEquipmentSlot.CHEST, storedChest);

			}
			if (storedLegs != null) {
				player.setItemStackToSlot(EntityEquipmentSlot.LEGS, storedLegs);

			}
			if (storedFeet != null) {
				player.setItemStackToSlot(EntityEquipmentSlot.FEET, storedFeet);

			}

			if (world.isRemote)
				this.spawnParticles(world, player, modifiers);

		}

		return true;
	}

	/**
	 * Spawns buff particles around the caster. Override to add a custom particle effect. Only called client-side.
	 */
	private void spawnParticles(World world, EntityLivingBase caster, SpellModifiers modifiers) {

		ParticleBuilder.create(ParticleBuilder.Type.BUFF).entity(caster).clr(1, 72, 0).spawn(world);
	}

	private static ItemStack update(EntityPlayer player, ItemStack stack) {
		if (!player.world.isRemote && player.ticksExisted % 200 == 0) {

			if (ItemArtefact.isArtefactActive(player, AncientSpellcraftItems.charm_enchanted_needle)) {

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
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
