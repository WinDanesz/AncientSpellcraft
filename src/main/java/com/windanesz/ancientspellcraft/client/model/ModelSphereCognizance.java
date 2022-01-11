package com.windanesz.ancientspellcraft.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSphereCognizance extends ModelBase {
	public ModelRenderer ball;

	public ModelSphereCognizance() {
		textureWidth = 16;
		textureHeight = 16;

		ball = new ModelRenderer(this);
		ball.setRotationPoint(0.0F, 0.0F, 0.0F);
		ball.cubeList.add(new ModelBox(ball, 0, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, false));
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		ball.render(scale);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}