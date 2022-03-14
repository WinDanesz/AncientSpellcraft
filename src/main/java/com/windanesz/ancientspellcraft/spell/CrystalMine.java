package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.BlockCrystalMine;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.tileentity.TileEntityPlayerSave;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class CrystalMine extends SpellRay {

	public CrystalMine() {
		super(AncientSpellcraft.MODID, "crystal_mine", SpellActions.POINT, false);
		this.soundValues(1, 1.4f, 0.4f);
		this.ignoreLivingEntities(true);
		addProperties(DAMAGE);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		boolean detonated = false;
		List<BlockPos> blockPosList = BlockUtils.getBlockSphere(pos, 10);

		for (BlockPos blockPos : blockPosList) {
			if (world.getBlockState(blockPos).getBlock() == ASBlocks.CRYSTAL_MINE) {

				TileEntity tileEntity = world.getTileEntity(blockPos);
				if (tileEntity instanceof TileEntityPlayerSave && ((TileEntityPlayerSave) tileEntity).getCaster() != null && ((TileEntityPlayerSave) tileEntity).getCaster() == caster) {
					((BlockCrystalMine) world.getBlockState(blockPos).getBlock()).explode(world, blockPos, caster);
					detonated = true;
				}
			}

		}
		if (detonated) {
			return true;
		}

		if (side == EnumFacing.UP && world.isSideSolid(pos, EnumFacing.UP) && BlockUtils.canBlockBeReplaced(world, pos.up())) {
			if (!world.isRemote) {
				world.setBlockState(pos.up(), ASBlocks.CRYSTAL_MINE.getDefaultState());
				((TileEntityPlayerSave) world.getTileEntity(pos.up())).setCaster(caster);
				((TileEntityPlayerSave) world.getTileEntity(pos.up())).sync();
			}
			return true;
		}

		return false;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		float brightness = world.rand.nextFloat() * 0.25f;
//		ParticleBuilder.create(Type.SPARKLE).pos(x, y, z).time(20 + world.rand.nextInt(8))
//				.clr(brightness, brightness + 0.1f, 0).spawn(world);
		ParticleBuilder.create(Type.SPARKLE).pos(x, y, z).vel(0, -0.01, 0).time(40 + world.rand.nextInt(10)).spawn(world);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}

}

