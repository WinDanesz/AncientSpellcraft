package com.windanesz.ancientspellcraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerCreeperCharge;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderVoidCreeper extends RenderCreeper {
	private static final ResourceLocation CREEPER_TEXTURES = new ResourceLocation("ancientspellcraft:textures/entity/void_creeper.png");

	public RenderVoidCreeper(RenderManager renderManagerIn) {
		super(renderManagerIn);
		this.addLayer(new LayerCreeperCharge(this));
	}

	/**
	 * Allows the render to do state modifications necessary before the model is rendered.
	 */
	protected void preRenderCallback(EntityCreeper entitylivingbaseIn, float partialTickTime) {
		float f = entitylivingbaseIn.getCreeperFlashIntensity(partialTickTime);
		float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		f = f * f;
		f = f * f;
		float f2 = (1.0F + f * 0.4F) * f1;
		float f3 = (1.0F + f * 0.1F) / f1;
		GlStateManager.scale(1 / f2, 1 / f3, 1 / f2);
	}

	/**
	 * Gets an RGBA int color multiplier to apply.
	 */
	protected int getColorMultiplier(EntityCreeper entitylivingbaseIn, float lightBrightness, float partialTickTime) {
		float f = entitylivingbaseIn.getCreeperFlashIntensity(partialTickTime);

		if ((int) (f * 10.0F) % 2 == 0) {
			return 0;
		} else {
			int i = (int) (f * 0.2F * 0.2F);
			i = MathHelper.clamp(i, 0, 255);
			return i << 24 | 822083583;
		}
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityCreeper entity) {
		return CREEPER_TEXTURES;
	}
}