package com.windanesz.ancientspellcraft.client.model;// Made with Blockbench 3.8.4

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelSentinelbackup extends ModelBase {
	private final ModelRenderer bb_main;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;

	public ModelSentinelbackup() {
		textureWidth = 32;
		textureHeight = 32;

		bb_main = new ModelRenderer(this);
		bb_main.setRotationPoint(0.0F, 1.0F, 0.0F);
		

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(1.9383F, 7.4621F, 0.9051F);
		bb_main.addChild(cube_r1);
		setRotationAngle(cube_r1, -0.7854F, 0.0F, 0.6109F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 5, -3.0F, 0.0F, -1.28F, 3, 3, 3, 0.0F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(0.6976F, 5.9963F, -1.2162F);
		bb_main.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.7854F, 0.0F, -0.6109F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 9, 9, -3.0F, 0.0F, -1.28F, 3, 3, 3, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bb_main.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}