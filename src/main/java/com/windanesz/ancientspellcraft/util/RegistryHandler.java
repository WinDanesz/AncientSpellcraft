package com.windanesz.ancientspellcraft.util;

import com.windanesz.ancientspellcraft.block.NetherFire;
import com.windanesz.ancientspellcraft.spell.HellGate;
import electroblob.wizardry.spell.Spell;
import net.minecraft.block.Block;
import net.minecraft.item.EnumAction;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class RegistryHandler {
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		final Block[] blocks = {

				new NetherFire()};

		event.getRegistry().registerAll(blocks);
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Spell> event) {
		event.getRegistry().register(new HellGate("ancientspellcraft", "hellgate", EnumAction.BLOCK, false));

	}
}
