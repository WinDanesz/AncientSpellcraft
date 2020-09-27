package com.windanesz.ancientspellcraft.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelVenusFlyTrap extends ModelBase {
	private final ModelRenderer base = new ModelRenderer(this, 0, 0);
	private final ModelRenderer upperJaw;
	private final ModelRenderer lowerJaw;

	public ModelVenusFlyTrap() {
//		this.base.setRotationPoint(-5.0F, 22.0F, -5.0F);
//		this.base.addBox(0.0F, 0.0F, 0.0F, 2, 12, 2);
//		this.upperJaw = new ModelRenderer(this, 40, 0);
//		this.upperJaw.setRotationPoint(1.5F, 22.0F, -4.0F);
//		this.upperJaw.addBox(0.0F, 0.0F, 0.0F, 4, 14, 16);
//		this.lowerJaw = new ModelRenderer(this, 40, 0);
//		this.lowerJaw.setRotationPoint(-1.5F, 22.0F, 4.0F);
//		this.lowerJaw.addBox(0.0F, 0.0F, 0.0F, 4, 14, 16);
		textureWidth = 64;
		textureHeight = 64;

		this.base.setRotationPoint(-5.0F, 22.0F, -5.0F);
		this.base.setTextureOffset(0, 26).addBox(4.0F, 0.0F, 4.0F, 2, 12, 2);

		this.upperJaw = new ModelRenderer(this);
		this.upperJaw.setRotationPoint(1.5F, 22.0F, -4.0F);
		this.upperJaw.setTextureOffset(0, 0).addBox(0.0F, 0.0F, -2.0F, 4, 14, 12);

		this.lowerJaw = new ModelRenderer(this);
		this.lowerJaw.setRotationPoint(-1.5F, 22.0F, 4.0F);
		this.lowerJaw.setTextureOffset(20, 20).addBox(0.0F, 0.0F, -2.0F, 4, 14, 12);
	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		float f = limbSwing * 2.0F;

		if (f > 1.0F) {
			f = 1.0F;
		}

		f = 1.0F - f * f * f;
		this.upperJaw.rotateAngleZ = (float) Math.PI - f * 0.35F * (float) Math.PI;
		this.lowerJaw.rotateAngleZ = (float) Math.PI + f * 0.35F * (float) Math.PI;
		this.lowerJaw.rotateAngleY = (float) Math.PI;
		float f1 = (limbSwing + MathHelper.sin(limbSwing * 2.7F)) * 0.6F * 12.0F;
		this.upperJaw.rotationPointY = 24.0F - f1;
		this.lowerJaw.rotationPointY = this.upperJaw.rotationPointY;
		this.base.rotationPointY = this.upperJaw.rotationPointY;
//		this.base.render(scale);
		this.upperJaw.render(scale);
		this.lowerJaw.render(scale);
	}
}
