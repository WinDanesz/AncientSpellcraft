package com.windanesz.ancientspellcraft.entity.construct;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityVenusFlyTrap extends EntityEvokerFangs {

	public EntityVenusFlyTrap(World worldIn) {
		super(worldIn);
	}

	public EntityVenusFlyTrap(World worldIn, double x, double y, double z, float rotation, int warmupDelayTicks, EntityLivingBase casterIn) {
		super(worldIn, x, y, z, rotation, warmupDelayTicks, casterIn);
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		for (EntityLivingBase entitylivingbase : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(0.2D, 0.0D, 0.2D))) {
			this.poison(entitylivingbase);
		}
	}

	private void poison(EntityLivingBase entity) {
		if (!world.isRemote) {
			entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS,
					100, 2));
			entity.addPotionEffect(new PotionEffect(MobEffects.POISON,
					100, 0));
		}
	}
}
