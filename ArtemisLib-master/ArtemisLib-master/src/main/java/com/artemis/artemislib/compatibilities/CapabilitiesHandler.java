package com.artemis.artemislib.compatibilities;

import com.artemis.artemislib.Reference;
import com.artemis.artemislib.compatibilities.sizeCap.ISizeCap;
import com.artemis.artemislib.compatibilities.sizeCap.SizeCapPro;
import com.artemis.artemislib.compatibilities.sizeCap.SizeDefaultCap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilitiesHandler {

	@SubscribeEvent
	public void onAddCapabilites(AttachCapabilitiesEvent<Entity> event)
	{
		if(event.getObject() instanceof EntityLivingBase  && !event.getObject().hasCapability(SizeCapPro.sizeCapability, null))
		{
			final EntityLivingBase entity = (EntityLivingBase) event.getObject();
			final boolean transformed = false;
			final float defaultWidth = entity.width;
			final float defaultHeight = entity.height;
			final ISizeCap cap = new SizeDefaultCap(transformed, defaultWidth, defaultHeight);
			event.addCapability(new ResourceLocation(Reference.MODID, "Capability"), new SizeCapPro(cap));
		}
	}
}
