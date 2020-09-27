package com.windanesz.ancientspellcraft.client.model;// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelAnt extends ModelBase {
	private final ModelRenderer spiderHead;
	private final ModelRenderer bone3;
	private final ModelRenderer bone2;
	private final ModelRenderer spiderNeck;
	private final ModelRenderer spiderBody;
	private final ModelRenderer bone;
	private final ModelRenderer spiderLeg1;
	private final ModelRenderer spiderLeg2;
	private final ModelRenderer spiderLeg3;
	private final ModelRenderer spiderLeg4;
	private final ModelRenderer spiderLeg5;
	private final ModelRenderer spiderLeg6;
	private final ModelRenderer spiderLeg7;
	private final ModelRenderer spiderLeg8;

	public ModelAnt() {
		textureWidth = 64;
		textureHeight = 64;

		spiderHead = new ModelRenderer(this);
		spiderHead.setRotationPoint(0.0F, 13.0F, -3.0F);
//		rotateAngleX, 0.2618F, 0.0F, 0.0F);
		spiderHead.cubeList.add(new ModelBox(spiderHead, 25, 31, -2.0F, -2.0F, -6.0F, 4, 4, 5, 0.0F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(-1.0F, 2.0F, -8.0F);
		spiderHead.addChild(bone3);
//		setRotationAngle(bone3, 0.0F, -0.3927F, 0.0F);
		bone3.cubeList.add(new ModelBox(bone3, 0, 4, -0.7758F, -2.0F, 0.1543F, 1, 1, 3, 0.0F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(1.0F, 2.0F, -8.0F);
		spiderHead.addChild(bone2);
//		setRotationAngle(bone2, 0.0F, 0.3927F, 0.0F);
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, -0.1481F, -2.0F, -0.2284F, 1, 1, 3, 0.0F, false));

		spiderNeck = new ModelRenderer(this);
		spiderNeck.setRotationPoint(0.0F, 15.0F, 0.0F);
		spiderNeck.cubeList.add(new ModelBox(spiderNeck, 0, 0, -2.0F, -2.0F, -4.0F, 4, 4, 13, 0.0F, false));

		spiderBody = new ModelRenderer(this);
		spiderBody.setRotationPoint(0.0F, 15.0F, 9.0F);


		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 9.0F, -2.0F);
		spiderBody.addChild(bone);
//		setRotationAngle(bone, 0.1745F, 0.0F, 0.0F);
		bone.cubeList.add(new ModelBox(bone, 0, 17, -3.0F, -11.1566F, 2.4627F, 6, 4, 8, 0.0F, false));
		bone.cubeList.add(new ModelBox(bone, 10, 9, -0.5F, -8.4924F, 8.0546F, 1, 1, 6, 0.0F, false));

		spiderLeg1 = new ModelRenderer(this);
		spiderLeg1.setRotationPoint(-4.0F, 15.0F, 2.0F);
		spiderLeg1.cubeList.add(new ModelBox(spiderLeg1, 0, 31, -11.0F, -1.0F, 2.0F, 13, 2, 2, 0.0F, false));

		spiderLeg2 = new ModelRenderer(this);
		spiderLeg2.setRotationPoint(4.0F, 15.0F, 2.0F);
		spiderLeg2.cubeList.add(new ModelBox(spiderLeg2, 26, 27, -2.0F, -1.0F, 2.0F, 13, 2, 2, 0.0F, false));

		spiderLeg3 = new ModelRenderer(this);
		spiderLeg3.setRotationPoint(-4.0F, 15.0F, 1.0F);


		spiderLeg4 = new ModelRenderer(this);
		spiderLeg4.setRotationPoint(4.0F, 15.0F, 1.0F);


		spiderLeg5 = new ModelRenderer(this);
		spiderLeg5.setRotationPoint(-4.0F, 15.0F, 0.0F);
		spiderLeg5.cubeList.add(new ModelBox(spiderLeg5, 21, 8, -11.0F, -1.0F, 1.0F, 13, 2, 2, 0.0F, false));

		spiderLeg6 = new ModelRenderer(this);
		spiderLeg6.setRotationPoint(4.0F, 15.0F, 0.0F);
		spiderLeg6.cubeList.add(new ModelBox(spiderLeg6, 21, 4, -2.0F, -1.0F, 1.0F, 13, 2, 2, 0.0F, false));

		spiderLeg7 = new ModelRenderer(this);
		spiderLeg7.setRotationPoint(-4.0F, 15.0F, -1.0F);
		spiderLeg7.cubeList.add(new ModelBox(spiderLeg7, 21, 0, -11.0F, -1.0F, -2.0F, 13, 2, 2, 0.0F, false));

		spiderLeg8 = new ModelRenderer(this);
		spiderLeg8.setRotationPoint(4.0F, 15.0F, -1.0F);
		spiderLeg8.cubeList.add(new ModelBox(spiderLeg8, 20, 20, -2.0F, -1.0F, -2.0F, 13, 2, 2, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		spiderHead.render(f5);
		spiderNeck.render(f5);
		spiderBody.render(f5);
		spiderLeg1.render(f5);
		spiderLeg2.render(f5);
		spiderLeg3.render(f5);
		spiderLeg4.render(f5);
		spiderLeg5.render(f5);
		spiderLeg6.render(f5);
		spiderLeg7.render(f5);
		spiderLeg8.render(f5);
	}

//		modelRenderer.rotateAngleX = x;
//		modelRenderer.rotateAngleY = y;
//		modelRenderer.rotateAngleZ = z;
	/**
	 * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
	 * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
	 * "far" arms and legs can swing at most.
	 */
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
	{
		this.spiderHead.rotateAngleY = netHeadYaw * 0.017453292F;
		this.spiderHead.rotateAngleX = headPitch * 0.017453292F;
		float f = ((float)Math.PI / 4F);
		this.spiderLeg1.rotateAngleZ = -((float)Math.PI / 4F);
		this.spiderLeg2.rotateAngleZ = ((float)Math.PI / 4F);
		this.spiderLeg3.rotateAngleZ = -0.58119464F;
		this.spiderLeg4.rotateAngleZ = 0.58119464F;
		this.spiderLeg5.rotateAngleZ = -0.58119464F;
		this.spiderLeg6.rotateAngleZ = 0.58119464F;
		this.spiderLeg7.rotateAngleZ = -((float)Math.PI / 4F);
		this.spiderLeg8.rotateAngleZ = ((float)Math.PI / 4F);
		float f1 = -0.0F;
		float f2 = 0.3926991F;
		this.spiderLeg1.rotateAngleY = ((float)Math.PI / 4F);
		this.spiderLeg2.rotateAngleY = -((float)Math.PI / 4F);
		this.spiderLeg3.rotateAngleY = 0.3926991F;
		this.spiderLeg4.rotateAngleY = -0.3926991F;
		this.spiderLeg5.rotateAngleY = -0.3926991F;
		this.spiderLeg6.rotateAngleY = 0.3926991F;
		this.spiderLeg7.rotateAngleY = -((float)Math.PI / 4F);
		this.spiderLeg8.rotateAngleY = ((float)Math.PI / 4F);
		float f3 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbSwingAmount;
		float f4 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + (float)Math.PI) * 0.4F) * limbSwingAmount;
		float f5 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + ((float)Math.PI / 2F)) * 0.4F) * limbSwingAmount;
		float f6 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + ((float)Math.PI * 3F / 2F)) * 0.4F) * limbSwingAmount;
		float f7 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + 0.0F) * 0.4F) * limbSwingAmount;
		float f8 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + (float)Math.PI) * 0.4F) * limbSwingAmount;
		float f9 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + ((float)Math.PI / 2F)) * 0.4F) * limbSwingAmount;
		float f10 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + ((float)Math.PI * 3F / 2F)) * 0.4F) * limbSwingAmount;
		this.spiderLeg1.rotateAngleY += f3;
		this.spiderLeg2.rotateAngleY += -f3;
		this.spiderLeg3.rotateAngleY += f4;
		this.spiderLeg4.rotateAngleY += -f4;
		this.spiderLeg5.rotateAngleY += f5;
		this.spiderLeg6.rotateAngleY += -f5;
		this.spiderLeg7.rotateAngleY += f6;
		this.spiderLeg8.rotateAngleY += -f6;
		this.spiderLeg1.rotateAngleZ += f7;
		this.spiderLeg2.rotateAngleZ += -f7;
		this.spiderLeg3.rotateAngleZ += f8;
		this.spiderLeg4.rotateAngleZ += -f8;
		this.spiderLeg5.rotateAngleZ += f9;
		this.spiderLeg6.rotateAngleZ += -f9;
		this.spiderLeg7.rotateAngleZ += f10;
		this.spiderLeg8.rotateAngleZ += -f10;
	}
}