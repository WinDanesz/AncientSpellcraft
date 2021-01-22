package com.windanesz.ancientspellcraft.client.renderer.entity;

import com.windanesz.ancientspellcraft.client.renderer.entity.layers.LayerSkeletonHorseArmor;
import com.windanesz.ancientspellcraft.entity.living.EntitySkeletonHorseMinion;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSkeletonHorseMinion extends RenderLiving<EntitySkeletonHorseMinion> {

	private static final ResourceLocation texture = new ResourceLocation("textures/entity/horse/horse_skeleton.png");

	public RenderSkeletonHorseMinion(RenderManager p_i47205_1_) {
		super(p_i47205_1_, new ModelHorse(), 0.75F);
		this.addLayer(new LayerSkeletonHorseArmor(this));
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntitySkeletonHorseMinion entity) {
		return texture;
	}
}
