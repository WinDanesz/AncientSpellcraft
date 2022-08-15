package com.windanesz.ancientspellcraft.spell;

import electroblob.wizardry.entity.living.EntityEvilWizard;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.potion.Curse;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class CursedTouch extends SpellRayAS {

	public CursedTouch() {
		super("cursed_touch", SpellActions.POINT, false);
		this.soundValues(1, 1.1f, 0.2f);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		if (EntityUtils.isLiving(target) && (target instanceof INpc || target instanceof EntityEvilWizard || target instanceof EntityPlayer)) {

			if (!world.isRemote) {

				List<PotionEffect> curses = new ArrayList<>();

				for(PotionEffect effect : new ArrayList<>(caster.getActivePotionEffects())){ // Get outta here, CMEs
					if(effect.getPotion() instanceof Curse){
						curses.add(effect);
					}
				}

				if (!curses.isEmpty()) {
					PotionEffect curse = curses.get(caster.world.rand.nextInt(curses.size()));
					((EntityLivingBase) target).addPotionEffect(curse);
					caster.removePotionEffect(curse.getPotion());
					return true;
				}
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


