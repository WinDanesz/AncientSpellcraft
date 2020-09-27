package com.windanesz.ancientspellcraft.client.renderer.entity;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderWolf;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;

public class RenderWolfMinion extends RenderWolf {
	private static final ResourceLocation WOLF_TEXTURES = new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/wolf_minion.png");

	public RenderWolfMinion(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityWolf entity) {
		return WOLF_TEXTURES;
	}
}
