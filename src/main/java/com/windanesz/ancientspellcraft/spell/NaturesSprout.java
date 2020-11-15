package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.ArcaneLock;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class NaturesSprout extends SpellRay {

	Random rand = new Random();

	public NaturesSprout(String modID, String name, EnumAction action, boolean isContinuous) {
		super(modID, name, SpellActions.SUMMON, false);
		this.soundValues(1.0f, 1, 0.4f);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		// Needs to be outside because it gets run on the client-side
		if (caster instanceof EntityPlayer) {
			if (caster.getHeldItemMainhand().getItem() instanceof ISpellCastingItem) {
				caster.swingArm(EnumHand.MAIN_HAND);
			} else if (caster.getHeldItemOffhand().getItem() instanceof ISpellCastingItem) {
				caster.swingArm(EnumHand.OFF_HAND);
			}
		}

		if (BlockUtils.isBlockUnbreakable(world, pos))
			return false;
		if (!(caster instanceof EntityPlayer) && !EntityUtils.canDamageBlocks(caster, world))
			return false;
		if (world.getTileEntity(pos) != null && world.getTileEntity(pos).getTileData().hasUniqueId(ArcaneLock.NBT_KEY))
			return false;

		int blastUpgradeCount = (int) ((modifiers.get(WizardryItems.blast_upgrade) - 1) / Constants.RANGE_INCREASE_PER_LEVEL + 0.5f);
		// Results in the following patterns:
		// 0 blast upgrades: single block
		// 1 blast upgrade: 3x3 without corners or edges
		// 2 blast upgrades: 3x3 with corners
		// 3 blast upgrades: 5x5 without corners or edges
		float radius = 0.5f + 0.73f * blastUpgradeCount;

		List<BlockPos> sphere = BlockUtils.getBlockSphere(pos, radius);

		if (radius <= 1) { // adding the block above to include vegetation too
			sphere.add(pos.up());
		}

		for (BlockPos pos1 : sphere) {
			if (BlockUtils.isBlockUnbreakable(world, pos1))
				continue;

			Block block = world.getBlockState(pos1).getBlock();

			//			if (caster instanceof EntityPlayerMP) { // Everything in here is server-side only so this is fine

			if (world.isRemote && !world.isAirBlock(pos1) && world.isAirBlock(pos1.up())) {
				ItemDye.spawnBonemealParticles(world, pos1.up(), 4);
			}

			if (world.getBlockState(pos1).getBlock() == Blocks.GRASS && world.isAirBlock(pos1.up())) {

				// spawn some grass - which is actually TALLGRASS, even if it is only a 1 block high variant
				if (rand.nextDouble() < 0.15 && !world.isRemote) {
					world.setBlockState(pos1.up(), Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS));
				}

			}
			if (!world.isRemote) {

				if (world.getBlockState(pos1).getBlock() == Blocks.DIRT && world.getBlockState(pos1).getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT && world.isAirBlock(pos1.up())) {
					world.setBlockState(pos1, Blocks.GRASS.getDefaultState());
					if (rand.nextDouble() < 0.10) {
						world.setBlockState(pos1.up(), Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS));
					}
				}

				if (world.getBlockState(pos1).getBlock() == Blocks.DIRT && world.getBlockState(pos1).getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.COARSE_DIRT) {
					world.setBlockState(pos1, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT));
				}

				if (block instanceof BlockSand) {
					world.setBlockState(pos1, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT));
				}
			}
			//			}
		}
		return true;
		////		}
		//		return false;
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
