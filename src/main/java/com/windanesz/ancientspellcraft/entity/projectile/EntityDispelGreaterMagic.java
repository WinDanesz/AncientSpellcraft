package com.windanesz.ancientspellcraft.entity.projectile;

import electroblob.wizardry.constants.Tier;
import net.minecraft.world.World;

public class EntityDispelGreaterMagic extends EntityDispelMagic {

	public EntityDispelGreaterMagic(World world) {
		super(world);
		this.setTier(Tier.ADVANCED);
		this.setSize(0.7f, 0.7f);

	}
}
