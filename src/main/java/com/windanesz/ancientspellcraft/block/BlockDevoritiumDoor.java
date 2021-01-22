package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.constants.AWConstants;
import com.windanesz.ancientspellcraft.material.IDevoritium;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftTabs;
import electroblob.wizardry.potion.Curse;
import electroblob.wizardry.potion.PotionMagicEffect;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BlockDevoritiumDoor extends BlockDoor implements IDevoritium {

	public BlockDevoritiumDoor() {
		super(AncientSpellcraft.DEVORITIUM);
		setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT);
		setHardness(AWConstants.DEVORITIUM_BLOCK_HARDNESS);
		setResistance(AWConstants.DEVORITIUM_BLOCK_RESISTANCE);
		setHarvestLevel(AWConstants.DEVORITIUM_HARVEST_TOOL, AWConstants.DEVORITIUM_HARVEST_LEVEL);
	}

	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
		onEntityWalkDelegate(worldIn, pos, entityIn);
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entity) {
		onEntityCollisionDelegate(worldIn, pos, state, entity);
	}

	/**
	 * Called when the block is right clicked by a player.
	 */
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(AncientSpellcraftItems.devoritium_door);
	}

	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? Items.AIR : AncientSpellcraftItems.devoritium_door;
	}

	@Nullable
	public Boolean isEntityInsideMaterial(IBlockAccess blockAccess, BlockPos blockpos, IBlockState iblockstate, Entity entity, double yToTest, Material materialIn, boolean testingHead) {

		// remove most of magical potion effect if the player steps through the door
		if (entity instanceof EntityLivingBase && entity.world.getTotalWorldTime() % 5 == 0) {
			Map<Potion, PotionEffect> map = ((EntityLivingBase) entity).getActivePotionMap();
			if (!map.isEmpty()) {
				List<Potion> toRemove = new ArrayList<>();
				for (Potion potion : map.keySet()) {
					if (potion instanceof PotionMagicEffect && !(potion instanceof Curse) && potion != AncientSpellcraftPotions.magical_exhaustion ||
							potion == MobEffects.FIRE_RESISTANCE || potion == MobEffects.ABSORPTION || potion == MobEffects.SPEED || potion == MobEffects.STRENGTH
							|| potion == MobEffects.REGENERATION) {
						if (!toRemove.contains(potion)) {
							toRemove.add(potion);
						}
					}
				}
				if (!toRemove.isEmpty()) {
					for (Potion potion : toRemove) {
						((EntityLivingBase) entity).removePotionEffect(potion);
					}
				}
			}
		}
		return null;
	}
}

