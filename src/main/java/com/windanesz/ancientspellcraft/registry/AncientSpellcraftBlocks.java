package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.NetherFire;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(AncientSpellcraft.MODID)
@Mod.EventBusSubscriber
public class AncientSpellcraftBlocks {
	private AncientSpellcraftBlocks() {} // no instances

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		final Block[] blocks = {

				new NetherFire()};

		event.getRegistry().registerAll(blocks);
	}
}
