package com.windanesz.ancientspellcraft.client.model;// Made with Blockbench 3.6.6

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;


public class ModelSentinel2 extends ModelBase {
	private final ModelRenderer bb_main;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;

	public ModelSentinel2() {
		textureWidth = 32;
		textureHeight = 32;

		bb_main = new ModelRenderer(this);
		bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
		bb_main.cubeList.add(new ModelBox(bb_main, 0, 0, -2.0F, -1.0F, -2.0F, 4, 1, 4, 0.0F, false));
		bb_main.cubeList.add(new ModelBox(bb_main, 0, 11, -1.0F, -3.0F, -1.0F, 2, 2, 2, 0.0F, false));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(1.9383F, -7.4621F, 0.9051F);
		bb_main.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.7854F, 0.0F, -0.6109F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 5, -3.0F, -3.0F, -1.28F, 3, 3, 3, 0.0F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(0.6976F, -5.9963F, -1.2162F);
		bb_main.addChild(cube_r2);
		setRotationAngle(cube_r2, -0.7854F, 0.0F, 0.6109F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 9, 9, -3.0F, -3.0F, -1.28F, 3, 3, 3, 0.0F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(-1.0F, -1.5858F, 0.0F);
		bb_main.addChild(cube_r3);
		setRotationAngle(cube_r3, -0.7854F, 0.0F, 0.0F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 12, 15, 0.0F, -4.0F, -1.0F, 2, 3, 1, 0.0F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(-1.0F, -1.5858F, 0.0F);
		bb_main.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.7854F, 0.0F, 0.0F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 17, 18, 0.0F, -4.0F, 0.0F, 2, 3, 1, 0.0F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(-0.7071F, -0.8787F, 0.0F);
		bb_main.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.0F, 0.0F, -0.7854F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 14, 3, 1.0F, -4.0F, -1.0F, 1, 3, 2, 0.0F, false));
		cube_r5.cubeList.add(new ModelBox(cube_r5, 0, 15, 1.0F, -4.0F, -1.0F, 1, 3, 2, 0.0F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(0.7071F, -0.8787F, 0.0F);
		bb_main.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.0F, 0.0F, 0.7854F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 6, 15, -2.0F, -4.0F, -1.0F, 1, 3, 2, 0.0F, false));
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