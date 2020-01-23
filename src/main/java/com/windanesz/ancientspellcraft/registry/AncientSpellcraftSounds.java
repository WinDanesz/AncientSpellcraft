package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;
import java.util.Map;

@GameRegistry.ObjectHolder(AncientSpellcraft.MOD_ID)
@Mod.EventBusSubscriber(modid = AncientSpellcraft.MOD_ID)
public class AncientSpellcraftSounds {

	public static Map<String, SoundEvent> SoundEvents = new HashMap<>();

	private AncientSpellcraftSounds() {}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<SoundEvent> event) {
		IForgeRegistry<SoundEvent> registry = event.getRegistry();

		registry.register(createSoundEvent("spell.hellgate"));
		registry.register(createSoundEvent("spell.tameanimal"));
	}

	private static SoundEvent createSoundEvent(String soundName) {
		ResourceLocation registryName = new ResourceLocation(AncientSpellcraft.MOD_ID, soundName);
		SoundEvent soundEvent = new SoundEvent(registryName).setRegistryName(registryName);
		SoundEvents.put(soundName, soundEvent);
		return soundEvent;
	}

}