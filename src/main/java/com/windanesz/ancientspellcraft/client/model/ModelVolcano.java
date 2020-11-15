package com.windanesz.ancientspellcraft.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelVolcano extends ModelBase {
	private final ModelRenderer volcano;

	public ModelVolcano() {
		textureWidth = 96;
		textureHeight = 96;

		volcano = new ModelRenderer(this);
		volcano.setRotationPoint(0.0F, 24.0F, 0.0F);
		volcano.cubeList.add(new ModelBox(volcano, 0, 0, -8.0F, -6.0F, -8.0F, 16, 6, 16, 0.0F, false));
		volcano.cubeList.add(new ModelBox(volcano, 0, 22, -7.0F, -11.0F, -7.0F, 14, 5, 14, 0.0F, false));
		volcano.cubeList.add(new ModelBox(volcano, 0, 41, -6.0F, -15.0F, -6.0F, 12, 4, 12, 0.0F, false));
		volcano.cubeList.add(new ModelBox(volcano, 42, 22, -5.0F, -19.0F, -5.0F, 10, 4, 10, 0.0F, false));
		volcano.cubeList.add(new ModelBox(volcano, 42, 22, -5.0F, -19.0F, -5.0F, 10, 4, 10, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		volcano.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}