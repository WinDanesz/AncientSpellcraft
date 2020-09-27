package com.artemis.artemislib.compatibilities.sizeCap;

import net.minecraft.nbt.NBTTagCompound;

public interface ISizeCap {

	boolean getTrans();

	void setTrans(boolean transformed);

	float getDefaultWidth();

	void setDefaultWidth(float width);

	float getDefaultHeight();

	void setDefaultHeight(float height);

	NBTTagCompound saveNBT();

	void loadNBT(NBTTagCompound compound);

}
