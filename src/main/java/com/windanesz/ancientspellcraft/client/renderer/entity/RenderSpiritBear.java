package com.windanesz.ancientspellcraft.client.renderer.entity;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.EntitySpiritBear;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPolarBear;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderSpiritBear extends RenderPolarBear {

	private static final ResourceLocation texture = new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/spirit_bear.png");

	public RenderSpiritBear(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityPolarBear entity) {
		return texture;
	}

	@Override
	protected void preRenderCallback(EntityPolarBear entity, float partialTickTime) {
		super.preRenderCallback(entity, partialTickTime);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		if (entity instanceof EntitySpiritBear) { // Always true
			GlStateManager.color(1, 1, 1, ((EntitySpiritBear) entity).getOpacity());
		}
	}
}
