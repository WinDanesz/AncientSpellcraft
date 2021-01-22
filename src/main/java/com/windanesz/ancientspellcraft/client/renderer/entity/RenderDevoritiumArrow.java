package com.windanesz.ancientspellcraft.client.renderer.entity;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderDevoritiumArrow extends RenderArrow {

	public static final ResourceLocation DEVORITIUM_ARROW_TEXTURE = new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/projectiles/devoritium_arrow.png");

	public RenderDevoritiumArrow(RenderManager manager) {
		super(manager);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return DEVORITIUM_ARROW_TEXTURE;
	}
}
