package com.windanesz.ancientspellcraft.client.model;// Made with Blockbench 3.8.4

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelLargeSentinelc extends ModelBase {
	private final ModelRenderer crystal;
	private final ModelRenderer gcube1;
	private final ModelRenderer gcube2;
	private final ModelRenderer gcube3;
	private final ModelRenderer gcube4;

	public ModelLargeSentinelc() {
		textureWidth = 32;
		textureHeight = 32;

		crystal = new ModelRenderer(this);
		crystal.setRotationPoint(0.0F, 1.0F, 0.0F);
		

		gcube1 = new ModelRenderer(this);
		gcube1.setRotationPoint(1.9383F, 7.4621F, 0.9051F);
		crystal.addChild(gcube1);
		setRotationAngle(gcube1, -0.7854F, 0.0F, 0.6109F);
		gcube1.cubeList.add(new ModelBox(gcube1, 9, 9, -2.8593F, 0.1715F, -1.1085F, 3, 3, 3, 0.0F, false));

		gcube2 = new ModelRenderer(this);
		gcube2.setRotationPoint(0.6976F, 5.9963F, -1.2162F);
		crystal.addChild(gcube2);
		setRotationAngle(gcube2, 0.7854F, 0.0F, -0.6109F);
		gcube2.cubeList.add(new ModelBox(gcube2, 0, 0, -3.1405F, 0.1713F, -1.4513F, 3, 3, 3, 0.0F, false));

		gcube3 = new ModelRenderer(this);
		gcube3.setRotationPoint(1.9383F, 9.4621F, 0.9051F);
		crystal.addChild(gcube3);
		setRotationAngle(gcube3, -0.7854F, 0.0F, 0.6109F);
		gcube3.cubeList.add(new ModelBox(gcube3, 0, 6, -3.0F, 0.0F, -1.28F, 3, 3, 3, 0.0F, false));

		gcube4 = new ModelRenderer(this);
		gcube4.setRotationPoint(0.6976F, 7.9963F, -1.2162F);
		crystal.addChild(gcube4);
		setRotationAngle(gcube4, 0.7854F, 0.0F, -0.6109F);
		gcube4.cubeList.add(new ModelBox(gcube4, 9, 3, -3.0F, 0.0F, -1.28F, 3, 3, 3, 0.0F, false));
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