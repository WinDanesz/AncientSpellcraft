package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.item.IConjuredItem;
import electroblob.wizardry.registry.Spells;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Objects;

public class ItemSpectralFishingRod extends ItemFishingRod implements IConjuredItem {

	private EnumRarity rarity = EnumRarity.COMMON;

	public ItemSpectralFishingRod() {
		this.setMaxStackSize(1);
		setMaxDamage(1800);
		setNoRepair();
		setCreativeTab(null);
		addAnimationPropertyOverrides();
		this.addPropertyOverride(new ResourceLocation("cast"), new IItemPropertyGetter()
		{
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
			{
				if (entityIn == null)
				{
					return 0.0F;
				}
				else
				{
					boolean flag = entityIn.getHeldItemMainhand() == stack;
					boolean flag1 = entityIn.getHeldItemOffhand() == stack;

					if (entityIn.getHeldItemMainhand().getItem() instanceof ItemSpectralFishingRod)
					{
						flag1 = false;
					}

					return (flag || flag1) && entityIn instanceof EntityPlayer && ((EntityPlayer)entityIn).fishEntity != null ? 1.0F : 0.0F;
				}
			}
		});
	}

	@Override
	public EnumRarity getRarity(ItemStack stack){
		return rarity;
	}

	@Override
	public int getMaxDamage(ItemStack stack){
		return this.getMaxDamageFromNBT(stack, Spells.conjure_sword);
	}


	/**
	 * Returns True is the item is renderer in full 3D when hold.
	 */
	@SideOnly(Side.CLIENT)
	public boolean isFull3D()
	{
		return true;
	}

	/**
	 * Returns true if this item should be rotated by 180 degrees around the Y axis when being held in an entities
	 * hands.
	 * Doesn't seem to work for non vanilla items
	 */
	@SideOnly(Side.CLIENT)
	public boolean shouldRotateAroundWhenRendering()
	{
		return false;
	}

	/**
	 * Called when the equipped item is right clicked.
	 */
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);

		if (playerIn.fishEntity != null) {
			int i = playerIn.fishEntity.handleHookRetraction();
			itemstack.damageItem(i, playerIn);
			playerIn.swingArm(handIn);
			worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
		} else {
			worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if (!worldIn.isRemote) {
				EntityFishHook entityfishhook = new EntityFishHook(worldIn, playerIn);
				int j = EnchantmentHelper.getFishingSpeedBonus(itemstack);      //   return getEnchantmentLevel(Enchantments.LURE, p_191528_0_);

				if (j > 0) {
					entityfishhook.setLureSpeed(j);
				}

				int k = EnchantmentHelper.getFishingLuckBonus(itemstack); // return getEnchantmentLevel(Enchantments.LUCK_OF_THE_SEA, p_191529_0_);

				if (k > 0) {
					entityfishhook.setLuck(k);
				}

				worldIn.spawnEntity(entityfishhook);
			}

			playerIn.swingArm(handIn);
			playerIn.addStat(Objects.requireNonNull(StatList.getObjectUseStats(this)));
		}

		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
	}

	/// standard conjured item overrides ///

	@Override
	// This method allows the code for the item's timer to be greatly simplified by damaging it directly from
	// onUpdate() and removing the workaround that involved WizardData and all sorts of crazy stuff.
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

		if (!oldStack.isEmpty() || !newStack.isEmpty()) {
			// We only care about the situation where we specifically want the animation NOT to play.
			if (oldStack.getItem() == newStack.getItem() && !slotChanged)
				return false;
		}

		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		int damage = stack.getItemDamage();
		if (damage > stack.getMaxDamage())
			entity.replaceItemInInventory(slot, ItemStack.EMPTY);
		stack.setItemDamage(damage + 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public boolean getIsRepairable(ItemStack stack, ItemStack par2ItemStack) {
		return false;
	}

	@Override
	public int getItemEnchantability() {
		return 0;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

	// Cannot be dropped
	@Override
	public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
		return false;
	}

}
