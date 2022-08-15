package com.windanesz.ancientspellcraft.spell;

import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.potion.Curse;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class Sufferance extends SpellRayAS {

	public Sufferance() {
		super("sufferance", SpellActions.POINT, false);
		this.soundValues(1, 1.1f, 0.2f);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		if (EntityUtils.isLiving(target)) {

			List<PotionEffect> curses = new ArrayList<>();

			for (PotionEffect effect : new ArrayList<>(((EntityLivingBase) target).getActivePotionEffects())) { // Get outta here, CMEs
				if (effect.getPotion() instanceof Curse) {
					curses.add(effect);
				}
			}

			if (!curses.isEmpty()) {
				for (PotionEffect curse : curses) {
					if (!world.isRemote) {
						caster.addPotionEffect(curse);
						((EntityLivingBase) target).removePotionEffect(curse.getPotion());
					}
				}
				return true;
			}
		}

		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return true;
	}

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0x42025c).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0x25162b).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).time(12 + world.rand.nextInt(8)).clr(0x4a002f).spawn(world);
	}
}


