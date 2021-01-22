package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(AncientSpellcraft.MODID)
@Mod.EventBusSubscriber(modid = AncientSpellcraft.MODID)
public class AncientSpellcraftSounds {
	private AncientSpellcraftSounds() {}

	public static final SoundEvent ENTITY_HEALING_HEATH_HEALS = createSound("entity.healing_heath_heals");
	public static final SoundEvent ENTITY_HEALING_HEALTH_DAMAGES = createSound("entity.healing_health_damages");
	public static final SoundEvent ENTITY_TRANSPORTATION_PORTAL_AMBIENT = createSound("entity.transportation_portal_ambient");
	public static final SoundEvent SHADOW_MAGIC_LOOP = createSound("shadow_magic_loop");
	public static final SoundEvent SHADOW_MAGIC_CHARGE = createSound("shadow_magic_charge");
	public static final SoundEvent TRANSPORTATION_PORTAL_TELEPORTS = createSound("transportation_portal_teleports");
	public static final SoundEvent WAR_HORN = createSound("war_horn");
	public static final SoundEvent SKULL_WATCH_ALARM = createSound("skull_watch_alarm");
	public static final SoundEvent SKULL_WATCH_SCREAM = createSound("skull_watch_scream");
	public static final SoundEvent CHARM_OMNICRON = createSound("charm_omnicron");
	public static final SoundEvent DISPEL = createSound("dispel");
	public static final SoundEvent DISPEL_ENTITY = createSound("dispel_entity");
	public static final SoundEvent IMBUEMENT_TABLE_BREAK = createSound("imbuement_table_break");
	public static final SoundEvent STONE_FIST = createSound("stone_fist");
	public static final SoundEvent DEVORITIUM_BOMB_HIT = createSound("devoritium_bomb_hit");
	public static final SoundEvent RELIC_ACTIVATE = createSound("relic_activate");
	public static final SoundEvent RELIC_ACTIVATE_2 = createSound("relic_activate_2");
	public static final SoundEvent RELIC_USE_LOOP = createSound("relic_use_loop");

	public static SoundEvent createSound(String name) {
		return createSound(AncientSpellcraft.MODID, name);
	}

	/**
	 * Creates a sound with the given name, to be read from {@code assets/[modID]/sounds.json}.
	 */
	public static SoundEvent createSound(String modID, String name) {
		// All the setRegistryName methods delegate to this one, it doesn't matter which you use.
		return new SoundEvent(new ResourceLocation(modID, name)).setRegistryName(name);
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<SoundEvent> event) {
		event.getRegistry().register(ENTITY_HEALING_HEATH_HEALS);
		event.getRegistry().register(ENTITY_HEALING_HEALTH_DAMAGES);
		event.getRegistry().register(ENTITY_TRANSPORTATION_PORTAL_AMBIENT);
		event.getRegistry().register(SHADOW_MAGIC_LOOP);
		event.getRegistry().register(SHADOW_MAGIC_CHARGE);
		event.getRegistry().register(WAR_HORN);
		event.getRegistry().register(SKULL_WATCH_ALARM);
		event.getRegistry().register(SKULL_WATCH_SCREAM);
		event.getRegistry().register(CHARM_OMNICRON);
		event.getRegistry().register(DISPEL);
		event.getRegistry().register(DISPEL_ENTITY);
		event.getRegistry().register(IMBUEMENT_TABLE_BREAK);
		event.getRegistry().register(STONE_FIST);
		event.getRegistry().register(DEVORITIUM_BOMB_HIT);
		event.getRegistry().register(RELIC_ACTIVATE);
		event.getRegistry().register(RELIC_USE_LOOP);
		event.getRegistry().register(RELIC_ACTIVATE_2);
	}
}