package com.windanesz.ancientspellcraft.client.renderer;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.EntityChaosOrb;
import electroblob.wizardry.client.renderer.entity.RenderProjectile;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.entity.projectile.EntityMagicProjectile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

public class RenderChaosOrb extends RenderProjectile {

	public static HashMap<Element, ResourceLocation> TEXTURE_MAP = new HashMap<Element, ResourceLocation>() {
		{
			put(Element.FIRE, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/chaos_orb_fire.png"));
			put(Element.ICE, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/chaos_orb_ice.png"));
			put(Element.LIGHTNING, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/chaos_orb_lightning.png"));
			put(Element.HEALING, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/chaos_orb_healing.png"));
			put(Element.NECROMANCY, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/chaos_orb_necromancy.png"));
			put(Element.MAGIC, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/chaos_orb_magic.png"));
			put(Element.EARTH, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/chaos_orb_earth.png"));
			put(Element.SORCERY, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/chaos_orb_sorcery.png"));
		}
	};


	public RenderChaosOrb(RenderManager renderManager, float scale, ResourceLocation texture, boolean doBlending) {
		super(renderManager, scale, texture, doBlending);
	}

	@Override
	public void doRender(EntityMagicProjectile entity, double par2, double par4, double par6, float par8, float par9) {
		boolean doBlend = true;
		GlStateManager.pushMatrix();
		this.bindTexture(getEntityTexture(entity));
		GlStateManager.translate((float)par2, (float)par4, (float)par6);
		GlStateManager.enableRescaleNormal();
		if(doBlend){
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		GlStateManager.disableLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

		float f2 = 0.4f;
		GlStateManager.scale(f2 / 1.0F, f2 / 1.0F, f2 / 1.0F);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		float f3 = 0.0f;
		float f4 = 1.0f;
		float f5 = 0.0f;
		float f6 = 1.0f;
		float f7 = 1.0F;
		float f8 = 0.5F;
		float f9 = 0.25F;

		// This counteracts the reverse rotation behaviour when in front f5 view.
		// Fun fact: this is a bug with vanilla too! Look at a snowball in front f5 view, for example.
		float yaw = Minecraft.getMinecraft().gameSettings.thirdPersonView == 2 ? this.renderManager.playerViewX
				: -this.renderManager.playerViewX;
		GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(yaw, 1.0F, 0.0F, 0.0F);

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		// buffer.normal(0.0F, 1.0F, 0.0F);
		buffer.pos((double)(0.0F - f8), (double)(0.0F - f9), 0.0D).tex((double)f3, (double)f6).endVertex();
		buffer.pos((double)(f7 - f8), (double)(0.0F - f9), 0.0D).tex((double)f4, (double)f6).endVertex();
		buffer.pos((double)(f7 - f8), (double)(1.0F - f9), 0.0D).tex((double)f4, (double)f5).endVertex();
		buffer.pos((double)(0.0F - f8), (double)(1.0F - f9), 0.0D).tex((double)f3, (double)f5).endVertex();
		tessellator.draw();

		GlStateManager.disableRescaleNormal();
		if(doBlend){
			GlStateManager.disableBlend();
		}
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMagicProjectile entity) {
		if (entity instanceof EntityChaosOrb) {
			return TEXTURE_MAP.get(((EntityChaosOrb) entity).getElement());
		}
		return super.getEntityTexture(entity);
	}
}
