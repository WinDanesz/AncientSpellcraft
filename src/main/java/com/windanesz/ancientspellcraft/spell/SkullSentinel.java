package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.tileentity.TileSkullWatch;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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

				if (world.isRemote) {
					int radius = 15;
					int particleCount = (int)Math.round(Math.PI * radius * radius);

					for(int i=0; i<particleCount; i++){

						double r = (1 + world.rand.nextDouble() * (radius - 1));
						float angle = world.rand.nextFloat() * (float)Math.PI * 2f;

						spawnParticle(world, pos.getX() + r * MathHelper.cos(angle), pos.getY(), pos.getZ() + r * MathHelper.sin(angle));
					}
				}
				if (world.isAirBlock(pos.offset(EnumFacing.UP))) {
					if (caster instanceof EntityPlayer) {
						world.setBlockState(pos.offset(EnumFacing.UP), ASBlocks.SKULL_WATCH.getDefaultState());
						if (!world.isRemote) {
							((TileSkullWatch) world.getTileEntity(pos.up())).setCaster(caster);
							if (ItemArtefact.isArtefactActive((EntityPlayer) caster, ASItems.charm_sentinel_eye)) {
								((TileSkullWatch) world.getTileEntity(pos.up())).setMarkEntities(true);
							}
							if (ItemArtefact.isArtefactActive((EntityPlayer) caster, ASItems.amulet_domus)) {
								((TileSkullWatch) world.getTileEntity(pos.up())).setSummonSkeleton(true);
							}
						}
						return true;
					}
				} else {
					if (caster instanceof EntityPlayer) {
						world.setBlockState(pos, ASBlocks.SKULL_WATCH.getDefaultState());
						if (!world.isRemote) {
							((TileSkullWatch) world.getTileEntity(pos)).setCaster(caster);
							if (ItemArtefact.isArtefactActive((EntityPlayer) caster, ASItems.charm_sentinel_eye)) {
								((TileSkullWatch) world.getTileEntity(pos.up())).setMarkEntities(true);
							}
							if (ItemArtefact.isArtefactActive((EntityPlayer) caster, ASItems.amulet_domus)) {
								((TileSkullWatch) world.getTileEntity(pos.up())).setSummonSkeleton(true);
							}
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

	protected void spawnParticle(World world, double x, double y, double z){
		ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, 0.03, 0).time(100).clr(0.5f, 0.4f, 0.75f).spawn(world);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) { return false; }

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) { return false; }
}
