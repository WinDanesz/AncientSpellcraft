package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.ArcaneLock;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static electroblob.wizardry.util.EntityUtils.canDamageBlocks;

public class Drought extends SpellRay {

	static Random rand = new Random();

	public Drought(String modID, String name, EnumAction action, boolean isContinuous) {
		super(modID, name, SpellActions.POINT, false);
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
		// The mine spell ignores the block damage setting for players, since that's the entire point of the spell
		// Instead, it triggers block break events at the appropriate points, which protection mods should be able to
		// pick up and allow/disallow accordingly
		// For the time being, dispensers respect the mobGriefing gamerule
		if (!(caster instanceof EntityPlayer) && !canDamageBlocks(caster, world))
			return false;
		// Can't mine arcane-locked blocks
		if (world.getTileEntity(pos) != null && world.getTileEntity(pos).getTileData().hasUniqueId(ArcaneLock.NBT_KEY))
			return false;

		IBlockState state = world.getBlockState(pos);
		// The maximum harvest level as determined by the potency multiplier. The + 0.5f is so that
		// weird float processing doesn't incorrectly round it down.

		boolean flag = false;

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

			if (world.isRemote && !world.isAirBlock(pos1) && world.isAirBlock(pos1.up())) {
				spawnCloudParticles(world, pos1.up(), 4);
			}
			if (!world.isRemote) {
				if (caster instanceof EntityPlayerMP) { // Everything in here is server-side only so this is fine

					if (block instanceof BlockBush && !(block instanceof BlockDeadBush) && !(block instanceof BlockSapling)) {
						world.setBlockToAir(pos1);
					}
					if (block instanceof BlockSapling) {
						world.setBlockState(pos1, Blocks.DEADBUSH.getDefaultState());
					}

					if (block instanceof BlockGrass) {
						world.setBlockState(pos1, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT));
					}

					if (block instanceof BlockGrass) {
						world.setBlockState(pos1, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT));
					}

					if (block instanceof BlockDirt) {
						world.setBlockState(pos1, Blocks.SAND.getDefaultState());
					}

					if (block instanceof BlockLiquid && (world.getBlockState(pos1).getMaterial() == Material.WATER)) {
						world.setBlockToAir(pos1);
					}
					if (block instanceof BlockLeaves) {
						world.setBlockToAir(pos1);
					}

				}
			}
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	public static void spawnCloudParticles(World worldIn, BlockPos pos, int amount) {
		if (amount == 0) {
			amount = 15;
		}

		IBlockState iblockstate = worldIn.getBlockState(pos);

		if (iblockstate.getMaterial() != Material.AIR) {
			for (int i = 0; i < amount; ++i) {
				double d0 = rand.nextGaussian() * 0.02D;
				double d1 = rand.nextGaussian() * 0.02D;
				double d2 = rand.nextGaussian() * 0.02D;
				worldIn.spawnParticle(EnumParticleTypes.CLOUD, (double) ((float) pos.getX() + rand.nextFloat()), (double) pos.getY() + (double) rand.nextFloat() * iblockstate.getBoundingBox(worldIn, pos).maxY, (double) ((float) pos.getZ() + rand.nextFloat()), d0, d1, d2);
			}
		} else {
			for (int i1 = 0; i1 < amount; ++i1) {
				double d0 = rand.nextGaussian() * 0.02D;
				double d1 = rand.nextGaussian() * 0.02D;
				double d2 = rand.nextGaussian() * 0.02D;
				worldIn.spawnParticle(EnumParticleTypes.CLOUD, (double) ((float) pos.getX() + rand.nextFloat()), (double) pos.getY() + (double) rand.nextFloat() * 1.0f, (double) ((float) pos.getZ() + rand.nextFloat()), d0, d1, d2, new int[0]);
			}
		}
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

