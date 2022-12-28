package com.windanesz.ancientspellcraft.client.renderer.entity;

import com.windanesz.ancientspellcraft.client.renderer.entity.layers.LayerSkeletonMageClothing;
import com.windanesz.ancientspellcraft.client.renderer.entity.layers.LayerSkeletonMageHat;
import com.windanesz.ancientspellcraft.entity.living.EntitySkeletonMage;
import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderSkeletonMage extends RenderBiped<EntitySkeletonMage> {
	private static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/stray.png");

	public RenderSkeletonMage(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelSkeleton(), 0.5F);
		this.addLayer(new LayerHeldItem(this));
		this.addLayer(new LayerSkeletonMageClothing(this));
		this.addLayer(new LayerSkeletonMageHat(this) {
			@Override
			protected void initArmor() {
				this.modelLeggings = new ModelSkeleton(0.5F, true);
				this.modelArmor = new ModelSkeleton(1.0F, true);
			}
		});
	}

	@Override
	public void transformHeldFull3DItemLayer() {
		GlStateManager.translate(0.09375F, 0.1875F, 0.0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySkeletonMage entity) {
		return SKELETON_TEXTURES;
	}

	@Override
	protected void preRenderCallback(EntitySkeletonMage entity, float partialTickTime) {
		super.preRenderCallback(entity, partialTickTime);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		if (entity instanceof EntitySkeletonMage) { // Always true
			if (entity.getOpacity() < 1) {
				GlStateManager.color(0.6f, 1, 0.6f, entity.getOpacity());
			} else {
				GlStateManager.color(1, 1, 1, entity.getOpacity());
			}
		}
	}
}