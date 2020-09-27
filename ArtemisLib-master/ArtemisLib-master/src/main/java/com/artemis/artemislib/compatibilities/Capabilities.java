package com.artemis.artemislib.compatibilities;

import java.util.concurrent.Callable;

import com.artemis.artemislib.compatibilities.sizeCap.ISizeCap;
import com.artemis.artemislib.compatibilities.sizeCap.SizeCapStorage;
import com.artemis.artemislib.compatibilities.sizeCap.SizeDefaultCap;

import net.minecraftforge.common.capabilities.CapabilityManager;

public class Capabilities {

	public static void init()
	{
		CapabilityManager.INSTANCE.register(ISizeCap.class, new SizeCapStorage(), new CababilityFactory());
	}

	private static class CababilityFactory implements Callable<ISizeCap>
	{
		@Override
		public ISizeCap call() throws Exception
		{
			return new SizeDefaultCap();
		}
	}
}
