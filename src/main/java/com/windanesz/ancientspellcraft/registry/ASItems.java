package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDevoritiumBomb;
import com.windanesz.ancientspellcraft.item.ItemASArtefact;
import com.windanesz.ancientspellcraft.item.ItemASArtemisLibArtefact;
import com.windanesz.ancientspellcraft.item.ItemASSpellBook;
import com.windanesz.ancientspellcraft.item.ItemAdvancedStoneFist;
import com.windanesz.ancientspellcraft.item.ItemAlchemicalEssence;
import com.windanesz.ancientspellcraft.item.ItemAmberMushroomAmulet;
import com.windanesz.ancientspellcraft.item.ItemAmnesiaScroll;
import com.windanesz.ancientspellcraft.item.ItemElementalAuraAmulet;
import com.windanesz.ancientspellcraft.item.ItemAncientHat;
import com.windanesz.ancientspellcraft.item.ItemArmourUpgradeMaterial;
import com.windanesz.ancientspellcraft.item.ItemBarterScroll;
import com.windanesz.ancientspellcraft.item.ItemBattlemageSword;
import com.windanesz.ancientspellcraft.item.ItemBeltScrollHolder;
import com.windanesz.ancientspellcraft.item.ItemBlockDevoritiumMaterial;
import com.windanesz.ancientspellcraft.item.ItemChargedAstralDiamond;
import com.windanesz.ancientspellcraft.item.ItemCoalBucket;
import com.windanesz.ancientspellcraft.item.ItemCornucopia;
import com.windanesz.ancientspellcraft.item.ItemCubePhasing;
import com.windanesz.ancientspellcraft.item.ItemDevoritium;
import com.windanesz.ancientspellcraft.item.ItemDevoritiumArmour;
import com.windanesz.ancientspellcraft.item.ItemDevoritiumArrow;
import com.windanesz.ancientspellcraft.item.ItemDevoritiumAxe;
import com.windanesz.ancientspellcraft.item.ItemDevoritiumBomb;
import com.windanesz.ancientspellcraft.item.ItemDevoritiumDoor;
import com.windanesz.ancientspellcraft.item.ItemDevoritiumMagnet;
import com.windanesz.ancientspellcraft.item.ItemDevoritiumOrb;
import com.windanesz.ancientspellcraft.item.ItemDevoritiumScimitar;
import com.windanesz.ancientspellcraft.item.ItemDevoritiumShield;
import com.windanesz.ancientspellcraft.item.ItemDevoritiumSword;
import com.windanesz.ancientspellcraft.item.ItemDuplicationScroll;
import com.windanesz.ancientspellcraft.item.ItemElementalOrb;
import com.windanesz.ancientspellcraft.item.ItemEmpowermentTomeUpgrade;
import com.windanesz.ancientspellcraft.item.ItemEmptyMysticSpellBook;
import com.windanesz.ancientspellcraft.item.ItemEnchantedNameTag;
import com.windanesz.ancientspellcraft.item.ItemEternityPendant;
import com.windanesz.ancientspellcraft.item.ItemEverfullManaFlask;
import com.windanesz.ancientspellcraft.item.ItemEvergrowingCrystal;
import com.windanesz.ancientspellcraft.item.ItemGlyphArtefact;
import com.windanesz.ancientspellcraft.item.ItemGoldBag;
import com.windanesz.ancientspellcraft.item.ItemGuardianBlade;
import com.windanesz.ancientspellcraft.item.ItemHorn;
import com.windanesz.ancientspellcraft.item.ItemIceCream;
import com.windanesz.ancientspellcraft.item.ItemIceShield;
import com.windanesz.ancientspellcraft.item.ItemKnowledgeOrb;
import com.windanesz.ancientspellcraft.item.ItemKnowledgeScroll;
import com.windanesz.ancientspellcraft.item.ItemLostRecipe;
import com.windanesz.ancientspellcraft.item.ItemMagicShield;
import com.windanesz.ancientspellcraft.item.ItemManaArtefact;
import com.windanesz.ancientspellcraft.item.ItemMasterBolt;
import com.windanesz.ancientspellcraft.item.ItemMoonLetterDictionary;
import com.windanesz.ancientspellcraft.item.ItemNewArtefact;
import com.windanesz.ancientspellcraft.item.ItemOmnicron;
import com.windanesz.ancientspellcraft.item.ItemOverlordScepter;
import com.windanesz.ancientspellcraft.item.ItemPhiloshopersStone;
import com.windanesz.ancientspellcraft.item.ItemRelic;
import com.windanesz.ancientspellcraft.item.ItemRitualBook;
import com.windanesz.ancientspellcraft.item.ItemRune;
import com.windanesz.ancientspellcraft.item.ItemRunicPlate;
import com.windanesz.ancientspellcraft.item.ItemSacredMace;
import com.windanesz.ancientspellcraft.item.ItemSageSpellBook;
import com.windanesz.ancientspellcraft.item.ItemSageTome;
import com.windanesz.ancientspellcraft.item.ItemSearingSkinAmulet;
import com.windanesz.ancientspellcraft.item.ItemSetArtefact;
import com.windanesz.ancientspellcraft.item.ItemShadowBlade;
import com.windanesz.ancientspellcraft.item.ItemSoulboundWandUpgrade;
import com.windanesz.ancientspellcraft.item.ItemSpectralFishingRod;
import com.windanesz.ancientspellcraft.item.ItemSpectralShield;
import com.windanesz.ancientspellcraft.item.ItemSpectralShovel;
import com.windanesz.ancientspellcraft.item.ItemStoneFist;
import com.windanesz.ancientspellcraft.item.ItemTheoryScroll;
import com.windanesz.ancientspellcraft.item.ItemTomeController;
import com.windanesz.ancientspellcraft.item.ItemTransmutationScroll;
import com.windanesz.ancientspellcraft.item.ItemUnsealingScroll;
import com.windanesz.ancientspellcraft.item.ItemWandUpgradeAS;
import com.windanesz.ancientspellcraft.item.ItemWizardTankard;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.misc.BehaviourSpellDispense;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardryTabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ObjectHolder(AncientSpellcraft.MODID)
@Mod.EventBusSubscriber
public final class ASItems {

	private ASItems() {} // No instances!

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	private static <T> T placeholder() { return null; }

	public static final Item ancient_spellcraft_spell_book = placeholder();
	public static final Item ancient_spell_book = placeholder();
	public static final Item ancient_spellcraft_scroll = placeholder();
	public static final Item spectral_shield = placeholder();

	// wizard gear
	public static final Item wizard_hat_ancient = placeholder();

	// ====================== Artefacts ======================

	/// ring
	public static final Item ring_lodestone = placeholder();
	public static final Item ring_poison_arrow = placeholder();
	public static final Item ring_power = placeholder();
	public static final Item ring_berserker = placeholder();
	public static final Item ring_prismarine = placeholder();
	public static final Item ring_mana_transfer = placeholder();
	public static final Item ring_blast = placeholder();
	public static final Item ring_range = placeholder();
	public static final Item ring_focus_crystal = placeholder();
	public static final Item ring_permanent_shrinkage = placeholder();
	public static final Item ring_permanent_growth = placeholder();
	public static final Item ring_unbinding = placeholder();
	public static final Item ring_disenchanter = placeholder();
	public static final Item ring_quicksand = placeholder();
	public static final Item ring_mana_lesser = placeholder();
	public static final Item ring_mana_greater = placeholder();
	public static final Item ring_protector = placeholder();
	public static final Item ring_devotion = placeholder();
	public static final Item ring_withdraw_life = placeholder();
	public static final Item ring_cloudwalker = placeholder();
	public static final Item ring_lily_flower = placeholder();
	public static final Item ring_spirit_ward = placeholder();
	public static final Item ring_kinetic = placeholder();
	public static final Item ring_charge = placeholder();
	public static final Item ring_griefing = placeholder();
	public static final Item ring_metamagic_preserve = placeholder();

	/// amulet
	public static final Item charm_cryostasis = placeholder();
	public static final Item amulet_poison_resistance = placeholder();
	public static final Item amulet_power = placeholder();
	public static final Item amulet_mana = placeholder();
	public static final Item amulet_rabbit = placeholder();
	public static final Item amulet_pendant_of_eternity = placeholder();
	public static final Item amulet_curse_ward = placeholder();
	public static final Item amulet_time_knot = placeholder();
	public static final Item amulet_arcane_catalyst = placeholder();
	public static final Item amulet_persistence = placeholder();
	public static final Item amulet_healing_mushroom = placeholder();
	public static final Item amulet_shield = placeholder();
	public static final Item amulet_imbued_marble = placeholder();
	public static final Item amulet_oakflesh = placeholder();
	public static final Item amulet_inspiration = placeholder();
	public static final Item amulet_domus = placeholder();
	public static final Item amulet_searing_skin = placeholder();
	public static final Item amulet_cursed_mirror = placeholder();

	/// charm
	public static final Item charm_rift_bottle = placeholder();
	public static final Item charm_mana_flask = placeholder();
	public static final Item charm_reanimation = placeholder();
	public static final Item charm_knowledge_orb = placeholder();
	public static final Item charm_power_orb = placeholder();
	public static final Item charm_mana_orb = placeholder();
	public static final Item charm_progression_orb = placeholder();
	public static final Item charm_suppression_orb = placeholder();
	public static final Item charm_lightning_orb = placeholder();
	public static final Item charm_ice_orb = placeholder();
	public static final Item charm_earth_orb = placeholder();
	public static final Item charm_healing_orb = placeholder();
	public static final Item charm_necromancy_orb = placeholder();
	public static final Item charm_fire_orb = placeholder();
	public static final Item charm_sorcery_orb = placeholder();
	public static final Item charm_majestic_mana = placeholder();
	public static final Item charm_guardian_blade = placeholder();

	public static final Item charm_bucket_coal = placeholder();
	public static final Item cornucopia = placeholder();
	public static final Item charm_gold_bag = placeholder();
	public static final Item charm_evergrowing_crystal = placeholder();
	public static final Item charm_shadow_blade = placeholder();
	public static final Item charm_magic_light = placeholder();
	public static final Item charm_war_horn = placeholder();
	public static final Item charm_elemental_grimoire = placeholder();
	public static final Item charm_wand_upgrade = placeholder();
	public static final Item charm_enchanted_needle = placeholder();
	public static final Item charm_seed_bag = placeholder();
	public static final Item charm_wizard_tankard = placeholder();
	public static final Item charm_omnicron = placeholder();
	public static final Item charm_burrow = placeholder();
	public static final Item charm_quicksand_walker = placeholder();
	//	public static final Item charm_book_death = placeholder();
	//	public static final Item charm_book_life = placeholder();
	public static final Item charm_prismatic_spray = placeholder();
	public static final Item charm_ice_cream = placeholder();
	public static final Item charm_runic_hammer = placeholder();
	public static final Item charm_fabrikator_toolkit = placeholder();
	public static final Item charm_scissors = placeholder();
	public static final Item charm_philosophers_stone = placeholder();
	public static final Item charm_cube_phasing = placeholder();
	public static final Item charm_cube_duplication = placeholder();
	public static final Item charm_spectral_army = placeholder();
	public static final Item charm_spectral_tome = placeholder();
	public static final Item charm_clover = placeholder();
	public static final Item charm_stone_tablet = placeholder();
	//public static final Item charm_ice_arrow = placeholder();
	public static final Item charm_hoarders_orb = placeholder();
	public static final Item charm_elemental_alkahest = placeholder();
	public static final Item charm_sage_diary = placeholder();
	public static final Item charm_devoritium_magnet = placeholder();
	public static final Item charm_wild_catalyst = placeholder();
	public static final Item charm_metamagic_amplifier = placeholder();
	public static final Item charm_sentinel_eye = placeholder();
	public static final Item charm_plunderers_mark = placeholder();
	public static final Item charm_wizard_ale = placeholder();
	public static final Item alchemical_essence = placeholder();
	public static final Item wand_channeling = placeholder();

	public static final Item purifying_elixir_recipe = placeholder();
	public static final Item large_mana_flask_recipe = placeholder();

	/// now with Belts!

	public static final Item belt_enchanter = placeholder();
	public static final Item belt_stone = placeholder();
	//	public static final Item belt_druid = placeholder();
	public static final Item belt_vortex = placeholder();
	public static final Item belt_horse = placeholder();
	public static final Item belt_scroll_holder = placeholder();
	public static final Item belt_spring_charge = placeholder();
	public static final Item belt_soul_scorch = placeholder();

	/// and head slot artefacts!
	public static final Item head_curse = placeholder();
	public static final Item head_minions = placeholder();
	public static final Item head_riftstone = placeholder();
	public static final Item head_fortune = placeholder();
	public static final Item head_merchant = placeholder();
	public static final Item head_shield = placeholder();
	public static final Item head_magelight = placeholder();
	public static final Item head_lightning = placeholder();

	/// misc
	public static final Item magic_shield = placeholder();
	public static final Item stone_tablet_small = placeholder();
	public static final Item stone_tablet = placeholder();
	public static final Item stone_tablet_large = placeholder();
	public static final Item stone_tablet_grand = placeholder();
	public static final Item ancient_mana_flask = placeholder();
	public static final Item ancient_bound_stone = placeholder();

	public static final Item astral_diamond_shard = placeholder();
	public static final Item astral_diamond_charged = placeholder();
	public static final Item crystal_shard_fire = placeholder();
	public static final Item crystal_shard_earth = placeholder();
	public static final Item crystal_shard_healing = placeholder();
	public static final Item crystal_shard_ice = placeholder();
	public static final Item crystal_shard_lightning = placeholder();
	public static final Item crystal_shard_necromancy = placeholder();
	public static final Item crystal_shard_sorcery = placeholder();

	public static final Item devoritium_ingot = placeholder();
	public static final Item devoritium_sword = placeholder();
	public static final Item devoritium_axe = placeholder();
	public static final Item devoritium_arrow = placeholder();
	public static final Item devoritium_bomb = placeholder();
	public static final Item devoritium_door = placeholder();
	public static final Item devoritium_scimitar = placeholder();
	public static final Item devoritium_shield = placeholder();
	public static final Item devoritium_nugget = placeholder();
	public static final Item transmutation_scroll = placeholder();
	public static final Item bartering_scroll = placeholder();
	public static final Item knowledge_scroll = placeholder();
	public static final Item duplication_scroll = placeholder();
	public static final Item amnesia_scroll = placeholder();

	/// handheld
	public static final Item shadow_blade = placeholder();
	public static final Item spectral_fishing_rod = placeholder();
	public static final Item ice_shield = placeholder();
	public static final Item battlemage_sword_novice = placeholder();
	public static final Item battlemage_sword_apprentice = placeholder();
	public static final Item battlemage_sword_advanced = placeholder();
	public static final Item battlemage_sword_master = placeholder();
	public static final Item devoritium_chestplate = placeholder();
	public static final Item master_bolt = placeholder();

	public static final Item sacred_mace = placeholder();
	public static final Item stone_fist = placeholder();
	public static final Item advanced_stone_fist = placeholder();
	public static final Item scepter_mind_control = placeholder();

	// ====================== Misc ======================
	public static final Item enchanted_name_tag = placeholder();

	public static final Item blank_rune = placeholder();
	public static final Item rune_feoh = placeholder();
	public static final Item rune_uruz = placeholder();
	public static final Item rune_thurisaz = placeholder();
	public static final Item rune_ansuz = placeholder();
	public static final Item rune_raido = placeholder();
	public static final Item rune_kaunan = placeholder();
	public static final Item rune_gyfu = placeholder();
	public static final Item rune_wynn = placeholder();
	public static final Item rune_haglaz = placeholder();
	public static final Item rune_naudiz = placeholder();
	public static final Item rune_isaz = placeholder();
	public static final Item rune_jera = placeholder();
	public static final Item rune_ihwaz = placeholder();
	public static final Item rune_peorth = placeholder();
	public static final Item rune_algiz = placeholder();
	public static final Item rune_sowilo = placeholder();
	public static final Item rune_tiwaz = placeholder();
	public static final Item rune_berkanan = placeholder();
	public static final Item rune_ehwaz = placeholder();
	public static final Item rune_mannaz = placeholder();
	public static final Item rune_laguz = placeholder();
	public static final Item rune_yngvi = placeholder();
	public static final Item rune_odal = placeholder();
	public static final Item rune_dagaz = placeholder();
	public static final Item ritual_book = placeholder();
	public static final Item arcane_compound = placeholder();
	public static final Item ice_cream = placeholder();

	public static final Item soulbound_upgrade = placeholder();
	public static final Item spectral_shovel = placeholder();
	public static final Item battlemage_sword_hilt = placeholder();
	public static final Item battlemage_sword_blade = placeholder();
	public static final Item crystal_silver_ingot = placeholder();
	public static final Item crystal_silver_nugget = placeholder();

	public static final Item enchanted_filament = placeholder();
	public static final Item enchanted_page = placeholder();

	public static final Item sage_tome_novice_fire = placeholder();
	public static final Item sage_tome_apprentice_fire = placeholder();
	public static final Item sage_tome_advanced_fire = placeholder();
	public static final Item sage_tome_master_fire = placeholder();

	public static final Item sage_tome_novice_ice = placeholder();
	public static final Item sage_tome_apprentice_ice = placeholder();
	public static final Item sage_tome_advanced_ice = placeholder();
	public static final Item sage_tome_master_ice = placeholder();

	public static final Item sage_tome_novice_lightning = placeholder();
	public static final Item sage_tome_apprentice_lightning = placeholder();
	public static final Item sage_tome_advanced_lightning = placeholder();
	public static final Item sage_tome_master_lightning = placeholder();

	public static final Item sage_tome_novice_necromancy = placeholder();
	public static final Item sage_tome_apprentice_necromancy = placeholder();
	public static final Item sage_tome_advanced_necromancy = placeholder();
	public static final Item sage_tome_master_necromancy = placeholder();

	public static final Item sage_tome_novice_earth = placeholder();
	public static final Item sage_tome_apprentice_earth = placeholder();
	public static final Item sage_tome_advanced_earth = placeholder();
	public static final Item sage_tome_master_earth = placeholder();

	public static final Item sage_tome_novice_sorcery = placeholder();
	public static final Item sage_tome_apprentice_sorcery = placeholder();
	public static final Item sage_tome_advanced_sorcery = placeholder();
	public static final Item sage_tome_master_sorcery = placeholder();

	public static final Item sage_tome_novice_healing = placeholder();
	public static final Item sage_tome_apprentice_healing = placeholder();
	public static final Item sage_tome_advanced_healing = placeholder();
	public static final Item sage_tome_master_healing = placeholder();

	public static final Item empty_theory_scroll = placeholder();
	public static final Item theory_scroll = placeholder();

	public static final Item runic_plate = placeholder();
	public static final Item mystic_spell_book = placeholder();
	public static final Item empty_mystic_spell_book = placeholder();
	public static final Item tome_controller = placeholder();
	public static final Item sentience_upgrade = placeholder();
	public static final Item empowerment_upgrade = placeholder();
	public static final Item unsealing_scroll = placeholder();

	//public static final Item forbidden_tome = placeholder(); TODO

	public static final Item charm_glyph_illumination = placeholder();
	public static final Item charm_glyph_leeching = placeholder();
	public static final Item charm_glyph_antigravity = placeholder();
	public static final Item charm_glyph_vitality = placeholder();
	public static final Item charm_glyph_warden = placeholder();
	public static final Item charm_glyph_imbuement = placeholder();



	// below registry methods are courtesy of EB
	public static void registerItem(IForgeRegistry<Item> registry, String name, Item item) {
		registerItem(registry, name, item, false);
	}

	// below registry methods are courtesy of EB
	public static void registerItem(IForgeRegistry<Item> registry, String name, String modid, Item item) {
		registerItem(registry, name, modid, item, false);
	}

	public static void registerItem(IForgeRegistry<Item> registry, String name, Item item, boolean setTabIcon) {
		item.setRegistryName(AncientSpellcraft.MODID, name);
		item.setTranslationKey(item.getRegistryName().toString());
		registry.register(item);

		if (setTabIcon && item.getCreativeTab() instanceof WizardryTabs.CreativeTabSorted) {
			((WizardryTabs.CreativeTabSorted) item.getCreativeTab()).setIconItem(new ItemStack(item));
		}

		if (item.getCreativeTab() instanceof WizardryTabs.CreativeTabListed) {
			((WizardryTabs.CreativeTabListed) item.getCreativeTab()).order.add(item);
		}
	}

	public static void registerItem(IForgeRegistry<Item> registry, String modid, String name, Item item, boolean setTabIcon) {
		item.setRegistryName(modid, name);
		item.setTranslationKey(item.getRegistryName().toString());
		registry.register(item);

		if (setTabIcon && item.getCreativeTab() instanceof WizardryTabs.CreativeTabSorted) {
			((WizardryTabs.CreativeTabSorted) item.getCreativeTab()).setIconItem(new ItemStack(item));
		}

		if (item.getCreativeTab() instanceof WizardryTabs.CreativeTabListed) {
			((WizardryTabs.CreativeTabListed) item.getCreativeTab()).order.add(item);
		}
	}

	private static void registerItemBlock(IForgeRegistry<Item> registry, Block block) {
		Item itemblock = new ItemBlock(block).setRegistryName(block.getRegistryName());
		registry.register(itemblock);
	}

	private static void registerItemBlock(IForgeRegistry<Item> registry, Block block, Item itemblock) {
		itemblock.setRegistryName(block.getRegistryName());
		registry.register(itemblock);
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {

		IForgeRegistry<Item> registry = event.getRegistry();

		//		registerItemBlock(registry, AncientSpellcraftBlocks.SNOW_SLAB);
		//		registerItemBlock(registry, AncientSpellcraftBlocks.ICE_CRAFTING_TABLE);
		registerItemBlock(registry, ASBlocks.ARTEFACT_PENSIVE, new ItemBlock(ASBlocks.ARTEFACT_PENSIVE) {
			@Override
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				Wizardry.proxy.addMultiLineDescription(tooltip, "item.ancientspellcraft:pensive.desc");
			}

			@Override
			public EnumRarity getRarity(ItemStack stack) {
				return EnumRarity.EPIC;
			}
		});

		registerItemBlock(registry, ASBlocks.SKULL_WATCH, new ItemBlock(ASBlocks.SKULL_WATCH));
		registerItemBlock(registry, ASBlocks.SCRIBING_DESK, new ItemBlock(ASBlocks.SCRIBING_DESK));
		registerItemBlock(registry, ASBlocks.IMBUEMENT_ALTAR_RUINED, new ItemBlock(ASBlocks.IMBUEMENT_ALTAR_RUINED));
		registerItemBlock(registry, ASBlocks.SPHERE_COGNIZANCE, new ItemBlock(ASBlocks.SPHERE_COGNIZANCE));
		registerItemBlock(registry, ASBlocks.SENTINEL_BLOCK_IRON, new ItemBlock(ASBlocks.SENTINEL_BLOCK_IRON));
		registerItemBlock(registry, ASBlocks.SENTINEL_BLOCK_GOLD, new ItemBlock(ASBlocks.SENTINEL_BLOCK_GOLD));
		registerItemBlock(registry, ASBlocks.SENTINEL_BLOCK_DIAMOND, new ItemBlock(ASBlocks.SENTINEL_BLOCK_DIAMOND));
		registerItemBlock(registry, ASBlocks.SENTINEL_BLOCK_LARGE_IRON, new ItemBlock(ASBlocks.SENTINEL_BLOCK_LARGE_IRON));
		registerItemBlock(registry, ASBlocks.SENTINEL_BLOCK_LARGE_GOLD, new ItemBlock(ASBlocks.SENTINEL_BLOCK_LARGE_GOLD));
		registerItemBlock(registry, ASBlocks.SENTINEL_BLOCK_LARGE_DIAMOND, new ItemBlock(ASBlocks.SENTINEL_BLOCK_LARGE_DIAMOND));

		registerItemBlock(registry, ASBlocks.CRYSTAL_ORE_FIRE, new ItemBlock(ASBlocks.CRYSTAL_ORE_FIRE));
		registerItemBlock(registry, ASBlocks.CRYSTAL_ORE_EARTH, new ItemBlock(ASBlocks.CRYSTAL_ORE_EARTH));
		registerItemBlock(registry, ASBlocks.CRYSTAL_ORE_HEALING, new ItemBlock(ASBlocks.CRYSTAL_ORE_HEALING));
		registerItemBlock(registry, ASBlocks.CRYSTAL_ORE_ICE, new ItemBlock(ASBlocks.CRYSTAL_ORE_ICE));
		registerItemBlock(registry, ASBlocks.CRYSTAL_ORE_LIGHTNING, new ItemBlock(ASBlocks.CRYSTAL_ORE_LIGHTNING));
		registerItemBlock(registry, ASBlocks.CRYSTAL_ORE_NECROMANCY, new ItemBlock(ASBlocks.CRYSTAL_ORE_NECROMANCY));
		registerItemBlock(registry, ASBlocks.CRYSTAL_ORE_SORCERY, new ItemBlock(ASBlocks.CRYSTAL_ORE_SORCERY));

		registerItemBlock(registry, ASBlocks.DEVORITIUM_ORE, new ItemBlockDevoritiumMaterial(ASBlocks.DEVORITIUM_ORE));
		registerItemBlock(registry, ASBlocks.DEVORITIUM_BLOCK, new ItemBlockDevoritiumMaterial(ASBlocks.DEVORITIUM_BLOCK));
		registerItemBlock(registry, ASBlocks.DEVORITIUM_BARS, new ItemBlockDevoritiumMaterial(ASBlocks.DEVORITIUM_BARS));

		registerItemBlock(registry, ASBlocks.LOG_CRYSTAL_TREE, new ItemBlock(ASBlocks.LOG_CRYSTAL_TREE));
		//		registerItemBlock(registry, AncientSpellcraftBlocks.log_crystal_tree2, new ItemBlock(AncientSpellcraftBlocks.log_crystal_tree2));
		registerItemBlock(registry, ASBlocks.LEAVES_CRYSTAL_TREE, new ItemBlock(ASBlocks.LEAVES_CRYSTAL_TREE));

		registerItemBlock(registry, ASBlocks.DIMENSION_BOUNDARY, new ItemBlock(ASBlocks.DIMENSION_BOUNDARY));
		registerItemBlock(registry, ASBlocks.DIMENSION_FOCUS, new ItemBlock(ASBlocks.DIMENSION_FOCUS));
		registerItemBlock(registry, ASBlocks.ARCANE_ANVIL, new ItemBlock(ASBlocks.ARCANE_ANVIL));
		registerItemBlock(registry, ASBlocks.sage_lectern, new ItemBlock(ASBlocks.sage_lectern));
		registerItemBlock(registry, ASBlocks.master_bolt, new ItemMasterBolt());

		registerItemBlock(registry, ASBlocks.sealed_stone, new ItemBlock(ASBlocks.sealed_stone));
		registerItemBlock(registry, ASBlocks.unsealed_stone, new ItemBlock(ASBlocks.unsealed_stone));
		registerItemBlock(registry, ASBlocks.unseal_button, new ItemBlock(ASBlocks.unseal_button));


		//		registerItemBlock(registry, AncientSpellcraftBlocks.ANCIENT_DIMENSION_BOUNDARY, new ItemBlock(AncientSpellcraftBlocks.ANCIENT_DIMENSION_BOUNDARY));
		registerItem(registry, "sage_tome_novice_fire", new ItemSageTome(Tier.NOVICE, Element.FIRE));
		registerItem(registry, "sage_tome_apprentice_fire", new ItemSageTome(Tier.APPRENTICE, Element.FIRE));
		registerItem(registry, "sage_tome_advanced_fire", new ItemSageTome(Tier.ADVANCED, Element.FIRE));
		registerItem(registry, "sage_tome_master_fire", new ItemSageTome(Tier.MASTER, Element.FIRE));

		registerItem(registry, "sage_tome_novice_ice", new ItemSageTome(Tier.NOVICE, Element.ICE));
		registerItem(registry, "sage_tome_apprentice_ice", new ItemSageTome(Tier.APPRENTICE, Element.ICE));
		registerItem(registry, "sage_tome_advanced_ice", new ItemSageTome(Tier.ADVANCED, Element.ICE));
		registerItem(registry, "sage_tome_master_ice", new ItemSageTome(Tier.MASTER, Element.ICE));

		registerItem(registry, "sage_tome_novice_lightning", new ItemSageTome(Tier.NOVICE, Element.LIGHTNING));
		registerItem(registry, "sage_tome_apprentice_lightning", new ItemSageTome(Tier.APPRENTICE, Element.LIGHTNING));
		registerItem(registry, "sage_tome_advanced_lightning", new ItemSageTome(Tier.ADVANCED, Element.LIGHTNING));
		registerItem(registry, "sage_tome_master_lightning", new ItemSageTome(Tier.MASTER, Element.LIGHTNING));

		registerItem(registry, "sage_tome_novice_necromancy", new ItemSageTome(Tier.NOVICE, Element.NECROMANCY));
		registerItem(registry, "sage_tome_apprentice_necromancy", new ItemSageTome(Tier.APPRENTICE, Element.NECROMANCY));
		registerItem(registry, "sage_tome_master_necromancy", new ItemSageTome(Tier.MASTER, Element.NECROMANCY));
		registerItem(registry, "sage_tome_advanced_necromancy", new ItemSageTome(Tier.ADVANCED, Element.NECROMANCY));

		registerItem(registry, "sage_tome_novice_earth", new ItemSageTome(Tier.NOVICE, Element.EARTH));
		registerItem(registry, "sage_tome_apprentice_earth", new ItemSageTome(Tier.APPRENTICE, Element.EARTH));
		registerItem(registry, "sage_tome_advanced_earth", new ItemSageTome(Tier.ADVANCED, Element.EARTH));
		registerItem(registry, "sage_tome_master_earth", new ItemSageTome(Tier.MASTER, Element.EARTH));

		registerItem(registry, "sage_tome_novice_sorcery", new ItemSageTome(Tier.NOVICE, Element.SORCERY));
		registerItem(registry, "sage_tome_apprentice_sorcery", new ItemSageTome(Tier.APPRENTICE, Element.SORCERY));
		registerItem(registry, "sage_tome_advanced_sorcery", new ItemSageTome(Tier.ADVANCED, Element.SORCERY));
		registerItem(registry, "sage_tome_master_sorcery", new ItemSageTome(Tier.MASTER, Element.SORCERY));

		registerItem(registry, "sage_tome_novice_healing", new ItemSageTome(Tier.NOVICE, Element.HEALING));
		registerItem(registry, "sage_tome_apprentice_healing", new ItemSageTome(Tier.APPRENTICE, Element.HEALING));
		registerItem(registry, "sage_tome_advanced_healing", new ItemSageTome(Tier.ADVANCED, Element.HEALING));
		registerItem(registry, "sage_tome_master_healing", new ItemSageTome(Tier.MASTER, Element.HEALING));

		registerItem(registry, "spectral_shield", new ItemSpectralShield());
		registerItem(registry, "ancient_spellcraft_spell_book", new ItemASSpellBook());
		registerItem(registry, "ancient_spell_book", new ItemASSpellBook());
		registerItem(registry, "ancient_spellcraft_scroll", new ItemScroll());
		registerItem(registry, "enchanted_name_tag", new ItemEnchantedNameTag());

		registerItem(registry, "wizard_hat_ancient", new ItemAncientHat(WizardryItems.Materials.SILK, 1, EntityEquipmentSlot.HEAD, Element.MAGIC));

		registerItem(registry, "battlemage_sword_novice", new ItemBattlemageSword(Tier.NOVICE, 3));
		registerItem(registry, "battlemage_sword_apprentice", new ItemBattlemageSword(Tier.APPRENTICE, 5));
		registerItem(registry, "battlemage_sword_advanced", new ItemBattlemageSword(Tier.ADVANCED, 7));
		registerItem(registry, "battlemage_sword_master", new ItemBattlemageSword(Tier.MASTER, 9));

		registerItem(registry, "devoritium_ingot", new ItemDevoritium());
		registerItem(registry, "devoritium_nugget", new ItemDevoritium());
		registerItem(registry, "devoritium_sword", new ItemDevoritiumSword());
		registerItem(registry, "devoritium_axe", new ItemDevoritiumAxe());
		registerItem(registry, "devoritium_arrow", new ItemDevoritiumArrow().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "devoritium_bomb", new ItemDevoritiumBomb());
		registerItem(registry, "devoritium_door", new ItemDevoritiumDoor(ASBlocks.DEVORITIUM_DOOR));
		registerItem(registry, "devoritium_chestplate", new ItemDevoritiumArmour(AncientSpellcraft.DEVORITIUM_ARMOR_MATERIAL, 0, EntityEquipmentSlot.CHEST));
		registerItem(registry, "devoritium_scimitar", new ItemDevoritiumScimitar());
		registerItem(registry, "devoritium_shield", new ItemDevoritiumShield());
		registerItem(registry, "magic_shield", new ItemMagicShield(EnumRarity.EPIC));


		// ====================== Artefacts ======================

		/// ring
		registerItem(registry, "ring_poison_arrow", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.RING));
		registerItem(registry, "ring_lodestone", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_berserker", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_power", new ItemSetArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.RING, "jewels_of_power", 4, new ArrayList<String>(Arrays.asList("ring_power", "amulet_power", "charm_power_orb"))));
		registerItem(registry, "ring_prismarine", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_mana_transfer", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_blast", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING) {
			@Override
			@SideOnly(Side.CLIENT)
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				tooltip.add(net.minecraft.client.resources.I18n.format("item." + this.getRegistryName() + ".desc"));
				tooltip.add("\u00A7c" + net.minecraft.client.resources.I18n.format("item." + this.getRegistryName() + ".desc2"));
			}
		});

		registerItem(registry, "ring_range", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING) {
			@Override
			@SideOnly(Side.CLIENT)
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				tooltip.add(net.minecraft.client.resources.I18n.format("item." + this.getRegistryName() + ".desc"));
				tooltip.add("\u00A7c" + net.minecraft.client.resources.I18n.format("item." + this.getRegistryName() + ".desc2"));
			}
		});
		registerItem(registry, "ring_focus_crystal", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_unbinding", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_disenchanter", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_quicksand", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_permanent_shrinkage", new ItemASArtemisLibArtefact(EnumRarity.EPIC, ItemArtefact.Type.RING));
		registerItem(registry, "ring_permanent_growth", new ItemASArtemisLibArtefact(EnumRarity.EPIC, ItemArtefact.Type.RING));

		registerItem(registry, "ring_mana_lesser", new ItemManaArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.RING, 500));
		registerItem(registry, "ring_mana_greater", new ItemManaArtefact(EnumRarity.RARE, ItemArtefact.Type.RING, 1000));
		registerItem(registry, "ring_protector", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.RING));
		registerItem(registry, "ring_devotion", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_withdraw_life", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_cloudwalker", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_lily_flower", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.RING));
		registerItem(registry, "ring_spirit_ward", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_kinetic", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_charge", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.RING));
		registerItem(registry, "ring_griefing", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_metamagic_preserve", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));

		/// amulet
		registerItem(registry, "amulet_mana", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_rabbit", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_poison_resistance", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_power", new ItemSetArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET, "jewels_of_power", 4, new ArrayList<String>(Arrays.asList("ring_power", "amulet_power", "charm_power_orb"))));
		registerItem(registry, "amulet_persistence", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_curse_ward", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_pendant_of_eternity", new ItemEternityPendant(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_time_knot", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_arcane_catalyst", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_healing_mushroom", new ItemAmberMushroomAmulet(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_shield", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_imbued_marble", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_oakflesh", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_inspiration", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_domus", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_searing_skin",	new ItemSearingSkinAmulet(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_elemental_aura", new ItemElementalAuraAmulet(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_cursed_mirror", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET));

		/// charm
		registerItem(registry, "charm_seed_bag", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_wizard_tankard", new ItemWizardTankard(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_mana_flask", new ItemEverfullManaFlask());
		registerItem(registry, "charm_ice_cream", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_enchanted_needle", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_bucket_coal", new ItemCoalBucket(EnumRarity.RARE));
		registerItem(registry, "cornucopia", new ItemCornucopia(EnumRarity.RARE));
		registerItem(registry, "charm_gold_bag", new ItemGoldBag(EnumRarity.RARE));
		registerItem(registry, "charm_quicksand_walker", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_magic_light", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_mana_orb", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_knowledge_orb", new ItemKnowledgeOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_power_orb", new ItemSetArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "jewels_of_power", 4, new ArrayList<String>(Arrays.asList("ring_power", "amulet_power", "charm_power_orb"))));
		registerItem(registry, "charm_progression_orb", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_suppression_orb", new ItemDevoritiumOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM));

		registerItem(registry, "charm_lightning_orb", new ItemElementalOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "ebwizardry:thunderstorm", 6000, Element.LIGHTNING));
		registerItem(registry, "charm_ice_orb", new ItemElementalOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "ebwizardry:ice_age", 6000, Element.ICE));
		registerItem(registry, "charm_earth_orb", new ItemElementalOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "ebwizardry:earthquake", 6000, Element.EARTH));
		registerItem(registry, "charm_healing_orb", new ItemElementalOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "ebwizardry:font_of_vitality", 6000, Element.HEALING));
		registerItem(registry, "charm_necromancy_orb", new ItemElementalOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "ebwizardry:plague_of_darkness", 6000, Element.NECROMANCY));
		registerItem(registry, "charm_fire_orb", new ItemElementalOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "ebwizardry:firestorm", 6000, Element.FIRE));
		registerItem(registry, "charm_sorcery_orb", new ItemElementalOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "ebwizardry:shockwave", 6000, Element.SORCERY));

		registerItem(registry, "charm_rift_bottle", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "charm_reanimation", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_evergrowing_crystal", new ItemEvergrowingCrystal(EnumRarity.EPIC));
		registerItem(registry, "charm_shadow_blade", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_elemental_grimoire", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_war_horn", new ItemHorn(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_wand_upgrade", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_cryostasis", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_omnicron", new ItemOmnicron(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_burrow", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_prismatic_spray", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_runic_hammer", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_fabrikator_toolkit", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_scissors", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_philosophers_stone", new ItemPhiloshopersStone(EnumRarity.EPIC));
		registerItem(registry, "charm_cube_phasing", new ItemCubePhasing(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_cube_duplication", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_spectral_army", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_spectral_tome", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_clover", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_stone_tablet", new ItemMoonLetterDictionary(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_hoarders_orb", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_elemental_alkahest", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_sage_diary", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_devoritium_magnet", new ItemDevoritiumMagnet(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_wild_catalyst", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_metamagic_amplifier", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_majestic_mana",	new	ItemManaArtefact(EnumRarity.RARE, ItemArtefact.Type.RING, 2500));
		registerItem(registry, "charm_guardian_blade", new ItemGuardianBlade(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_sentinel_eye",	new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_plunderers_mark",	new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_wizard_ale",	new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));

		registerItem(registry, "charm_glyph_illumination", new ItemGlyphArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_glyph_leeching", new ItemGlyphArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_glyph_antigravity", new ItemGlyphArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_glyph_vitality", new ItemGlyphArtefact.ItemGlyphViality(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_glyph_warden", new ItemGlyphArtefact.ItemGlyphWarden(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_glyph_imbuement", new ItemGlyphArtefact.ItemGlyphWarden(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "tome_controller", new ItemTomeController());

		registerItem(registry, "scepter_mind_control", new ItemOverlordScepter());
		registerItem(registry, "purifying_elixir_recipe", new ItemLostRecipe(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "large_mana_flask_recipe", new ItemLostRecipe(EnumRarity.EPIC, ItemArtefact.Type.CHARM));


		registerItem(registry, "belt_enchanter", new ItemNewArtefact(EnumRarity.RARE, ItemNewArtefact.AdditionalType.BELT));
		registerItem(registry, "belt_stone", new ItemNewArtefact(EnumRarity.RARE, ItemNewArtefact.AdditionalType.BELT));
		registerItem(registry, "belt_vortex", new ItemNewArtefact(EnumRarity.RARE, ItemNewArtefact.AdditionalType.BELT));
		registerItem(registry, "belt_horse", new ItemNewArtefact(EnumRarity.UNCOMMON, ItemNewArtefact.AdditionalType.BELT));
		registerItem(registry, "belt_scroll_holder", new ItemBeltScrollHolder(EnumRarity.RARE, ItemNewArtefact.AdditionalType.BELT));
		registerItem(registry, "belt_spring_charge", new ItemNewArtefact(EnumRarity.RARE, ItemNewArtefact.AdditionalType.BELT));
		registerItem(registry, "belt_soul_scorch", new ItemNewArtefact(EnumRarity.RARE, ItemNewArtefact.AdditionalType.BELT));

		registerItem(registry, "head_curse", new ItemNewArtefact(EnumRarity.RARE, ItemNewArtefact.AdditionalType.HEAD));
		registerItem(registry, "head_merchant", new ItemNewArtefact(EnumRarity.RARE, ItemNewArtefact.AdditionalType.HEAD));
		registerItem(registry, "head_minions", new ItemNewArtefact(EnumRarity.EPIC, ItemNewArtefact.AdditionalType.HEAD));
		registerItem(registry, "head_riftstone", new ItemNewArtefact(EnumRarity.EPIC, ItemNewArtefact.AdditionalType.HEAD));
		registerItem(registry, "head_fortune", new ItemNewArtefact(EnumRarity.EPIC, ItemNewArtefact.AdditionalType.HEAD));
		registerItem(registry, "head_shield", new ItemNewArtefact(EnumRarity.EPIC, ItemNewArtefact.AdditionalType.HEAD));
		registerItem(registry, "head_magelight", new ItemNewArtefact(EnumRarity.RARE, ItemNewArtefact.AdditionalType.HEAD));
		registerItem(registry, "head_lightning", new ItemNewArtefact(EnumRarity.EPIC, ItemNewArtefact.AdditionalType.HEAD));

		/// misc

		registerItem(registry, "stone_tablet_small", new ItemRelic("stone_tablet_small", EnumRarity.COMMON));
		registerItem(registry, "stone_tablet", new ItemRelic("stone_tablet", EnumRarity.UNCOMMON));
		registerItem(registry, "stone_tablet_large", new ItemRelic("stone_tablet_large", EnumRarity.RARE));
		registerItem(registry, "stone_tablet_grand", new ItemRelic("stone_tablet_grand", EnumRarity.EPIC));
		registerItem(registry, "ancient_mana_flask", new ItemRelic("ancient_mana_flask", EnumRarity.UNCOMMON).setCreativeTab(null));
		registerItem(registry, "ancient_bound_stone", new ItemRelic("ancient_bound_stone", EnumRarity.EPIC).setCreativeTab(null));

		registerItem(registry, "shadow_blade", new ItemShadowBlade());
		registerItem(registry, "spectral_fishing_rod", new ItemSpectralFishingRod());
		registerItem(registry, "ice_shield", new ItemIceShield());
		registerItem(registry, "stone_fist", new ItemStoneFist());
		registerItem(registry, "advanced_stone_fist", new ItemAdvancedStoneFist());
		registerItem(registry, "sacred_mace", new ItemSacredMace());

		registerItem(registry, "astral_diamond_shard", new Item().setMaxDamage(0).setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "astral_diamond_charged", new ItemChargedAstralDiamond());

		registerItem(registry, "crystal_shard_fire", new Item().setMaxDamage(0).setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_earth", new Item().setMaxDamage(0).setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_healing", new Item().setMaxDamage(0).setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_ice", new Item().setMaxDamage(0).setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_lightning", new Item().setMaxDamage(0).setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_necromancy", new Item().setMaxDamage(0).setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_sorcery", new Item().setMaxDamage(0).setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "battlemage_sword_hilt", new Item().setMaxDamage(0).setMaxStackSize(1).setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "battlemage_sword_blade", new Item().setMaxDamage(0).setMaxStackSize(1).setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_silver_nugget", new ItemArmourUpgradeMaterial());
		registerItem(registry, "crystal_silver_ingot", new ItemArmourUpgradeMaterial());
		registerItem(registry, "enchanted_filament", new ItemArmourUpgradeMaterial());
		registerItem(registry, "enchanted_page", new ItemArmourUpgradeMaterial());

		registerItem(registry, "wand_channeling", new Item().setMaxDamage(0).setCreativeTab(null));

		registerItem(registry, "transmutation_scroll", new ItemTransmutationScroll());
		registerItem(registry, "bartering_scroll", new ItemBarterScroll());
		registerItem(registry, "knowledge_scroll", new ItemKnowledgeScroll());
		registerItem(registry, "duplication_scroll", new ItemDuplicationScroll());
		registerItem(registry, "amnesia_scroll", new ItemAmnesiaScroll());
		registerItem(registry, "unsealing_scroll", new ItemUnsealingScroll());
		registerItem(registry, "empty_theory_scroll", new ItemTheoryScroll());
		registerItem(registry, "theory_scroll", new ItemTheoryScroll());

		registerItem(registry, "blank_rune", new Item().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT).setMaxStackSize(16));
		registerItem(registry, "rune_feoh", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_uruz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_thurisaz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_ansuz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_raido", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_kaunan", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_gyfu", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_wynn", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_haglaz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_naudiz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_isaz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_jera", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_ihwaz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_peorth", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_algiz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_sowilo", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_tiwaz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_berkanan", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_ehwaz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_mannaz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_laguz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_yngvi", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_odal", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_dagaz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));

		registerItem(registry, "ritual_book", new ItemRitualBook());
		registerItem(registry, "arcane_compound", new Item().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT).setMaxStackSize(16));

		registerItem(registry, "soulbound_upgrade", new ItemSoulboundWandUpgrade());
		registerItem(registry, "sentience_upgrade", new ItemWandUpgradeAS());
		registerItem(registry, "empowerment_upgrade", new ItemEmpowermentTomeUpgrade());

		registerItem(registry, "spectral_shovel", new ItemSpectralShovel());
		registerItem(registry, "ice_cream", new ItemIceCream());
		registerItem(registry, "alchemical_essence", new ItemAlchemicalEssence());

		registerItem(registry, "runic_plate", new ItemRunicPlate(), true);
		registerItem(registry, "mystic_spell_book", new ItemSageSpellBook());
		registerItem(registry, "empty_mystic_spell_book", new ItemEmptyMysticSpellBook().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT).setMaxStackSize(16));
		//registerItem(registry, "forbidden_tome", new ItemWarlockSpellBook()); TODO

		//registerItem(registry, "master_bolt", new ItemMasterBolt());

	}

	public static void registerDispenseBehaviours() {
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ancient_spellcraft_scroll, new BehaviourSpellDispense());

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(devoritium_bomb, new BehaviorProjectileDispense() {
			@Override
			protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack stack) {
				EntityDevoritiumBomb entity = new EntityDevoritiumBomb(world);
				entity.setPosition(position.getX(), position.getY(), position.getZ());
				return entity;
			}
		});

	}

}