package com.windanesz.ancientspellcraft.client.renderer.tileentity;

import com.windanesz.ancientspellcraft.client.model.ModelLargeSentinel;
import com.windanesz.ancientspellcraft.client.model.ModelSentinel;
import com.windanesz.ancientspellcraft.tileentity.TileSentinel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTileSentinel extends TileEntitySpecialRenderer<TileSentinel> {

	// add warm up color?
	private static final ResourceLocation TEXTURE_SENTINEL = new ResourceLocation("ancientspellcraft:textures/entity/sentinel.png");
	private static final ResourceLocation TEXTURE_SENTINEL_LARGE = new ResourceLocation("ancientspellcraft:textures/entity/sentinel_large.png");
	private final ModelSentinel modelSentinel = new ModelSentinel();
	private final ModelLargeSentinel modelSentinelLarge = new ModelLargeSentinel();

	public void render(TileSentinel te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y +0.1f, (float) z + 0.5F);
		float f = partialTicks;
		float f1;

		for (f1 = te.crystalRotation - te.crystalRotationPrev; f1 >= (float) Math.PI; f1 -= ((float) Math.PI * 2F)) {
			;
		}

		while (f1 < -(float) Math.PI) {
			f1 += ((float) Math.PI * 2F);
		}

		float f2 = te.crystalRotationPrev + f1 * partialTicks;
		GlStateManager.rotate(-f2 * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
		// down and up motion
		GlStateManager.translate(0, MathHelper.sin(te.crystalRotation * 2) * 0.12f , 0);
//		GlStateManager.translate(0, MathHelper.sin(te.sphereRotationPrev) , 0); this created an up and flow motion

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		// the color of the element
		//		GlStateManager.color(1,0,0,1);

		if (te.isLarge()) {
			this.bindTexture(TEXTURE_SENTINEL_LARGE);
		} else {
			this.bindTexture(TEXTURE_SENTINEL);
		}
//

		GlStateManager.enableCull();
		if (te.isLarge()) {
			this.modelSentinelLarge.render(null, f, 1, 1, 0, 0.0F, 0.0625F);
		} else {
			this.modelSentinel.render(null, f, 1, 1, 0, 0.0F, 0.0625F);
		}
		GlStateManager.popMatrix();
	}
}