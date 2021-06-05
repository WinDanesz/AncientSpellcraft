package com.windanesz.ancientspellcraft.material;

import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class Devoritium {

	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityMagicConstruct && event.getEntity().world.getBlockState(event.getEntity().getPosition().down()).getBlock() instanceof IDevoritium) {
			((EntityMagicConstruct) event.getEntity()).lifetime = 20;
		}
	}

}
