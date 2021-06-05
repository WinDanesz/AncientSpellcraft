package com.windanesz.ancientspellcraft.client.model;// Made with Blockbench 3.8.4

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;


public class ModelLargeSentinel extends ModelBase {
	private final ModelRenderer crystal;
	private final ModelRenderer gcube1;
	private final ModelRenderer gcube2;
	private final ModelRenderer gcube3;
	private final ModelRenderer gcube4;

	public ModelLargeSentinel() {
		textureWidth = 32;
		textureHeight = 32;

		crystal = new ModelRenderer(this);
		crystal.setRotationPoint(0.0F, 8.0F, 0.0F);
		

		gcube1 = new ModelRenderer(this);
		gcube1.setRotationPoint(2.2872F, 4.6653F, 1.068F);
		crystal.addChild(gcube1);
		setRotationAngle(gcube1, -0.7854F, 0.0F, 0.6109F);
		gcube1.cubeList.add(new ModelBox(gcube1, 5, 0, -5.2508F, -1.7274F, -3.2378F, 4, 4, 4, 0.0F, false));

		gcube2 = new ModelRenderer(this);
		gcube2.setRotationPoint(0.8232F, 2.9356F, -1.4351F);
		crystal.addChild(gcube2);
		setRotationAngle(gcube2, 0.7854F, 0.0F, -0.6109F);
		gcube2.cubeList.add(new ModelBox(gcube2, 5, 0, -2.2634F, -1.7536F, -0.2169F, 4, 4, 4, 0.0F, false));

		gcube3 = new ModelRenderer(this);
		gcube3.setRotationPoint(2.2872F, 7.0253F, 1.068F);
		crystal.addChild(gcube3);
		setRotationAngle(gcube3, -0.7854F, 0.0F, 0.6109F);
		gcube3.cubeList.add(new ModelBox(gcube3, 0, 0, -5.3144F, -1.7525F, -3.2629F, 4, 4, 4, 0.0F, false));

		gcube4 = new ModelRenderer(this);
		gcube4.setRotationPoint(0.8232F, 5.2956F, -1.4351F);
		crystal.addChild(gcube4);
		setRotationAngle(gcube4, 0.7854F, 0.0F, -0.6109F);
		gcube4.cubeList.add(new ModelBox(gcube4, 0, 0, -2.2255F, -1.7527F, -0.2178F, 4, 4, 4, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		crystal.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}