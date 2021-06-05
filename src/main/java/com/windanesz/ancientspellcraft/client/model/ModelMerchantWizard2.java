package com.windanesz.ancientspellcraft.client.model;

// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelMerchantWizard2 extends ModelBase {
	ModelRenderer Shape5;
	ModelRenderer Shape8;
	ModelRenderer Shape9;
	ModelRenderer Shape10;
	ModelRenderer Shape7;
	ModelRenderer Shape11;
	ModelRenderer Shape12;
	ModelRenderer Shape13;
	ModelRenderer beard;

	public ModelMerchantWizard2() {
		textureWidth = 16;
		textureHeight = 16;

		Shape5 = new ModelRenderer(this);
		Shape5.setRotationPoint(-6.0F, -7.0F, -6.0F);
		Shape5.cubeList.add(new ModelBox(Shape5, 0, 51, 0.0F, 0.0F, 0.0F, 12, 1, 12, 0.0F, true));

		Shape8 = new ModelRenderer(this);
		Shape8.setRotationPoint(-3.0F, -9.0F, -3.0F);
		setRotationAngle(Shape8, -0.0349F, 0.0F, 0.0F);
		Shape8.cubeList.add(new ModelBox(Shape8, 0, 32, 0.0F, 0.0F, 0.0F, 6, 1, 6, 0.0F, true));

		Shape9 = new ModelRenderer(this);
		Shape9.setRotationPoint(-1.5F, -13.0F, -0.5F);
		setRotationAngle(Shape9, -0.2512F, 0.0F, 0.0F);
		Shape9.cubeList.add(new ModelBox(Shape9, 24, 32, 0.0F, 0.0F, 0.0F, 3, 3, 3, 0.0F, true));

		Shape10 = new ModelRenderer(this);
		Shape10.setRotationPoint(-2.5F, -10.0F, -2.5F);
		setRotationAngle(Shape10, -0.0698F, 0.0F, 0.0F);
		Shape10.cubeList.add(new ModelBox(Shape10, 0, 39, 0.0F, 0.0F, 0.0F, 5, 1, 5, 0.0F, true));

		Shape7 = new ModelRenderer(this);
		Shape7.setRotationPoint(-2.0F, -11.0F, -1.5F);
		setRotationAngle(Shape7, -0.1396F, 0.0F, 0.0F);
		Shape7.cubeList.add(new ModelBox(Shape7, 0, 45, 0.0F, 0.0F, 0.0F, 4, 2, 4, 0.0F, true));

		Shape11 = new ModelRenderer(this);
		Shape11.setRotationPoint(-1.0F, -15.0F, 1.0F);
		setRotationAngle(Shape11, -0.4363F, 0.0F, 0.0F);
		Shape11.cubeList.add(new ModelBox(Shape11, 20, 39, 0.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F, true));

		Shape12 = new ModelRenderer(this);
		Shape12.setRotationPoint(-0.5F, -16.0F, 2.5F);
		setRotationAngle(Shape12, -0.7156F, 0.0F, 0.0F);
		Shape12.cubeList.add(new ModelBox(Shape12, 28, 39, 0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F, true));

		Shape13 = new ModelRenderer(this);
		Shape13.setRotationPoint(-4.0F, 0.0F, -3.0F);
		Shape13.cubeList.add(new ModelBox(Shape13, 36, 16, 4.0F, 0.0F, 2.0F, 8, 20, 6, 0.0F, true));

		beard = new ModelRenderer(this);
		beard.setRotationPoint(-4.0F, 0.0F, -4.0F);
		beard.cubeList.add(new ModelBox(beard, 32, 0, 0.0F, 0.0F, 0.0F, 8, 5, 0, 0.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		Shape5.render(f5);
		Shape8.render(f5);
		Shape9.render(f5);
		Shape10.render(f5);
		Shape7.render(f5);
		Shape11.render(f5);
		Shape12.render(f5);
		Shape13.render(f5);
		beard.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}