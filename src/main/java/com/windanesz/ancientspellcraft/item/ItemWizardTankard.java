package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.client.DrawingUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemWizardTankard extends ItemASArtefact implements ITickableArtefact {

	public ItemWizardTankard(EnumRarity rarity, Type type) {
		super(rarity, type);
		setMaxDamage(100);
	}

	public void onWornTick(ItemStack stack, EntityLivingBase player) {
		if (!player.world.isRemote && player.ticksExisted % 120 == 0) {
			setMana(stack, getMana(stack) + 1);
		}
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		// Overridden to do nothing to stop repair things from 'repairing' the mana in a wand
	}

	public void setMana(ItemStack stack, int mana) {
		// Using super (which can only be done from in here) bypasses the above override
		super.setDamage(stack, getManaCapacity(stack) - mana);
	}

	public int getMana(ItemStack stack) {
		return getManaCapacity(stack) - getDamage(stack);
	}

	public int getManaCapacity(ItemStack stack) {
		return this.getMaxDamage(stack);
	}

	// Max damage is modifiable with upgrades.
	@Override
	public int getMaxDamage(ItemStack stack) {
		// + 0.5f corrects small float errors rounding down
		return super.getMaxDamage(stack);
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return DrawingUtils.mix(0xff8bfe, 0x8e2ee4, (float) getDurabilityForDisplay(stack));
	}

	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) entityLiving;
			worldIn.playSound((EntityPlayer) null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);

			if (entityplayer instanceof EntityPlayerMP) {
				CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) entityplayer, stack);
			}

			entityLiving.addPotionEffect(new PotionEffect(AncientSpellcraftPotions.mana_regeneration, 400, 7));
		}

		setMana(stack, 0);
		return stack;
	}

	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}

	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.DRINK;
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);

		if (getMana(itemstack) == 100) {
			playerIn.setActiveHand(handIn);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
		} else {
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
		}
	}
}
