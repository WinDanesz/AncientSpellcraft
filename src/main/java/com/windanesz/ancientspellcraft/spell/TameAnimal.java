package com.windanesz.ancientspellcraft.spell;

import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class TameAnimal extends SpellRay {

	EntityPlayer casterPlayer;
	Random rand = new Random();

	public TameAnimal(String modID, String name, boolean isContinuous, EnumAction action) {
		super(modID, name, isContinuous, action);
		this.soundValues(0.7F, 1.2F, 0.4F);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		casterPlayer = caster;
		return super.cast(world, caster, hand, ticksInUse, modifiers);
	}

	@Override
	protected boolean onEntityHit(World world, Entity entity, Vec3d vec3d,
			@Nullable EntityLivingBase entityLivingBase, Vec3d vec3d1, int i, SpellModifiers spellModifiers) {
		if (casterPlayer != null) {
			world.playSound((double) entity.getPosition().getX(), (double) entity.getPosition().getY(), (double) entity.getPosition().getZ(), SoundEvents.BLOCK_NOTE_CHIME, SoundCategory.BLOCKS, 0.5F, rand.nextFloat() * 0.4F + 0.8F, false);

			if (entity instanceof EntityParrot) {
				EntityParrot parrot = (EntityParrot) entity;
				if (!parrot.isTamed() && casterPlayer != null) {
					parrot.setTamedBy(casterPlayer);
					playTameEffect(true, parrot);
					parrot.world.setEntityState(parrot, (byte) 7);
					return true;
				}
			} else if (entity instanceof EntityWolf) {
				EntityWolf wolf = (EntityWolf) entity;
				if (!wolf.isTamed() && casterPlayer != null) {
					wolf.setTamedBy(casterPlayer);
					wolf.getNavigator().clearPath();
					wolf.setAttackTarget((EntityLivingBase) null);
					wolf.getAISit().setSitting(true);
					wolf.setHealth(20.0F);
					playTameEffect(true, wolf);
					wolf.world.setEntityState(wolf, (byte) 7);
					return true;
				}
				return false;
			} else if (entity instanceof EntityOcelot) {
				EntityOcelot ocelot = (EntityOcelot) entity;
				if (!ocelot.isTamed() && casterPlayer != null) {
					ocelot.setTamedBy(casterPlayer);
					ocelot.setTameSkin(1 + ocelot.world.rand.nextInt(3));
					playTameEffect(true, ocelot);
					ocelot.getAISit().setSitting(true);
					ocelot.world.setEntityState(ocelot, (byte) 7);
					return true;
				}
				return false;
			} else if (entity instanceof EntityHorse || entity instanceof EntityDonkey || entity instanceof EntityMule || entity instanceof EntityLlama) {
				AbstractHorse horse = (AbstractHorse) entity;
				if (!horse.isTame() && !horse.isBeingRidden()) {
					horse.setTamedBy(casterPlayer);
					playTameEffect(true, horse);
					return true;
				}
				return false;
			}
		}
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos blockPos, EnumFacing enumFacing, Vec3d vec3d,
			@Nullable EntityLivingBase entityLivingBase, Vec3d vec3d1, int i, SpellModifiers spellModifiers) {
		return false;
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase entityLivingBase, Vec3d vec3d, Vec3d vec3d1, int i, SpellModifiers spellModifiers) {
		return false;
	}

	protected void playTameEffect(boolean play, EntityAnimal entityAnimal) {
		EnumParticleTypes enumparticletypes = EnumParticleTypes.HEART;

		if (!play) {
			enumparticletypes = EnumParticleTypes.SMOKE_NORMAL;
		}

		for (int i = 0; i < 7; ++i) {
			double d0 = this.rand.nextGaussian() * 0.02D;
			double d1 = this.rand.nextGaussian() * 0.02D;
			double d2 = this.rand.nextGaussian() * 0.02D;
			entityAnimal.world.spawnParticle(enumparticletypes,
					entityAnimal.posX + (double) (this.rand.nextFloat() * entityAnimal.width * 2.0F) - (double) entityAnimal.width,
					entityAnimal.posY + 0.5D + (double) (this.rand.nextFloat() * entityAnimal.height),
					entityAnimal.posZ + (double) (this.rand.nextFloat() * entityAnimal.width * 2.0F) - (double) entityAnimal.width, d0, d1, d2);
		}
	}

	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		ParticleBuilder.create(ParticleBuilder.Type.LEAF).pos(x, y, z).vel(vx, vy, vz).collide(true).spawn(world);
//		ParticleBuilder.create(ParticleBuilder.Type.SPARK).pos(x, y, z).vel(vx, vy, vz).collide(true).spawn(world);
	}

}
