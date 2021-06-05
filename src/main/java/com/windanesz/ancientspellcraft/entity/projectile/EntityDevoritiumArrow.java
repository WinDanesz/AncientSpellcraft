package com.windanesz.ancientspellcraft.entity.projectile;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import electroblob.wizardry.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.List;

public class EntityDevoritiumArrow extends EntityArrow {

	public EntityDevoritiumArrow(World worldIn) {
		super(worldIn);
	}

	public EntityDevoritiumArrow(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

	public EntityDevoritiumArrow(World worldIn, EntityLivingBase shooter) {
		super(worldIn, shooter);
	}

	@Override
	protected ItemStack getArrowStack() {
		return new ItemStack(AncientSpellcraftItems.devoritium_arrow);
	}

	protected void arrowHit(EntityLivingBase living) {
		super.arrowHit(living);
		PotionEffect potioneffect = new PotionEffect(AncientSpellcraftPotions.magical_exhaustion, 60, 2);
		living.addPotionEffect(potioneffect);
	}

	@Override
	public void onUpdate() {
		List<Entity> targets = EntityUtils.getEntitiesWithinRadius(2, posX, posY, posZ, world, Entity.class);
		if (!targets.isEmpty()) {
			for (Entity target : targets) {
				if (target instanceof EntityMagicConstruct) {
					int oldLifetime = ((EntityMagicConstruct) target).lifetime;

					if (oldLifetime < 0 && world.rand.nextFloat() <= 0.2) {
						target.setDead();
					} else {
						((EntityMagicConstruct) target).lifetime = (int)(oldLifetime * 0.8f);
					}
				}
			}
		}
		super.onUpdate();

	}
}
