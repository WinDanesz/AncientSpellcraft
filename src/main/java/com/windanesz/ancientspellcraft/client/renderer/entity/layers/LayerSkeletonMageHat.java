package com.windanesz.ancientspellcraft.client.renderer.entity.layers;

import com.windanesz.ancientspellcraft.entity.living.EntitySkeletonMage;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public abstract class LayerSkeletonMageHat extends LayerArmorBase<ModelBiped> {

	private final RenderLivingBase<?> renderer;

	protected ModelBiped modelLeggings;
	protected ModelBiped modelArmor;
	private float alpha = 1.0F;
	private float colorR = 1.0F;
	private float colorG = 1.0F;
	private float colorB = 1.0F;

	public LayerSkeletonMageHat(RenderLivingBase<?> rendererIn) {
		super(rendererIn);
		this.renderer = rendererIn;
	}

	private boolean isLegSlot(EntityEquipmentSlot slotIn) {
		return slotIn == EntityEquipmentSlot.LEGS;
	}

	@Override
	protected void setModelSlotVisible(ModelBiped modelBiped, EntityEquipmentSlot slot) {
		this.setModelVisible(modelBiped);
		switch (slot) {
			case HEAD:
				modelBiped.bipedHead.showModel = true;
				modelBiped.bipedHeadwear.showModel = true;
				break;
			case CHEST:
				modelBiped.bipedBody.showModel = true;
				modelBiped.bipedRightArm.showModel = true;
				modelBiped.bipedLeftArm.showModel = true;
				break;
			case LEGS:
				modelBiped.bipedBody.showModel = true;
				modelBiped.bipedRightLeg.showModel = true;
				modelBiped.bipedLeftLeg.showModel = true;
				break;
			case FEET:
				modelBiped.bipedRightLeg.showModel = true;
				modelBiped.bipedLeftLeg.showModel = true;
		}
	}

	@Override
	public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		this.renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.CHEST);
		this.renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.LEGS);
		this.renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.FEET);
		this.renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.HEAD);
	}

	protected void setModelVisible(ModelBiped model) {
		model.setVisible(false);
	}

	private void renderArmorLayer(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn) {
		ItemStack itemstack = entityLivingBaseIn.getItemStackFromSlot(slotIn);

		if (entityLivingBaseIn instanceof EntitySkeletonMage) { // Always true
			if (((EntitySkeletonMage) entityLivingBaseIn).getOpacity() < 1) {
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				colorR = 0.6f;
				colorG = 1;
				colorB = 0.6f;
				alpha = ((EntitySkeletonMage) entityLivingBaseIn).getOpacity();
			} else {
				colorR = 1f;
				colorG = 1f;
				colorB = 1f;
				alpha = 1f;

			}
		}

		if (itemstack.getItem() instanceof ItemArmor) {
			ItemArmor itemarmor = (ItemArmor) itemstack.getItem();

			if (itemarmor.getEquipmentSlot() == slotIn) {
				ModelBiped t = this.getModelFromSlot(slotIn);
				t = getArmorModelHook(entityLivingBaseIn, itemstack, slotIn, t);
				t.setModelAttributes(this.renderer.getMainModel());
				t.setLivingAnimations(entityLivingBaseIn, limbSwing, limbSwingAmount, partialTicks);
				this.setModelSlotVisible(t, slotIn);
				boolean flag = this.isLegSlot(slotIn);
				this.renderer.bindTexture(this.getArmorResource(entityLivingBaseIn, itemstack, slotIn, null));

				{
					if (itemarmor.hasOverlay(itemstack)) // Allow this for anything, not only cloth
					{
						int i = itemarmor.getColor(itemstack);
						float f = (float) (i >> 16 & 255) / 255.0F;
						float f1 = (float) (i >> 8 & 255) / 255.0F;
						float f2 = (float) (i & 255) / 255.0F;
						GlStateManager.color(this.colorR * f, this.colorG * f1, this.colorB * f2, this.alpha);
						t.render(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
						this.renderer.bindTexture(this.getArmorResource(entityLivingBaseIn, itemstack, slotIn, "overlay"));
					}
					{ // Non-colored
						GlStateManager.color(this.colorR, this.colorG, this.colorB, this.alpha);
						t.render(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
					} // Default
					boolean skipRenderGlint = true;
					if (!skipRenderGlint && itemstack.hasEffect()) {
						renderEnchantedGlint(this.renderer, entityLivingBaseIn, t, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
					}
				}
			}
		}
	}

	@Override
	protected ModelBiped getArmorModelHook(EntityLivingBase entity, ItemStack itemStack, EntityEquipmentSlot slot, ModelBiped model) {
		return net.minecraftforge.client.ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
	}
}
