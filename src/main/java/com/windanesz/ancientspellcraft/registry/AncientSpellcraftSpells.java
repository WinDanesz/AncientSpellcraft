package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.construct.EntitySpiritWard;
import com.windanesz.ancientspellcraft.entity.living.EntitySkeletonMageMinion;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDispelMagic;
import com.windanesz.ancientspellcraft.entity.projectile.EntityHeart;
import com.windanesz.ancientspellcraft.spell.AntiMagicField;
import com.windanesz.ancientspellcraft.spell.AquaticAgility;
import com.windanesz.ancientspellcraft.spell.ArcaneAugmentation;
import com.windanesz.ancientspellcraft.spell.ArcaneBeam;
import com.windanesz.ancientspellcraft.spell.ArcaneMagnetism;
import com.windanesz.ancientspellcraft.spell.AttireAlteration;
import com.windanesz.ancientspellcraft.spell.BlockWeaving;
import com.windanesz.ancientspellcraft.spell.CallOfThePack;
import com.windanesz.ancientspellcraft.spell.Candlelight;
import com.windanesz.ancientspellcraft.spell.Celerity;
import com.windanesz.ancientspellcraft.spell.ChannelPower;
import com.windanesz.ancientspellcraft.spell.Conflagration;
import com.windanesz.ancientspellcraft.spell.ConjureFishingRod;
import com.windanesz.ancientspellcraft.spell.ConjureShadowBlade;
import com.windanesz.ancientspellcraft.spell.ConjureWater;
import com.windanesz.ancientspellcraft.spell.ContinuityCharm;
import com.windanesz.ancientspellcraft.spell.Cryostasis;
import com.windanesz.ancientspellcraft.spell.CureZombie;
import com.windanesz.ancientspellcraft.spell.CurseArmor;
import com.windanesz.ancientspellcraft.spell.CurseOfDeath;
import com.windanesz.ancientspellcraft.spell.CurseOfEnder;
import com.windanesz.ancientspellcraft.spell.CurseWard;
import com.windanesz.ancientspellcraft.spell.DispelItemCurse;
import com.windanesz.ancientspellcraft.spell.Drought;
import com.windanesz.ancientspellcraft.spell.Extinguish;
import com.windanesz.ancientspellcraft.spell.EyeOfTheStorm;
import com.windanesz.ancientspellcraft.spell.FireWall;
import com.windanesz.ancientspellcraft.spell.Forcefend;
import com.windanesz.ancientspellcraft.spell.Harvest;
import com.windanesz.ancientspellcraft.spell.HellGate;
import com.windanesz.ancientspellcraft.spell.IceTower;
import com.windanesz.ancientspellcraft.spell.IceWorkbench;
import com.windanesz.ancientspellcraft.spell.Igloo;
import com.windanesz.ancientspellcraft.spell.IntensifyingFocus;
import com.windanesz.ancientspellcraft.spell.KnowledgeTransfer;
import com.windanesz.ancientspellcraft.spell.LavaVision;
import com.windanesz.ancientspellcraft.spell.Magelight;
import com.windanesz.ancientspellcraft.spell.MagmaStrider;
import com.windanesz.ancientspellcraft.spell.ManaFlare;
import com.windanesz.ancientspellcraft.spell.Martyr;
import com.windanesz.ancientspellcraft.spell.NaturesSprout;
import com.windanesz.ancientspellcraft.spell.Pyrokinesis;
import com.windanesz.ancientspellcraft.spell.RaiseSkeletonMage;
import com.windanesz.ancientspellcraft.spell.SilencingSigil;
import com.windanesz.ancientspellcraft.spell.SkullSentinel;
import com.windanesz.ancientspellcraft.spell.SnowBlock;
import com.windanesz.ancientspellcraft.spell.SpellBuffAS;
import com.windanesz.ancientspellcraft.spell.SpellConjurationAS;
import com.windanesz.ancientspellcraft.spell.SpellWard;
import com.windanesz.ancientspellcraft.spell.StaticCharge;
import com.windanesz.ancientspellcraft.spell.SummonAnchor;
import com.windanesz.ancientspellcraft.spell.SummonBoat;
import com.windanesz.ancientspellcraft.spell.SummonSpiritBear;
import com.windanesz.ancientspellcraft.spell.SummonVolcano;
import com.windanesz.ancientspellcraft.spell.TameAnimal;
import com.windanesz.ancientspellcraft.spell.TemporalCasualty;
import com.windanesz.ancientspellcraft.spell.TimeKnot;
import com.windanesz.ancientspellcraft.spell.Transference;
import com.windanesz.ancientspellcraft.spell.TransportationPortal;
import com.windanesz.ancientspellcraft.spell.UnholyAlliance;
import com.windanesz.ancientspellcraft.spell.VenusFlyTrap;
import com.windanesz.ancientspellcraft.spell.WaterWalking;
import com.windanesz.ancientspellcraft.spell.WillOWisp;
import com.windanesz.ancientspellcraft.spell.Zombification;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellConstruct;
import electroblob.wizardry.spell.SpellProjectile;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
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
	public static final Spell venus_fly_trap = placeholder();
	public static final Spell conjure_shadow_blade = placeholder();
	public static final Spell magelight = placeholder();
	public static final Spell curse_ward = placeholder();
	public static final Spell ice_shield = placeholder();
	public static final Spell curse_of_death = placeholder();
	public static final Spell frost_nova = placeholder();
	public static final Spell arcane_augmentation = placeholder();
	public static final Spell intensifying_focus = placeholder();
	public static final Spell continuity_charm = placeholder();
	public static final Spell projectile_ward = placeholder();
	public static final Spell bulwark = placeholder();
	public static final Spell arcane_aegis = placeholder();
	public static final Spell skull_sentinel = placeholder();
	public static final Spell antimagic_field = placeholder();

	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public static void register(RegistryEvent.Register<Spell> event) {

		IForgeRegistry<Spell> registry = event.getRegistry();

		Item[] asSpellItems = {AncientSpellcraftItems.ancient_spellcraft_spell_book, AncientSpellcraftItems.ancient_spellcraft_scroll};

		// AS 1.0.0 Spells
		registry.register(new HellGate(modId, "hellgate", EnumAction.BLOCK, false));

		// AS 1.0.1 Spells
		registry.register(new TameAnimal(modId, "tameanimal", false, EnumAction.BLOCK));
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
		registry.register(new Igloo(modId, "create_igloo", EnumAction.BLOCK, false));
		registry.register(new IceTower());
		registry.register(new IceWorkbench(modId, "ice_workbench", EnumAction.BLOCK, false));
		registry.register(new SnowBlock(modId, "snow_block", EnumAction.BLOCK, false));

		registry.register(new CurseArmor(modId, "curse_armor", EnumAction.BLOCK, false));
		registry.register(new DispelItemCurse(modId, "dispel_item_curse"));
		registry.register(new ChannelPower(modId, "channel_power", EnumAction.BOW, true) {});
		registry.register(new Zombification(modId, "zombification", EnumAction.BLOCK, false) {});
		registry.register(new CureZombie(modId, "cure_zombie", EnumAction.BLOCK, false) {});
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
		registry.register(new KnowledgeTransfer());
		registry.register(new SummonSpiritBear());
		registry.register(new VenusFlyTrap());

		registry.register(new SummonBoat());
		registry.register(new ConjureShadowBlade());
		registry.register(new Magelight());
		registry.register(new Candlelight());
		registry.register(new Celerity());

		registry.register(new SpellProjectile<EntityDispelMagic>(AncientSpellcraft.MODID, "dispel_lesser_magic", EntityDispelMagic::new) {
			@Override
			public boolean applicableForItem(Item item) {
				return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
			}
		});

		registry.register(new ConjureFishingRod());
		registry.register(new CurseWard());
		registry.register(new ArcaneBeam());
		registry.register(new SpellConjurationAS("ice_shield", AncientSpellcraftItems.ice_shield));
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
		registry.register(new SpellBuffAS("aspect_hunter", 22, 102, 48, () -> AncientSpellcraftPotions.fortified_archery));
		registry.register(new StaticCharge());
		registry.register(new Pyrokinesis());
		registry.register(new Conflagration());

	}
}
