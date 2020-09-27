package com.artemis.artemislib.compatibilities.sizeCap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class SizeCapPro implements ICapabilitySerializable<NBTTagCompound>{

	private ISizeCap capabilitySize = null;

	public SizeCapPro()
	{
		this.capabilitySize = new SizeDefaultCap();
	}

	public SizeCapPro(ISizeCap capability)
	{
		this.capabilitySize = capability;
	}

	@CapabilityInject(ISizeCap.class)
	public static final Capability<ISizeCap> sizeCapability = null;

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == sizeCapability;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (sizeCapability != null && capability == sizeCapability)
		{
			return (T) this.capabilitySize;
		}
		return null;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		return this.capabilitySize.saveNBT();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		this.capabilitySize.loadNBT(nbt);
	}
}