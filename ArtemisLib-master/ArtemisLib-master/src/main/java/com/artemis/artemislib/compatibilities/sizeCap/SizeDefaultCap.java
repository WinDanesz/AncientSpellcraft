package com.artemis.artemislib.compatibilities.sizeCap;

import net.minecraft.nbt.NBTTagCompound;

public class SizeDefaultCap implements ISizeCap {

	boolean transformed = false;
	float defaultWidth;
	float defaultHeight;

	public SizeDefaultCap(){}

	public SizeDefaultCap(boolean transformed, float defaultWidth, float defaultHeight)
	{
		this.transformed = transformed;
		this.defaultWidth = defaultWidth;
		this.defaultHeight = defaultHeight;
	}

	@Override
	public boolean getTrans()
	{
		return this.transformed;
	}

	@Override
	public void setTrans(boolean transformed)
	{
		this.transformed = transformed;
	}

	@Override
	public float getDefaultWidth()
	{
		return this.defaultWidth;
	}

	@Override
	public void setDefaultWidth(float defaultWidth)
	{
		this.defaultWidth = defaultWidth;
	}

	@Override
	public float getDefaultHeight() {
		return this.defaultHeight;
	}

	@Override
	public void setDefaultHeight(float defaultHeight)
	{
		this.defaultHeight = defaultHeight;
	}

	@Override
	public NBTTagCompound saveNBT()
	{
		return (NBTTagCompound) SizeCapStorage.storage.writeNBT(SizeCapPro.sizeCapability, this, null);
	}

	@Override
	public void loadNBT(NBTTagCompound compound)
	{
		SizeCapStorage.storage.readNBT(SizeCapPro.sizeCapability, this, null, compound);
	}
}
