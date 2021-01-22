package com.windanesz.ancientspellcraft.client.renderer.entity.layers;

import com.windanesz.ancientspellcraft.entity.living.EntitySkeletonHorseMinion;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerSkeletonHorseArmor implements LayerRenderer<EntitySkeletonHorseMinion> {
	private static final ResourceLocation STRAY_CLOTHES_TEXTURES = new ResourceLocation("ancientspellcraft:textures/entity/skeleton_horse_armor.png");
	private final RenderLivingBase<?> renderer;
	private final ModelHorse layerModel = new ModelHorse();

	public LayerSkeletonHorseArmor(RenderLivingBase<?> rendererIn) {
		this.renderer = rendererIn;
	}

	public void doRenderLayer(EntitySkeletonHorseMinion skeletonHorseMinion, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		this.layerModel.setModelAttributes(this.renderer.getMainModel());
		this.layerModel.setLivingAnimations(skeletonHorseMinion, limbSwing, limbSwingAmount, partialTicks);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.renderer.bindTexture(STRAY_CLOTHES_TEXTURES);
		this.layerModel.render(skeletonHorseMinion, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
	}

	public boolean shouldCombineTextures() {
		return true;
	}
}