package com.windanesz.ancientspellcraft.client.model;

import net.minecraft.client.model.ModelZombie;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelAnimatedItem extends ModelZombie {
	public ModelAnimatedItem() {
		this(0.0F, false);
	}

	public ModelAnimatedItem(float modelSize, boolean p_i1168_2_) {
		super(modelSize, p_i1168_2_);
		this.bipedRightArm.cubeList.clear();
		this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
		this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + 0.0f, 0.0F);
	}
}