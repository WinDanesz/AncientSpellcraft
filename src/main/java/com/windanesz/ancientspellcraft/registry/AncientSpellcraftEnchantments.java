package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.enchantment.EnchantmentTimed;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;

/**
 * Class responsible for defining, storing and registering all of wizardry's enchantments.
 *
 * @author WinDanesz
 */

@GameRegistry.ObjectHolder(AncientSpellcraft.MODID)
@Mod.EventBusSubscriber
public class AncientSpellcraftEnchantments {

	private AncientSpellcraftEnchantments() {}

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	private static <T> T placeholder() { return null; }

	public static final Enchantment static_charge = placeholder();

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Enchantment> event) {

		event.getRegistry().register(new EnchantmentTimed().setRegistryName(AncientSpellcraft.MODID, "static_charge"));
	}

}

