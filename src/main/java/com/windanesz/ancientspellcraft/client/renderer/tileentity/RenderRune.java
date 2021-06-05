package com.windanesz.ancientspellcraft.client.renderer.tileentity;

import com.windanesz.ancientspellcraft.ritual.IHasItemToRender;
import com.windanesz.ancientspellcraft.tileentity.TileRune;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderRune extends TileEntitySpecialRenderer<TileRune> {

	@Override
	public void render(TileRune tileentity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

		GlStateManager.pushMatrix();

		GlStateManager.translate((float) x + 0.5F, (float) y + 0F, (float) z + 0.5F);

		float t = Minecraft.getMinecraft().player.ticksExisted + partialTicks;
		//		GlStateManager.translate(0, 0.05f * MathHelper.sin(t/15), 0);
		this.renderItem(tileentity, t);

		if (tileentity.isMaster() && tileentity.getRitual() != null && tileentity.getRitual() instanceof IHasItemToRender) {
			((IHasItemToRender) tileentity.getRitual()).renderItem(tileentity, partialTicks, destroyStage, alpha);
		}



		GlStateManager.popMatrix();
	}


	private void renderItem(TileRune tileentity, float t) {

		ItemStack stack = new ItemStack(tileentity.getRune());
		if (!stack.isEmpty()) {
			GlStateManager.pushMatrix();

			GlStateManager.rotate(180, 0, 180, 180);
			GlStateManager.scale(0.85F, 0.85F, 0.85F);

			GlStateManager.color(1, 1, 1, 1);
			Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
			GlStateManager.popMatrix();
		}
	}

}