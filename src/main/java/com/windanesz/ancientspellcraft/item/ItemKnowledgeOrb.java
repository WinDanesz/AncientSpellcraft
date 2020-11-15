package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class ItemKnowledgeOrb extends ItemASArtefact {

	public ItemKnowledgeOrb(EnumRarity rarity, ItemArtefact.Type type) {
		super(rarity, type);
		setMaxDamage(10);
		this.addPropertyOverride(new ResourceLocation("swirl"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return getSwirlProgress(stack);
			}
		});
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (world.getTotalWorldTime() % 5 == 0) {
			progessSwirlAnimation(stack);
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		setDamage(stack, 1);
		return EnumActionResult.SUCCESS;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return false;
	}

	@Override
	public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
		// Ignore durability changes
		if (ItemStack.areItemsEqualIgnoreDurability(oldStack, newStack))
			return true;
		return super.canContinueUsing(oldStack, newStack);
	}

	@Override
	// Only called client-side
	// This method is always called on the item in oldStack, meaning that oldStack.getItem() == this
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

		// This method does some VERY strange things! Despite its name, it also seems to affect the updating of NBT...

		if (!oldStack.isEmpty() || !newStack.isEmpty()) {
			// We only care about the situation where we specifically want the animation NOT to play.
			if (oldStack.getItem() == newStack.getItem() && !slotChanged && oldStack.getItem() instanceof ItemKnowledgeOrb
					&& newStack.getItem() instanceof ItemKnowledgeOrb)
				return false;
		}

		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}

	public boolean isSwirlAnimationInProgess(ItemStack stack) {
		return stack.isItemDamaged();
	}

	public void startSwirlAnimation(ItemStack stack) {
		setDamage(stack, 1);
	}

	private float getSwirlProgress(ItemStack stack) {
		return ((float) stack.getItemDamage() / 10);
	}

	private void progessSwirlAnimation(ItemStack stack) {
		if (getDamage(stack) == 8) {
			setDamage(stack, 0);
		} else if (stack.isItemDamaged()) {
			setDamage(stack, getDamage(stack) + 1);
		}
	}

	@Override
	public boolean isEnchantable(ItemStack stack){
		return false;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book){
		return false;
	}
}
