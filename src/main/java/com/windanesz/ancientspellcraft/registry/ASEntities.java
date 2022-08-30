package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.entity.EntityMageLight;
import com.windanesz.ancientspellcraft.entity.EntityWisp;
import com.windanesz.ancientspellcraft.entity.construct.EntityAntiMagicField;
import com.windanesz.ancientspellcraft.entity.construct.EntityArcaneBarrier;
import com.windanesz.ancientspellcraft.entity.construct.EntityBarterConstruct;
import com.windanesz.ancientspellcraft.entity.construct.EntityBuilder;
import com.windanesz.ancientspellcraft.entity.construct.EntityHealingSigil;
import com.windanesz.ancientspellcraft.entity.construct.EntityMoltenBoulder;
import com.windanesz.ancientspellcraft.entity.construct.EntitySentinel;
import com.windanesz.ancientspellcraft.entity.construct.EntitySilencingSigil;
import com.windanesz.ancientspellcraft.entity.construct.EntitySpellTicker;
import com.windanesz.ancientspellcraft.entity.construct.EntitySpiritWard;
import com.windanesz.ancientspellcraft.entity.construct.EntityTransportationPortal;
import com.windanesz.ancientspellcraft.entity.construct.EntityVenusFlyTrap;
import com.windanesz.ancientspellcraft.entity.living.EntityAnimatedItem;
import com.windanesz.ancientspellcraft.entity.living.EntityCreeperMinion;
import com.windanesz.ancientspellcraft.entity.living.EntityEvilClassWizard;
import com.windanesz.ancientspellcraft.entity.living.EntityFireAnt;
import com.windanesz.ancientspellcraft.entity.living.EntityOrdinarySpiderMinion;
import com.windanesz.ancientspellcraft.entity.living.EntityPigZombieMinion;
import com.windanesz.ancientspellcraft.entity.living.EntitySkeletonHorseMinion;
import com.windanesz.ancientspellcraft.entity.living.EntitySkeletonMageMinion;
import com.windanesz.ancientspellcraft.entity.living.EntitySpellCaster;
import com.windanesz.ancientspellcraft.entity.living.EntitySpiritBear;
import com.windanesz.ancientspellcraft.entity.living.EntityStoneGuardian;
import com.windanesz.ancientspellcraft.entity.living.EntityVoidCreeper;
import com.windanesz.ancientspellcraft.entity.living.EntityVolcano;
import com.windanesz.ancientspellcraft.entity.living.EntityWizardMerchant;
import com.windanesz.ancientspellcraft.entity.living.EntityWolfMinion;
import com.windanesz.ancientspellcraft.entity.projectile.EntityAOEProjectile;
import com.windanesz.ancientspellcraft.entity.projectile.EntityContingencyProjectile;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDevoritiumArrow;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDevoritiumBomb;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDispelGreaterMagic;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDispelMagic;
import com.windanesz.ancientspellcraft.entity.projectile.EntityFlint;
import com.windanesz.ancientspellcraft.entity.projectile.EntityHeart;
import com.windanesz.ancientspellcraft.entity.projectile.EntityManaVortex;
import com.windanesz.ancientspellcraft.entity.projectile.EntityMasterBolt;
import com.windanesz.ancientspellcraft.entity.projectile.EntityMetamagicProjectile;
import com.windanesz.ancientspellcraft.entity.projectile.EntitySafeIceShard;
import com.windanesz.ancientspellcraft.entity.projectile.EntityStoneGuardianShard;
import electroblob.wizardry.Wizardry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Arrays;
import java.util.stream.Collectors;

//import com.windanesz.ancientspellcraft.entity.EntityMageLight;

@Mod.EventBusSubscriber
public class ASEntities {

	private ASEntities() {}

	private final static int VOID_CREEPER_SPAWN_RATE = 30;

	/**
	 * Most entity trackers fall into one of a few categories, so they are defined here for convenience. This
	 * generally follows the values used in vanilla for each entity type.
	 */
	enum TrackingType {

		LIVING(80, 3, true),
		PROJECTILE(64, 1, true),
		CONSTRUCT(160, 10, false);

		int range;
		int interval;
		boolean trackVelocity;

		TrackingType(int range, int interval, boolean trackVelocity) {
			this.range = range;
			this.interval = interval;
			this.trackVelocity = trackVelocity;
		}
	}

	/**
	 * Incrementing index for the mod-specific entity network ID.
	 */
	private static int id = 0;

	@SubscribeEvent
	public static void register(RegistryEvent.Register<EntityEntry> event) {

		IForgeRegistry<EntityEntry> registry = event.getRegistry();

		// projectile entities
		registry.register(createEntry(EntityWisp.class, "will_o_wisp", ASEntities.TrackingType.PROJECTILE).build());

		registry.register(createEntry(EntityMageLight.class, "mage_light_entity", ASEntities.TrackingType.PROJECTILE).build());

		registry.register(createEntry(EntityHeart.class, "healing_heart", ASEntities.TrackingType.PROJECTILE).build());
		registry.register(createEntry(EntityDispelMagic.class, "dispel_magic", ASEntities.TrackingType.PROJECTILE).build());
		registry.register(createEntry(EntityDispelGreaterMagic.class, "dispel_greater_magic", ASEntities.TrackingType.PROJECTILE).build());
		registry.register(createEntry(EntityManaVortex.class, "wizard_blast").tracker(160, 3, false).build());
		registry.register(createEntry(EntityDevoritiumBomb.class, "devoritium_bomb", ASEntities.TrackingType.PROJECTILE).build());

		registry.register(createEntry(EntityDevoritiumArrow.class, "devoritium_arrow", ASEntities.TrackingType.PROJECTILE).build());
		registry.register(createEntry(EntityFlint.class, "flint_projectile", ASEntities.TrackingType.PROJECTILE).build());
		registry.register(createEntry(EntitySafeIceShard.class, "safe_ice_shard", ASEntities.TrackingType.PROJECTILE).build());
		//		registry.register(createEntry(EntitySpectralFishHook.class, "spectral_fish_hook", AncientSpellcraftEntities.TrackingType.PROJECTILE).build());

		// mobs

		registry.register(createEntry(EntityVoidCreeper.class, "void_creeper", TrackingType.LIVING).egg(0x271b3b, 0x7f35fb)
				.spawn(EnumCreatureType.MONSTER, Settings.generalSettings.void_creeper_spawn_rate, 1, 1, ForgeRegistries.BIOMES.getValuesCollection().stream()
						.filter(b -> !Arrays.asList(AncientSpellcraft.settings.voidCreeperBiomeBlacklist).contains(b.getRegistryName()))
						.collect(Collectors.toSet())).build());

		registry.register(createEntry(EntitySkeletonMageMinion.class, "skeleton_mage_minion", TrackingType.LIVING).build());
		registry.register(createEntry(EntityWolfMinion.class, "wolf_minion", TrackingType.LIVING).egg(0xcc6f47, 0x676767).build());
		registry.register(createEntry(EntitySpiritBear.class, "spirit_bear", TrackingType.LIVING).egg(0xbcc2e8, 0xffffff).build());
		registry.register(createEntry(EntityVenusFlyTrap.class, "venus_fly_trap", TrackingType.LIVING).egg(0xbcc2e8, 0xffffff).build());
		registry.register(createEntry(EntityFireAnt.class, "fire_ant", TrackingType.LIVING).build());
		registry.register(createEntry(EntitySkeletonHorseMinion.class, "skeleton_horse_minion", TrackingType.LIVING).build());
		registry.register(createEntry(EntityOrdinarySpiderMinion.class, "ordinary_spider_minion", TrackingType.LIVING).build());

		//		registry.register(createEntry(EntitySpellBook.class, "rouge_spell_book", TrackingType.LIVING).build());

		// constructs
		registry.register(createEntry(EntityTransportationPortal.class, "transportation_portal", TrackingType.CONSTRUCT).build());
		registry.register(createEntry(EntitySpiritWard.class, "spirit_ward", TrackingType.CONSTRUCT).build());
		registry.register(createEntry(EntitySilencingSigil.class, "silencing_sigil", TrackingType.CONSTRUCT).build());
		registry.register(createEntry(EntityHealingSigil.class, "healing_sigil", TrackingType.CONSTRUCT).build());
		registry.register(createEntry(EntityVolcano.class, "entity_volcano", TrackingType.LIVING).build());

		registry.register(createEntry(EntityAntiMagicField.class, "anti_magic_field", TrackingType.CONSTRUCT).build());
		registry.register(createEntry(EntityMetamagicProjectile.class, "contingency_projectile", ASEntities.TrackingType.PROJECTILE).build());
		registry.register(createEntry(EntityContingencyProjectile.class, "metamagic_projectile", ASEntities.TrackingType.PROJECTILE).build());

		registry.register(createEntry(EntitySentinel.class, "sentinel", TrackingType.CONSTRUCT).build());
		registry.register(createEntry(EntitySpellCaster.class, "spellcaster_entity", TrackingType.LIVING).build());
		registry.register(createEntry(EntitySpellTicker.class, "mushroom_forest", TrackingType.LIVING).build());
		registry.register(createEntry(EntityBuilder.class, "builder_entity", TrackingType.CONSTRUCT).build());
		registry.register(createEntry(EntityAnimatedItem.class, "animated_item", TrackingType.LIVING).build());
		registry.register(createEntry(EntityMasterBolt.class, 	"master_bolt", 	TrackingType.PROJECTILE).build());
		registry.register(createEntry(EntityMoltenBoulder.class, "molten_boulder").tracker(160, 1, true).build()); // Vertical velocity is not constant
		registry.register(createEntry(EntityCreeperMinion.class, "creeper_minion", TrackingType.LIVING).build());
		registry.register(createEntry(EntityPigZombieMinion.class, "pig_zombie_minion", TrackingType.LIVING).build());
		registry.register(createEntry(EntityStoneGuardian.class, "stone_guardian", TrackingType.LIVING).egg(0x86848c, 0xa100e6).build());
		registry.register(createEntry(EntityStoneGuardianShard.class, "stone_guardian_shard", TrackingType.PROJECTILE).build());

		//// Living Entity Overrides

//		if (Settings.generalSettings.apply_wizard_entity_changes) {
//			AncientSpellcraft.logger.info("Applying ebwizardry:wizard entity overrides by Ancient Spellcraft");
//			registry.register(createEntry(EntityWizardAS.class, "wizard_as", AncientSpellcraft.MODID, TrackingType.LIVING).build());
//
//			AncientSpellcraft.logger.info("Applying ebwizardry:evil_wizard entity overrides by Ancient Spellcraft");
//			registry.register(createEntry(EntityEvilWizardAS.class, "evil_wizard_as", AncientSpellcraft.MODID, TrackingType.LIVING).build());
//		}
		registry.register(createEntry(EntityWizardMerchant.class, "wizardmerchant", AncientSpellcraft.MODID, TrackingType.LIVING).build());
		//registry.register(createEntry(EntityClassWizard.class, "class_wizard", AncientSpellcraft.MODID, TrackingType.LIVING).egg(0xbcc2e8, 0xffffff).build());
		registry.register(createEntry(EntityEvilClassWizard.class, 		"evil_class_wizard", AncientSpellcraft.MODID, TrackingType.LIVING).egg(0x290404, 0xee9312)
				// For reference: 5, 1, 1 are the parameters for the witch in vanilla
				.spawn(EnumCreatureType.MONSTER, Settings.generalSettings.evil_class_wizard_spawn_rate, 1, 1, ForgeRegistries.BIOMES.getValuesCollection().stream()
						.filter(b -> !Arrays.asList(Wizardry.settings.mobSpawnBiomeBlacklist).contains(b.getRegistryName()))
						.collect(Collectors.toSet())).build());


		registry.register(createEntry(EntityBarterConstruct.class, "barter_entity", AncientSpellcraft.MODID, TrackingType.CONSTRUCT).build());
		registry.register(createEntry(EntityArcaneBarrier.class, "arcane_barrier", TrackingType.CONSTRUCT).build());
		//		registry.register(createEntry(EntityArcaneBarrierProxy.class, "arcane_barrier_proxy", TrackingType.CONSTRUCT).build());
		registry.register(createEntry(EntityAOEProjectile.class, "aoe_projectile_entity", TrackingType.PROJECTILE).build());

	}

	/**
	 * Private helper method that simplifies the parts of an {@link EntityEntry} that are common to all entities.
	 * This automatically assigns a network id, and accepts a {@link TrackingType} for automatic tracker assignment.
	 *
	 * @param entityClass The entity class to use.
	 * @param name        The name of the entity. This will form the path of a {@code ResourceLocation} with domain
	 *                    {@code ebwizardry}, which in turn will be used as both the registry name and the 'command' name.
	 * @param tracking    The {@link TrackingType} to use for this entity.
	 * @param <T>         The type of entity.
	 * @return The (part-built) builder instance, allowing other builder methods to be added as necessary.
	 */
	private static <T extends Entity> EntityEntryBuilder<T> createEntry(Class<T> entityClass, String name, TrackingType tracking) {
		return createEntry(entityClass, name).tracker(tracking.range, tracking.interval, tracking.trackVelocity);
	}

	/**
	 * Private helper method that simplifies the parts of an {@link EntityEntry} that are common to all entities.
	 * This automatically assigns a network id.
	 *
	 * @param entityClass The entity class to use.
	 * @param name        The name of the entity. This will form the path of a {@code ResourceLocation} with domain
	 *                    {@code ebwizardry}, which in turn will be used as both the registry name and the 'command' name.
	 * @param <T>         The type of entity.
	 * @return The (part-built) builder instance, allowing other builder methods to be added as necessary.
	 */
	private static <T extends Entity> EntityEntryBuilder<T> createEntry(Class<T> entityClass, String name) {
		ResourceLocation registryName = new ResourceLocation(AncientSpellcraft.MODID, name);
		return EntityEntryBuilder.<T>create().entity(entityClass).id(registryName, id++).name(registryName.toString());
	}

	/**
	 * Private helper method that simplifies the parts of an {@link EntityEntry} that are common to all entities.
	 * This automatically assigns a network id, and accepts a {@link TrackingType} for automatic tracker assignment.
	 *
	 * @param entityClass The entity class to use.
	 * @param name        The name of the entity. This will form the path of a {@code ResourceLocation} with domain
	 *                    {@code ebwizardry}, which in turn will be used as both the registry name and the 'command' name.
	 * @param tracking    The {@link TrackingType} to use for this entity.
	 * @param <T>         The type of entity.
	 * @return The (part-built) builder instance, allowing other builder methods to be added as necessary.
	 */
	private static <T extends Entity> EntityEntryBuilder<T> createEntry(Class<T> entityClass, String name, String modid, TrackingType tracking) {
		return createEntry(entityClass, name, modid).tracker(tracking.range, tracking.interval, tracking.trackVelocity);
	}

	/**
	 * Private helper method that simplifies the parts of an {@link EntityEntry} that are common to all entities.
	 * This automatically assigns a network id.
	 *
	 * @param entityClass The entity class to use.
	 * @param name        The name of the entity. This will form the path of a {@code ResourceLocation} with domain
	 *                    {@code ebwizardry}, which in turn will be used as both the registry name and the 'command' name.
	 * @param <T>         The type of entity.
	 * @return The (part-built) builder instance, allowing other builder methods to be added as necessary.
	 */
	private static <T extends Entity> EntityEntryBuilder<T> createEntry(Class<T> entityClass, String name, String modid) {
		ResourceLocation registryName = new ResourceLocation(modid, name);
		return EntityEntryBuilder.<T>create().entity(entityClass).id(registryName, id++).name(registryName.toString());
	}
}
