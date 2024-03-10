package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.block.BlockDimensionBoundary;
import com.windanesz.ancientspellcraft.block.BlockDimensionFocus;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASDimensions;
import com.windanesz.ancientspellcraft.spell.OrbSpace;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemDimensionalDisk extends ItemASArtefact {

	public ItemDimensionalDisk(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (!world.isRemote && Settings.isArtefactEnabled(this) && player.dimension == ASDimensions.POCKET_DIM_ID) {
			if (world.getBlockState(player.getPosition().down()).getBlock() instanceof BlockDimensionFocus) {
				int currentRadius = 0;
				IBlockState oldBlock = null;
				for (int i = 0; i < 40; i++) {
					oldBlock = world.getBlockState(player.getPosition().offset(EnumFacing.NORTH, i));
					if (!(oldBlock.getBlock() instanceof BlockDimensionBoundary)) {
						currentRadius++;
					} else {
						break;
					}
				}

				if (currentRadius != 0) {
					player.getCooldownTracker().setCooldown(this, 40);

					player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 40));
					for (int i = currentRadius; i < currentRadius + 3; i++) {
					OrbSpace.createPocket(player.getPosition().down(), world, Blocks.AIR.getDefaultState(), oldBlock.getBlock() == ASBlocks.DIMENSION_BOUNDARY ?
							ASBlocks.DIMENSION_FOCUS.getDefaultState() : ASBlocks.DIMENSION_FOCUS_GOLD.getDefaultState(), currentRadius );
					}
					for (int i = currentRadius + 1; i <= currentRadius + 3; i++) {
						OrbSpace.createPocket(player.getPosition().down(), world, oldBlock, oldBlock.getBlock() == ASBlocks.DIMENSION_BOUNDARY ?
								ASBlocks.DIMENSION_FOCUS.getDefaultState() : ASBlocks.DIMENSION_FOCUS_GOLD.getDefaultState(),  i);
					}
					stack.shrink(1);
				}
			} else {
				ASUtils.sendMessage(player, "You must stand on the Dimensional Focus (center) of the pocket dimension!", true);
			}
		}

		return new ActionResult<>(EnumActionResult.FAIL, stack);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, world, entity, itemSlot, isSelected);

		if (world.isRemote && entity.dimension == ASDimensions.POCKET_DIM_ID && entity instanceof EntityPlayer && ((EntityPlayer) entity).getHeldItemMainhand().getItem() == this && Settings.isArtefactEnabled(this)) {
			if (world.getBlockState(new BlockPos(entity.posX, entity.posY, entity.posZ).down()).getBlock() instanceof BlockDimensionFocus) {
				ParticleBuilder.create(ParticleBuilder.Type.DUST).clr(0xf5ad42).vel(0, 0.03f, 0).spin(0.8f, 0.03f).time(60).entity(entity).pos(0, 0.1f, 0).scale(1.2f).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).face(EnumFacing.DOWN).clr(0xc90000).pos(0,0.1f,0).time(20).entity(entity).scale(1.2f).spawn(world);
			}
		}
	}
}
