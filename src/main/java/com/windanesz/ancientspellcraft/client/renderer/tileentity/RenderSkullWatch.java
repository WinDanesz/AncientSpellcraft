package com.windanesz.ancientspellcraft.client.renderer.tileentity;

import com.windanesz.ancientspellcraft.client.model.ModelSkullWatch;
import com.windanesz.ancientspellcraft.tileentity.TileSkullWatch;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSkullWatch extends TileEntitySpecialRenderer<TileSkullWatch> {

	private static final ResourceLocation TEXTURE_SKULL_WATCH = new ResourceLocation("ancientspellcraft:textures/entity/skull_watch.png");
	private final ModelSkullWatch modelSkullWatch = new ModelSkullWatch();

	public void render(TileSkullWatch te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 0.75F, (float) z + 0.5F);
		float f = (float) te.tickCount + partialTicks;
		GlStateManager.translate(0.0F, 0.1F + MathHelper.sin(f * 0.1F) * 0.01F, 0.0F);
		float f1;

		for (f1 = te.skullRotation - te.skullRotationPrev; f1 >= (float) Math.PI; f1 -= ((float) Math.PI * 2F)) {
			;
		}

		while (f1 < -(float) Math.PI) {
			f1 += ((float) Math.PI * 2F);
		}

		float f2 = te.skullRotationPrev + f1 * partialTicks;
		GlStateManager.rotate(-f2 * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(85.0F, 0.0F, 0.0F, 0.2F);
		this.bindTexture(TEXTURE_SKULL_WATCH);
		float f3 = te.pageFlipPrev + (te.pageFlip - te.pageFlipPrev) * partialTicks + 0.25F;
		float f4 = te.pageFlipPrev + (te.pageFlip - te.pageFlipPrev) * partialTicks + 0.75F;
		f3 = (f3 - (float) MathHelper.fastFloor((double) f3)) * 1.6F - 0.3F;
		f4 = (f4 - (float) MathHelper.fastFloor((double) f4)) * 1.6F - 0.3F;

		if (f3 < 0.0F) {
			f3 = 0.0F;
		}

		if (f4 < 0.0F) {
			f4 = 0.0F;
		}

		if (f3 > 1.0F) {
			f3 = 1.0F;
		}

		if (f4 > 1.0F) {
			f4 = 1.0F;
		}

		//			float f5 = te.skullSpreadPrev + (te.skullSpread - te.skullSpreadPrev) * partialTicks;
		GlStateManager.enableCull();
		//			this.modelSkullWatch.render((Entity)null, f, f3, f4, 0, 0.0F, 0.0625F);
		this.modelSkullWatch.render((Entity) null, f, f3, f4, 0, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
	}
}