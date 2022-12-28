package com.windanesz.ancientspellcraft.client.renderer.entity.layers;

import com.windanesz.ancientspellcraft.entity.living.EntitySkeletonMage;
import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerSkeletonMageClothing implements LayerRenderer<EntitySkeletonMage> {
	private static final ResourceLocation STRAY_CLOTHES_TEXTURES = new ResourceLocation("ancientspellcraft:textures/entity/skeleton_mage_overlay.png");
	private final RenderLivingBase<?> renderer;
	private final ModelSkeleton layerModel = new ModelSkeleton(0.25F, true);

	public LayerSkeletonMageClothing(RenderLivingBase<?> rendererIn) {
		this.renderer = rendererIn;
	}

	public void doRenderLayer(EntitySkeletonMage entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		this.layerModel.setModelAttributes(this.renderer.getMainModel());
		this.layerModel.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
		if (entity.getOpacity() < 1) {
			GlStateManager.color(0.6f, 1, 0.6f, entity.getOpacity());
		} else {
			GlStateManager.color(1, 1, 1, entity.getOpacity());
		}
		this.renderer.bindTexture(STRAY_CLOTHES_TEXTURES);
		this.layerModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
	}

	public boolean shouldCombineTextures() {
		return true;
	}
}