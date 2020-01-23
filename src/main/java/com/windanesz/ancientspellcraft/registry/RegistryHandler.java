package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.NetherFire;
import com.windanesz.ancientspellcraft.spell.HellGate;
import com.windanesz.ancientspellcraft.spell.SummonLicheLord;
import com.windanesz.ancientspellcraft.spell.TameAnimal;
import electroblob.wizardry.entity.living.EntitySkeletonMinion;
import electroblob.wizardry.spell.Spell;
import net.minecraft.block.Block;
import net.minecraft.item.EnumAction;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class RegistryHandler {

	private static final String modId = AncientSpellcraft.MOD_ID;

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		final Block[] blocks = {

				new NetherFire()};

		event.getRegistry().registerAll(blocks);
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Spell> event) {
		event.getRegistry().register(new HellGate(modId, "hellgate", EnumAction.BLOCK, false));
		event.getRegistry().register(new TameAnimal(modId, "tameanimal", false, EnumAction.BLOCK));
		event.getRegistry().register(new SummonLicheLord(modId, "summonlichelord", EntitySkeletonMinion::new));
	}
}
