package com.windanesz.ancientspellcraft.client.renderer.entity;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.client.model.ModelVenusFlyTrap;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEvokerFangs;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.util.ResourceLocation;

public class RenderVenusFlyTrap extends RenderEvokerFangs {

	private static final ResourceLocation VENUS_FLY_TRAP_TEXTURE = new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/venus_fly_trap.png");
	private final ModelVenusFlyTrap model = new ModelVenusFlyTrap();


	public RenderVenusFlyTrap(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityEvokerFangs entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		float f = entity.getAnimationProgress(partialTicks);

		if (f != 0.0F)
		{
			float f1 = 2.0F;

			if (f > 0.9F)
			{
				f1 = (float)((double)f1 * ((1.0D - (double)f) / 0.10000000149011612D));
			}

			GlStateManager.pushMatrix();
			GlStateManager.disableCull();
			GlStateManager.enableAlpha();
			this.bindEntityTexture(entity);
			GlStateManager.translate((float)x, (float)y, (float)z);
			GlStateManager.rotate(90.0F - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
			GlStateManager.scale(-f1, -f1, f1);
			float f2 = 0.03125F;
			GlStateManager.translate(0.0F, -0.626F, 0.0F);
			this.model.render(entity, f, 0.0F, 0.0F, entity.rotationYaw, entity.rotationPitch, 0.03125F);
			GlStateManager.popMatrix();
			GlStateManager.enableCull();
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityEvokerFangs entity)
	{
		return VENUS_FLY_TRAP_TEXTURE;
	}
}
