package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.entity.EntityAOEProjectile;
import com.windanesz.ancientspellcraft.entity.construct.EntitySpiritWard;
import com.windanesz.ancientspellcraft.entity.living.EntitySkeletonMageMinion;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDispelGreaterMagic;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDispelMagic;
import com.windanesz.ancientspellcraft.entity.projectile.EntityHeart;
import com.windanesz.ancientspellcraft.spell.*;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellConstruct;
import electroblob.wizardry.spell.SpellProjectile;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

@ObjectHolder(AncientSpellcraft.MODID)
@EventBusSubscriber
public final class AncientSpellcraftSpells {


	private AncientSpellcraftSpells() {} // no instances

	private static final String modId = AncientSpellcraft.MODID;

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	private static <T> T placeholder() { return null; }

	public static final Spell hellgate = placeholder();
	public static final Spell tameanimal = placeholder();
	//	public static final Spell summonlichelord = placeholder();
	public static final Spell extinguish = placeholder();
	public static final Spell curse_of_ender = placeholder();
	public static final Spell conjure_water = placeholder();
	public static final Spell conjure_shield = placeholder();
	public static final Spell drought = placeholder();
	public static final Spell will_o_wisp = placeholder();
	public static final Spell natures_sprout = placeholder();
	public static final Spell create_igloo = placeholder();
	public static final Spell ice_tower = placeholder();
	public static final Spell ice_workbench = placeholder();
	public static final Spell snow_block = placeholder();
	public static final Spell curse_armor = placeholder();
	public static final Spell dispel_item_curse = placeholder();
	public static final Spell channel_power = placeholder();
	public static final Spell zombification = placeholder();
	public static final Spell cure_zombie = placeholder();
	public static final Spell raise_skeleton_mage = placeholder();
	public static final Spell unholy_alliance = placeholder();
	public static final Spell transportation_portal = placeholder();
	public static final Spell healing_heart = placeholder();
	public static final Spell martyr = placeholder();
	public static final Spell aquatic_agility = placeholder();
	public static final Spell lava_vision = placeholder();
	public static final Spell magma_strider = placeholder();
	public static final Spell summon_anchor = placeholder();
	public static final Spell call_of_the_pack = placeholder();
	public static final Spell blockweaving = placeholder();
	public static final Spell cryostasis = placeholder();
	public static final Spell arcane_magnetism = placeholder();
	public static final Spell time_knot = placeholder();
	//	public static final Spell venus_fly_trap = placeholder();
	public static final Spell conjure_shadow_blade = placeholder();
	public static final Spell magelight = placeholder();
	public static final Spell curse_ward = placeholder();
	public static final Spell ice_shield = placeholder();
	public static final Spell curse_of_death = placeholder();
	public static final Spell frost_nova = placeholder();
	public static final Spell arcane_augmentation = placeholder();
	public static final Spell arcane_beam = placeholder();
	public static final Spell aspect_hunter = placeholder();
	public static final Spell dispel_lesser_magic = placeholder();
	public static final Spell dispel_greater_magic = placeholder();
	public static final Spell forcefend = placeholder();
	public static final Spell mana_flare = placeholder();
	public static final Spell intensifying_focus = placeholder();
	public static final Spell silencing_sigil = placeholder();
	public static final Spell continuity_charm = placeholder();
	public static final Spell crystal_mine = placeholder();
	public static final Spell water_walking = placeholder();
	public static final Spell projectile_ward = placeholder();
	public static final Spell bulwark = placeholder();
	public static final Spell arcane_aegis = placeholder();
	public static final Spell skull_sentinel = placeholder();
	public static final Spell antimagic_field = placeholder();
	public static final Spell conduit = placeholder();
	public static final Spell hand_of_gaia = placeholder();
	public static final Spell essence_extraction = placeholder();
	public static final Spell stone_punch = placeholder();
	public static final Spell bubble_head = placeholder();
	public static final Spell prismatic_spray = placeholder();
	public static final Spell covenant = placeholder();
	public static final Spell mana_vortex = placeholder();
	public static final Spell eagle_eye = placeholder();
	public static final Spell farsight = placeholder();
	public static final Spell flint_shard = placeholder();
	public static final Spell living_comet = placeholder();
	public static final Spell might_and_magic = placeholder();
	public static final Spell channel_effect = placeholder();
	public static final Spell pocket_dimension = placeholder();

	public static final Spell metamagic_projectile = placeholder();
	public static final Spell contingency_fire = placeholder();
	public static final Spell contingency_fall = placeholder();
	public static final Spell contingency_damage = placeholder();
	public static final Spell contingency_hostile_spellcast = placeholder();
	public static final Spell contingency_critical_health = placeholder();
	public static final Spell contingency_immobility = placeholder();
	public static final Spell contingency_death = placeholder();
	public static final Spell contingency_drowning = placeholder();
	public static final Spell contingency_paralysis = placeholder();
	public static final Spell wizard_shield = placeholder();
	public static final Spell armageddon = placeholder();
	public static final Spell fimbulwinter = placeholder();
	public static final Spell shrink_self = placeholder();
	public static final Spell grow_self = placeholder();
	public static final Spell mass_shrink = placeholder();
	public static final Spell mass_growth = placeholder();
	public static final Spell permashrink = placeholder();
	public static final Spell permagrowth = placeholder();
	public static final Spell words_of_unbinding = placeholder();
	public static final Spell astral_projection = placeholder();
	public static final Spell dimensional_anchor = placeholder();
	public static final Spell conjure_lesser_sentry = placeholder();
	public static final Spell conjure_greater_sentry = placeholder();
	public static final Spell fairy_ring = placeholder();
	public static final Spell mushroom_forest = placeholder();
	public static final Spell conjure_shovel = placeholder();
	public static final Spell ice_cream = placeholder();
	public static final Spell burrow = placeholder();
	public static final Spell magma_shell = placeholder();
	public static final Spell summon_fire_ant = placeholder();
	public static final Spell summon_spider = placeholder();
	public static final Spell soul_scorch = placeholder();
	public static final Spell swamp = placeholder();

	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public static void register(RegistryEvent.Register<Spell> event) {

		IForgeRegistry<Spell> registry = event.getRegistry();

		Item[] asSpellItems = {AncientSpellcraftItems.ancient_spellcraft_spell_book, AncientSpellcraftItems.ancient_spellcraft_scroll};

		// AS 1.0.0 Spells
		registry.register(new HellGate(modId, "hellgate", EnumAction.BLOCK, false));

		// AS 1.0.1 Spells
		registry.register(new TameAnimal());
		//		registry.register(new SummonLicheLord(modId, "summonlichelord", EntitySkeletonMinion::new)); // unused

		// AS 1.0.2 Spells
		registry.register(new Extinguish(modId, "extinguish"));
		registry.register(new CurseOfEnder(modId, "curse_of_ender", EnumAction.NONE, false) {});
		registry.register(new ConjureWater(modId, "conjure_water", EnumAction.BLOCK, false));
		registry.register(new SpellConjurationAS("conjure_shield", AncientSpellcraftItems.spectral_shield));
		registry.register(new Drought(modId, "drought", EnumAction.BLOCK, false));
		registry.register(new WillOWisp(modId, "will_o_wisp"));
		registry.register(new NaturesSprout(modId, "natures_sprout", EnumAction.BLOCK, false));

		// AS 1.1 Spells
		registry.register(new CreateIgloo());
		registry.register(new IceTower());
		registry.register(new IceWorkbench(modId, "ice_workbench", EnumAction.BLOCK, false));
		registry.register(new SnowBlock());

		registry.register(new CurseArmor(modId, "curse_armor", EnumAction.BLOCK, false));
		registry.register(new DispelItemCurse(modId, "dispel_item_curse"));
		registry.register(new ChannelPower(modId, "channel_power", EnumAction.BOW, true) {});
		registry.register(new Zombification());
		registry.register(new CureZombie());
		registry.register(new RaiseSkeletonMage(modId, "raise_skeleton_mage", EntitySkeletonMageMinion::new) {});
		registry.register(new UnholyAlliance(modId, "unholy_alliance", EntitySkeletonMageMinion::new) {});
		registry.register(new TransportationPortal(modId, "transportation_portal", EnumAction.BOW, false) {});

		registry.register(new SpellProjectile<EntityHeart>(AncientSpellcraft.MODID, "healing_heart", EntityHeart::new) {
			@Override
			public boolean applicableForItem(Item item) {
				return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
			}
		}.addProperties(Spell.HEALTH, Spell.DAMAGE, Spell.SEEKING_STRENGTH));

		registry.register(new Martyr());
		registry.register(new AquaticAgility());
		registry.register(new LavaVision());
		registry.register(new MagmaStrider());
		registry.register(new SummonAnchor());
		registry.register(new CallOfThePack());

		registry.register(new SpellConstruct<EntitySpiritWard>(AncientSpellcraft.MODID, "spirit_ward", EnumAction.BOW, EntitySpiritWard::new, false) {
			@Override
			public boolean applicableForItem(Item item) {
				return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
			}
		});

		registry.register(new BlockWeaving());
		registry.register(new Cryostasis());
		registry.register(new ArcaneMagnetism());
		registry.register(new Conduit());
		registry.register(new SummonSpiritBear());
		//		registry.register(new VenusFlyTrap());

		registry.register(new SummonBoat());
		registry.register(new ConjureShadowBlade());
		registry.register(new Magelight());
		registry.register(new Candlelight());
		registry.register(new Celerity());

		registry.register(new SpellProjectile<EntityDispelMagic>(AncientSpellcraft.MODID, "dispel_lesser_magic", EntityDispelMagic::new) {
			@Override
			public boolean applicableForItem(Item item) {
				return item == AncientSpellcraftItems.ancient_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
			}
		});

		registry.register(new SpellProjectile<EntityDispelGreaterMagic>(AncientSpellcraft.MODID, "dispel_greater_magic", EntityDispelGreaterMagic::new) {
			@Override
			public boolean applicableForItem(Item item) {
				return item == AncientSpellcraftItems.ancient_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
			}
		});

		registry.register(new ConjureFishingRod());
		registry.register(new CurseWard());
		registry.register(new ArcaneBeam());
		registry.register(new SpellConjurationAS("ice_shield", AncientSpellcraftItems.ice_shield) {
			@Override
			protected void spawnParticles(World world, EntityLivingBase caster, SpellModifiers modifiers) {
				for (int i = 0; i < 2; i++) {
					double x = caster.posX;
					double y = caster.posY + caster.getEyeHeight() - 0.5 + world.rand.nextDouble();
					double z = caster.posZ;
					//					ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x, y, z).clr(0.7f, 0.9f, 1).spawn(world);
					ParticleBuilder.create(ParticleBuilder.Type.FLASH).scale(2f).entity(caster).pos(0, 1.5, 0).clr(168, 255, 255).spawn(world);
				}
			}
		});
		registry.register(new CurseOfDeath());
		registry.register(new AttireAlteration());
		registry.register(new TimeKnot());
		registry.register(new TemporalCasualty());
		registry.register(new ManaFlare());

		registry.register(new EyeOfTheStorm());
		registry.register(new SpellBuffAS("feather_fall", 230, 230, 255, () -> AncientSpellcraftPotions.feather_fall));
		registry.register(new WaterWalking());

		// metamagic!
		registry.register(new ArcaneAugmentation());
		registry.register(new IntensifyingFocus());
		registry.register(new ContinuityCharm());

		///

		registry.register(new SpellWard("projectile_ward", 230, 230, 255, () -> AncientSpellcraftPotions.projectile_ward));
		registry.register(new SpellWard("bulwark", 230, 230, 255, () -> AncientSpellcraftPotions.bulwark));
		registry.register(new SpellWard("arcane_aegis", 230, 230, 255, () -> AncientSpellcraftPotions.arcane_aegis));

		registry.register(new Forcefend());
		registry.register(new SkullSentinel());
		registry.register(new SilencingSigil());

		registry.register(new AntiMagicField());

		registry.register(new SummonVolcano());
		registry.register(new Transference());

		registry.register(new FireWall());
		registry.register(new Harvest());
		registry.register(new AspectHunter());
		registry.register(new StaticCharge());
		registry.register(new Pyrokinesis());
		registry.register(new Conflagration());
		registry.register(new PocketDimension());
		registry.register(new FrostNova());
		registry.register(new Regrowth());
		registry.register(new EssenceExtraction());
		registry.register(new StonePunch());
		registry.register(new StoneFist());
		registry.register(new EagleEye());
		registry.register(new SpellConjurationAS("sacred_mace", AncientSpellcraftItems.sacred_mace));
		registry.register(new SpellBuffAS("bubble_head", 52, 195, 235, () -> AncientSpellcraftPotions.bubble_head) {
			@Override
			public boolean applicableForItem(Item item) {
				return item == AncientSpellcraftItems.ancient_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
			}
		});

		registry.register(new PrismaticSpray());
		registry.register(new Covenant());
		registry.register(new ManaVortex());
		registry.register(new FlintShard());
		registry.register(new Farsight());
		registry.register(new CrystalMine());
		registry.register(new PuppetMaster());
		registry.register(new SummonSkeletonHorse());
		registry.register(new HorseWhistle());
		registry.register(new LivingComet());
		registry.register(new Suppression());
		registry.register(new MightAndMagic());
		registry.register(new ChannelEffects());

		// 1.2 spells
		registry.register(new Contingency("contingency_fire", SpellActions.POINT_UP, 1, 1, 1));
		registry.register(new Contingency("contingency_fall", SpellActions.POINT_UP, 1, 1, 1));
		registry.register(new Contingency("contingency_damage", SpellActions.POINT_UP, 1, 1, 1));
		registry.register(new Contingency("contingency_critical_health", SpellActions.POINT_UP, 1, 1, 1));
		registry.register(new Contingency("contingency_death", SpellActions.POINT_UP, 1, 1, 1));
		registry.register(new Contingency("contingency_drowning", SpellActions.POINT_UP, 1, 1, 1));
		registry.register(new Contingency("contingency_hostile_spellcast", SpellActions.POINT_UP, 1, 1, 1));
		registry.register(new Contingency("contingency_immobility", SpellActions.POINT_UP, 1, 1, 1));
		registry.register(new MetamagicProjectile());
		registry.register(new WizardShield());
		registry.register(new Armageddon());
		registry.register(new Fimbulwinter());

		registry.register(new SpellResizeSelf("shrink_self", 173, 80, 172, () -> AncientSpellcraftPotions.shrinkage));
		registry.register(new SpellResizeSelf("grow_self", 173, 80, 172, () -> AncientSpellcraftPotions.growth));
		registry.register(new SpellProjectileAOEPotion<>("mass_shrink", EntityAOEProjectile::new, 173, 80, 172, () -> AncientSpellcraftPotions.shrinkage));
		registry.register(new SpellProjectileAOEPotion<>("mass_growth", EntityAOEProjectile::new, 173, 80, 172, () -> AncientSpellcraftPotions.growth));
		registry.register(new Permashrink());
		registry.register(new Permagrowth());
		registry.register(new WordsOfUnbinding());
		registry.register(new AstralTravel());
		registry.register(new DimensionalAnchor());
		registry.register(new ConjureSentry("conjure_lesser_sentry", false));
		registry.register(new ConjureSentry("conjure_greater_sentry", true));
		registry.register(new FairyRing());
		registry.register(new MushroomForest());
		registry.register(new QuicksandRing());
		registry.register(new SummonQuicksand());
		registry.register(new WildSporeling());
		registry.register(new SporelingsAid());
		registry.register(new Weakness());
		registry.register(new SpellConjurationAS("conjure_shovel", AncientSpellcraftItems.spectral_shovel));
		registry.register(new IceCream());
		registry.register(new Burrow());
		registry.register(new MagmaShell());
		registry.register(new SummonFireAnt());
		registry.register(new SummonSpider());
		registry.register(new SoulScorch());
		registry.register(new Swamp());
		registry.register(new MagmaWall());
		registry.register(new MoltenEarth());

		//registry.register(new WarpWeapon()); TODO

		/// BASE SPELL MODIFICATION OVERRIDES  ///

		if (Settings.spellCompatSettings.mineSpellOverride) {
			registry.register(new MineAS());
		}
		if (Settings.spellCompatSettings.conjurePickaxeSpellOverride) {
			registry.register(new ConjurePickaxe("conjure_pickaxe", WizardryItems.spectral_pickaxe));
		}

		/// BASE SPELL MODIFICATION OVERRIDES ///

		//		registry.register(new HandOfGaia());
		//		registry.register(new WarpWood());
	}
}
