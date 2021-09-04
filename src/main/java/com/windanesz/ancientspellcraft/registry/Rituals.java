package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.ritual.ArcaneBarrier;
import com.windanesz.ancientspellcraft.ritual.Bonfire;
import com.windanesz.ancientspellcraft.ritual.Condensing;
import com.windanesz.ancientspellcraft.ritual.Forest;
import com.windanesz.ancientspellcraft.ritual.None;
import com.windanesz.ancientspellcraft.ritual.Rejuvenation;
import com.windanesz.ancientspellcraft.ritual.Ritual;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import javax.annotation.Nonnull;

@ObjectHolder(AncientSpellcraft.MODID)
@Mod.EventBusSubscriber
public final class Rituals {

	private Rituals() {} // no instances

	// This is here because this class is already an event handler.
	@SubscribeEvent
	public static void createRegistry(RegistryEvent.NewRegistry event){

		RegistryBuilder<Ritual> builder = new RegistryBuilder<>();
		builder.setType(Ritual.class);
		builder.setName(new ResourceLocation(AncientSpellcraft.MODID, "rituals"));
		builder.setIDRange(0, 5000); // Is there any penalty for using a larger number?

		Ritual.registry = builder.create();
	}

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	private static <T> T placeholder(){ return null; }

	// AS 1.4 spells
	public static final Ritual none = placeholder();
	public static final Ritual elevation = placeholder();
	//public static final Ritual mark = placeholder();
	public static final Ritual forest = placeholder();
	public static final Ritual bonfire = placeholder();
	public static final Ritual condensing = placeholder();
	public static final Ritual arcane_barrier = placeholder();


	@SubscribeEvent
	public static void register(RegistryEvent.Register<Ritual> event){

		IForgeRegistry<Ritual> registry = event.getRegistry();

		registry.register(new None()); // dummy ritual

		registry.register(new Rejuvenation());
		//registry.register(new Mark());
		registry.register(new Forest());
		registry.register(new Bonfire());
		registry.register(new Condensing());
		registry.register(new ArcaneBarrier());
	}

}
