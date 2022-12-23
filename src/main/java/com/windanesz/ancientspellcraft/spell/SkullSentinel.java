package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.tileentity.TileEntityPlayerSave;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SkullSentinel extends SpellRay {

	public SkullSentinel() {
		super(AncientSpellcraft.MODID, "skull_sentinel", SpellActions.POINT, false);
		this.soundValues(0.7F, 1.1F, 0.2F);
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (world.getBlockState(pos).getBlock() == Blocks.SKULL && world.getTileEntity(pos) != null &&
				world.getTileEntity(pos) instanceof TileEntitySkull) {
			TileEntitySkull skullTe = (TileEntitySkull) world.getTileEntity(pos);

			// regular skeleton 0, wither = 1 (net.minecraft.block.BlockSkull.checkWitherSpawn)
			if (skullTe.getSkullType() == 0) {
				world.setBlockToAir(pos);

				// TODO: add particles at spellcast?
				if (world.isAirBlock(pos.offset(EnumFacing.UP))) {
					if (caster != null) {
						world.setBlockState(pos.offset(EnumFacing.UP), ASBlocks.SKULL_WATCH.getDefaultState());
						if (!world.isRemote) {
							((TileEntityPlayerSave) world.getTileEntity(pos.up())).setCaster(caster);
						}
						return true;
					}
				} else {
					if (caster != null) {
						world.setBlockState(pos, ASBlocks.SKULL_WATCH.getDefaultState());
						if (!world.isRemote) {
							((TileEntityPlayerSave) world.getTileEntity(pos)).setCaster(caster);
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	protected boolean onEntityHit(World world, Entity entity, Vec3d vec3d,
			@Nullable EntityLivingBase entityLivingBase, Vec3d vec3d1, int i, SpellModifiers spellModifiers) {
		return false;
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase entityLivingBase, Vec3d vec3d, Vec3d vec3d1, int i, SpellModifiers spellModifiers) {
		return false;
	}

	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		// TODO
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
