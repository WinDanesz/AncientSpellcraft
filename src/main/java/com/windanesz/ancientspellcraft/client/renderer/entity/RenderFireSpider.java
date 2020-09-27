package com.windanesz.ancientspellcraft.client.renderer.entity;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.client.model.ModelAnt;
import com.windanesz.ancientspellcraft.entity.living.EntityFireAnt;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFireSpider extends RenderLiving<EntityFireAnt> {

	private static final ResourceLocation FIRE_SPIDER_TEXTURES = new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/fire_ant.png");

	public RenderFireSpider(RenderManager renderManagerIn)
	{
		super(renderManagerIn, new ModelAnt(), 1.0F);
		this.shadowSize *= 0.35F;
	}


	/**
	 * Allows the render to do state modifications necessary before the model is rendered.
	 */
	@Override
	protected void preRenderCallback(EntityFireAnt entitylivingbaseIn, float partialTickTime) {
		GlStateManager.scale(0.35F, 0.35F, 0.35F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFireAnt entity) {
		return FIRE_SPIDER_TEXTURES;
	}



}