package com.windanesz.ancientspellcraft.entity.construct;

import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntitySentinel extends EntityMagicConstruct {

	public EntitySentinel(World world) {
		super(world);
		this.height = 1.0f;
		this.width = 1.0f;
	}

	@Override
	public void onUpdate() {

		super.onUpdate();

		if (this.world.isRemote && this.rand.nextInt(15) == 0) {
			double radius = 0.5 + rand.nextDouble() * 0.3;
			float angle = rand.nextFloat() * (float) Math.PI * 2;
			;

			ParticleBuilder.create(ParticleBuilder.Type.SPARKLE, rand, this.posX + radius * MathHelper.cos(angle), this.posY + 0.1,
					this.posZ + radius * MathHelper.sin(angle), 0.03, true).clr(0.15f, 0.7f, 0.15f).spawn(world);
		}
	}

	@Override
	protected void entityInit() {}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

}
