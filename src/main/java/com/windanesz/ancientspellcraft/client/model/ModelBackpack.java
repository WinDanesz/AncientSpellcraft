package com.windanesz.ancientspellcraft.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBackpack extends ModelBase {
	private final ModelRenderer Backpack;

	public ModelBackpack() {
		textureWidth = 64;
		textureHeight = 64;

		Backpack = new ModelRenderer(this);
		Backpack.setRotationPoint(-7.5F, 2.0F, -6.0F);
		Backpack.cubeList.add(new ModelBox(Backpack, 0, 0, 1.0F, -4.0F, 8.0F, 13, 12, 6, 0.0F, false));
		Backpack.cubeList.add(new ModelBox(Backpack, 26, 30, 2.0F, -2.0F, 14.0F, 11, 8, 1, 0.0F, false));
		Backpack.cubeList.add(new ModelBox(Backpack, 0, 18, -1.0F, -1.0F, 9.0F, 17, 8, 4, 0.0F, false));
		Backpack.cubeList.add(new ModelBox(Backpack, 0, 30, 3.0F, -5.0F, 9.0F, 9, 5, 4, 0.0F, false));
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		Backpack.render(scale);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}