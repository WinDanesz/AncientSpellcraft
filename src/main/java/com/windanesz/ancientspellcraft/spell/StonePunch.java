package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.item.ItemStoneFist;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.item.IConjuredItem;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellConjuration;
import electroblob.wizardry.util.InventoryUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class StonePunch extends SpellConjuration {

	public StonePunch() {
		super(AncientSpellcraft.MODID, "stone_punch", AncientSpellcraftItems.stone_fist);
		addProperties(DAMAGE);
	}

	@Override
	protected void spawnParticles(World world, EntityLivingBase caster, SpellModifiers modifiers) {

		for (int i = 0; i < 10; i++) {
			double x = caster.posX + world.rand.nextDouble() * 2 - 1;
			double y = caster.getEntityBoundingBox().minY + caster.getEyeHeight() - 0.5 + world.rand.nextDouble();
			double z = caster.posZ + world.rand.nextDouble() * 2 - 1;

			ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x, y, z).time(6).vel(caster.world.rand.nextGaussian() / 40, caster.world.rand.nextDouble() / 40,
					caster.world.rand.nextGaussian() / 40).clr(0, 0, 0).collide(false).scale(0.5F).spawn(caster.world);
		}
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (caster.getHeldItemMainhand().getItem() instanceof ItemStoneFist) {
			return false;
		}

		if (hand != EnumHand.OFF_HAND) {
			if (!world.isRemote)
				caster.sendStatusMessage(new TextComponentTranslation("spell." + getRegistryName() + ".wrong_hand"), true);
			return false;
		}
		if (caster.getHeldItemMainhand() != ItemStack.EMPTY) {
			if (!world.isRemote)
				caster.sendStatusMessage(new TextComponentTranslation("spell." + getRegistryName() + ".full_hand"), true);
			return false;
		}

		return super.cast(world, caster, hand, ticksInUse, modifiers);
	}


	/** Adds this spell's item to the given player's inventory, placing it in the main hand if the main hand is empty.
	 * Returns true if the item was successfully added to the player's inventory, false if there as no space or if the
	 * player already had the item. Override to add special conjuring behaviour. */
	protected boolean conjureItem(EntityPlayer caster, SpellModifiers modifiers){

		ItemStack stack = new ItemStack(item);

		if(InventoryUtils.doesPlayerHaveItem(caster, item)) return false;

		IConjuredItem.setDamageMultiplier(stack, modifiers.get(SpellModifiers.POTENCY));

		addItemExtras(caster, stack, modifiers);

		if(caster.getHeldItemMainhand().isEmpty()){
			caster.setHeldItem(EnumHand.MAIN_HAND, stack);
		}else{
			if(!caster.inventory.addItemStackToInventory(stack)) return false;
		}

		return true;
	}

}
