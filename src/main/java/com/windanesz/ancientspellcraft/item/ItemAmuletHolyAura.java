package com.windanesz.ancientspellcraft.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemAmuletHolyAura extends ItemASArtefact implements ITickableArtefact {
	public ItemAmuletHolyAura(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
		if (player.world.getTotalWorldTime() % 40 == 0 && !player.world.isRemote) {
			double radius = 6.0;
			List<EntityLivingBase> entities = player.world.getEntitiesWithinAABB(EntityLivingBase.class,
					player.getEntityBoundingBox().grow(radius), e -> e != null && e.isEntityAlive() && e.isEntityUndead() && e != player);
			for (EntityLivingBase entity : entities) {
				entity.setFire(5);
			}
		}
	}
}
