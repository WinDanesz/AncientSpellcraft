package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.tileentity.TileEntityLightningBlock;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryBlocks;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

@Mod.EventBusSubscriber
public class BlockLightning extends Block implements ITileEntityProvider {

	public BlockLightning() {
		super(Material.AIR);
		this.setSoundType(SoundType.GLASS);
		setLightOpacity(1);
		setBlockUnbreakable();
		setResistance(6000000.0F);
	}

	@SubscribeEvent
	public static void onBlockPlaceEvent(BlockEvent.PlaceEvent event) {
		// Spectral blocks cannot be built on
		if (event.getPlacedAgainst() == WizardryBlocks.spectral_block) {
			event.setCanceled(true);
		}
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}

	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
	}

	public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
		return true;
	}

	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	// See BlockTransportationStone for what all these do
	@Override
	public boolean isFullCube(IBlockState state) { return false; }

	@Override
	public boolean isBlockNormalCube(IBlockState state) { return false; }

	@Override
	public boolean isNormalCube(IBlockState state) { return false; }

	@Override
	public boolean isOpaqueCube(IBlockState state) { return false; }

	//	// Overriden to make the block always look full brightness despite not emitting
	//	// full light.
	//	@Override
	//	public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, BlockPos pos){
	//		return 15;
	//	}

	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {

		for (int i = 0; i < 4; i++) {
			ParticleBuilder.create(ParticleBuilder.Type.SPARK)
					.pos(pos.getX() + random.nextDouble(), pos.getY() + random.nextDouble(), pos.getZ() + random.nextDouble())
					.time(5)
					.spawn(world);
		}
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity target) {
		if (target.ticksExisted % 5 != 0) return;

		if (target instanceof EntityPlayer && ItemArtefact.isArtefactActive((EntityPlayer) target, ASItems.ring_kinetic)) {
			((EntityPlayer) target).addPotionEffect(new PotionEffect(MobEffects.SPEED, 40,1));
		}

		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityLightningBlock) {
			EntityLivingBase caster = ((TileEntityLightningBlock) tile).getCaster();

			if (caster != null) {
				if (target == caster || target instanceof EntityLivingBase && AllyDesignationSystem.isAllied(caster, (EntityLivingBase)target)) {
					return;
				}
			}
			double velX = target.motionX;
			double velY = target.motionY;
			double velZ = target.motionZ;
			target.attackEntityFrom((caster != null
							? MagicDamage.causeIndirectMagicDamage(caster, caster, MagicDamage.DamageType.SHOCK)
							: DamageSource.MAGIC),
					((TileEntityLightningBlock) tile).damage);
			target.motionX = velX;
			target.motionY = velY;
			target.motionZ = velZ;
		}
	}

	public static void setProperties(World world, BlockPos pos, EntityLivingBase caster, int blockLifetime, float damage) {
		if (!world.isRemote && world.getBlockState(pos).getBlock() == ASBlocks.lightning_block) {
			TileEntity tile = world.getTileEntity(pos);

			if (tile instanceof TileEntityLightningBlock) {
				TileEntityLightningBlock lightningBlock = (TileEntityLightningBlock) tile;

				if (caster != null) {
					lightningBlock.setCaster(caster);
				}
				lightningBlock.setLifetime(blockLifetime);
				lightningBlock.setDamage(damage);
			}
		}
	}

	@Override
	public boolean hasTileEntity(IBlockState state) { return true; }

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityLightningBlock().setLifetime(600);
	}

	@Override
	public int quantityDropped(Random par1Random) {
		return 0;
	}

	@SuppressWarnings("deprecation")
	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos,
			EnumFacing side) {

		IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
		Block block = iblockstate.getBlock();

		return block == this ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}

}
